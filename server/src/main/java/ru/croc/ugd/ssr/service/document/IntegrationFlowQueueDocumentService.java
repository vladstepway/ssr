package ru.croc.ugd.ssr.service.document;

import static java.util.Optional.ofNullable;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.db.dao.IntegrationFlowQueueDao;
import ru.croc.ugd.ssr.integrationflow.IntegrationFlowType;
import ru.croc.ugd.ssr.model.integrationflow.IntegrationFlowQueueDocument;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.reinform.cdp.db.model.DocumentData;
import ru.reinform.cdp.document.model.DocumentType;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class IntegrationFlowQueueDocumentService extends SsrAbstractDocumentService<IntegrationFlowQueueDocument> {

    private final IntegrationFlowQueueDao integrationFlowQueueDao;

    @NotNull
    @Override
    public DocumentType<IntegrationFlowQueueDocument> getDocumentType() {
        return SsrDocumentTypes.INTEGRATION_FLOW_QUEUE;
    }

    public Optional<IntegrationFlowQueueDocument> fetchByEno(final String eno) {
        final List<DocumentData> integrationFlowQueueDocuments = integrationFlowQueueDao
            .fetchByEnoList(Collections.singletonList(eno));

        if (integrationFlowQueueDocuments.size() > 1) {
            log.warn("More than one integration flow queue document have been found: eno = {}", eno);
        }
        return integrationFlowQueueDocuments.stream()
            .map(this::parseDocumentData)
            .findFirst();
    }

    public List<IntegrationFlowQueueDocument> fetchByEnoList(final List<String> enoList) {
        return integrationFlowQueueDao.fetchByEnoList(enoList)
            .stream()
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
    }

    public List<IntegrationFlowQueueDocument> fetchNotPostponedByFlowType(
        final IntegrationFlowType flowType
    ) {
        final String flowTypeString = ofNullable(flowType)
            .map(IntegrationFlowType::value)
            .orElse(null);

        return integrationFlowQueueDao
            .fetchNotPostponedByFlowTypeAndCreatedBefore(
                flowTypeString,
                LocalDateTime.now().minusHours(3)
            )
            .stream()
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
    }

    public List<IntegrationFlowQueueDocument> fetchNotPostponed() {
        return fetchNotPostponedByFlowType(null);
    }
}
