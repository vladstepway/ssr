package ru.croc.ugd.ssr.service.integrationflow;

import static java.util.Objects.nonNull;
import static ru.croc.ugd.ssr.integrationflow.IntegrationFlowType.DGP_TO_MFR_ADMINISTRATIVE_DOCUMENTS_INFO;
import static ru.croc.ugd.ssr.integrationflow.IntegrationFlowType.DGP_TO_MFR_CONTRACT_INFO;
import static ru.croc.ugd.ssr.integrationflow.IntegrationFlowType.DGP_TO_MFR_CONTRACT_PROJECT_INFO;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.dto.mqetpmv.flow.FlowTypeDto;
import ru.croc.ugd.ssr.integration.util.XmlUtils;
import ru.croc.ugd.ssr.integrationflow.IntegrationFlowData;
import ru.croc.ugd.ssr.integrationflow.IntegrationFlowStatus;
import ru.croc.ugd.ssr.integrationflow.MfrFlowData;
import ru.croc.ugd.ssr.integrationflow.StatusData;
import ru.croc.ugd.ssr.mapper.IntegrationFlowMapper;
import ru.croc.ugd.ssr.model.integrationflow.IntegrationFlowDocument;
import ru.croc.ugd.ssr.service.document.IntegrationFlowDocumentService;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Objects;

@Slf4j
@Service
@AllArgsConstructor
public class DefaultIntegrationFlowService implements IntegrationFlowService {

    private final IntegrationFlowDocumentService integrationFlowDocumentService;
    private final IntegrationFlowMapper integrationFlowMapper;
    private final XmlUtils xmlUtils;

    @Override
    public IntegrationFlowDocument createDocument(
        final String eno,
        final FlowTypeDto flowTypeDto,
        final String message,
        final IntegrationFlowStatus integrationFlowStatus
    ) {
        return createDocument(eno, flowTypeDto, message, integrationFlowStatus, null);
    }

    @Override
    public IntegrationFlowDocument createDocument(
        final String eno,
        final FlowTypeDto flowTypeDto,
        final String message,
        final IntegrationFlowStatus integrationFlowStatus,
        final MfrFlowData mfrFlowData
    ) {
        final IntegrationFlowDocument integrationFlowDocument = integrationFlowMapper.toIntegrationFlowDocument(
            eno, flowTypeDto, integrationFlowStatus, mfrFlowData
        );
        integrationFlowDocumentService.createDocument(integrationFlowDocument, true, "createDocument");

        return saveMessage(integrationFlowDocument, message);
    }

    private IntegrationFlowDocument saveMessage(
        final IntegrationFlowDocument integrationFlowDocument, final String message
    ) {
        final String fileStoreId = xmlUtils.saveXmlToAlfresco(message, integrationFlowDocument.getFolderId());
        integrationFlowDocument.getDocument().getIntegrationFlowData().setFileStoreId(fileStoreId);
        return integrationFlowDocumentService.updateDocument(integrationFlowDocument, "saveMessage");
    }

    @Override
    public void saveReceivedStatus(
        final String eno, final String message, final String statusCode, final String statusTitle
    ) {
        final IntegrationFlowDocument integrationFlowDocument = integrationFlowDocumentService.fetchByEno(eno)
            .orElse(null);
        if (nonNull(integrationFlowDocument)) {
            final String fileStoreId = xmlUtils.saveXmlToAlfresco(message, integrationFlowDocument.getFolderId());
            final StatusData statusData = integrationFlowMapper.toStatusData(
                fileStoreId, statusCode, statusTitle, IntegrationFlowStatus.RECEIVED
            );
            final IntegrationFlowData integrationFlowData = integrationFlowDocument.getDocument()
                .getIntegrationFlowData();
            integrationFlowData.setFlowStatus(IntegrationFlowStatus.PROCESSED);
            integrationFlowData.getStatuses().add(statusData);
            integrationFlowDocumentService.updateDocument(integrationFlowDocument, "saveReceivedStatus");
        } else {
            log.error("Integration flow document not found by eno = {}", eno);
        }
    }

    @Override
    public void savePreparedStatus(
        final IntegrationFlowDocument integrationFlowDocument,
        final String message,
        final String statusCode,
        final String statusTitle
    ) {
        final String fileStoreId = xmlUtils.saveXmlToAlfresco(message, integrationFlowDocument.getFolderId());
        final StatusData statusData = integrationFlowMapper.toStatusData(
            fileStoreId, statusCode, statusTitle, IntegrationFlowStatus.PREPARED
        );
        final IntegrationFlowData integrationFlowData = integrationFlowDocument.getDocument()
            .getIntegrationFlowData();
        integrationFlowData.getStatuses().add(statusData);
        integrationFlowDocumentService.updateDocument(integrationFlowDocument, "savePreparedStatus");
    }

    @Override
    public boolean existsSentDgpToMfrContractProjectInfoFlow(
        final String affairId, final String contractOrderId, final String contractProjectStatus
    ) {
        return integrationFlowDocumentService.existsSentMfrFlow(
            DGP_TO_MFR_CONTRACT_PROJECT_INFO.value(), affairId, contractOrderId, null, contractProjectStatus, null
        );
    }

    @Override
    public boolean existsSentDgpToMfrContractInfoFlow(
        final String affairId, final String contractOrderId, final String contractStatus
    ) {
        return integrationFlowDocumentService.existsSentMfrFlow(
            DGP_TO_MFR_CONTRACT_INFO.value(), affairId, contractOrderId, contractStatus, null, null
        );
    }

    @Override
    public boolean existsSentDgpToMfrAdministrativeDocumentsInfoFlow(final String affairId, final String letterId) {
        return integrationFlowDocumentService.existsSentMfrFlow(
            DGP_TO_MFR_ADMINISTRATIVE_DOCUMENTS_INFO.value(), affairId, null, null, null, letterId
        );
    }

    @Override
    public void updateSentDocument(final IntegrationFlowDocument integrationFlowDocument, final String etpMessageId) {
        final IntegrationFlowData integrationFlowData = integrationFlowDocument.getDocument().getIntegrationFlowData();
        integrationFlowData.setFlowStatus(IntegrationFlowStatus.SENT);
        integrationFlowData.setEtpMessageId(etpMessageId);
        integrationFlowDocumentService.updateDocument(integrationFlowDocument, "updateSentDocument");
    }

    @Override
    public void updateDocumentWithSentStatus(
        final IntegrationFlowDocument integrationFlowDocument, final String statusCode, final String etpMessageId
    ) {
        final IntegrationFlowData integrationFlowData = integrationFlowDocument.getDocument().getIntegrationFlowData();
        integrationFlowData.getStatuses()
            .stream()
            .filter(statusData -> Objects.equals(statusData.getStatusCode(), statusCode))
            .sorted(Comparator.comparing(StatusData::getCreatedAt, Comparator.nullsFirst(LocalDateTime::compareTo)))
            .reduce((s1, s2) -> s2)
            .ifPresent(statusData -> updateDocumentWithSentStatus(integrationFlowDocument, statusData, etpMessageId));
    }

    private void updateDocumentWithSentStatus(
        final IntegrationFlowDocument integrationFlowDocument, final StatusData statusData, final String etpMessageId
    ) {
        statusData.setFlowStatus(IntegrationFlowStatus.SENT);
        statusData.setEtpMessageId(etpMessageId);
        integrationFlowDocumentService.updateDocument(integrationFlowDocument, "updateDocumentWithSentStatus");
    }
}
