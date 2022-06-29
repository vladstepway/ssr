package ru.croc.ugd.ssr.controller.integrationflow;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.integration.service.flows.integrationflowqueue.IntegrationFlowQueueService;

/**
 * Контроллер для работы с внутренней очередью по обработке поточных сообщений.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/integration-flow-queue")
public class IntegrationFlowQueueController {

    private final IntegrationFlowQueueService integrationFlowQueueService;

    /**
     * Обработка поточного сообщения из очереди.
     * @param integrationFlowQueueDocumentId integrationFlowQueueDocumentId
     */
    @PostMapping("/{id}/process")
    public void process(@PathVariable("id") final String integrationFlowQueueDocumentId) {
        integrationFlowQueueService.processFlowMessage(integrationFlowQueueDocumentId);
    }
}
