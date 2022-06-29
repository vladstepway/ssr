package ru.croc.ugd.ssr.service.document;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.db.dao.IntegrationFlowDao;
import ru.croc.ugd.ssr.model.integrationflow.IntegrationFlowDocument;
import ru.croc.ugd.ssr.service.DocumentWithFolder;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.reinform.cdp.db.model.DocumentData;
import ru.reinform.cdp.document.model.DocumentType;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class IntegrationFlowDocumentService extends DocumentWithFolder<IntegrationFlowDocument> {

    private final IntegrationFlowDao integrationFlowDao;

    @NotNull
    @Override
    public DocumentType<IntegrationFlowDocument> getDocumentType() {
        return SsrDocumentTypes.INTEGRATION_FLOW;
    }

    public Optional<IntegrationFlowDocument> fetchByEno(final String eno) {
        final List<DocumentData> integrationFlowDocuments = integrationFlowDao.fetchByEno(eno);

        if (integrationFlowDocuments.size() > 1) {
            log.warn("More than one integration flow document have been found: eno = {}", eno);
        }
        return integrationFlowDocuments.stream()
            .map(this::parseDocumentData)
            .findFirst();
    }

    public boolean existsSentMfrFlow(
        final String flowType,
        final String affairId,
        final String contractOrderId,
        final String contractStatus,
        final String contractProjectStatus,
        final String letterId
    ) {
        return integrationFlowDao.existsSentMfrFlow(
            flowType, affairId, contractOrderId, contractStatus, contractProjectStatus, letterId
        );
    }
}
