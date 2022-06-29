package ru.croc.ugd.ssr.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.dto.ActualizeTaskCandidatesRequestDto;
import ru.croc.ugd.ssr.dto.bpm.RestCheckTaskCandidateDto;
import ru.croc.ugd.ssr.service.bpm.RestBpmTaskService;
import ru.reinform.cdp.bpm.model.Task;

import java.util.List;

/**
 * Контроллер для работы с задачами BPM.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/bpm")
public class BpmTaskController {
    private final RestBpmTaskService bpmTaskService;

    /**
     * Актуализировать список кандидатов активной задачи.
     *
     * @param actualizeTaskCandidatesRequestDto Запрос на актуализацию списка кандидатов по задаче.
     */
    @PostMapping(value = "/actualize-task-candidates")
    public void actualizeTaskCandidates(
        @RequestBody final ActualizeTaskCandidatesRequestDto actualizeTaskCandidatesRequestDto
    ) {
        bpmTaskService.actualizeTaskCandidates(actualizeTaskCandidatesRequestDto);
    }

    /**
     * Актуализировать список кандидатов активной задачи по документу.
     *
     * @param anyDocumentTypeCode Тип документа.
     * @param documentId         Идентификатор документа.
     */
    @PostMapping(value = "/{anyDocumentTypeCode}/{documentId}/actualize-task-candidates")
    public void actualizeTaskCandidates(
        @PathVariable("anyDocumentTypeCode") final String anyDocumentTypeCode,
        @PathVariable("documentId") final String documentId
    ) {
        bpmTaskService.actualizeTaskCandidates(anyDocumentTypeCode, documentId);
    }

    /**
     * Назначить активные задач на группу пользователей.
     *
     * @param processDefinitionKey Наименование процесса.
     */
    @PostMapping(value = "/assign-tasks-to-group")
    public void assignActiveTasksToGroup(
        @RequestParam("processDefinitionKey") final String processDefinitionKey
    ) {
        bpmTaskService.assignActiveTasksToGroup(processDefinitionKey);
    }

    /**
     * Является ли текущий пользователь кандидатом по задачам.
     *
     * @param taskIds список задач
     * @return результат проверки
     */
    @GetMapping(value = "/check-task-candidates")
    public List<RestCheckTaskCandidateDto> checkTaskCandidates(@RequestParam final List<String> taskIds) {
        return bpmTaskService.checkTaskCandidates(taskIds);
    }

    /**
     * Получить список задач.
     *
     * @param processDefinitionKey processDefinitionKey
     * @param processInstanceId processInstanceId
     * @return список задач
     */
    @GetMapping(value = "/tasks")
    public List<Task> findTasks(
        @RequestParam(required = false) final String processDefinitionKey,
        @RequestParam final String processInstanceId
    ) {
        return bpmTaskService.findTasks(processDefinitionKey, processInstanceId);
    }
}
