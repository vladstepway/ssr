package ru.croc.ugd.ssr.service;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static ru.croc.ugd.ssr.report.defects.DefectExcelDataPrinter.ADDRESS;
import static ru.croc.ugd.ssr.report.defects.DefectExcelDataPrinter.AREA_AND_DISTRICT;
import static ru.croc.ugd.ssr.utils.StreamUtils.not;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import ru.croc.ugd.ssr.ApartmentDefectConfirmation;
import ru.croc.ugd.ssr.ApartmentDefectConfirmationData;
import ru.croc.ugd.ssr.ApartmentDefectElimination;
import ru.croc.ugd.ssr.ApartmentDefectType;
import ru.croc.ugd.ssr.ApartmentEliminationDefectType;
import ru.croc.ugd.ssr.ApartmentInspection;
import ru.croc.ugd.ssr.ApartmentInspectionType;
import ru.croc.ugd.ssr.ApartmentInspectionType.ApartmentDefects;
import ru.croc.ugd.ssr.Cip;
import ru.croc.ugd.ssr.CipType;
import ru.croc.ugd.ssr.DefectData;
import ru.croc.ugd.ssr.OrganizationInformation;
import ru.croc.ugd.ssr.Person;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.commission.CommissionInspectionData;
import ru.croc.ugd.ssr.config.ApartmentInspectionProperties;
import ru.croc.ugd.ssr.db.dao.ApartmentInspectionDao;
import ru.croc.ugd.ssr.db.projection.ApartmentDefectProjection;
import ru.croc.ugd.ssr.dto.apartmentdefectconfirmation.DefectConfirmationRequestDto;
import ru.croc.ugd.ssr.dto.apartmentdefectconfirmation.DefectFlatDto;
import ru.croc.ugd.ssr.dto.apartmentdefectconfirmation.RestDefectDto;
import ru.croc.ugd.ssr.dto.apartmentinspection.AcceptedDefectsActDto;
import ru.croc.ugd.ssr.dto.apartmentinspection.ApartmentInspectionDto;
import ru.croc.ugd.ssr.dto.apartmentinspection.CloseActWithoutConsentDto;
import ru.croc.ugd.ssr.dto.apartmentinspection.DelayReasonDto;
import ru.croc.ugd.ssr.dto.apartmentinspection.IntegrationLogDto;
import ru.croc.ugd.ssr.dto.apartmentinspection.PersonConsentDto;
import ru.croc.ugd.ssr.dto.apartmentinspection.RestApartmentInspectionDto;
import ru.croc.ugd.ssr.dto.apartmentinspection.RestApartmentInspectionReportDto;
import ru.croc.ugd.ssr.dto.apartmentinspection.RestCloseActsWithoutConsentDto;
import ru.croc.ugd.ssr.dto.apartmentinspection.RestConfirmClosingWithoutConsentDto;
import ru.croc.ugd.ssr.dto.apartmentinspection.RestDefectsEliminationDto;
import ru.croc.ugd.ssr.dto.apartmentinspection.RestFileDto;
import ru.croc.ugd.ssr.dto.apartmentinspection.RestSendTasksToGeneralContractorDto;
import ru.croc.ugd.ssr.dto.apartmentinspection.RestUpdateDefectDto;
import ru.croc.ugd.ssr.dto.commissioninspection.DefectDto;
import ru.croc.ugd.ssr.enums.SsrCcoOrganizationType;
import ru.croc.ugd.ssr.exception.NotPossibleToRemoveApartmentInspection;
import ru.croc.ugd.ssr.exception.SsrException;
import ru.croc.ugd.ssr.integration.service.flows.DefectEliminationFlowsService;
import ru.croc.ugd.ssr.integration.service.notification.ElkUserNotificationService;
import ru.croc.ugd.ssr.mapper.ApartmentInspectionMapper;
import ru.croc.ugd.ssr.model.ApartmentDefectConfirmationDocument;
import ru.croc.ugd.ssr.model.ApartmentInspectionDocument;
import ru.croc.ugd.ssr.model.CipDocument;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.api.chess.FullFlatResponse;
import ru.croc.ugd.ssr.model.api.chess.FullOksHousesResponse;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPDefectEliminationActType;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPDefectEliminationAgreementType;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPDefectEliminationTransferType;
import ru.croc.ugd.ssr.model.ssrcco.SsrCcoDocument;
import ru.croc.ugd.ssr.report.defects.DefectReportGenerator;
import ru.croc.ugd.ssr.service.apartmentdefectconfirmation.ApartmentDefectConfirmationService;
import ru.croc.ugd.ssr.service.bpm.BpmService;
import ru.croc.ugd.ssr.service.cip.CipService;
import ru.croc.ugd.ssr.service.commissioninspection.CommissionInspectionService;
import ru.croc.ugd.ssr.service.document.SsrCcoDocumentService;
import ru.croc.ugd.ssr.service.reporter.ReportFieldType;
import ru.croc.ugd.ssr.service.reporter.SsrReporterService;
import ru.croc.ugd.ssr.service.reporter.apartmentinspection.ApartmentInspectionFieldType;
import ru.croc.ugd.ssr.ssrcco.SsrCco;
import ru.croc.ugd.ssr.ssrcco.SsrCcoData;
import ru.croc.ugd.ssr.ssrcco.SsrCcoEmployee;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.croc.ugd.ssr.utils.ApartmentInspectionUtils;
import ru.croc.ugd.ssr.utils.DateTimeUtils;
import ru.croc.ugd.ssr.utils.PageUtils;
import ru.croc.ugd.ssr.utils.PersonUtils;
import ru.croc.ugd.ssr.utils.RealEstateUtils;
import ru.reinform.cdp.bpm.model.FormProperty;
import ru.reinform.cdp.bpm.model.Task;
import ru.reinform.cdp.bpm.model.Variable;
import ru.reinform.cdp.db.model.DocumentData;
import ru.reinform.cdp.document.model.DocumentType;
import ru.reinform.cdp.document.service.DocumentDataService;
import ru.reinform.cdp.ldap.model.OrganizationBean;
import ru.reinform.cdp.ldap.model.UserBean;
import ru.reinform.cdp.search.service.SearchRemoteService;
import ru.reinform.cdp.utils.mapper.JsonMapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.annotation.Nonnull;

/**
 * Сервис по утранению дефектов квартиры.
 */
@Slf4j
@Service
public class DefaultApartmentInspectionService
    extends DocumentWithFolder<ApartmentInspectionDocument>
    implements ApartmentInspectionService {

    @Value("${ugd.ssr.apartment-inspection.defect-elimination-flows.enabled:false}")
    private boolean defectEliminationFlowsEnabled;

    private static final String CHED_DOCUMENT_CODE = "13049";
    private static final String CHED_DOCUMENT_CLASS = "CommissionInspectionReport";
    private static final String CANDIDATES_ORG_TASK_VAR_KEY = "candidatesOrgInput";
    private static final String DEVELOPERS_GROUP_TASK_VAR_KEY = "developersGroupInput";
    private static final String CONTRACTOR_GROUP_FILTER = "UGD_SSR_PERSON_DEVELOPER";
    private static final String PROCESS_KEY_DEFECTS_ELIMINATION_START = "ugdssr_DefectsEliminationStart_prc";
    private static final String TASK_TIMER_DIFINITION_KEY = "auto_tmrWaitAndProceed_ID";
    private static final String SHORT_EMPTY_PDF_LINE = "_______________________________________________________";
    private static final String LONG_EMPTY_PDF_LINE = "_______________________________________________________"
        + "_____________________________________________________________";

    private final BpmService bpmService;
    private final ApartmentInspectionDao apartmentInspectionDao;
    private final PersonDocumentService personDocumentService;
    private final SearchRemoteService searchRemoteService;
    private final UserService userService;
    private final ApartmentInspectionProperties apartmentInspectionProperties;
    private final CipService cipService;
    private final CapitalConstructionObjectService capitalConstructionObjectService;
    private final ApartmentInspectionMapper apartmentInspectionMapper;
    private final ChedFileService chedFileService;
    private final CommissionInspectionService commissionInspectionService;
    private final DefectEliminationFlowsService defectEliminationFlowsService;
    private final ElkUserNotificationService elkUserNotificationService;
    private final SsrCcoDocumentService ssrCcoDocumentService;
    private final DocumentDataService documentDataService;
    private final SsrFilestoreService ssrFilestoreService;
    private final DefectReportGenerator defectReportGenerator;
    private final ApartmentInspectionTaskService apartmentInspectionTaskService;
    private final SsrReporterService ssrReporterService;
    private final FlatService flatService;
    private final ApartmentDefectConfirmationService apartmentDefectConfirmationService;

    public DefaultApartmentInspectionService(
        final BpmService bpmService,
        final ApartmentInspectionDao apartmentInspectionDao,
        final JsonMapper jsonMapper,
        final PersonDocumentService personDocumentService,
        final SearchRemoteService searchRemoteService,
        final UserService userService,
        final ApartmentInspectionProperties apartmentInspectionProperties,
        final CipService cipService,
        final CapitalConstructionObjectService capitalConstructionObjectService,
        final ApartmentInspectionMapper apartmentInspectionMapper,
        final ChedFileService chedFileService,
        @Lazy final CommissionInspectionService commissionInspectionService,
        final DefectEliminationFlowsService defectEliminationFlowsService,
        @Lazy final ElkUserNotificationService elkUserNotificationService,
        final SsrCcoDocumentService ssrCcoDocumentService,
        final DocumentDataService documentDataService,
        final SsrFilestoreService ssrFilestoreService,
        final DefectReportGenerator defectReportGenerator,
        final ApartmentInspectionTaskService apartmentInspectionTaskService,
        final SsrReporterService ssrReporterService,
        final FlatService flatService,
        @Lazy final ApartmentDefectConfirmationService apartmentDefectConfirmationService
    ) {
        this.bpmService = bpmService;
        this.apartmentInspectionDao = apartmentInspectionDao;
        this.jsonMapper = jsonMapper;
        this.personDocumentService = personDocumentService;
        this.searchRemoteService = searchRemoteService;
        this.userService = userService;
        this.apartmentInspectionProperties = apartmentInspectionProperties;
        this.cipService = cipService;
        this.capitalConstructionObjectService = capitalConstructionObjectService;
        this.apartmentInspectionMapper = apartmentInspectionMapper;
        this.chedFileService = chedFileService;
        this.commissionInspectionService = commissionInspectionService;
        this.defectEliminationFlowsService = defectEliminationFlowsService;
        this.elkUserNotificationService = elkUserNotificationService;
        this.ssrCcoDocumentService = ssrCcoDocumentService;
        this.documentDataService = documentDataService;
        this.ssrFilestoreService = ssrFilestoreService;
        this.defectReportGenerator = defectReportGenerator;
        this.apartmentInspectionTaskService = apartmentInspectionTaskService;
        this.ssrReporterService = ssrReporterService;
        this.flatService = flatService;
        this.apartmentDefectConfirmationService = apartmentDefectConfirmationService;
    }

    @Nonnull
    @Override
    public DocumentType<ApartmentInspectionDocument> getDocumentType() {
        return SsrDocumentTypes.APARTMENT_INSPECTION;
    }

    @Override
    public ApartmentInspectionDocument createDocument(@Nonnull ApartmentInspectionDocument document) {
        return super.createDocument(document, true, null);
    }

    @Override
    public ApartmentInspectionDocument createOrUpdateDocument(@Nonnull ApartmentInspectionDocument document) {
        if (document.getId() == null) {
            return createDocument(document);
        }

        return super.updateDocument(document.getId(), document, true, true, null);
    }

    public void beforeCreate(@NotNull ApartmentInspectionDocument document) {
        addDefectIdsIfNeeded(document);
    }

    public void beforeUpdate(
        @NotNull ApartmentInspectionDocument oldDocument, @NotNull ApartmentInspectionDocument newDocument
    ) {
        addDefectIdsIfNeeded(newDocument);
    }

    private void addDefectIdsIfNeeded(final ApartmentInspectionDocument document) {
        ofNullable(document)
            .map(ApartmentInspectionDocument::getDocument)
            .map(ApartmentInspection::getApartmentInspectionData)
            .map(ApartmentInspectionType::getApartmentDefects)
            .map(List::stream)
            .orElseGet(Stream::empty)
            .map(ApartmentDefects::getApartmentDefectData)
            .forEach(defect -> {
                if (nonNull(defect) && isNull(defect.getId())) {
                    defect.setId(UUID.randomUUID().toString());
                }
            });
    }

    @Override
    public ApartmentInspectionDocument findDocumentWithStartedProcessByPersonId(
        @Nonnull String personId) {
        return apartmentInspectionDao.fetchDocWithStartedProcessByPersonId(personId)
            .map(this::parseDocumentData)
            .orElse(null); // TODO replace with well-named exception and handlers for 404
    }

    @Override
    public void safeDeleteActByActId(@Nonnull final String actId) {
        final ApartmentInspectionDocument apartmentInspectionDocument =
            fetchDocument(actId);
        if (isPersonAgreementGiven(apartmentInspectionDocument)) {
            throw new NotPossibleToRemoveApartmentInspection();
        }
        forceFinishProcess(apartmentInspectionDocument);
        apartmentInspectionDocument.getDocument().getApartmentInspectionData().setIsRemoved(true);
        this.updateDocument(apartmentInspectionDocument.getId(),
            apartmentInspectionDocument,
            true,
            true,
            null);

        deleteFromSolrById(apartmentInspectionDocument.getId());
    }

    @Override
    public void setCurrentDefectsEliminatedDate(
        @Nonnull String apartmentInspectionId
    ) {
        final ApartmentInspectionDocument apartmentInspectionDocument = fetchDocument(apartmentInspectionId);
        if (isDocNotEmpty(apartmentInspectionDocument)) {
            apartmentInspectionDocument.getDocument()
                .getApartmentInspectionData()
                .setDefectsEliminatedNotificationDate(LocalDateTime.now());
            this.updateDocument(apartmentInspectionDocument.getId(),
                apartmentInspectionDocument,
                true,
                true,
                null);

            sendDefectEliminationAgreement(apartmentInspectionDocument);
        }
    }

    private boolean isPersonAgreementGiven(final ApartmentInspectionDocument apartmentInspectionDocument) {
        if (isDocNotEmpty(apartmentInspectionDocument)) {
            return apartmentInspectionDocument.getDocument()
                .getApartmentInspectionData().getAcceptedDefectsDate() != null;
        }
        return false;
    }

    @Override
    public void forceFinishProcess(final String apartmentInspectionDocumentId, final boolean sendDefectsFixedIfNeeded) {
        final ApartmentInspectionDocument apartmentInspectionDocument = fetchDocument(apartmentInspectionDocumentId);
        if (sendDefectsFixedIfNeeded) {
            defectsFixed(apartmentInspectionDocument);
        }

        forceFinishProcess(apartmentInspectionDocument);
    }

    @Override
    public void forceFinishProcess(final ApartmentInspectionDocument apartmentInspectionDocument) {
        if (isDocNotEmpty(apartmentInspectionDocument)) {
            final String processInstanceId = getActiveProcessInstanceId(apartmentInspectionDocument);
            if (StringUtils.isNotBlank(processInstanceId)) {
                bpmService.deleteProcessInstance(processInstanceId);
            }
        }
    }

    @Override
    public void processCommissionInspectionRefusal(
        final String apartmentInspectionDocumentId, final boolean isFlatRefusal
    ) {
        final ApartmentInspectionDocument apartmentInspectionDocument = fetchDocument(apartmentInspectionDocumentId);
        final ApartmentInspectionType apartmentInspection = apartmentInspectionDocument
            .getDocument()
            .getApartmentInspectionData();

        if (isFlatRefusal) {
            apartmentInspection.setFlatRefusalDate(LocalDate.now());
        }

        updateDocument(apartmentInspectionDocument);

        forceFinishProcess(apartmentInspectionDocument);
    }

    @Override
    public String createDocumentAndStartDefectEliminationProcess(ApartmentInspectionDocument document) {
        final ApartmentInspectionType apartmentInspection = document
            .getDocument()
            .getApartmentInspectionData();
        final String actNumber = ofNullable(apartmentInspection.getActNum())
            .orElse("б/н");
        apartmentInspection.setActNum(actNumber);

        final String cipId = retrieveCipId(document);
        apartmentInspection.setCipId(cipId);

        apartmentInspection.setPending(false);

        ofNullable(apartmentInspection.getSignedActFileId())
            .map(this::uploadActFileToChed)
            .ifPresent(apartmentInspection::setSignedActChedFileId);

        ofNullable(document.getId())
            .map(this::fetchDocument)
            .ifPresent(apartmentInspectionDocument -> {
                final ApartmentInspectionType apartmentInspectionData =
                    apartmentInspectionDocument.getDocument().getApartmentInspectionData();
                if (nonNull(apartmentInspectionData.getProcessInstanceId())) {
                    apartmentInspection.setProcessInstanceId(apartmentInspectionData.getProcessInstanceId());
                }
            });

        final String documentId = this.createOrUpdateDocument(document)
            .getId();

        final String commissionInspectionId = apartmentInspection.getCommissionInspectionId();
        if (apartmentInspection.isHasDefects()) {
            if (isNull(apartmentInspection.getProcessInstanceId())) {
                final String processInstanceId = createDefectsEliminationProcess(documentId, apartmentInspection);
                apartmentInspection.setProcessInstanceId(processInstanceId);
            } else {
                log.info(
                    "Bpm process has been already created: processInstanceId = {}",
                    apartmentInspection.getProcessInstanceId()
                );
            }

            actualizeTaskCandidates(document);
            if (defectEliminationFlowsEnabled && notExistSignedContract(apartmentInspection.getPersonID())) {
                apartmentInspection.setUseDefectEliminationFlows(true);
            }

            this.updateDocument(documentId, document, true, true, null);

            if (nonNull(commissionInspectionId)) {
                commissionInspectionService.processDefectsFound(commissionInspectionId);
            }

            sendDefectEliminationAct(document);
        } else {
            if (nonNull(commissionInspectionId)) {
                commissionInspectionService.processDefectsNotFound(commissionInspectionId);
            }
        }

        return documentId;
    }

    private boolean notExistSignedContract(final String personId) {
        final PersonDocument personDocument = personDocumentService.fetchDocument(personId);
        final boolean existsOfferLetter = PersonUtils.getOfferLetterStream(personDocument)
            .findAny()
            .isPresent();
        final boolean existsSignedContract = PersonUtils.getContractsStream(personDocument)
            .anyMatch(contract -> nonNull(contract.getContractSignDate()));
        return existsOfferLetter && !existsSignedContract;
    }

    private String createDefectsEliminationProcess(
        final String apartmentInspectionId, final ApartmentInspectionType apartmentInspection
    ) {
        final Map<String, String> variablesMap = new HashMap<>();

        variablesMap.put(BpmService.PROCESS_DOCUMENT_KEY_ENTITY_ID_VAR, apartmentInspectionId);
        final String foundGroup = apartmentInspectionTaskService.getUserGroupByOksDevelopers(
            apartmentInspection.getDevelopers()
        );
        variablesMap.put(CANDIDATES_ORG_TASK_VAR_KEY, getDefaultUserOrg());
        variablesMap.put(DEVELOPERS_GROUP_TASK_VAR_KEY, foundGroup);

        return bpmService.startNewProcess(PROCESS_KEY_DEFECTS_ELIMINATION_START, variablesMap);
    }

    private String getDefaultUserOrg() {
        return apartmentInspectionProperties.getDefaultTechUserOrg();
    }

    private boolean isDocNotEmpty(final ApartmentInspectionDocument apartmentInspectionDocument) {
        return apartmentInspectionDocument != null
            && apartmentInspectionDocument.getDocument() != null
            && apartmentInspectionDocument.getDocument().getApartmentInspectionData() != null;
    }

    @Override
    public List<ApartmentInspectionDto> findAll(final String personId, final String commissionInspectionId) {
        final String nullablePersonId = ofNullable(personId)
            .map(String::trim)
            .filter(not(String::isEmpty))
            .orElse(null);

        final String nullableCommissionInspectionId = ofNullable(commissionInspectionId)
            .map(String::trim)
            .filter(not(String::isEmpty))
            .orElse(null);

        final List<ApartmentInspectionDocument> apartmentInspectionDocuments = apartmentInspectionDao
            .findAll(nullablePersonId, nullableCommissionInspectionId)
            .stream()
            .filter(Objects::nonNull)
            .map(this::parseDocumentData)
            .collect(Collectors.toList());

        return apartmentInspectionDocuments.stream()
            .map(ApartmentInspectionDocument::getDocument)
            .map(apartmentInspectionMapper::toApartmentInspectionDto)
            .collect(Collectors.toList());
    }

    @Override
    public List<ApartmentInspectionDocument> getApartmentInspections() {
        final List<DocumentData> documents = apartmentInspectionDao.findAll();
        return documents
            .stream()
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
    }

    private void deleteFromSolrById(final String id) {
        searchRemoteService.deleteDocumentsByTypeAndIds("UGD_SSR_APARTMENT-INSPECTION",
            Collections.singletonList(id));
    }

    @Override
    public List<ApartmentInspectionDocument> fetchByPersonId(final String personId) {
        return apartmentInspectionDao.fetchByPersonId(personId)
            .stream()
            .filter(Objects::nonNull)
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
    }

    @Override
    public void actualizeTasksCandidates(final String documentId) {
        final ApartmentInspectionDocument apartmentInspectionDocument =
            this.fetchDocument(documentId);
        actualizeTaskCandidates(apartmentInspectionDocument);
    }

    @Async
    @Override
    public void actualizeTaskCandidates() {
        log.info("Началась актуализация списка кандидатов активных задач по всем актам по дефектам");
        getApartmentInspections().forEach(apartmentInspectionDocument -> {
            try {
                actualizeTaskCandidates(apartmentInspectionDocument);
            } catch (Exception e) {
                log.error(
                    "Возникла ошибка при актуализации списка кандидатов активных задач apartmentInspectionId = {}: {}",
                    apartmentInspectionDocument.getId(),
                    e.getMessage(),
                    e
                );
            }
        });
        log.info("Завершилась актуализация списка кандидатов активных задач по всем актам по дефектам");
    }

    private void actualizeTaskCandidates(final ApartmentInspectionDocument apartmentInspectionDocument) {
        final List<Task> activeTasks = getActiveTasksToProcess(apartmentInspectionDocument);

        if (CollectionUtils.isEmpty(activeTasks)) {
            return;
        }

        documentDataService.reindexDocuments(SsrDocumentTypes.APARTMENT_INSPECTION,
            Collections.singletonList(apartmentInspectionDocument));

        final List<SsrCcoEmployee> activeEmployees = getActiveEmployees(apartmentInspectionDocument);
        final List<String> developerUsers = activeEmployees.stream()
            .filter(e -> SsrCcoOrganizationType.ofTypeCode(e.getType()) == SsrCcoOrganizationType.DEVELOPER)
            .map(SsrCcoEmployee::getLogin)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

        final Task contractorTask = getContractorTask(activeTasks);
        final String assignedGenContractorInn = getAssignedGenContractor(apartmentInspectionDocument)
            .map(OrganizationInformation::getExternalId)
            .orElse(null);
        if (contractorTask != null && assignedGenContractorInn != null) {
            actualizeContractorTask(contractorTask, assignedGenContractorInn);
        }

        if (!developerUsers.isEmpty()) {
            activeTasks.stream()
                .filter(this::isDeveloperTask)
                .forEach(task -> apartmentInspectionTaskService.actualizeTaskCandidates(task, developerUsers));
        } else {
            final List<OrganizationInformation> developers = apartmentInspectionDocument.getDocument()
                .getApartmentInspectionData()
                .getDevelopers();
            final String foundGroup = apartmentInspectionTaskService.getUserGroupByOksDevelopers(developers);
            activeTasks.stream()
                .filter(this::isDeveloperTask)
                .forEach(task -> apartmentInspectionTaskService.addTaskCandidateGroup(task.getId(), foundGroup));
        }

        apartmentInspectionTaskService.deleteAssigneesIfNeeded(activeTasks);
    }

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
        fetchByUnom(unom).forEach(apartmentInspectionDocument -> {
            try {
                actualizeTaskCandidates(apartmentInspectionDocument);
            } catch (Exception e) {
                log.error(
                    "Возникла ошибка при актуализации списка кандидатов активных задач apartmentInspectionId = {}: {}",
                    apartmentInspectionDocument.getId(),
                    e.getMessage(),
                    e
                );
            }
        });
    }

    private List<SsrCcoEmployee> getActiveEmployees(final ApartmentInspectionDocument apartmentInspectionDocument) {
        return ofNullable(apartmentInspectionDocument)
            .map(ApartmentInspectionDocument::getDocument)
            .map(ApartmentInspection::getApartmentInspectionData)
            .map(ApartmentInspectionType::getUnom)
            .map(apartmentInspectionTaskService::getActiveEmployees)
            .orElse(Collections.emptyList());
    }

    @Override
    public List<ApartmentInspectionDocument> fetchByUnomAndFlatNumber(String unom, String flatNumber) {
        return apartmentInspectionDao.fetchByUnomAndFlatNumber(unom, flatNumber)
            .stream()
            .filter(Objects::nonNull)
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
    }

    @Override
    public void defectsFixed(final String apartmentInspectionId) {
        final ApartmentInspectionDocument apartmentInspectionDocument = fetchDocument(apartmentInspectionId);
        defectsFixed(apartmentInspectionDocument);
    }

    private void defectsFixed(final ApartmentInspectionDocument apartmentInspectionDocument) {
        final ApartmentInspectionType apartmentInspection = apartmentInspectionDocument
            .getDocument()
            .getApartmentInspectionData();

        final String commissionInspectionId = apartmentInspection.getCommissionInspectionId();
        if (nonNull(commissionInspectionId)) {

            apartmentInspection.setDefectsEliminatedNotificationDate(LocalDateTime.now());
            updateDocument(apartmentInspectionDocument, "defectsFixed");

            commissionInspectionService.processDefectsFixed(commissionInspectionId);
        }
    }

    @Override
    public void close(final String apartmentInspectionId, final PersonConsentDto personConsentDto) {
        closeAct(apartmentInspectionId, personConsentDto, false);
    }

    @Override
    public void addPersonConsent(final String apartmentInspectionId, final PersonConsentDto personConsentDto) {
        closeAct(apartmentInspectionId, personConsentDto, true);
    }

    private void closeAct(
        final String apartmentInspectionId,
        final PersonConsentDto personConsentDto,
        final boolean shouldClosePersonConsentTask
    ) {
        final ApartmentInspectionDocument apartmentInspectionDocument = fetchDocument(apartmentInspectionId);

        final ApartmentInspectionType apartmentInspection = apartmentInspectionDocument
            .getDocument()
            .getApartmentInspectionData();

        apartmentInspection.setAcceptedDefectsDate(personConsentDto.getAcceptedDefectsDate());
        apartmentInspection.setAcceptedDefectsActFileId(personConsentDto.getAcceptedDefectsActFileId());
        apartmentInspection.setHasConsent(personConsentDto.isHasConsent());
        ofNullable(apartmentInspection.getAcceptedDefectsActFileId())
            .map(this::uploadActFileToChed)
            .ifPresent(apartmentInspection::setAcceptedDefectsActChedFileId);

        super.updateDocument(apartmentInspectionId, apartmentInspectionDocument, true, true, null);

        final String commissionInspectionId = apartmentInspection.getCommissionInspectionId();
        if (nonNull(commissionInspectionId)) {
            commissionInspectionService.processActClosure(commissionInspectionId, apartmentInspection);
        }

        final String processInstanceId = getActiveProcessInstanceId(apartmentInspectionDocument);

        if (shouldClosePersonConsentTask) {
            finishPersonConsentTask(processInstanceId, false);
        } else {
            startDefectsEliminationConfirmationTaskIfNeeded(processInstanceId);
        }

        sendDefectEliminationAgreement(apartmentInspectionDocument);
    }

    /**
     * Сформировать задачу "Утвердить информацию об устранении дефектов", если она не существовала ранее.
     * В случае устранения всех дефектов задача нужна для ознакомления пользователя с тем,
     * что было внесено согласие об устранении дефектов.
     *
     * @param processInstanceId ИД процесса
     */
    private void startDefectsEliminationConfirmationTaskIfNeeded(final String processInstanceId) {
        bpmService
            .getTasksByProcessId(PROCESS_KEY_DEFECTS_ELIMINATION_START, processInstanceId)
            .stream()
            .findFirst()
            .ifPresent(activeTask -> {
                if (StringUtils.equals(TASK_TIMER_DIFINITION_KEY, activeTask.getTaskDefinitionKey())) {
                    bpmService.completeTaskViaForm(activeTask.getId(), Collections.EMPTY_LIST);
                }
            });
    }

    private void finishPersonConsentTask(final String processInstanceId, final boolean isClosedWithoutConsent) {
        final List<FormProperty> props = new ArrayList<>();
        props.add(new FormProperty("IsClosedWithoutConsent", String.valueOf(isClosedWithoutConsent)));

        bpmService
            .getTasksByProcessId(PROCESS_KEY_DEFECTS_ELIMINATION_START, processInstanceId)
            .stream()
            .findFirst()
            .ifPresent(activeTask -> bpmService.completeTaskViaForm(activeTask.getId(), props));
    }

    private void finishClosingWithoutConsentTask(final String processInstanceId, final boolean isClosingConfirmed) {
        final List<FormProperty> props = new ArrayList<>();
        props.add(new FormProperty("IsClosingConfirmed", String.valueOf(isClosingConfirmed)));

        bpmService
            .getTasksByProcessId(PROCESS_KEY_DEFECTS_ELIMINATION_START, processInstanceId)
            .stream()
            .findFirst()
            .ifPresent(activeTask -> bpmService.completeTaskViaForm(activeTask.getId(), props));
    }

    @Override
    public ApartmentInspectionDocument createDocumentFromPrevious(final ApartmentInspectionType apartmentInspection) {
        final ApartmentInspectionDocument apartmentInspectionDocument = apartmentInspectionMapper
            .createDocumentFromPrevious(apartmentInspection);

        return createDocument(apartmentInspectionDocument);
    }

    @Override
    public ApartmentInspectionDocument createPendingApartmentInspectionDocument(
        final String commissionInspectionId,
        final CommissionInspectionData commissionInspection,
        final LocalDateTime confirmedDateTime
    ) {
        final ApartmentInspectionDocument apartmentInspectionDocument = apartmentInspectionMapper
            .createPendingApartmentInspectionDocument(commissionInspectionId, commissionInspection, confirmedDateTime);

        return createDocument(apartmentInspectionDocument);
    }

    @Override
    public void deleteAll() {
        fetchDocumentsPage(0, 1000)
            .forEach(document ->
                deleteDocument(document.getId(), true, null)
            );
    }

    @Override
    public ApartmentInspectionDto updateAcceptedDefectsAct(
        final String apartmentInspectionId, final AcceptedDefectsActDto acceptedDefectsActDto
    ) {
        final ApartmentInspectionDocument apartmentInspectionDocument = fetchDocument(apartmentInspectionId);

        final ApartmentInspectionType apartmentInspectionType = apartmentInspectionDocument
            .getDocument()
            .getApartmentInspectionData();

        if (nonNull(acceptedDefectsActDto.getAcceptedDefectsActFileId())) {
            apartmentInspectionType.setAcceptedDefectsActFileId(acceptedDefectsActDto.getAcceptedDefectsActFileId());
        }

        ofNullable(apartmentInspectionType.getAcceptedDefectsActFileId())
            .map(this::uploadActFileToChed)
            .ifPresent(apartmentInspectionType::setAcceptedDefectsActChedFileId);

        final ApartmentInspectionDocument updatedApartmentInspectionDocument =
            updateDocument(apartmentInspectionId, apartmentInspectionDocument, true, true, null);

        return apartmentInspectionMapper.toApartmentInspectionDto(updatedApartmentInspectionDocument.getDocument());
    }

    @Override
    public void closeWithoutConsent(
        final String apartmentInspectionId, final CloseActWithoutConsentDto closeActWithoutConsentDto
    ) {
        final ApartmentInspectionDocument apartmentInspectionDocument = fetchDocument(apartmentInspectionId);
        closeWithoutConsent(apartmentInspectionDocument, closeActWithoutConsentDto);
    }

    private void closeWithoutConsent(
        final ApartmentInspectionDocument apartmentInspectionDocument,
        final CloseActWithoutConsentDto closeActWithoutConsentDto
    ) {
        if (canCloseActWithoutConsent()) {
            closeWithoutConsent(
                apartmentInspectionDocument,
                closeActWithoutConsentDto.getActClosureReasonCode(),
                closeActWithoutConsentDto.getActClosureReasonComment()
            );
        } else {
            startClosingActConfirmationProcess(
                apartmentInspectionDocument,
                closeActWithoutConsentDto.getActClosureReasonCode(),
                closeActWithoutConsentDto.getActClosureReasonComment()
            );
        }
    }

    @Override
    public void closeWithoutConsent(final RestCloseActsWithoutConsentDto restCloseActsWithoutConsentDto) {
        ofNullable(restCloseActsWithoutConsentDto.getApartmentInspectionIds())
            .map(this::fetchDocuments)
            .orElse(Collections.emptyList())
            .forEach(apartmentInspection -> closeWithoutConsent(
                apartmentInspection, restCloseActsWithoutConsentDto.getReason()
            ));
    }

    private void closeWithoutConsent(
        final ApartmentInspectionDocument apartmentInspectionDocument,
        final String actClosureReasonCode,
        final String actClosureReasonComment
    ) {
        final ApartmentInspectionType apartmentInspection = apartmentInspectionDocument
            .getDocument()
            .getApartmentInspectionData();

        apartmentInspection.setAcceptedDefectsDate(LocalDateTime.now());

        if (nonNull(actClosureReasonCode)) {
            apartmentInspection.setActClosureReasonCode(actClosureReasonCode);
            apartmentInspection.setActClosureReasonComment(actClosureReasonComment);
        } else if (nonNull(apartmentInspection.getPendingActClosureReasonCode())) {
            apartmentInspection.setActClosureReasonCode(apartmentInspection.getPendingActClosureReasonCode());
            apartmentInspection.setActClosureReasonComment(apartmentInspection.getPendingActClosureReasonComment());
        }

        updateDocument(apartmentInspectionDocument);

        final String commissionInspectionId = apartmentInspection.getCommissionInspectionId();
        if (nonNull(commissionInspectionId)) {
            commissionInspectionService.processActClosureWithoutConsent(
                commissionInspectionId,
                apartmentInspection.getActClosureReasonCode(),
                apartmentInspection.getActClosureReasonComment()
            );
        }

        forceFinishProcess(apartmentInspectionDocument);

        sendDefectEliminationAgreement(apartmentInspectionDocument);
    }

    @Override
    public void confirmClosingWithoutConsent(
        final String apartmentInspectionId,
        final RestConfirmClosingWithoutConsentDto restConfirmClosingWithoutConsentDto
    ) {
        final ApartmentInspectionDocument apartmentInspectionDocument = fetchDocument(apartmentInspectionId);

        if (restConfirmClosingWithoutConsentDto.isConfirmed()) {
            closeWithoutConsent(apartmentInspectionDocument, null, null);
        } else {
            apartmentInspectionDocument.getDocument().getApartmentInspectionData().setClosingInitiatorLogin(null);
            updateDocument(apartmentInspectionDocument);
            final String processInstanceId = getActiveProcessInstanceId(apartmentInspectionDocument);
            finishClosingWithoutConsentTask(processInstanceId, false);
        }
    }

    private boolean canCloseActWithoutConsent() {
        return ofNullable(apartmentInspectionProperties.getClosingActConfirmationGroup())
            .map(userService::checkUserGroup)
            .orElse(false);
    }

    private void startClosingActConfirmationProcess(
        final ApartmentInspectionDocument apartmentInspectionDocument,
        final String actClosureReasonCode,
        final String actClosureReasonComment
    ) {
        final ApartmentInspectionType apartmentInspection = apartmentInspectionDocument
            .getDocument()
            .getApartmentInspectionData();

        if (nonNull(actClosureReasonCode)) {
            apartmentInspection.setPendingActClosureReasonCode(actClosureReasonCode);
            apartmentInspection.setPendingActClosureReasonComment(actClosureReasonComment);
            apartmentInspection.setClosingInitiatorLogin(userService.getCurrentUserLogin());
            updateDocument(apartmentInspectionDocument);
        }

        final String processInstanceId = getActiveProcessInstanceId(apartmentInspectionDocument);
        finishPersonConsentTask(processInstanceId, true);
    }

    @Override
    public void defectsProlongation(
        final String apartmentInspectionId, final DelayReasonDto delayReasonDto
    ) {
        final ApartmentInspectionDocument apartmentInspectionDocument = fetchDocument(apartmentInspectionId);
        defectsProlongation(apartmentInspectionDocument, delayReasonDto);
    }

    private void defectsProlongation(
        final ApartmentInspectionDocument apartmentInspectionDocument, final DelayReasonDto delayReasonDto
    ) {
        final ApartmentInspectionType apartmentInspection = apartmentInspectionDocument
            .getDocument()
            .getApartmentInspectionData();

        final LocalDate lastDelayDate = ApartmentInspectionUtils.getLastDelayDate(apartmentInspection)
            .orElse(null);

        if (lastDelayDate != null && lastDelayDate.equals(delayReasonDto.getDelayDate())) {
            return;
        }

        apartmentInspection
            .getDelayReason()
            .add(apartmentInspectionMapper.toDelayReasonData(delayReasonDto));

        updateDocument(apartmentInspectionDocument);

        final String commissionInspectionId = apartmentInspection.getCommissionInspectionId();
        if (nonNull(commissionInspectionId)) {
            commissionInspectionService.defectsProlongation(commissionInspectionId);
        }

        sendDefectEliminationTransfer(apartmentInspectionDocument);
    }

    @Override
    public void defectsElimination(
        final String apartmentInspectionId, final DelayReasonDto delayReasonDto
    ) {
        final ApartmentInspectionDocument apartmentInspectionDocument = fetchDocument(apartmentInspectionId);
        defectsElimination(apartmentInspectionDocument, delayReasonDto);
    }

    private void defectsElimination(
        final ApartmentInspectionDocument apartmentInspectionDocument, final DelayReasonDto delayReasonDto
    ) {
        if (delayReasonDto.isCreatedByGeneralContractor()) {
            requestConfirmation(apartmentInspectionDocument, delayReasonDto);
        } else {
            final ApartmentInspectionType apartmentInspection = apartmentInspectionDocument
                .getDocument()
                .getApartmentInspectionData();

            if (!CollectionUtils.isEmpty(delayReasonDto.getDefects())) {
                delayReasonDto.getDefects()
                    .forEach(defect -> setDefectElimination(defect, apartmentInspection.getApartmentDefects()));
            }

            defectsProlongation(apartmentInspectionDocument, delayReasonDto);

            completeTask(apartmentInspectionDocument, delayReasonDto);

            if (delayReasonDto.isAreDefectsEliminated()) {
                if (isNull(apartmentInspection.getCommissionInspectionId())) {
                    final String personId = apartmentInspection.getPersonID();
                    final String cipId = apartmentInspection.getCipId();

                    elkUserNotificationService.sendNotificationFixDefects(
                        personId, cipId, apartmentInspectionDocument.getId()
                    );
                }

                defectsFixed(apartmentInspectionDocument);
            }
        }
    }

    @Override
    public void defectsElimination(final RestDefectsEliminationDto restDefectsEliminationDto) {
        if (nonNull(restDefectsEliminationDto) && nonNull(restDefectsEliminationDto.getDelayDate())) {
            ofNullable(restDefectsEliminationDto.getApartmentInspectionIds())
                .map(this::fetchDocuments)
                .orElse(Collections.emptyList())
                .forEach(apartmentInspection -> defectsElimination(
                    apartmentInspection,
                    DelayReasonDto.builder()
                        .delayDate(restDefectsEliminationDto.getDelayDate())
                        .build()
                ));
        }
    }

    private void requestConfirmation(
        final ApartmentInspectionDocument apartmentInspectionDocument, final DelayReasonDto delayReasonDto
    ) {
        final List<DefectDto> defectDtos = delayReasonDto.getDefects();
        final LocalDate delayDate = delayReasonDto.getDelayDate();
        final LocalDate lastDelayDate = ApartmentInspectionUtils.getLastDelayDate(apartmentInspectionDocument)
            .orElse(null);

        if (CollectionUtils.isEmpty(defectDtos) && (isNull(delayDate) || Objects.equals(lastDelayDate, delayDate))) {
            log.info(
                "Skip request confirmation: apartmentInspectionDocumentId = {}", apartmentInspectionDocument.getId()
            );
            return;
        }

        final ApartmentInspectionType apartmentInspectionType = apartmentInspectionDocument.getDocument()
            .getApartmentInspectionData();
        final List<FullFlatResponse> fullFlatResponses = ofNullable(apartmentInspectionType.getUnom())
            .map(capitalConstructionObjectService::getFullCcoChessInfo)
            .map(FullOksHousesResponse::getFlats)
            .orElse(Collections.emptyList());
        final DefectFlatDto defectFlatDto = retrieveFullFlatResponses(
            fullFlatResponses, apartmentInspectionType.getFlat()
        );
        final String affairId = retrieveAffairId(apartmentInspectionDocument);
        final List<DefectConfirmationRequestDto> defectConfirmationRequestDtos = apartmentInspectionType
            .getApartmentDefects()
            .stream()
            .map(ApartmentDefects::getApartmentDefectData)
            .filter(defect -> !Objects.equals(lastDelayDate, delayDate)
                || existsDefect(defect, defectDtos))
            .map(defect -> {
                final DefectDto defectDto = retrieveDefectDto(defect, defectDtos);
                return DefectConfirmationRequestDto.builder()
                    .id(defect.getId())
                    .apartmentInspectionId(apartmentInspectionDocument.getId())
                    .flatElement(defect.getFlatElement())
                    .description(defect.getDescription())
                    .flatData(defectFlatDto)
                    .eliminated(retrieveEliminationMark(defect, defectDto))
                    .oldEliminationDate(retrieveOldEliminationDate(defect, lastDelayDate, delayDate))
                    .eliminationDateComment(delayReasonDto.getDelayReason())
                    .eliminationDate(delayDate)
                    .affairId(affairId)
                    .build();
            })
            .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(defectConfirmationRequestDtos)) {
            log.info(
                "Skip request confirmation: defects haven't been changed, apartmentInspectionDocumentId = {}",
                apartmentInspectionDocument.getId()
            );
            return;
        }

        log.info("Start request confirmation: apartmentInspectionDocumentId = {}", apartmentInspectionDocument.getId());
        final ApartmentDefectConfirmationDocument apartmentDefectConfirmationDocument =
            apartmentDefectConfirmationService.createApartmentDefectConfirmation(
                apartmentInspectionDocument, defectConfirmationRequestDtos
            );

        final List<String> defectIds = ofNullable(apartmentDefectConfirmationDocument)
            .map(ApartmentDefectConfirmationDocument::getDocument)
            .map(ApartmentDefectConfirmation::getApartmentDefectConfirmationData)
            .map(ApartmentDefectConfirmationData::getDefects)
            .map(ApartmentDefectConfirmationData.Defects::getDefect)
            .map(List::stream)
            .orElse(Stream.empty())
            .map(DefectData::getId)
            .collect(Collectors.toList());

        if (!defectIds.isEmpty()) {
            addBlockingInformation(apartmentInspectionDocument, defectIds, true);
        }

        apartmentDefectConfirmationService.startConfirmationProcess(apartmentDefectConfirmationDocument);
        log.info(
            "Finish request confirmation: apartmentInspectionDocumentId = {}", apartmentInspectionDocument.getId()
        );
    }

    private LocalDate retrieveOldEliminationDate(
        final ApartmentEliminationDefectType defect,
        final LocalDate lastDelayDate,
        final LocalDate delayDate
    ) {
        final LocalDate currentEliminationDate = ofNullable(defect.getEliminationData())
            .map(ApartmentDefectElimination::getEliminationDate)
            .orElse(lastDelayDate);
        return Objects.equals(currentEliminationDate, delayDate) ? null : currentEliminationDate;
    }

    private boolean retrieveEliminationMark(
        final ApartmentEliminationDefectType apartmentEliminationDefectType, final DefectDto defectDto
    ) {
        return ofNullable(defectDto)
            .map(DefectDto::isEliminated)
            .orElse(apartmentEliminationDefectType.isIsEliminated());
    }

    private DefectDto retrieveDefectDto(
        final ApartmentEliminationDefectType defect, final List<DefectDto> defectDtos
    ) {
        return defectDtos.stream()
            .filter(d -> Objects.equals(d.getId(), defect.getId()))
            .findFirst()
            .orElse(null);
    }

    private boolean existsDefect(final ApartmentEliminationDefectType defect, final List<DefectDto> defectDtos) {
        return defectDtos.stream()
            .anyMatch(d -> Objects.equals(d.getId(), defect.getId()));
    }

    private void setDefectElimination(final DefectDto defect, final List<ApartmentDefects> defects) {
        defects.stream()
            .map(ApartmentDefects::getApartmentDefectData)
            .filter(d -> Objects.equals(d.getId(), defect.getId()))
            .forEach(d -> d.setIsEliminated(defect.isEliminated()));
    }

    @Override
    public void sendTasksToGeneralContractor(
        final RestSendTasksToGeneralContractorDto restSendTasksToGeneralContractorDto
    ) {
        if (nonNull(restSendTasksToGeneralContractorDto)
            && nonNull(restSendTasksToGeneralContractorDto.getGeneralContractorId())) {
            ofNullable(restSendTasksToGeneralContractorDto.getApartmentInspectionIds())
                .map(this::fetchDocuments)
                .orElse(Collections.emptyList())
                .forEach(apartmentInspection -> sendTaskToGeneralContractor(
                    apartmentInspection,
                    restSendTasksToGeneralContractorDto.getGeneralContractorId()
                ));
        }
    }

    @Override
    public void sendTaskToGeneralContractor(final String apartmentInspectionId, final String generalContractorId) {
        if (nonNull(generalContractorId)) {
            ofNullable(apartmentInspectionId)
                .map(this::fetchDocument)
                .ifPresent(apartmentInspection -> sendTaskToGeneralContractor(
                    apartmentInspection,
                    generalContractorId
                ));
        }
    }

    private void sendTaskToGeneralContractor(
        final ApartmentInspectionDocument apartmentInspectionDocument, final String generalContractorId
    ) {
        final ApartmentInspectionType apartmentInspection = apartmentInspectionDocument
            .getDocument()
            .getApartmentInspectionData();

        apartmentInspection.getGeneralContractors()
            .stream()
            .filter(contractor -> generalContractorId.equals(contractor.getExternalId()))
            .findFirst()
            .ifPresent(contractor -> {
                apartmentInspection.getGeneralContractors().forEach(c -> c.setIsAssigned(false));
                contractor.setIsAssigned(true);
            });

        updateDocument(apartmentInspectionDocument, "sendTaskToGeneralContractor");

        actualizeTaskCandidates(apartmentInspectionDocument);
    }

    private void completeTask(
        final ApartmentInspectionDocument apartmentInspectionDocument, final DelayReasonDto delayReasonDto
    ) {
        final String processInstanceId = getActiveProcessInstanceId(apartmentInspectionDocument);

        final long daysBetween = DAYS.between(LocalDate.now(), delayReasonDto.getDelayDate()) - 1;
        final String daysToNotificationForActivity = (daysBetween < 0) ? "P0D" : "P" + daysBetween + "D";

        final List<FormProperty> props = new ArrayList<>();
        props.add(new FormProperty("AreDefectsEliminated", String.valueOf(delayReasonDto.isAreDefectsEliminated())));
        props.add(new FormProperty("NotificationPeriodVar", daysToNotificationForActivity));

        bpmService
            .getTasksByProcessId(PROCESS_KEY_DEFECTS_ELIMINATION_START, processInstanceId)
            .stream()
            .findFirst()
            .ifPresent(activeTask -> bpmService.completeTaskViaForm(activeTask.getId(), props));
    }

    @Override
    public List<ApartmentInspectionDocument> fetchByUnom(String unom) {
        return apartmentInspectionDao.fetchByUnom(unom)
            .stream()
            .filter(Objects::nonNull)
            .map(this::parseDocumentData)
            .collect(Collectors.toList());

    }

    /**
     * Получить ApartmentInspection по unom и flatNumber и personId.
     *
     * @param unom       уном
     * @param flatNumber номер квартиры
     * @param personId   personId
     * @return список ApartmentInspection
     */
    public List<ApartmentInspectionDocument> fetchByUnomAndFlatNumberAndPersonId(
        final String unom, final String flatNumber, final String personId
    ) {
        return apartmentInspectionDao.fetchByUnomAndFlatNumberAndPersonId(unom, flatNumber, personId)
            .stream()
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
    }

    private List<Task> getActiveTasksToProcess(final ApartmentInspectionDocument apartmentInspectionDocument) {
        final String activeProcessId = getActiveProcessInstanceId(apartmentInspectionDocument);
        return bpmService.getTasksByProcessId(PROCESS_KEY_DEFECTS_ELIMINATION_START, activeProcessId);
    }

    private Task getDeveloperTask(final List<Task> tasks) {
        return tasks
            .stream()
            .filter(this::isDeveloperTask)
            .findFirst()
            .orElse(null);
    }

    private Task getContractorTask(final List<Task> tasks) {
        return tasks
            .stream()
            .filter(not(this::isDeveloperTask))
            .findFirst()
            .orElse(null);
    }

    private void actualizeContractorTask(final Task task, final String orgInn) {
        ListUtils
            .emptyIfNull(userService.getAllOrganization())
            .stream()
            .filter(organizationBean -> StringUtils.equals(organizationBean.getOrgINN(), orgInn))
            .map(OrganizationBean::getDescription)
            .findFirst()
            .ifPresent(description -> assignContractorToTask(task, description));
    }

    private void assignContractorToTask(final Task task, final String organization) {
        assignUsersToTask(task, CONTRACTOR_GROUP_FILTER, organization);
    }

    private void assignUsersToTask(final Task task, final String existingGroup, final String existingOrg) {
        final List<String> actualUsers = ListUtils
            .emptyIfNull(userService.findUsersByLdapGroupAndDepartment(existingGroup, existingOrg))
            .stream()
            .map(UserBean::getAccountName)
            .collect(Collectors.toList());

        apartmentInspectionTaskService.assignUsersToTask(task, actualUsers);
    }

    private boolean isDeveloperTask(final Task task) {
        final String groupTaskVariableValue = ListUtils.emptyIfNull(task.getVariables())
            .stream()
            .filter(variable -> StringUtils.equals(variable.getName(), "GroupTaskLocalVar"))
            .findFirst()
            .map(Variable::getValue)
            .filter(varValue -> varValue instanceof String)
            .map(String.class::cast)
            .orElse(null);
        return StringUtils.equals(groupTaskVariableValue, getDefaultUserOrg());
    }

    private Optional<OrganizationInformation> getAssignedGenContractor(
        final ApartmentInspectionDocument apartmentInspectionDocument) {
        final List<OrganizationInformation> getContractors = apartmentInspectionDocument
            .getDocument()
            .getApartmentInspectionData()
            .getGeneralContractors();
        if (CollectionUtils.isEmpty(getContractors)) {
            return Optional.empty();
        }
        return getContractors
            .stream()
            .filter(OrganizationInformation::isIsAssigned)
            .findFirst();
    }

    private String getActiveProcessInstanceId(final ApartmentInspectionDocument apartmentInspectionDocument) {
        return ofNullable(apartmentInspectionDocument)
            .map(ApartmentInspectionDocument::getDocument)
            .map(ApartmentInspection::getApartmentInspectionData)
            .map(ApartmentInspectionType::getProcessInstanceId)
            .orElse(null);
    }

    private String retrieveCipId(final ApartmentInspectionDocument document) {
        final String unom = ofNullable(document)
            .map(ApartmentInspectionDocument::getDocument)
            .map(ApartmentInspection::getApartmentInspectionData)
            .map(ApartmentInspectionType::getUnom)
            .orElse(null);

        final List<String> cipIds = ofNullable(unom)
            .map(capitalConstructionObjectService::getCcoIdByUnom)
            .map(cipService::fetchByCcoId)
            .map(List::stream)
            .orElse(Stream.empty())
            .map(CipDocument::getDocument)
            .map(Cip::getCipData)
            .map(CipType::getCipID)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

        final String personId = ofNullable(document)
            .map(ApartmentInspectionDocument::getDocument)
            .map(ApartmentInspection::getApartmentInspectionData)
            .map(ApartmentInspectionType::getPersonID)
            .orElse(null);

        return retrieveCipId(cipIds, personId);
    }

    private String retrieveCipId(final List<String> cipIds, final String personId) {
        if (cipIds.size() == 1) {
            return cipIds.get(0);
        }

        final List<PersonType.OfferLetters.OfferLetter> offerLetters = personDocumentService.fetchById(personId)
            .map(PersonDocument::getDocument)
            .map(Person::getPersonData)
            .map(PersonType::getOfferLetters)
            .map(PersonType.OfferLetters::getOfferLetter)
            .orElse(null);

        if (offerLetters == null && cipIds.isEmpty()) {
            return null;
        }

        final String cipIdFromLastOfferLetter = ofNullable(offerLetters)
            .map(PersonUtils::getLastOfferLetter)
            .map(PersonType.OfferLetters.OfferLetter::getIdCIP)
            .filter(cipIds::contains)
            .orElse(null);

        if (cipIdFromLastOfferLetter != null) {
            return cipIdFromLastOfferLetter;
        }

        final PersonType.OfferLetters.OfferLetter firstLetterWithCipId =
            ofNullable(offerLetters)
                .map(List::stream)
                .orElse(Stream.empty())
                .filter(letter -> letter.getIdCIP() != null)
                .findFirst()
                .orElse(null);

        if (firstLetterWithCipId != null) {
            return firstLetterWithCipId.getIdCIP();
        }

        return cipIds.isEmpty() ? null : cipIds.get(0);
    }

    private String uploadActFileToChed(final String fileStoreFileId) {
        try {
            return chedFileService.uploadFileToChed(fileStoreFileId, CHED_DOCUMENT_CODE, CHED_DOCUMENT_CLASS);
        } catch (Exception e) {
            log.warn(
                "Unable to upload filestore file {} to ched: documentCode {}, documentClass {}",
                fileStoreFileId, CHED_DOCUMENT_CODE, CHED_DOCUMENT_CLASS, e
            );

            return null;
        }
    }

    private void sendDefectEliminationAct(final ApartmentInspectionDocument apartmentInspectionDocument) {
        try {
            final ApartmentInspectionType apartmentInspectionData = apartmentInspectionDocument
                .getDocument()
                .getApartmentInspectionData();
            if (shouldSendFlow(apartmentInspectionData)) {
                final PersonDocument personDocument = personDocumentService
                    .fetchDocument(apartmentInspectionData.getPersonID());

                final SuperServiceDGPDefectEliminationActType defectEliminationAct = apartmentInspectionMapper
                    .toSuperServiceDgpDefectEliminationActType(apartmentInspectionDocument, personDocument);

                defectEliminationFlowsService.sendDefectEliminationAct(
                    apartmentInspectionDocument, defectEliminationAct
                );
            }
        } catch (Exception e) {
            log.error(
                "Unable to send defect elimination act for apartmentInspectionId = {}: {}",
                apartmentInspectionDocument.getId(),
                e.getMessage(),
                e
            );
        }
    }

    private void sendDefectEliminationTransfer(final ApartmentInspectionDocument apartmentInspectionDocument) {
        try {
            final ApartmentInspectionType apartmentInspectionData = apartmentInspectionDocument
                .getDocument()
                .getApartmentInspectionData();
            if (shouldSendFlow(apartmentInspectionData)) {
                final PersonDocument personDocument = personDocumentService
                    .fetchDocument(apartmentInspectionData.getPersonID());

                final SuperServiceDGPDefectEliminationTransferType defectEliminationTransfer = apartmentInspectionMapper
                    .toSuperServiceDgpDefectEliminationTransferType(apartmentInspectionDocument, personDocument);

                defectEliminationFlowsService.sendDefectEliminationTransfer(
                    apartmentInspectionDocument, defectEliminationTransfer
                );
            }
        } catch (Exception e) {
            log.error(
                "Unable to send defect elimination transfer for apartmentInspectionId = {}: {}",
                apartmentInspectionDocument.getId(),
                e.getMessage(),
                e
            );
        }
    }

    private void sendDefectEliminationAgreement(final ApartmentInspectionDocument apartmentInspectionDocument) {
        try {
            final ApartmentInspectionType apartmentInspectionData = apartmentInspectionDocument
                .getDocument()
                .getApartmentInspectionData();
            if (shouldSendFlow(apartmentInspectionData)) {
                final PersonDocument personDocument = personDocumentService
                    .fetchDocument(apartmentInspectionData.getPersonID());

                final SuperServiceDGPDefectEliminationAgreementType defectEliminationAgreement =
                    apartmentInspectionMapper.toSuperServiceDgpDefectEliminationAgreementType(
                        apartmentInspectionDocument, personDocument
                    );

                defectEliminationFlowsService.sendDefectEliminationAgreement(
                    apartmentInspectionDocument, defectEliminationAgreement
                );
            }
        } catch (Exception e) {
            log.error(
                "Unable to send defect elimination agreement for apartmentInspectionId = {}: {}",
                apartmentInspectionDocument.getId(),
                e.getMessage(),
                e
            );
        }
    }

    private boolean shouldSendFlow(final ApartmentInspectionType apartmentInspectionData) {
        return defectEliminationFlowsEnabled && apartmentInspectionData.isUseDefectEliminationFlows();
    }

    @Override
    public List<IntegrationLogDto> fetchIntegrationLogs(final String apartmentInspectionId) {
        final ApartmentInspectionDocument apartmentInspectionDocument = fetchDocument(apartmentInspectionId);
        return apartmentInspectionMapper.toIntegrationLogDtos(apartmentInspectionDocument);
    }

    @Override
    public ApartmentInspectionDto updateDefects(
        final String apartmentInspectionId, final RestUpdateDefectDto restUpdateDefectDto
    ) {
        final ApartmentInspectionDocument apartmentInspectionDocument = fetchDocument(apartmentInspectionId);
        if (nonNull(restUpdateDefectDto)) {
            final ApartmentInspectionType apartmentInspectionData = apartmentInspectionDocument.getDocument()
                .getApartmentInspectionData();

            final List<ApartmentEliminationDefectType> apartmentDefects = apartmentInspectionData.getApartmentDefects()
                .stream()
                .map(ApartmentDefects::getApartmentDefectData)
                .collect(Collectors.toList());

            final List<ApartmentEliminationDefectType> excludedApartmentDefects =
                apartmentInspectionData.getExcludedApartmentDefects()
                    .stream()
                    .map(ApartmentInspectionType.ExcludedApartmentDefects::getApartmentDefectData)
                    .collect(Collectors.toList());

            final Map<String, ApartmentEliminationDefectType> allApartmentDefects = Stream.concat(
                    apartmentDefects.stream(), excludedApartmentDefects.stream()
                )
                .collect(Collectors.toMap(
                    ApartmentEliminationDefectType::getId,
                    Function.identity(),
                    (d1, d2) -> d1
                ));

            final List<ApartmentInspectionType.ApartmentDefects> actualApartmentDefects =
                apartmentInspectionMapper.toApartmentDefects(restUpdateDefectDto.getDefects(), allApartmentDefects);
            final List<ApartmentInspectionType.ExcludedApartmentDefects> actualExcludedApartmentDefects =
                apartmentInspectionMapper.toExcludedApartmentDefects(
                    restUpdateDefectDto.getExcludedDefects(), allApartmentDefects
                );

            apartmentInspectionData.getApartmentDefects().clear();
            apartmentInspectionData.getApartmentDefects().addAll(actualApartmentDefects);

            apartmentInspectionData.getExcludedApartmentDefects().clear();
            apartmentInspectionData.getExcludedApartmentDefects().addAll(actualExcludedApartmentDefects);

            actualExcludedApartmentDefects.stream()
                .map(ApartmentInspectionType.ExcludedApartmentDefects::getApartmentDefectData)
                .filter(ApartmentEliminationDefectType::isIsBlocked)
                .findFirst()
                .ifPresent(apartmentDefectData -> {
                    throw new SsrException(
                        String.format(
                            "Существуют несогласованные изменения по дефекту \"%s\" для элемента квартиры \"%s\"",
                            apartmentDefectData.getDescription(),
                            apartmentDefectData.getFlatElement()
                        )
                    );
                });

            apartmentInspectionData.setDefectExclusionReason(restUpdateDefectDto.getDefectExclusionReason());

            updateDocument(apartmentInspectionDocument, "updateDefects");
        }
        return apartmentInspectionMapper.toApartmentInspectionDto(apartmentInspectionDocument.getDocument());
    }

    @Async
    @Override
    public void actualizeDefectIds() {
        log.info("Start actualize defect ids.");
        getApartmentInspections().forEach(apartmentInspectionDocument -> {
            try {
                actualizeDefectIds(apartmentInspectionDocument);
            } catch (Exception e) {
                log.error(
                    "Unable to actualize defect ids for apartmentInspectionId = {}: {}",
                    apartmentInspectionDocument.getId(),
                    e.getMessage(),
                    e
                );
            }
        });
        log.info("Finish actualize defect ids.");
    }

    private void actualizeDefectIds(final ApartmentInspectionDocument apartmentInspectionDocument) {
        final ApartmentInspectionType apartmentInspection = apartmentInspectionDocument.getDocument()
            .getApartmentInspectionData();
        apartmentInspection.getApartmentDefects().forEach(apartmentDefect -> {
            final ApartmentEliminationDefectType apartmentDefectData = apartmentDefect.getApartmentDefectData();
            if (nonNull(apartmentDefectData) && isNull(apartmentDefectData.getId())) {
                apartmentDefectData.setId(UUID.randomUUID().toString());
            }
        });
        updateDocument(apartmentInspectionDocument, "actualizeDefectIds");
        log.info("Finish actualize defect ids for apartmentInspectionId = {}", apartmentInspectionDocument.getId());
    }

    @Override
    public Page<RestDefectDto> fetchDefects(
        final int pageNum,
        final int pageSize,
        final String unom,
        final String flat,
        final String flatElement,
        final String description,
        final Boolean isEliminated,
        final boolean isDeveloper,
        final String sort
    ) {
        final List<FullFlatResponse> fullFlatResponses = ofNullable(
            capitalConstructionObjectService.getFullCcoChessInfo(unom)
        )
            .map(FullOksHousesResponse::getFlats)
            .orElse(Collections.emptyList());

        final Page<ApartmentDefectProjection> apartmentDefectProjections = apartmentInspectionDao.fetchDefects(
            unom, flat, flatElement, description, isEliminated, isDeveloper,
            PageRequest.of(pageNum, pageSize, PageUtils.retrieveSort(sort))
        );
        return apartmentDefectProjections.map(apartmentDefectProjection -> {
            final DefectFlatDto defectFlatDto = retrieveFullFlatResponses(
                fullFlatResponses, apartmentDefectProjection.getFlat()
            );
            return apartmentInspectionMapper.toRestDefectDto(apartmentDefectProjection, defectFlatDto);
        });
    }

    private List<RestDefectDto> fetchDefects(
        final String unom,
        final String flat,
        final String actNum,
        final LocalDate filingDate,
        final String flatElement,
        final String description,
        final Boolean isEliminated,
        final boolean skipNotPlannedElimination
    ) {
        final List<FullFlatResponse> fullFlatResponses = ofNullable(
            capitalConstructionObjectService.getFullCcoChessInfo(unom)
        )
            .map(FullOksHousesResponse::getFlats)
            .orElse(Collections.emptyList());

        final List<ApartmentDefectProjection> apartmentDefectProjections = apartmentInspectionDao.fetchDefects(
            unom, flat, actNum, filingDate, flatElement, description, isEliminated, skipNotPlannedElimination
        );
        return apartmentDefectProjections.stream()
            .map(apartmentDefectProjection -> {
                final DefectFlatDto defectFlatDto = retrieveFullFlatResponses(
                    fullFlatResponses, apartmentDefectProjection.getFlat()
                );
                return apartmentInspectionMapper.toRestDefectDto(apartmentDefectProjection, defectFlatDto);
            })
            .collect(Collectors.toList());
    }

    private DefectFlatDto retrieveFullFlatResponses(
        final List<FullFlatResponse> fullFlatResponses, final String flatNumber
    ) {
        if (nonNull(flatNumber)) {
            return fullFlatResponses.stream()
                .filter(fullFlatResponse -> nonNull(fullFlatResponse)
                    && Objects.equals(fullFlatResponse.getFlatNumber(), flatNumber))
                .findFirst()
                .map(fullFlatResponse -> DefectFlatDto.builder()
                    .flat(fullFlatResponse.getFlatNumber())
                    .floor(fullFlatResponse.getFloor())
                    .entrance(fullFlatResponse.getEntrance())
                    .build())
                .orElseGet(() -> DefectFlatDto.builder()
                    .flat(flatNumber)
                    .build());
        } else {
            return null;
        }
    }

    @Override
    public ApartmentInspectionDto fetchApartmentInspectionById(final String apartmentInspectionId) {
        final ApartmentInspectionDocument apartmentInspectionDocument = fetchDocument(apartmentInspectionId);
        return apartmentInspectionMapper.toApartmentInspectionDto(apartmentInspectionDocument.getDocument());
    }

    @Override
    public List<RestApartmentInspectionDto> fetchAllByUnomAndFlatNumber(final String unom, final String flatNumber) {
        return apartmentInspectionDao.fetchByUnomAndFlatNumber(unom, flatNumber)
            .stream()
            .filter(Objects::nonNull)
            .map(this::parseDocumentData)
            .map(ApartmentInspectionDocument::getDocument)
            .map(ApartmentInspection::getApartmentInspectionData)
            .map(apartmentInspection -> apartmentInspectionMapper.toRestApartmentInspection(
                apartmentInspection, retrievePerson(apartmentInspection.getPersonID())
            ))
            .collect(Collectors.toList());
    }

    private Person retrievePerson(final String personDocumentId) {
        return personDocumentService.fetchById(personDocumentId)
            .map(PersonDocument::getDocument)
            .orElse(null);
    }

    @Override
    public RestFileDto getZipReport(
        final String unom,
        final String flat,
        final String actNum,
        final LocalDate filingDate
    ) {
        if (isNull(unom)) {
            throw new SsrException("Не указан уном");
        }
        final List<String> signedActFileIds =
            fetchActiveByUnomAndFlatAndActNumAndFillingDate(unom, flat, actNum, filingDate).stream()
                .map(ApartmentInspectionDocument::getDocument)
                .map(ApartmentInspection::getApartmentInspectionData)
                .map(ApartmentInspectionType::getSignedActFileId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        try {
            final byte[] content = retrieveZipWithActs(signedActFileIds);
            return RestFileDto.builder()
                .fileName("acts_" + unom + ".zip")
                .content(content)
                .build();
        } catch (IOException e) {
            throw new SsrException("Не удалось объединить все акты в zip");
        }
    }

    public List<ApartmentInspectionDocument> fetchActiveByUnomAndFlatAndActNumAndFillingDate(
        final String unom, final String flat, final String actNum, final LocalDate filingDate
    ) {
        return apartmentInspectionDao.fetchActiveByUnomAndFlatAndActNumAndFillingDate(unom, flat, actNum, filingDate)
            .stream()
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
    }

    private byte[] retrieveZipWithActs(final List<String> signedActFileIds) throws IOException {
        try (final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             final ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream)
        ) {
            signedActFileIds.forEach(signedActFileId -> retrieveZipEntry(zipOutputStream, signedActFileId));
            zipOutputStream.close();
            return byteArrayOutputStream.toByteArray();
        }
    }

    private void retrieveZipEntry(final ZipOutputStream zipOutputStream, final String signedActFileId) {
        try {
            final String fileName = ssrFilestoreService.getFileInfo(signedActFileId).getFileName();
            final String changedFileName = fileName.substring(0, fileName.lastIndexOf("."))
                + "_" + UUID.randomUUID()
                + "." + fileName.substring(fileName.lastIndexOf(".") + 1);
            final ZipEntry zipEntry = new ZipEntry(changedFileName);
            zipOutputStream.putNextEntry(zipEntry);
            zipOutputStream.write(ssrFilestoreService.getFile(signedActFileId));
            zipOutputStream.closeEntry();
        } catch (IOException e) {
            throw new SsrException("Не удалось добавить файл акта в zip: fileStoreId = " + signedActFileId);
        }
    }

    @Override
    public RestFileDto getExcelReport(
        final String unom,
        final String flat,
        final String actNum,
        final LocalDate filingDate,
        final String flatElement,
        final String description,
        final Boolean isEliminated,
        final boolean skipNotPlannedElimination
    ) {
        if (isNull(unom)) {
            throw new SsrException("Не указан уном");
        }
        final List<RestDefectDto> restDefectDtos = fetchDefects(
            unom, flat, actNum, filingDate, flatElement, description, isEliminated, skipNotPlannedElimination
        );

        final Map<String, String> generalValues = retrieveGeneralValues(
            unom, flat, actNum, filingDate, flatElement, description, isEliminated
        );

        final byte[] content = defectReportGenerator.printDefectReport(unom, restDefectDtos, generalValues);
        return RestFileDto.builder()
            .fileName("defects_" + unom + ".xlsx")
            .content(content)
            .build();
    }

    @Override
    public RestFileDto getReport(final String id) {
        final ApartmentInspectionDocument apartmentInspectionDocument = fetchDocument(id);
        final Map<ReportFieldType, String> reportFields = retrieveReportFields(apartmentInspectionDocument);
        return ssrReporterService.createPdfReport(
            "Акт",
            "reports/apartmentinspection/ApartmentInspection.rtf",
            reportFields,
            apartmentInspectionDocument.getFolderId()
        );
    }

    @Override
    public RestFileDto createReport(final RestApartmentInspectionReportDto restApartmentInspectionReportDto) {
        final Map<ReportFieldType, String> reportFields = retrieveReportFields(restApartmentInspectionReportDto);
        return ssrReporterService.createPdfReport(
            "Акт",
            "reports/apartmentinspection/ApartmentInspection.rtf",
            reportFields,
            restApartmentInspectionReportDto.getFolderId()
        );
    }

    @Override
    public void addBlockingInformation(
        final String apartmentInspectionId, final List<String> defectIds, final boolean isBlocked
    ) {
        final ApartmentInspectionDocument apartmentInspectionDocument = fetchDocument(apartmentInspectionId);
        addBlockingInformation(apartmentInspectionDocument, defectIds, isBlocked);
    }

    @Override
    public void addBlockingInformation(
        final ApartmentInspectionDocument apartmentInspectionDocument,
        final List<String> defectIds,
        final boolean isBlocked
    ) {
        final ApartmentInspectionType apartmentInspection = apartmentInspectionDocument.getDocument()
            .getApartmentInspectionData();
        apartmentInspection.getApartmentDefects()
            .stream()
            .filter(apartmentDefect -> nonNull(apartmentDefect)
                && nonNull(apartmentDefect.getApartmentDefectData())
                && nonNull(apartmentDefect.getApartmentDefectData().getId()))
            .forEach(apartmentDefect -> {
                final ApartmentEliminationDefectType apartmentDefectData = apartmentDefect.getApartmentDefectData();
                if (defectIds.contains(apartmentDefectData.getId())) {
                    apartmentDefectData.setIsBlocked(isBlocked);
                }
            });
        updateDocument(apartmentInspectionDocument, "addBlockingInformation");
    }

    @Override
    public String retrieveAffairId(final String apartmentInspectionId) {
        return retrieveAffairId(
            fetchDocument(apartmentInspectionId)
        );
    }

    private String retrieveAffairId(final ApartmentInspectionDocument apartmentInspectionDocument) {
        final String personDocumentId = apartmentInspectionDocument
            .getDocument()
            .getApartmentInspectionData()
            .getPersonID();
        return personDocumentService.fetchById(personDocumentId)
            .map(PersonDocument::getDocument)
            .map(Person::getPersonData)
            .map(PersonType::getAffairId)
            .orElse(null);
    }

    @Override
    public SsrCcoDocument retrieveSsrCcoDocument(final ApartmentInspectionDocument apartmentInspectionDocument) {
        final String unom = apartmentInspectionDocument
            .getDocument()
            .getApartmentInspectionData()
            .getUnom();
        return ssrCcoDocumentService.fetchByUnom(unom)
            .orElse(null);
    }

    private Map<String, String> retrieveGeneralValues(
        final String unom,
        final String flat,
        final String actNum,
        final LocalDate filingDate,
        final String flatElement,
        final String description,
        final Boolean isEliminated
    ) {
        final SsrCcoData ssrCcoData = ssrCcoDocumentService.fetchByUnom(unom)
            .map(SsrCcoDocument::getDocument)
            .map(SsrCco::getSsrCcoData)
            .orElse(null);

        final String address = ofNullable(ssrCcoData)
            .map(SsrCcoData::getAddress)
            .orElse(null);

        final String district = ofNullable(ssrCcoData)
            .map(SsrCcoData::getDistrict)
            .orElse(null);

        final String areaAndDistrict = ofNullable(ssrCcoData)
            .map(SsrCcoData::getArea)
            .map(area -> isNull(district) ? area : area.concat(", ").concat(district))
            .orElse(district);

        final Map<String, String> generalValues = new HashMap<>();
        generalValues.put(ADDRESS, address);
        generalValues.put(AREA_AND_DISTRICT, areaAndDistrict);

        if (nonNull(flat)) {
            generalValues.put("Квартира:", flat);
        }
        if (nonNull(actNum)) {
            generalValues.put("Номер акта:", actNum);
        }
        final String filingDateString = ofNullable(filingDate)
            .map(DateTimeUtils::getFormattedDate)
            .orElse(null);
        if (nonNull(filingDate)) {
            generalValues.put("Дата регистрации акта:", filingDateString);
        }
        if (nonNull(flatElement)) {
            generalValues.put("Помещение:", flatElement);
        }
        if (nonNull(description)) {
            generalValues.put("Описание дефекта:", description);
        }
        if (nonNull(isEliminated)) {
            generalValues.put("Отметка об устранении:", isEliminated ? "Устранено" : "Не устранено");
        }

        return generalValues;
    }

    private Map<ReportFieldType, String> retrieveReportFields(
        final ApartmentInspectionDocument apartmentInspectionDocument
    ) {
        final ApartmentInspectionType apartmentInspectionData = apartmentInspectionDocument.getDocument()
            .getApartmentInspectionData();
        final Map<ReportFieldType, String> reportFields = new HashMap<>();
        reportFields.put(
            ApartmentInspectionFieldType.CURRENT_DATE_TEXT, DateTimeUtils.getFormattedDate(LocalDate.now())
        );
        final String apartmentInspectionNumber = ofNullable(apartmentInspectionData.getActNum())
            .orElse("");
        reportFields.put(ApartmentInspectionFieldType.APARTMENT_INSPECTION_NUMBER, apartmentInspectionNumber);

        final PersonType personData = ofNullable(apartmentInspectionData.getPersonID())
            .flatMap(personDocumentService::fetchById)
            .map(PersonDocument::getDocument)
            .map(Person::getPersonData)
            .orElse(null);
        final String applicantFio = ofNullable(personData)
            .map(PersonUtils::getFullName)
            .orElse("");
        reportFields.put(ApartmentInspectionFieldType.APPLICANT_FIO, applicantFio);
        final String applicantPhone = ofNullable(personData)
            .map(PersonType::getPhones)
            .map(PersonUtils::getBasePhoneNumber)
            .orElse("");
        reportFields.put(ApartmentInspectionFieldType.APPLICANT_PHONE, applicantPhone);
        final String newFlatAddress = ofNullable(apartmentInspectionData.getAddress())
            .orElse("");
        reportFields.put(ApartmentInspectionFieldType.NEW_FLAT_ADDRESS, newFlatAddress);
        final String oldFlatAddress = ofNullable(personData)
            .map(PersonType::getFlatID)
            .map(flatService::fetchRealEstateAndFlat)
            .map(RealEstateUtils::getRealEstateAddress)
            .orElse("");
        reportFields.put(ApartmentInspectionFieldType.OLD_FLAT_ADDRESS, oldFlatAddress);
        final String generalContractorOrganization = ofNullable(apartmentInspectionData.getGeneralContractors())
            .map(Collection::stream)
            .orElse(Stream.empty())
            .filter(OrganizationInformation::isIsAssigned)
            .findFirst()
            .map(OrganizationInformation::getOrgFullName)
            .orElse("");
        reportFields.put(ApartmentInspectionFieldType.GENERAL_CONTRACTOR_ORGANIZATION, generalContractorOrganization);
        final String developerOrganization = ofNullable(apartmentInspectionData.getDevelopers())
            .map(ApartmentInspectionUtils::joinOrgInfoFullName)
            .orElse("");
        reportFields.put(ApartmentInspectionFieldType.DEVELOPER_ORGANIZATION, developerOrganization);
        reportFields.put(ApartmentInspectionFieldType.DEFECT_LIST, retrieveDefects(apartmentInspectionData));
        final String commissionDecision = ApartmentInspectionUtils.getLastDelayDate(apartmentInspectionData)
            .map(DateTimeUtils::getFormattedDate)
            .map(date -> "устранение в срок до " + date)
            .orElse("");
        reportFields.put(ApartmentInspectionFieldType.COMMISSION_DECISION, commissionDecision);
        return reportFields;
    }

    private Map<ReportFieldType, String> retrieveReportFields(
        final RestApartmentInspectionReportDto restApartmentInspectionReportDto
    ) {
        final Map<ReportFieldType, String> reportFields = new HashMap<>();
        reportFields.put(
            ApartmentInspectionFieldType.CURRENT_DATE_TEXT, DateTimeUtils.getFormattedDate(LocalDate.now())
        );
        final String apartmentInspectionNumber = ofNullable(restApartmentInspectionReportDto.getActNumber())
            .orElse("");
        reportFields.put(ApartmentInspectionFieldType.APARTMENT_INSPECTION_NUMBER, apartmentInspectionNumber);

        final PersonType personData = ofNullable(restApartmentInspectionReportDto.getPersonDocumentId())
            .flatMap(personDocumentService::fetchById)
            .map(PersonDocument::getDocument)
            .map(Person::getPersonData)
            .orElse(null);
        final String applicantFio = ofNullable(personData)
            .map(PersonUtils::getFullName)
            .orElse("");
        reportFields.put(ApartmentInspectionFieldType.APPLICANT_FIO, applicantFio);
        final String applicantPhone = ofNullable(personData)
            .map(PersonType::getPhones)
            .map(PersonUtils::getBasePhoneNumber)
            .orElse("");
        reportFields.put(ApartmentInspectionFieldType.APPLICANT_PHONE, applicantPhone);
        final String newFlatAddress = ofNullable(restApartmentInspectionReportDto.getAddress())
            .orElse("");
        reportFields.put(ApartmentInspectionFieldType.NEW_FLAT_ADDRESS, newFlatAddress);
        final String oldFlatAddress = ofNullable(personData)
            .map(PersonType::getFlatID)
            .map(flatService::fetchRealEstateAndFlat)
            .map(RealEstateUtils::getRealEstateAddress)
            .orElse("");
        reportFields.put(ApartmentInspectionFieldType.OLD_FLAT_ADDRESS, oldFlatAddress);
        reportFields.put(ApartmentInspectionFieldType.GENERAL_CONTRACTOR_ORGANIZATION, SHORT_EMPTY_PDF_LINE);
        reportFields.put(ApartmentInspectionFieldType.DEVELOPER_ORGANIZATION, SHORT_EMPTY_PDF_LINE);
        reportFields.put(ApartmentInspectionFieldType.DEFECT_LIST, retrieveDefects(restApartmentInspectionReportDto));
        reportFields.put(ApartmentInspectionFieldType.COMMISSION_DECISION, LONG_EMPTY_PDF_LINE);
        return reportFields;
    }

    private String retrieveDefects(final ApartmentInspectionType apartmentInspectionData) {
        return apartmentInspectionData.getApartmentDefects()
            .stream()
            .map(ApartmentDefects::getApartmentDefectData)
            .collect(Collectors.groupingBy(ApartmentDefectType::getFlatElement))
            .entrySet()
            .stream()
            .map(entry -> retrieveFlatElementDefects(entry.getKey(), entry.getValue()))
            .filter(StringUtils::isNotBlank)
            .collect(Collectors.joining(";<br>"));
    }

    private String retrieveDefects(final RestApartmentInspectionReportDto restApartmentInspectionReportDto) {
        return restApartmentInspectionReportDto.getDefects()
            .stream()
            .collect(Collectors.groupingBy(DefectDto::getFlatElement))
            .entrySet()
            .stream()
            .map(entry -> entry.getKey().concat(" - " + retrieveDefectsText(entry.getValue())))
            .filter(StringUtils::isNotBlank)
            .collect(Collectors.joining(";<br>"));
    }

    private String retrieveFlatElementDefects(final String flatElement, List<ApartmentEliminationDefectType> defects) {
        final String defectsText = defects.stream()
            .map(ApartmentDefectType::getDescription)
            .filter(StringUtils::isNotBlank)
            .collect(Collectors.joining(", "));
        return flatElement.concat(" - " + defectsText);
    }

    private String retrieveDefectsText(List<DefectDto> defects) {
        return defects.stream()
            .map(DefectDto::getDescription)
            .filter(StringUtils::isNotBlank)
            .collect(Collectors.joining(", "));
    }
}
