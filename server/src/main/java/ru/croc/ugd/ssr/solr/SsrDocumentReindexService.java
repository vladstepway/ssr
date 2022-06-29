package ru.croc.ugd.ssr.solr;

import static java.util.Optional.ofNullable;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import ru.reinform.cdp.backend.service.AppIdentity;
import ru.reinform.cdp.document.model.DocumentAbstract;
import ru.reinform.cdp.document.model.DocumentType;
import ru.reinform.cdp.document.service.DocumentsReindexService;
import ru.reinform.cdp.search.model.document.SolrDocument;
import ru.reinform.cdp.search.service.IReindexService;
import ru.reinform.cdp.utils.core.RIExceptionUtils;
import ru.reinform.cdp.utils.core.RIStringUtils;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;

@Slf4j
@Primary
@Component
public class SsrDocumentReindexService extends DocumentsReindexService {
    @Autowired
    private IReindexService reindexService;
    @Autowired
    private AppIdentity appIdentity;
    @Autowired(
        required = false
    )
    private Collection<SsrDocumentConverter> converters;

    @Override
    public <T extends DocumentAbstract> void reindexDocumentsInSolr(
        @Nonnull final DocumentType<T> documentType, @Nonnull final List<T> documents
    ) {
        try {
            final List<SsrDocumentConverter> converters = getConverters(documentType);
            if (!converters.isEmpty()) {
                converters.stream()
                    .unordered()
                    .parallel()
                    .forEach(converter -> convertAndUpdateSolrDocument(converter, documents));
            } else {
                log.debug(
                    "Converter for document type {} not found, skipping reindex process...", documentType.getCode()
                );
            }
        } catch (Exception var6) {
            throw RIExceptionUtils.wrap(
                    var6,
                    "error on {0} (documentType: {1}, documents.size: {2})",
                    RIExceptionUtils.method(),
                    RIStringUtils.objToString(documentType),
                    documents == null ? 0 : documents.size()
                )
                .withUserMessage("Ошибка переиндексации документов");
        }
    }

    private <T extends DocumentAbstract> void convertAndUpdateSolrDocument(
        final SsrDocumentConverter converter, final List<T> documents
    ) {
        final String completeDocumentTypeCode = appIdentity.completeDocumentTypeCode(converter.getDocumentTypeCode());
        final List<SolrDocument> solrDocuments = documents.stream()
            .unordered()
            .parallel()
            .map((Function<T, SolrDocument>) converter::convertDocument)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(solrDocuments)) {
            log.debug("sending {} documents to solr", solrDocuments.size());
            reindexService.update(completeDocumentTypeCode, solrDocuments);
        }
    }

    @Override
    public void deleteDocumentsInSolr(@Nonnull final DocumentType documentType, @Nonnull final List<String> ids) {
        try {
            final List<SsrDocumentConverter> converters = getConverters(documentType);
            if (!converters.isEmpty()) {
                converters.forEach(converter -> {
                    final String completeDocumentTypeCode =
                        appIdentity.completeDocumentTypeCode(converter.getDocumentTypeCode());
                    reindexService.deleteDocumentsByTypeAndIds(completeDocumentTypeCode, ids);
                });
            } else {
                log.debug(
                    "Converter for document type {} not found, skipping document deletion...", documentType.getCode()
                );
            }

        } catch (Exception var4) {
            throw RIExceptionUtils.wrap(var4, "error on {0} (documentType: {1}, ids.size: {2})",
                    RIExceptionUtils.method(),
                    RIStringUtils.objToString(documentType),
                    ids == null ? 0 : ids.size())
                .withUserMessage("Ошибка удаления документов из индекса");
        }
    }

    private List<SsrDocumentConverter> getConverters(@Nonnull final DocumentType documentType) {
        return ofNullable(converters)
            .map(Collection::stream)
            .orElse(Stream.empty())
            .filter(converter -> converter.getDocumentType().equals(documentType))
            .collect(Collectors.toList());
    }

    public boolean hasConverter(@Nonnull final DocumentType documentType) {
        return !getConverters(documentType).isEmpty();
    }
}
