package ru.croc.ugd.ssr.integration.service.flows.integrationflowqueue;

import ru.croc.ugd.ssr.model.integrationflow.IntegrationFlowQueueDocument;

import java.util.List;

/**
 * Сервис для работы с внутренней очередью по обработке поточных сообщений.
 */
public interface IntegrationFlowQueueService {

    /**
     * Обработка поточного сообщения из очереди без ошибок.
     * @param integrationFlowQueueDocument integrationFlowQueueDocument
     */
    void processFlowMessageSafely(final IntegrationFlowQueueDocument integrationFlowQueueDocument);

    /**
     * Обработка поточного сообщения из очереди.
     * @param integrationFlowQueueDocumentId integrationFlowQueueDocumentId
     */
    void processFlowMessage(final String integrationFlowQueueDocumentId);

    /**
     * Включить обработку отложенных сообщений.
     * @param enoList enoList
     */
    void enablePostponedFlowMessages(final List<String> enoList);
}
