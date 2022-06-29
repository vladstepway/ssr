package ru.croc.ugd.ssr.solr.converter.personaldocument;

import static java.util.Optional.of;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.model.personaldocument.PersonalDocumentApplicationDocument;
import ru.croc.ugd.ssr.personaldocument.PersonalDocumentApplication;
import ru.croc.ugd.ssr.personaldocument.PersonalDocumentApplicationType;
import ru.croc.ugd.ssr.solr.UgdSsrPersonalDocumentApplication;
import ru.croc.ugd.ssr.solr.converter.SolrDocumentConversionException;
import ru.croc.ugd.ssr.solr.SsrDocumentConverter;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.reinform.cdp.document.model.DocumentType;

/**
 * PersonalDocumentApplicationDocumentConverter
 */
@Service
@AllArgsConstructor
public class PersonalDocumentApplicationDocumentConverter
    extends SsrDocumentConverter<PersonalDocumentApplicationDocument, UgdSsrPersonalDocumentApplication> {

    private final SolrPersonalDocumentApplicationMapper solrPersonalDocumentApplicationMapper;

    @NotNull
    @Override
    public DocumentType<PersonalDocumentApplicationDocument> getDocumentType() {
        return SsrDocumentTypes.PERSONAL_DOCUMENT_APPLICATION;
    }

    @NotNull
    @Override
    public UgdSsrPersonalDocumentApplication convertDocument(
        @NotNull final PersonalDocumentApplicationDocument personalDocumentApplicationDocument
    ) {
        final UgdSsrPersonalDocumentApplication ugdSsrPersonalDocumentApplication = createDocument(
            getAnyAccessType(), personalDocumentApplicationDocument.getId()
        );

        final PersonalDocumentApplicationType personalDocumentApplication =
            of(personalDocumentApplicationDocument.getDocument())
                .map(PersonalDocumentApplication::getPersonalDocumentApplicationData)
                .orElseThrow(() -> new SolrDocumentConversionException(personalDocumentApplicationDocument.getId()));

        return solrPersonalDocumentApplicationMapper.toUgdSsrPersonalDocumentApplication(
            ugdSsrPersonalDocumentApplication, personalDocumentApplication
        );
    }
}
