package ru.croc.ugd.ssr.service.bpm;

import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.dto.ActualizeTaskCandidatesRequestDto;
import ru.croc.ugd.ssr.dto.SsrBpmProperties;
import ru.croc.ugd.ssr.dto.bpm.RestCheckTaskCandidateDto;
import ru.croc.ugd.ssr.exception.SsrException;
import ru.croc.ugd.ssr.service.UserService;
import ru.reinform.cdp.bpm.model.Task;
import ru.reinform.cdp.bpm.model.Variable;
import ru.reinform.cdp.document.model.DocumentType;
import ru.reinform.cdp.document.service.DocumentTypesRegistry;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * DefaultRestBpmTaskService.
 */
@Slf4j
@Service
@AllArgsConstructor
public class DefaultRestBpmTaskService implements RestBpmTaskService {

    private final BpmService bpmService;
    private final DocumentTypesRegistry documentTypesRegistry;
    private final List<BpmEntityService> bpmEntityServices;
    private final SsrBpmProperties ssrBpmProperties;
    private final UserService userService;

    @Override
    public void actualizeTaskCandidates(final ActualizeTaskCandidatesRequestDto actualizeTaskCandidatesRequestDto) {
        final String processInstanceId = ofNullable(actualizeTaskCandidatesRequestDto.getProcessInstanceId())
            .orElseThrow(() -> new SsrException("processInstanceId is null"));

        final List<Task> tasksByProcessId = bpmService.getTasksByProcessId(processInstanceId);

        ofNullable(actualizeTaskCandidatesRequestDto.getLdapGroups())
            .map(ldapGroups -> Arrays.stream(ldapGroups.split(",")))
            .orElse(Stream.empty())
            .map(String::trim)
            .filter(StringUtils::isNotEmpty)
            .forEach(ldapGroup -> bpmService.actualizeTaskCandidates(tasksByProcessId, ldapGroup));

        ofNullable(actualizeTaskCandidatesRequestDto.getLogins())
            .map(logins -> Arrays.stream(logins.split(",")))
            .orElse(Stream.empty())
            .map(String::trim)
            .filter(StringUtils::isNotEmpty)
            .forEach(login -> bpmService.addTaskCandidateUser(tasksByProcessId, login));
    }

    @Override
    public void actualizeTaskCandidates(String anyDocumentTypeCode, String documentId) {
        final DocumentType documentType = documentTypesRegistry.getDocumentType(anyDocumentTypeCode);

        final String ldapGroup = ofNullable(ssrBpmProperties)
            .map(SsrBpmProperties::getDocumentTypeLdapGroupMap)
            .map(map -> map.get(anyDocumentTypeCode))
            .orElseThrow(() -> new SsrException("Ldap group is not configured for " + anyDocumentTypeCode));

        retrieveBpmEntityService(documentType)
            .map(entityService -> entityService.getProcessInstanceId(documentId))
            .ifPresent(processInstanceId -> bpmService.actualizeTaskCandidates(processInstanceId, ldapGroup));
    }

    private Optional<BpmEntityService> retrieveBpmEntityService(final DocumentType documentType) {
        return bpmEntityServices
            .stream()
            .filter(p -> documentType == p.getDocumentType())
            .findFirst();
    }

    @Async
    @Override
    public void assignActiveTasksToGroup(final String processDefinitionKey) {
        log.info("Start assigning active tasks to group: processDefinitionKey {}", processDefinitionKey);
        try {
            final List<Task> tasks = bpmService.getTasksWithoutAssigneeByProcessDefinitionKey(processDefinitionKey);
            if ("ugdssr_DefectsEliminationStart_prc".equals(processDefinitionKey)) {
                tasks.stream()
                    .filter(this::isDeveloperTask)
                    .forEach(this::assignApartmentInspectionTaskToGroup);
            } else {
                final String candidateGroup = retrieveCandidateGroup(processDefinitionKey);
                tasks.forEach(task -> assignTaskToGroup(task.getId(), candidateGroup));
            }
        } catch (Exception e) {
            log.error(
                "Unable to assign active tasks to group: processDefinitionKey {}, {}",
                processDefinitionKey,
                e.getMessage(),
                e
            );
        }
        log.info("Finish assigning active tasks to group: processDefinitionKey {}", processDefinitionKey);
    }

    @Override
    public List<RestCheckTaskCandidateDto> checkTaskCandidates(final List<String> taskIds) {
        final String currentUserLogin = userService.getCurrentUserLogin();
        return taskIds.stream()
            .map(taskId -> RestCheckTaskCandidateDto.builder()
                .candidate(bpmService.existsCandidate(taskId, currentUserLogin))
                .taskId(taskId)
                .build())
            .collect(Collectors.toList());
    }

    private void assignApartmentInspectionTaskToGroup(final Task task) {
        ofNullable(task)
            .map(Task::getVariables)
            .orElse(Collections.emptyList())
            .stream()
            .filter(variable -> "developersGroupInput".equals(variable.getName()))
            .findFirst()
            .map(Variable::getValue)
            .map(String::valueOf)
            .ifPresent(candidateGroup -> assignTaskToGroup(task.getId(), candidateGroup));
    }

    private boolean isDeveloperTask(final Task task) {
        if (isNull(task.getVariables())) {
            return false;
        }
        return task.getVariables()
            .stream()
            .anyMatch(variable -> "GroupTaskLocalVar".equals(variable.getName())
                && "NO_SUCH_ORG".equals(variable.getValue()));
    }

    private String retrieveCandidateGroup(final String processDefinitionKey) {
        switch (processDefinitionKey) {
            case "ugdssr_firstFlowErrorAnalytics":
                return "UGD_SSR_OPERATOR";
            case "ugdssrCommissionInspection_confirmDate":
                return "UGD_SSR_COMMISSION_INSPECTION_READ";
            case "ugdssrContract_processAppointment":
                return "UGD_SSR_CONTRACT_APPOINTMENT_READ";
            case "ugdssrFlat_processAppointment":
                return "UGD_SSR_FLAT_APPOINTMENT_READ";
            case "ugdssrGuardianship_enterDetails":
                return "UGD_SSR_GUARDIANSHIP_HANDLE_REQUEST";
            case "ugdssrOfferLetter_parseAddress":
                return "UGD_SSR_ADDRESS_RECOGNITION";
            case "ugdssrPersonalDocument_parseDocuments":
                return "UGD_SSR_PERSONAL_DOCUMENT_PARSE";
            case "ugdssrShipping_confirmApplication":
            case "ugdssrShipping_finishShipping":
                return "UGD_SSR_SHIPPING_APPLICATION";
            default:
                throw new SsrException(
                    "Unable to retrieve candidate group: processDefinitionKey " + processDefinitionKey
                );
        }
    }

    private void assignTaskToGroup(final String taskId, final String candidateGroup) {
        try {
            log.info("Start assigning active task to group: taskId {}, candidateGroup {}", taskId, candidateGroup);
            bpmService.addTaskCandidateGroup(taskId, candidateGroup);
            log.info("Finish assigning active task to group: taskId {}, candidateGroup {}", taskId, candidateGroup);
        } catch (Exception e) {
            log.error(
                "Error on assigning active task to group: taskId {}, candidateGroup {}",
                taskId, candidateGroup, e
            );
        }
    }

    @Override
    public List<Task> findTasks(final String processDefinitionKey, final String processInstanceId) {
        return bpmService.getTasksByProcessId(processDefinitionKey, processInstanceId);
    }
}
