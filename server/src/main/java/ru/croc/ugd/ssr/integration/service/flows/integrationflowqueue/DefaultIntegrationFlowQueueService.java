package ru.croc.ugd.ssr.integration.service.flows.integrationflowqueue;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.exception.SsrException;
import ru.croc.ugd.ssr.integrationflow.IntegrationFlowData;
import ru.croc.ugd.ssr.integrationflow.IntegrationFlowQueueData;
import ru.croc.ugd.ssr.integrationflow.IntegrationFlowStatus;
import ru.croc.ugd.ssr.integrationflow.IntegrationFlowType;
import ru.croc.ugd.ssr.model.integrationflow.IntegrationFlowDocument;
import ru.croc.ugd.ssr.model.integrationflow.IntegrationFlowQueueDocument;
import ru.croc.ugd.ssr.mq.listener.EtpQueueListener;
import ru.croc.ugd.ssr.service.SsrFilestoreService;
import ru.croc.ugd.ssr.service.document.IntegrationFlowDocumentService;
import ru.croc.ugd.ssr.service.document.IntegrationFlowQueueDocumentService;

import java.util.List;
import javax.transaction.Transactional;

@Slf4j
@Service
@AllArgsConstructor
public class DefaultIntegrationFlowQueueService implements IntegrationFlowQueueService {

    private final IntegrationFlowQueueDocumentService integrationFlowQueueDocumentService;
    private final IntegrationFlowDocumentService integrationFlowDocumentService;
    private final List<EtpQueueListener<?, ?>> etpQueueListeners;
    private final SsrFilestoreService ssrFilestoreService;

    @Transactional
    public void processFlowMessageSafely(final IntegrationFlowQueueDocument integrationFlowQueueDocument) {
        try {
            processFlowMessage(integrationFlowQueueDocument);
        } catch (Exception e) {
            log.warn(
                "Unable to process integrationFlowQueueDocument {}: {}",
                integrationFlowQueueDocument.getId(),
                e.getMessage(),
                e
            );
        }
    }

    @Transactional
    public void processFlowMessage(final String integrationFlowQueueDocumentId) {
        final IntegrationFlowQueueDocument integrationFlowQueueDocument = integrationFlowQueueDocumentService
            .fetchDocument(integrationFlowQueueDocumentId);

        processFlowMessage(integrationFlowQueueDocument);
    }

    private void processFlowMessage(final IntegrationFlowQueueDocument integrationFlowQueueDocument) {
        log.info("Start integrationFlowQueueDocument {} processing", integrationFlowQueueDocument.getId());

        final IntegrationFlowQueueData integrationFlowQueueData = integrationFlowQueueDocument
            .getDocument()
            .getIntegrationFlowQueueData();
        final String integrationFlowDocumentId = integrationFlowQueueData.getIntegrationFlowDocumentId();

        final IntegrationFlowDocument integrationFlowDocument = integrationFlowDocumentService
            .fetchById(integrationFlowDocumentId)
            .orElseThrow(
                () -> new SsrException("Unable to find integrationFlowDocument " + integrationFlowDocumentId)
            );
        final IntegrationFlowData integrationFlowData = integrationFlowDocument.getDocument().getIntegrationFlowData();

        final EtpQueueListener<?, ?> etpQueueListener = retrieveEtpQueueListener(
            integrationFlowData.getFlowType()
        );

        final String message = new String(ssrFilestoreService.getFile(integrationFlowData.getFileStoreId()));

        etpQueueListener.handle(message, integrationFlowQueueData);

        log.info("Delete integrationFlowQueueDocument {}", integrationFlowQueueDocument.getId());
        integrationFlowQueueDocumentService
            .deleteDocument(integrationFlowQueueDocument.getId(), false, "processFlowMessage");

        integrationFlowData.setFlowStatus(IntegrationFlowStatus.PROCESSED);
        integrationFlowDocumentService.updateDocument(integrationFlowDocument, "processFlowMessage");

        log.info("Finish integrationFlowQueueDocument {} processing", integrationFlowQueueDocument.getId());
    }

    private EtpQueueListener<?, ?> retrieveEtpQueueListener(final IntegrationFlowType integrationFlowType) {
        return etpQueueListeners.stream()
            .filter(etpQueueListener -> etpQueueListener.getIntegrationFlowType() == integrationFlowType)
            .findFirst()
            .orElseThrow(() -> new SsrException("Unable to find etpQueueListener by flowType " + integrationFlowType));
    }

    @Override
    public void enablePostponedFlowMessages(final List<String> enoList) {
        log.info("Start enabling postponed flow messages: enoList = {}", String.join(", ", enoList));

        final List<IntegrationFlowQueueDocument> integrationFlowQueueDocuments = integrationFlowQueueDocumentService
            .fetchByEnoList(enoList);

        log.info("Found {} queue documents to be enabled", integrationFlowQueueDocuments.size());

        integrationFlowQueueDocuments.forEach(this::enablePostponedFlowMessage);
    }

    private void enablePostponedFlowMessage(final IntegrationFlowQueueDocument integrationFlowQueueDocument) {
        final IntegrationFlowQueueData integrationFlowQueueData = integrationFlowQueueDocument
            .getDocument()
            .getIntegrationFlowQueueData();

        integrationFlowQueueData.setProcessingPostponed(false);

        integrationFlowQueueDocumentService.updateDocument(integrationFlowQueueDocument, "enablePostponedFlowMessage");

        log.info(
            "Enabled postponed flow message: integrationFlowQueueDocumentId = {}",
            integrationFlowQueueDocument.getId()
        );
    }
}
