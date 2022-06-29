package ru.croc.ugd.ssr.solr.converter.ssrcco;

import static java.util.Optional.of;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.model.ssrcco.SsrCcoDocument;
import ru.croc.ugd.ssr.solr.UgdSsrSsrCco;
import ru.croc.ugd.ssr.solr.converter.SolrDocumentConversionException;
import ru.croc.ugd.ssr.solr.SsrDocumentConverter;
import ru.croc.ugd.ssr.ssrcco.SsrCco;
import ru.croc.ugd.ssr.ssrcco.SsrCcoData;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.reinform.cdp.document.model.DocumentType;

/**
 * SsrCcoDocumentConverter.
 */
@Service
@AllArgsConstructor
public class SsrCcoDocumentConverter extends SsrDocumentConverter<SsrCcoDocument, UgdSsrSsrCco> {

    private final SolrSsrCcoMapper solrSsrCcoMapper;

    @NotNull
    @Override
    public DocumentType<SsrCcoDocument> getDocumentType() {
        return SsrDocumentTypes.SSR_CCO;
    }

    @NotNull
    @Override
    public UgdSsrSsrCco convertDocument(@NotNull final SsrCcoDocument ssrCcoDocument) {
        final UgdSsrSsrCco ugdSsrSsrCco = createDocument(getAnyAccessType(), ssrCcoDocument.getId());

        final SsrCcoData ssrCcoData = of(ssrCcoDocument.getDocument())
            .map(SsrCco::getSsrCcoData)
            .orElseThrow(() -> new SolrDocumentConversionException(ssrCcoDocument.getId()));

        return solrSsrCcoMapper.toUgdSsrSsrCco(ugdSsrSsrCco, ssrCcoData);
    }

}
