package ru.croc.ugd.ssr.service.bpm;

import static ru.reinform.cdp.bpm.utils.VariableUtils.TASK_TAKEON_DATE_VAR;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import ru.reinform.cdp.bpm.model.Task;
import ru.reinform.cdp.bpm.model.rest.TasksResponse;
import ru.reinform.cdp.utils.rest.utils.SendRestUtils;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SsrBpmRuntimeService {

    private static final String DELETE_VARIABLE_URL = "/runtime/tasks/{taskId}/variables/{variable}";
    private static final String TASK_URL = "/runtime/tasks/{taskId}";
    private static final String FIND_TASKS_URL = "/query/tasks?size=1000000";

    @Value("${bpm.url}")
    private String bpmUrl;

    private final SendRestUtils sendRestUtils;

    public void deleteVariable(final String taskId, final String variable) {
        try {
            this.sendRestUtils.sendJsonRequest(
                bpmUrl + DELETE_VARIABLE_URL, HttpMethod.DELETE, null, Void.class, taskId, variable
            );
        } catch (Exception e) {
            log.error("Unable to delete variable {} (taskId = {})", variable, taskId);
        }
    }

    public void setEmptyTaskAssignee(final String taskId) {
        try {
            //TODO: use object for request (assignee with null or empty string doesn't work)
            this.sendRestUtils.sendJsonRequest(
                bpmUrl + TASK_URL, HttpMethod.PUT, "{\"assignee\": null}", Void.class, taskId
            );
        } catch (Exception e) {
            log.error("Unable to set empty task assignee (taskId = {})", taskId);
        }
    }

    public void deleteAssignee(final String taskId) {
        setEmptyTaskAssignee(taskId);
        deleteVariable(taskId, TASK_TAKEON_DATE_VAR);
    }

    public List<Task> findTasks(final SsrTasksRequest ssrTasksRequest) {
        final TasksResponse tasksResponse = this.sendRestUtils.sendJsonRequest(
            bpmUrl + FIND_TASKS_URL, HttpMethod.POST, ssrTasksRequest, TasksResponse.class
        );
        return tasksResponse.getTasks();
    }
}
