package ru.croc.ugd.ssr.solr.converter.notary;

import static java.util.Optional.of;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.model.notary.NotaryDocument;
import ru.croc.ugd.ssr.notary.Notary;
import ru.croc.ugd.ssr.notary.NotaryType;
import ru.croc.ugd.ssr.solr.UgdSsrNotary;
import ru.croc.ugd.ssr.solr.converter.SolrDocumentConversionException;
import ru.croc.ugd.ssr.solr.SsrDocumentConverter;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.reinform.cdp.document.model.DocumentType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * NotaryDocumentConverter.
 */
@Service
@AllArgsConstructor
public class NotaryDocumentConverter extends SsrDocumentConverter<NotaryDocument, UgdSsrNotary> {

    private final SolrNotaryMapper solrNotaryMapper;

    @NotNull
    @Override
    public DocumentType<NotaryDocument> getDocumentType() {
        return SsrDocumentTypes.NOTARY;
    }

    @NotNull
    @Override
    public UgdSsrNotary convertDocument(
        @NotNull final NotaryDocument notaryDocument) {
        final UgdSsrNotary ugdSsrNotary = createDocument(getAnyAccessType(), notaryDocument.getId());

        final NotaryType notary =
            of(notaryDocument.getDocument())
                .map(Notary::getNotaryData)
                .orElseThrow(() -> new SolrDocumentConversionException(notaryDocument.getId()));

        final List<String> notaryEmployeeLogins = of(notaryDocument.getDocument())
            .map(Notary::getNotaryData)
            .map(NotaryType::getEmployeeLogin)
            .map(Arrays::asList)
            .orElse(Collections.emptyList());

        return solrNotaryMapper.toUgdSsrNotary(ugdSsrNotary, notary, notaryEmployeeLogins);
    }

}
