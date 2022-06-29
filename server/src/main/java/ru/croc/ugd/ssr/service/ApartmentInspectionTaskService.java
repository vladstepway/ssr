package ru.croc.ugd.ssr.service;

import static java.util.Objects.isNull;
import static ru.croc.ugd.ssr.utils.StreamUtils.not;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import ru.croc.ugd.ssr.OrganizationInformation;
import ru.croc.ugd.ssr.config.ApartmentInspectionProperties;
import ru.croc.ugd.ssr.model.ssrcco.SsrCcoDocument;
import ru.croc.ugd.ssr.service.bpm.BpmService;
import ru.croc.ugd.ssr.service.document.SsrCcoDocumentService;
import ru.croc.ugd.ssr.ssrcco.SsrCco;
import ru.croc.ugd.ssr.ssrcco.SsrCcoData;
import ru.croc.ugd.ssr.ssrcco.SsrCcoEmployee;
import ru.reinform.cdp.bpm.model.Task;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@AllArgsConstructor
public class ApartmentInspectionTaskService {

    private static final String RENOVATION_FUND_LDAP_GROUP = "UGD_SSR_PERSON_RENOVATION_FOND";
    private static final String KP_UGS_LDAP_GROUP = "UGD_SSR_PERSON_KP_UGS";

    private final BpmService bpmService;
    private final SsrCcoDocumentService ssrCcoDocumentService;
    private final ApartmentInspectionProperties apartmentInspectionProperties;

    public void actualizeTaskCandidates(final Task task, final List<String> users) {
        log.info(
            "ApartmentInspectionTaskService.actualizeTaskCandidates: taskId = {}, users = {}",
            task.getId(),
            String.join(", ", users)
        );
        bpmService.deleteCandidateGroups(task.getId());
        assignUsersToTask(task, users);
    }

    public void addTaskCandidateGroup(final String taskId, final String candidate) {
        log.info(
            "ApartmentInspectionTaskService.addTaskCandidateGroup: taskId = {}, candidate = {}", taskId, candidate
        );
        bpmService.addTaskCandidateGroup(taskId, candidate);
    }

    public void assignUsersToTask(final Task task, final List<String> usersToAssign) {
        final List<String> assignedUsers = getAssignedUsersToTask(task);
        log.info(
            "ApartmentInspectionTaskService.assignUsersToTask: taskId = {}, usersToAssign = {}, assignedUsers = {}",
            task.getId(),
            String.join(", ", usersToAssign),
            String.join(", ", assignedUsers)
        );
        usersToAssign
            .stream()
            .filter(not(assignedUsers::contains))
            .forEach(userNameToAssign -> bpmService.addTaskCandidateUser(task.getId(), userNameToAssign));
        assignedUsers
            .stream()
            .filter(not(usersToAssign::contains))
            .forEach(userNameToAssign -> bpmService.deleteTaskCandidateUser(task.getId(), userNameToAssign));
    }

    public List<SsrCcoEmployee> getActiveEmployees(final String unom) {
        return ssrCcoDocumentService.fetchByUnom(unom)
            .map(SsrCcoDocument::getDocument)
            .map(SsrCco::getSsrCcoData)
            .map(SsrCcoData::getEmployees)
            .map(List::stream)
            .orElse(Stream.empty())
            .filter(Objects::nonNull)
            .filter(this::isEmployeeActive)
            .collect(Collectors.toList());
    }

    private List<String> getAssignedUsersToTask(final Task task) {
        return bpmService.getCandidateUsers(task.getId());
    }

    private boolean isEmployeeActive(final SsrCcoEmployee ssrCcoEmployee) {
        return (isNull(ssrCcoEmployee.getPeriodFrom())
            || !ssrCcoEmployee.getPeriodFrom().isAfter(LocalDate.now()))
            && (isNull(ssrCcoEmployee.getPeriodTo())
            || !ssrCcoEmployee.getPeriodTo().isBefore(LocalDate.now()));
    }

    public String getUserGroupByOksDevelopers(final List<OrganizationInformation> developers) {
        if (CollectionUtils.isEmpty(developers)) {
            return RENOVATION_FUND_LDAP_GROUP;
        }
        final String kpUgsInn = apartmentInspectionProperties.getKpUgsInn();
        final boolean isKpUgs = developers
            .stream()
            .map(OrganizationInformation::getExternalId)
            .anyMatch(orgInn -> StringUtils.equals(kpUgsInn, orgInn));
        return isKpUgs ? KP_UGS_LDAP_GROUP : RENOVATION_FUND_LDAP_GROUP;
    }

    public void deleteAssigneesIfNeeded(final List<Task> tasks) {
        tasks.forEach(task -> bpmService.deleteAssigneeIfNeeded(task.getId()));
    }
}
