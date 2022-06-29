package ru.croc.ugd.ssr.solr.converter.personaldocument;

import static java.util.Optional.of;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.model.personaldocument.PersonalDocumentDocument;
import ru.croc.ugd.ssr.personaldocument.PersonalDocument;
import ru.croc.ugd.ssr.personaldocument.PersonalDocumentType;
import ru.croc.ugd.ssr.solr.UgdSsrPersonalDocument;
import ru.croc.ugd.ssr.solr.converter.SolrDocumentConversionException;
import ru.croc.ugd.ssr.solr.SsrDocumentConverter;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.reinform.cdp.document.model.DocumentType;

/**
 * PersonalDocumentDocumentConverter
 */
@Service
@AllArgsConstructor
public class PersonalDocumentDocumentConverter
    extends SsrDocumentConverter<PersonalDocumentDocument, UgdSsrPersonalDocument> {

    private final SolrPersonalDocumentMapper solrPersonalDocumentMapper;

    @NotNull
    @Override
    public DocumentType<PersonalDocumentDocument> getDocumentType() {
        return SsrDocumentTypes.PERSONAL_DOCUMENT;
    }

    @NotNull
    @Override
    public UgdSsrPersonalDocument convertDocument(
        @NotNull final PersonalDocumentDocument personalDocumentDocument
    ) {
        final UgdSsrPersonalDocument ugdSsrPersonalDocument = createDocument(
            getAnyAccessType(), personalDocumentDocument.getId()
        );

        final PersonalDocumentType personalDocument = of(personalDocumentDocument.getDocument())
            .map(PersonalDocument::getPersonalDocumentData)
            .orElseThrow(() -> new SolrDocumentConversionException(personalDocumentDocument.getId()));

        return solrPersonalDocumentMapper.toUgdSsrPersonalDocument(ugdSsrPersonalDocument, personalDocument);
    }
}
