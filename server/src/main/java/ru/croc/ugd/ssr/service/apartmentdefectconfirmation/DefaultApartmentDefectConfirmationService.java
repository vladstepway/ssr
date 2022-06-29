package ru.croc.ugd.ssr.service.apartmentdefectconfirmation;

import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import ru.croc.ugd.ssr.ApartmentDefectConfirmation;
import ru.croc.ugd.ssr.ApartmentDefectConfirmationData;
import ru.croc.ugd.ssr.CcoData;
import ru.croc.ugd.ssr.OrganizationInformation;
import ru.croc.ugd.ssr.dto.apartmentdefectconfirmation.DefectConfirmationRequestDto;
import ru.croc.ugd.ssr.enums.SsrCcoOrganizationType;
import ru.croc.ugd.ssr.mapper.ApartmentDefectConfirmationMapper;
import ru.croc.ugd.ssr.model.ApartmentDefectConfirmationDocument;
import ru.croc.ugd.ssr.model.ApartmentInspectionDocument;
import ru.croc.ugd.ssr.model.api.oks.Organization;
import ru.croc.ugd.ssr.model.ssrcco.SsrCcoDocument;
import ru.croc.ugd.ssr.service.ApartmentInspectionService;
import ru.croc.ugd.ssr.service.ApartmentInspectionTaskService;
import ru.croc.ugd.ssr.service.CapitalConstructionObjectService;
import ru.croc.ugd.ssr.service.UserService;
import ru.croc.ugd.ssr.service.bpm.BpmService;
import ru.croc.ugd.ssr.service.document.ApartmentDefectConfirmationDocumentService;
import ru.croc.ugd.ssr.service.document.SsrCcoDocumentService;
import ru.croc.ugd.ssr.ssrcco.SsrCco;
import ru.croc.ugd.ssr.ssrcco.SsrCcoData;
import ru.croc.ugd.ssr.ssrcco.SsrCcoEmployee;
import ru.reinform.cdp.bpm.model.FormProperty;
import ru.reinform.cdp.bpm.model.Task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class DefaultApartmentDefectConfirmationService implements ApartmentDefectConfirmationService {

    private static final String PROCESS_CONFIRMATION_KEY = "ugdssrApartmentDefectConfirmation_processConfirmation";
    private static final String CANDIDATE_CORRECTION_TASK_VAR_KEY = "generalContractorLogin";
    private static final String CONFIRM_INFORMATION_TASK_DEFINITION_KEY =
        "ugdssrApartmentDefectConfirmation_ConfirmInformation_task";
    private static final String DEVELOPERS_GROUP_TASK_VAR_KEY = "developersGroupInput";
    private static final String DEVELOPER_ROLE_NAME = "Застройщик";

    private final ApartmentDefectConfirmationDocumentService apartmentDefectConfirmationDocumentService;
    private final SsrCcoDocumentService ssrCcoDocumentService;
    private final CapitalConstructionObjectService capitalConstructionObjectService;
    private final BpmService bpmService;
    private final ApartmentInspectionTaskService apartmentInspectionTaskService;
    private final ApartmentInspectionService apartmentInspectionService;
    private final ApartmentDefectConfirmationMapper apartmentDefectConfirmationMapper;
    private final UserService userService;

    @Override
    public void actualizeTaskCandidatesBySsrCco() {
        log.info("Началась актуализация списка кандидатов активных задач по SsrCco");

        ssrCcoDocumentService.fetchAll()
            .stream()
            .map(SsrCcoDocument::getDocument)
            .map(SsrCco::getSsrCcoData)
            .filter(Objects::nonNull)
            .filter(data -> nonNull(data.getUnom()))
            .filter(data -> data.getEmployees().size() > 0)
            .map(SsrCcoData::getUnom)
            .forEach(this::actualizeTaskCandidatesBySsrCco);

        log.info("Завершилась актуализация списка кандидатов активных задач по SsrCco");
    }

    @Override
    public void actualizeTaskCandidatesBySsrCco(final String unom) {
        apartmentDefectConfirmationDocumentService
            .fetchByUnom(unom)
            .forEach(apartmentDefectConfirmationDocument -> {
                try {
                    actualizeTaskCandidates(apartmentDefectConfirmationDocument);
                } catch (Exception e) {
                    log.error(
                        "Возникла ошибка при актуализации списка кандидатов активных задач"
                            + " apartmentDefectConfirmationId = {}: {}",
                        apartmentDefectConfirmationDocument.getId(),
                        e.getMessage(),
                        e
                    );
                }
            });
    }

    @Override
    public void actualizeTaskCandidates(
        final ApartmentDefectConfirmationDocument apartmentDefectConfirmationDocument
    ) {
        final List<Task> activeTasks = getActiveTasksToProcess(apartmentDefectConfirmationDocument);

        if (CollectionUtils.isEmpty(activeTasks)) {
            return;
        }

        final List<SsrCcoEmployee> activeEmployees = getActiveEmployees(apartmentDefectConfirmationDocument);
        final List<String> developerUsers = activeEmployees
            .stream()
            .filter(e -> SsrCcoOrganizationType.ofTypeCode(e.getType()) == SsrCcoOrganizationType.DEVELOPER)
            .map(SsrCcoEmployee::getLogin)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

        if (!developerUsers.isEmpty()) {
            activeTasks.stream()
                .filter(this::isConfirmInformationTask)
                .forEach(task -> apartmentInspectionTaskService.actualizeTaskCandidates(task, developerUsers));
        } else {
            final ApartmentDefectConfirmationData apartmentDefectConfirmationData =
                apartmentDefectConfirmationDocument.getDocument().getApartmentDefectConfirmationData();
            final String ccoDocumentId = ofNullable(apartmentDefectConfirmationData.getCcoData())
                .map(CcoData::getCcoDocumentId)
                .orElse(null);
            final List<OrganizationInformation> developers = getOrgNamesByRole(ccoDocumentId, DEVELOPER_ROLE_NAME);
            final String foundGroup = apartmentInspectionTaskService.getUserGroupByOksDevelopers(developers);
            activeTasks.stream()
                .filter(this::isConfirmInformationTask)
                .forEach(task -> apartmentInspectionTaskService.addTaskCandidateGroup(task.getId(), foundGroup));
        }

        apartmentInspectionTaskService.deleteAssigneesIfNeeded(activeTasks);
    }

    @Override
    public void startConfirmationProcess(
        final ApartmentDefectConfirmationDocument apartmentDefectConfirmationDocument
    ) {
        log.info(
            "Start confirmation process creation: apartmentDefectConfirmationId = {}",
            apartmentDefectConfirmationDocument.getId()
        );
        createConfirmationProcess(apartmentDefectConfirmationDocument)
            .ifPresent(processInstanceId -> {
                log.info(
                    "Confirmation process created: apartmentDefectConfirmationId = {}, processInstanceId = {}",
                    apartmentDefectConfirmationDocument.getId(),
                    processInstanceId
                );
                apartmentDefectConfirmationDocument
                    .getDocument()
                    .getApartmentDefectConfirmationData()
                    .setProcessInstanceId(processInstanceId);
                apartmentDefectConfirmationDocumentService.updateDocument(
                    apartmentDefectConfirmationDocument, "createConfirmationProcess"
                );
                actualizeTaskCandidates(apartmentDefectConfirmationDocument);
            });
        log.info(
            "Finish confirmation process creation: apartmentDefectConfirmationId = {}",
            apartmentDefectConfirmationDocument.getId()
        );
    }

    private Optional<String> createConfirmationProcess(
        final ApartmentDefectConfirmationDocument apartmentDefectConfirmationDocument
    ) {
        final String apartmentDefectConfirmationId = apartmentDefectConfirmationDocument.getId();

        final ApartmentDefectConfirmationData apartmentDefectConfirmation = apartmentDefectConfirmationDocument
            .getDocument()
            .getApartmentDefectConfirmationData();

        final String generalContractorLogin = apartmentDefectConfirmation.getGeneralContractorLogin();
        final String ccoDocumentId = ofNullable(apartmentDefectConfirmation.getCcoData())
            .map(CcoData::getCcoDocumentId)
            .orElse(null);

        final Map<String, String> variablesMap = new HashMap<>();
        variablesMap.put(BpmService.PROCESS_DOCUMENT_KEY_ENTITY_ID_VAR, apartmentDefectConfirmationId);
        variablesMap.put(CANDIDATE_CORRECTION_TASK_VAR_KEY, generalContractorLogin);

        final List<OrganizationInformation> developers = getOrgNamesByRole(ccoDocumentId, DEVELOPER_ROLE_NAME);

        final String foundGroup = apartmentInspectionTaskService.getUserGroupByOksDevelopers(developers);
        variablesMap.put(DEVELOPERS_GROUP_TASK_VAR_KEY, foundGroup);

        return ofNullable(bpmService.startNewProcess(PROCESS_CONFIRMATION_KEY, variablesMap));
    }

    @Override
    public void closeConfirmationTask(final String processInstanceId, final boolean areConfirmed) {
        final Optional<Task> optionalTask = bpmService
            .getTasksByProcessId(PROCESS_CONFIRMATION_KEY, processInstanceId)
            .stream()
            .findFirst();

        if (!optionalTask.isPresent()) {
            log.warn("Apartment defect confirmation task not found for process {}", processInstanceId);
            return;
        }
        final Task task = optionalTask.get();
        final List<FormProperty> formProperties = new ArrayList<>();
        formProperties.add(new FormProperty("AreConfirmed", Boolean.toString(areConfirmed)));

        bpmService.completeTaskViaForm(task.getId(), formProperties);
    }

    @Override
    public void closeCorrectionTask(final String processInstanceId) {
        final Optional<Task> optionalTask = bpmService
            .getTasksByProcessId(PROCESS_CONFIRMATION_KEY, processInstanceId)
            .stream()
            .findFirst();

        if (!optionalTask.isPresent()) {
            log.warn("Apartment defect correction task not found for process {}", processInstanceId);
            return;
        }
        final Task task = optionalTask.get();

        bpmService.completeTaskViaForm(task.getId(), Collections.emptyList());
    }

    @Override
    public ApartmentDefectConfirmationDocument createApartmentDefectConfirmation(
        final ApartmentInspectionDocument apartmentInspectionDocument,
        final List<DefectConfirmationRequestDto> defectConfirmationRequestDtos
    ) {
        final ApartmentDefectConfirmationDocument apartmentDefectConfirmationDocument =
            apartmentDefectConfirmationMapper.toApartmentDefectConfirmationDocument(
                LocalDateTime.now(),
                userService.getCurrentUserLogin(),
                defectConfirmationRequestDtos,
                apartmentInspectionService.retrieveSsrCcoDocument(apartmentInspectionDocument)
            );
        return apartmentDefectConfirmationDocumentService.createDocument(
            apartmentDefectConfirmationDocument, true, "createByApartmentInspectionDocument"
        );
    }

    private List<Task> getActiveTasksToProcess(
        final ApartmentDefectConfirmationDocument apartmentDefectConfirmationDocument
    ) {
        final String activeProcessId = getActiveProcessInstanceId(apartmentDefectConfirmationDocument);
        return bpmService.getTasksByProcessId(PROCESS_CONFIRMATION_KEY, activeProcessId);
    }

    private String getActiveProcessInstanceId(
        final ApartmentDefectConfirmationDocument apartmentDefectConfirmationDocument
    ) {
        return ofNullable(apartmentDefectConfirmationDocument)
            .map(ApartmentDefectConfirmationDocument::getDocument)
            .map(ApartmentDefectConfirmation::getApartmentDefectConfirmationData)
            .map(ApartmentDefectConfirmationData::getProcessInstanceId)
            .orElse(null);
    }

    private boolean isConfirmInformationTask(final Task task) {
        return CONFIRM_INFORMATION_TASK_DEFINITION_KEY.equals(task.getTaskDefinitionKey());
    }

    private List<SsrCcoEmployee> getActiveEmployees(
        final ApartmentDefectConfirmationDocument apartmentDefectConfirmationDocument
    ) {
        return ofNullable(apartmentDefectConfirmationDocument)
            .map(ApartmentDefectConfirmationDocument::getDocument)
            .map(ApartmentDefectConfirmation::getApartmentDefectConfirmationData)
            .map(ApartmentDefectConfirmationData::getCcoData)
            .map(CcoData::getUnom)
            .map(apartmentInspectionTaskService::getActiveEmployees)
            .orElse(Collections.emptyList());
    }

    private List<OrganizationInformation> getOrgNamesByRole(final String ccoDocumentId, final String role) {
        return ofNullable(ccoDocumentId)
            .map(Collections::singletonList)
            .map(capitalConstructionObjectService::psGetOrganizationsFromCco)
            .orElse(Collections.emptyList())
            .stream()
            .filter(organization -> StringUtils.equalsIgnoreCase(organization.getRoleName(), role))
            .map(this::mapToOrganizationInformation)
            .collect(Collectors.toList());
    }

    private OrganizationInformation mapToOrganizationInformation(final Organization organization) {
        final OrganizationInformation organizationInformation = new OrganizationInformation();
        organizationInformation.setExternalId(organization.getInn());
        organizationInformation.setOrgFullName(organization.getFullName());
        return organizationInformation;
    }
}
