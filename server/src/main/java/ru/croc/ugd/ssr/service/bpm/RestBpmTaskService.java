package ru.croc.ugd.ssr.service.bpm;

import ru.croc.ugd.ssr.dto.ActualizeTaskCandidatesRequestDto;
import ru.croc.ugd.ssr.dto.bpm.RestCheckTaskCandidateDto;
import ru.reinform.cdp.bpm.model.Task;

import java.util.List;

/**
 * Сервис для работы с задачами BPM.
 */
public interface RestBpmTaskService {

    /**
     * Актуализировать список кандидатов активной задачи.
     *
     * @param actualizeTaskCandidatesRequestDto Запрос на актуализацию списка кандидатов по задаче.
     */
    void actualizeTaskCandidates(final ActualizeTaskCandidatesRequestDto actualizeTaskCandidatesRequestDto);

    /**
     * Актуализировать список кандидатов активной задачи по документу.
     *
     * @param anyDocumentTypeCode Тип документа.
     * @param documentId         Идентификатор документа.
     */
    void actualizeTaskCandidates(final String anyDocumentTypeCode, final String documentId);

    /**
     * Назначить активные задач на группу пользователей.
     *
     * @param processDefinitionKey Наименование процесса.
     */
    void assignActiveTasksToGroup(final String processDefinitionKey);

    /**
     * Является ли текущий пользователь кандидатом по задачам.
     *
     * @param taskIds список задач
     * @return результат проверки
     */
    List<RestCheckTaskCandidateDto> checkTaskCandidates(final List<String> taskIds);

    /**
     * Получить список задач.
     *
     * @param processDefinitionKey processDefinitionKey
     * @param processInstanceId processInstanceId
     * @return список задач
     */
    List<Task> findTasks(final String processDefinitionKey, final String processInstanceId);
}
