package ru.croc.ugd.ssr.service.bpm;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static ru.croc.ugd.ssr.utils.StreamUtils.not;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import ru.croc.ugd.ssr.service.UserService;
import ru.reinform.cdp.bpm.model.FormProperty;
import ru.reinform.cdp.bpm.model.IdentityLink;
import ru.reinform.cdp.bpm.model.IdentityLinkTypeEnum;
import ru.reinform.cdp.bpm.model.ProcessInstance;
import ru.reinform.cdp.bpm.model.Task;
import ru.reinform.cdp.bpm.model.Variable;
import ru.reinform.cdp.bpm.service.BpmRuntimeService;
import ru.reinform.cdp.ldap.model.UserBean;
import ru.reinform.cdp.utils.core.RIExceptionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Сервис по работе с BPM.
 */
@Slf4j
@Component
@AllArgsConstructor
public class BpmService {

    /**
     * EntityIdVar.
     */
    public static final String PROCESS_DOCUMENT_KEY_ENTITY_ID_VAR = "EntityIdVar";
    /**
     * Кандидаты на назначение задачи.
     */
    public static final String PROCESS_DOCUMENT_KEY_CANDIDATE_USERS = "CandidateUsers";

    public static final String PROCESS_DOCUMENT_KEY_CANDIDATE_GROUPS = "CandidateGroups";

    private final BpmRuntimeService bpmRuntimeService;
    private final UserService userService;
    private final SsrBpmRuntimeService ssrBpmRuntimeService;

    /**
     * Получить группы по наименованию схемы bpmn.
     *
     * @param processDefinitionKey наименование
     * @return спикок наименований уникальных групп
     */
    public List<String> getGroupsByProcessDefinitionKey(String processDefinitionKey) {
        Set<String> groups = new HashSet<>();

        List<Task> tasks =
            bpmRuntimeService.findTasks(processDefinitionKey, null, null, null, true);
        if (nonNull(tasks) && !tasks.isEmpty()) {
            for (Task task : tasks) {
                List<Variable> taskVariables = bpmRuntimeService.getTaskVariables(task.getId());
                Variable groupTaskLocalVar = taskVariables.stream()
                    .filter(i -> i.getName().equals("GroupTaskLocalVar"))
                    .findFirst()
                    .orElse(null);
                if (nonNull(groupTaskLocalVar)) {
                    groups.add(groupTaskLocalVar.getValue().toString());
                }
            }
            return new ArrayList<>(groups);
        } else {
            return null;
        }
    }

    /**
     * Запустить новый процесс.
     *
     * @param processDefinitionKey наименование
     * @param variablesMap         переменные для процесса
     * @return ID процесса
     */
    public String startNewProcess(final String processDefinitionKey,
                                  final Map<String, String> variablesMap) {
        try {
            if (variablesMap == null
                || variablesMap.get(PROCESS_DOCUMENT_KEY_ENTITY_ID_VAR) == null) {
                throw new RuntimeException("No required params to start process"); // TODO replace with well-named
            }
            final List<FormProperty> variables = variablesMap.entrySet()
                .stream()
                .map(stringObjectEntry -> new FormProperty(stringObjectEntry.getKey(),
                    stringObjectEntry.getValue()))
                .collect(Collectors.toList());
            final ProcessInstance processInstance =
                bpmRuntimeService.startProcessViaForm(processDefinitionKey, variables);
            return processInstance.getId();
        } catch (Exception e) {
            throw RIExceptionUtils.wrap(e, "error on {0}", RIExceptionUtils.method())
                .withUserMessage("Ошибка создания документа-заявки.");
        }
    }

    /**
     * Получить процесс по id.
     *
     * @param processInstanceId id процесса
     * @return ProcessInstance
     */
    public ProcessInstance getProcessInstance(final String processInstanceId) {
        try {
            return bpmRuntimeService.getProcessInstance(processInstanceId, true);
        } catch (Exception e) {
            throw RIExceptionUtils.wrap(e, "error on {0}", RIExceptionUtils.method())
                .withUserMessage("Ошибка создания документа-заявки.");
        }
    }

    /**
     * Удалить процесс по id.
     *
     * @param processInstanceId id процесса
     */
    public void deleteProcessInstance(final String processInstanceId) {
        try {
            bpmRuntimeService.delProcessInstance(processInstanceId, true);
        } catch (Exception e) {
            throw RIExceptionUtils.wrap(e, "error on {0}", RIExceptionUtils.method())
                .withUserMessage("Ошибка создания документа-заявки.");
        }
    }

    /**
     * Ищет задачу по ID.
     *
     * @param taskId - идентификатор задачи
     * @return - найденная задача.
     */
    public Task getTaskById(final String taskId) {
        return bpmRuntimeService.getTask(taskId, true);
    }

    /**
     * Ищет задачи по идентификатору процесса.
     *
     * @param processInstanceId processInstanceId
     * @return task list
     */
    public List<Task> getTasksByProcessId(final String processInstanceId) {
        return getTasksByProcessId(null, processInstanceId);
    }

    /**
     * Ищет задачи.
     *
     * @param processDefinitionKey processDefinitionKey
     * @param processInstanceId processInstanceId
     * @return List Task
     */
    public List<Task> getTasksByProcessId(final String processDefinitionKey, final String processInstanceId) {
        List<Task> tasks = ssrBpmRuntimeService.findTasks(
            SsrTasksRequest.builder()
                .processDefinitionKeyLike(processDefinitionKey)
                .processInstanceId(processInstanceId)
                .includeProcessVariables(true)
                .includeTaskLocalVariables(true)
                .build()
        );
        addLogToGetTasksByProcessIdIfNeeded(
            tasks, "tasks not found by processInstanceId", processDefinitionKey, processInstanceId
        );
        if (CollectionUtils.isEmpty(tasks)) {
            tasks = bpmRuntimeService.findTasks(
                processDefinitionKey, null, null, null, true
            );
            addLogToGetTasksByProcessIdIfNeeded(tasks, "tasks not found", processDefinitionKey, processInstanceId);
        }
        final List<Task> filteredTasks = ListUtils.emptyIfNull(tasks)
            .stream()
            .filter(task -> StringUtils.equals(task.getProcessInstanceId(), processInstanceId))
            .collect(Collectors.toList());
        addLogToGetTasksByProcessIdIfNeeded(
            filteredTasks, "filtered task list is empty", processDefinitionKey, processInstanceId
        );
        return filteredTasks;
    }

    private void addLogToGetTasksByProcessIdIfNeeded(
        final List<Task> tasks, final String message, final String processDefinitionKey, final String processInstanceId
    ) {
        if (CollectionUtils.isEmpty(tasks)) {
            log.info(
                "getTasksByProcessId: " + message + " (processDefinitionKey = {}, processInstanceId = {})",
                processDefinitionKey,
                processInstanceId
            );
        }
    }

    /**
     * Поиск по наименованию процесса активных задач, не взятых в работу.
     *
     * @param processDefinitionKey наименование процесса
     * @return список задач
     */
    public List<Task> getTasksWithoutAssigneeByProcessDefinitionKey(final String processDefinitionKey) {
        return bpmRuntimeService.findTasks(processDefinitionKey, null, null, null, true)
            .stream()
            .filter(task -> isNull(task.getAssignee()))
            .collect(Collectors.toList());
    }

    /**
     * addTaskCandidateUser.
     * @param tasks tasks
     * @param candidate candidate
     */
    public void addTaskCandidateUser(final List<Task> tasks, final String candidate) {
        tasks.forEach(task -> addTaskCandidateUser(task, candidate));
    }

    /**
     * addTaskCandidateUser.
     * @param task task
     * @param candidate candidate
     */
    public void addTaskCandidateUser(final Task task, final String candidate) {
        final String taskId = task.getId();

        final List<String> candidateUsers = getCandidateUsers(taskId);

        if (!candidateUsers.contains(candidate)) {
            bpmRuntimeService.addTaskCandidateUser(taskId, candidate);
        }
    }

    /**
     * addTaskCandidateUser.
     * @param taskId taskId
     * @param candidate candidate
     */
    public void addTaskCandidateUser(final String taskId, final String candidate) {
        bpmRuntimeService.addTaskCandidateUser(taskId, candidate);
    }

    /**
     * delTaskCandidateUser.
     * @param taskId taskId
     * @param candidate candidate
     */
    public void deleteTaskCandidateUser(final String taskId, final String candidate) {
        bpmRuntimeService.delTaskCandidateUser(taskId, candidate);
    }

    /**
     * Назначить задачу группе пользователей, удалив назначение конкретным пользователям.
     * @param taskId ИД задачи
     * @param candidate группа пользователей
     */
    public void addTaskCandidateGroup(final String taskId, final String candidate) {
        deleteCandidateUsers(taskId);
        bpmRuntimeService.addTaskCandidateGroup(taskId, candidate);
    }

    /**
     * delTaskCandidateGroup.
     * @param taskId taskId
     * @param candidate candidate
     */
    public void deleteTaskCandidateGroup(final String taskId, final String candidate) {
        bpmRuntimeService.delTaskCandidateGroup(taskId, candidate);
    }

    /**
     * getIdentityLinks.
     *
     * @param taskId taskId
     * @return List IdentityLink
     */
    public List<IdentityLink> getIdentityLinks(final String taskId) {
        return bpmRuntimeService.getIdentityLinks(taskId);
    }

    /**
     * Завершение задачи заполнением формы.
     *
     * @param taskId - идентификатор задачи.
     * @param props  - данные формы (activiti:formProperty).
     */
    public void completeTaskViaForm(final String taskId, List<FormProperty> props) {
        log.info("Run finishTaskViaForm: taskId = {}", taskId);
        bpmRuntimeService.finishTaskViaForm(taskId, props);
    }

    /**
     * Актуализировать список кандидатов.
     * @param processInstanceId processInstanceId
     * @param ldapGroup ldapGroup
     */
    public void actualizeTaskCandidates(final String processInstanceId, final String ldapGroup) {
        getTasksByProcessId(processInstanceId)
            .forEach(task -> actualizeTaskCandidates(task, ldapGroup));
    }

    /**
     * Актуализировать список кандидатов.
     * @param tasks tasks
     * @param ldapGroup ldapGroup
     */
    public void actualizeTaskCandidates(final List<Task> tasks, final String ldapGroup) {
        tasks.forEach(task -> actualizeTaskCandidates(task, ldapGroup));
    }

    private void actualizeTaskCandidates(final Task task, final String ldapGroup) {
        final String taskId = task.getId();

        final List<String> candidateUsers = getCandidateUsers(taskId);
        final List<String> ldapGroupUsers = userService.findUsersByLdapGroup(ldapGroup)
            .stream()
            .map(UserBean::getAccountName)
            .collect(Collectors.toList());

        ldapGroupUsers.stream()
            .filter(not(candidateUsers::contains))
            .forEach(userLogin -> addTaskCandidateUser(taskId, userLogin));
        candidateUsers.stream()
            .filter(not(ldapGroupUsers::contains))
            .forEach(userLogin -> deleteTaskCandidateUser(taskId, userLogin));
    }

    /**
     * Добавить кандидата при необходимости.
     * @param processInstanceId processInstanceId
     * @param userLogin candidate
     */
    public void addTaskCandidate(final String processInstanceId, final String userLogin) {
        getTasksByProcessId(processInstanceId)
            .forEach(task -> addTaskCandidate(task, userLogin));
    }

    private void addTaskCandidate(final Task task, final String userLogin) {
        final String taskId = task.getId();
        if (!getCandidateUsers(taskId).contains(userLogin)) {
            addTaskCandidateUser(taskId, userLogin);
        }
    }

    public List<String> getCandidateUsers(final String taskId) {
        return getIdentityLinks(taskId)
            .stream()
            .filter(identityLink -> Objects.equals(identityLink.getType(), IdentityLinkTypeEnum.CANDIDATE))
            .map(IdentityLink::getUser)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    private List<String> getCandidateGroups(final String taskId) {
        return getIdentityLinks(taskId)
            .stream()
            .filter(identityLink -> Objects.equals(identityLink.getType(), IdentityLinkTypeEnum.CANDIDATE))
            .map(IdentityLink::getGroup)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    /**
     * Отменить назначение задачи пользователям.
     * @param taskId ИД задачи
     */
    public void deleteCandidateUsers(final String taskId) {
        getCandidateUsers(taskId)
            .forEach(userLogin -> {
                log.info("Delete task candidate user: taskId {}, userLogin {}", taskId, userLogin);
                deleteTaskCandidateUser(taskId, userLogin);
            });
    }

    /**
     * Отменить назначение задачи группам.
     * @param taskId ИД задачи
     */
    public void deleteCandidateGroups(final String taskId) {
        getCandidateGroups(taskId)
            .forEach(group -> {
                log.info("Delete task candidate group: taskId {}, group {}", taskId, group);
                deleteTaskCandidateGroup(taskId, group);
            });
    }

    /**
     * Снять задачу с исполнителя, если он не является кандидатом.
     *
     * @param taskId ИД задачи
     */
    public void deleteAssigneeIfNeeded(final String taskId) {
        final String assignee = getTaskById(taskId).getAssignee();
        if (nonNull(assignee) && !existsCandidate(taskId, assignee)) {
            log.info("Delete assignee: taskId = {}, assignee = {}", taskId, assignee);
            ssrBpmRuntimeService.deleteAssignee(taskId);
        }
    }

    /**
     * Является ли пользователь кандидатом по задаче.
     *
     * @param taskId ИД задачи
     * @param userLogin логин пользователя
     * @return пользователь является кандидатом по задаче
     */
    public boolean existsCandidate(final String taskId, final String userLogin) {
        return getCandidateUsers(taskId).contains(userLogin)
            || getCandidateGroups(taskId).stream()
            .anyMatch(candidateGroup -> userService.checkUserGroup(candidateGroup, userLogin));
    }
}
