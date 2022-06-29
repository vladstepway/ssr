package ru.croc.ugd.ssr.solr;

import org.jetbrains.annotations.NotNull;
import ru.reinform.cdp.document.converter.search.DocumentConverter;
import ru.reinform.cdp.document.model.DocumentAbstract;
import ru.reinform.cdp.search.model.document.SolrDocument;
import ru.reinform.cdp.utils.core.RIExceptionUtils;

import javax.annotation.Nonnull;

public abstract class SsrDocumentConverter<T extends DocumentAbstract, V extends SolrDocument>
    extends DocumentConverter<T, V> {

    public String getDocumentTypeCode() {
        return getDocumentType().getCode();
    }

    @NotNull
    @Override
    public String getAnyAccessType() {
        return this.appIdentity.completeDocumentTypeCode(getDocumentTypeCode()) + "_ANY";
    }

    @Nonnull
    @Override
    public V createDocument(@Nonnull final String accessType, @Nonnull final String id) {
        try {
            V solrDocument = this.getSolrDocumentClass().newInstance();
            solrDocument.setSysDocumentId(id);
            solrDocument.setSysType(this.appIdentity.completeDocumentTypeCode(getDocumentTypeCode()));
            solrDocument.setSysAccessTypes(accessType);
            return solrDocument;
        } catch (IllegalAccessException | InstantiationException var4) {
            throw RIExceptionUtils.wrap(
                var4,
                "error on {0} (accessType: {1}, id: {2})",
                new Object[]{RIExceptionUtils.method(), accessType, id}
            ).withUserMessage("Ошибка создания solr-документа");
        }
    }
}
