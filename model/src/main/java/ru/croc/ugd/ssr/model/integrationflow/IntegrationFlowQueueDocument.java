package ru.croc.ugd.ssr.model.integrationflow;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import ru.croc.ugd.ssr.integrationflow.IntegrationFlowData;
import ru.croc.ugd.ssr.integrationflow.IntegrationFlowQueue;
import ru.croc.ugd.ssr.integrationflow.IntegrationFlowQueueData;
import ru.reinform.cdp.document.model.DocumentAbstract;

import java.time.LocalDateTime;

public class IntegrationFlowQueueDocument extends DocumentAbstract<IntegrationFlowQueue> {

    @Getter
    @Setter
    @JsonProperty("integrationFlowQueue")
    private IntegrationFlowQueue document;

    @Override
    public String getId() {
        return document.getDocumentID();
    }

    @Override
    public void assignId(String id) {
        document.setDocumentID(id);
    }

    @Override
    public String getFolderId() {
        return document.getFolderGUID();
    }

    @Override
    public void assignFolderId(String id) {
        document.setFolderGUID(id);
    }

    public static IntegrationFlowQueueDocument of(
        final IntegrationFlowDocument integrationFlowDocument,
        final boolean processingPostponed,
        final String enoMessage
    ) {
        final IntegrationFlowQueueDocument integrationFlowQueueDocument = new IntegrationFlowQueueDocument();

        final IntegrationFlowQueue integrationFlowQueue = new IntegrationFlowQueue();
        integrationFlowQueueDocument.setDocument(integrationFlowQueue);

        final IntegrationFlowData integrationFlowData = integrationFlowDocument.getDocument().getIntegrationFlowData();

        final IntegrationFlowQueueData integrationFlowQueueData = new IntegrationFlowQueueData();
        integrationFlowQueueData.setIntegrationFlowDocumentId(integrationFlowDocument.getId());
        integrationFlowQueueData.setEno(integrationFlowData.getEno());
        integrationFlowQueueData.setFlowType(integrationFlowData.getFlowType());
        integrationFlowQueueData.setEnoMessage(enoMessage);
        integrationFlowQueueData.setProcessingPostponed(processingPostponed);
        integrationFlowQueueData.setCreatedAt(LocalDateTime.now());

        integrationFlowQueue.setIntegrationFlowQueueData(integrationFlowQueueData);

        return integrationFlowQueueDocument;
    }
}
