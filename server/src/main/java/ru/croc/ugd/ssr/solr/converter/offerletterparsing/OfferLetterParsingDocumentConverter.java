package ru.croc.ugd.ssr.solr.converter.offerletterparsing;

import static java.util.Optional.of;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.model.offerletterparsing.OfferLetterParsingDocument;
import ru.croc.ugd.ssr.offerletterparsing.OfferLetterParsing;
import ru.croc.ugd.ssr.offerletterparsing.OfferLetterParsingType;
import ru.croc.ugd.ssr.solr.UgdSsrOfferLetterParsing;
import ru.croc.ugd.ssr.solr.converter.SolrDocumentConversionException;
import ru.croc.ugd.ssr.solr.SsrDocumentConverter;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.reinform.cdp.document.model.DocumentType;

/**
 * OfferLetterParsingDocumentConverter
 */
@Service
@AllArgsConstructor
public class OfferLetterParsingDocumentConverter
    extends SsrDocumentConverter<OfferLetterParsingDocument, UgdSsrOfferLetterParsing> {

    private final SolrOfferLetterParsingMapper solrOfferLetterParsingMapper;

    @NotNull
    @Override
    public DocumentType<OfferLetterParsingDocument> getDocumentType() {
        return SsrDocumentTypes.OFFER_LETTER_PARSING;
    }

    @NotNull
    @Override
    public UgdSsrOfferLetterParsing convertDocument(
        @NotNull final OfferLetterParsingDocument offerLetterParsingDocument
    ) {
        final UgdSsrOfferLetterParsing ugdSsrOfferLetterParsing = createDocument(
            getAnyAccessType(), offerLetterParsingDocument.getId()
        );

        final OfferLetterParsingType offerLetterParsing = of(offerLetterParsingDocument.getDocument())
            .map(OfferLetterParsing::getOfferLetterParsingData)
            .orElseThrow(() -> new SolrDocumentConversionException(offerLetterParsingDocument.getId()));

        return solrOfferLetterParsingMapper.toUgdSsrOfferLetterParsing(ugdSsrOfferLetterParsing, offerLetterParsing);
    }
}
