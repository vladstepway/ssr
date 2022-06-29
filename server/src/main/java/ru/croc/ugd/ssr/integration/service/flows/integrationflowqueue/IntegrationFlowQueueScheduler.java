package ru.croc.ugd.ssr.integration.service.flows.integrationflowqueue;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.integrationflow.IntegrationFlowType;
import ru.croc.ugd.ssr.model.integrationflow.IntegrationFlowQueueDocument;
import ru.croc.ugd.ssr.service.document.IntegrationFlowQueueDocumentService;

import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class IntegrationFlowQueueScheduler {

    private final IntegrationFlowQueueService integrationFlowQueueService;
    private final IntegrationFlowQueueDocumentService integrationFlowQueueDocumentService;

    @Scheduled(fixedDelayString = "${schedulers.integration-flow-queue.fixed-delay:900000}")
    public void processFlowMessages() {
        log.info("Start integrationFlowQueueScheduler");

        final List<IntegrationFlowQueueDocument> personResponseQueueDocuments = integrationFlowQueueDocumentService
            .fetchNotPostponedByFlowType(IntegrationFlowType.DGI_TO_DGP_PERSON_RESPONSE);

        log.info("Processing {} personResponseFlow documents", personResponseQueueDocuments.size());
        personResponseQueueDocuments.forEach(integrationFlowQueueService::processFlowMessageSafely);

        final List<IntegrationFlowQueueDocument> personUpdateFlowQueueDocuments = integrationFlowQueueDocumentService
            .fetchNotPostponedByFlowType(IntegrationFlowType.DGI_TO_DGP_PERSON_UPDATE);

        log.info("Processing {} personUpdateFlow documents", personUpdateFlowQueueDocuments.size());
        personUpdateFlowQueueDocuments.forEach(integrationFlowQueueService::processFlowMessageSafely);

        final List<IntegrationFlowQueueDocument> integrationFlowQueueDocuments = integrationFlowQueueDocumentService
            .fetchNotPostponed();

        log.info("Processing {} other flow documents", integrationFlowQueueDocuments.size());
        integrationFlowQueueDocuments
            .forEach(integrationFlowQueueService::processFlowMessageSafely);

        log.info("Finish integrationFlowQueueScheduler");
    }
}
