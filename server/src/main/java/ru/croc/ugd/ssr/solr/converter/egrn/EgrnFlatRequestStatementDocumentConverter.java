package ru.croc.ugd.ssr.solr.converter.egrn;

import static java.util.Optional.of;
import static java.util.Optional.ofNullable;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.egrn.EgrnFlatRequest;
import ru.croc.ugd.ssr.egrn.EgrnFlatRequestData;
import ru.croc.ugd.ssr.egrn.FlatEgrnResponse;
import ru.croc.ugd.ssr.model.egrn.EgrnFlatRequestDocument;
import ru.croc.ugd.ssr.solr.UgdSsrEgrnFlatRequestStatement;
import ru.croc.ugd.ssr.solr.converter.SolrDocumentConversionException;
import ru.croc.ugd.ssr.solr.SsrDocumentConverter;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.reinform.cdp.document.model.DocumentType;

/**
 * EgrnFlatRequestStatementDocumentConverter
 */
@Service
@AllArgsConstructor
public class EgrnFlatRequestStatementDocumentConverter
    extends SsrDocumentConverter<EgrnFlatRequestDocument, UgdSsrEgrnFlatRequestStatement> {

    private static final String EGRN_FLAT_REQUEST_STATEMENT = "EGRN-FLAT-REQUEST-STATEMENT";

    private final SolrEgrnFlatRequestStatementMapper solrEgrnFlatRequestStatementMapper;

    @NotNull
    @Override
    public DocumentType<EgrnFlatRequestDocument> getDocumentType() {
        return SsrDocumentTypes.EGRN_FLAT_REQUEST;
    }

    @NotNull
    @Override
    public UgdSsrEgrnFlatRequestStatement convertDocument(
        @NotNull final EgrnFlatRequestDocument egrnFlatRequestDocument
    ) {
        final UgdSsrEgrnFlatRequestStatement ugdSsrEgrnFlatRequestStatement = createDocument(
            getAnyAccessType(), egrnFlatRequestDocument.getId()
        );

        final EgrnFlatRequestData egrnFlatRequestData = of(egrnFlatRequestDocument.getDocument())
            .map(EgrnFlatRequest::getEgrnFlatRequestData)
            .orElseThrow(() -> new SolrDocumentConversionException(egrnFlatRequestDocument.getId()));

        return solrEgrnFlatRequestStatementMapper.toUgdSsrEgrnFlatRequestStatement(
            ugdSsrEgrnFlatRequestStatement,
            ofNullable(egrnFlatRequestData.getEgrnResponse())
                .map(FlatEgrnResponse::getExtractAboutPropertyRoom)
                .orElse(null),
            egrnFlatRequestData
        );
    }

    @Override
    public String getDocumentTypeCode() {
        return EGRN_FLAT_REQUEST_STATEMENT;
    }
}
