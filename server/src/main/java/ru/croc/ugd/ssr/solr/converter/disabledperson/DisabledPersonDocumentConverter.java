package ru.croc.ugd.ssr.solr.converter.disabledperson;

import static java.util.Optional.of;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.disabledperson.DisabledPerson;
import ru.croc.ugd.ssr.disabledperson.DisabledPersonData;
import ru.croc.ugd.ssr.model.disabledperson.DisabledPersonDocument;
import ru.croc.ugd.ssr.solr.SsrDocumentConverter;
import ru.croc.ugd.ssr.solr.UgdSsrDisabledPerson;
import ru.croc.ugd.ssr.solr.converter.SolrDocumentConversionException;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.reinform.cdp.document.model.DocumentType;

/**
 * DisabledPersonDocumentConverter.
 */
@Service
@AllArgsConstructor
public class DisabledPersonDocumentConverter
    extends SsrDocumentConverter<DisabledPersonDocument, UgdSsrDisabledPerson> {

    private final SolrDisabledPersonMapper solrDisabledPersonMapper;

    @NotNull
    @Override
    public DocumentType<DisabledPersonDocument> getDocumentType() {
        return SsrDocumentTypes.DISABLED_PERSON;
    }

    @NotNull
    @Override
    public UgdSsrDisabledPerson convertDocument(@NotNull final DisabledPersonDocument disabledPersonDocument) {
        final UgdSsrDisabledPerson ugdSsrDisabledPerson = createDocument(
            getAnyAccessType(), disabledPersonDocument.getId()
        );

        final DisabledPersonData disabledPersonData = of(disabledPersonDocument.getDocument())
            .map(DisabledPerson::getDisabledPersonData)
            .orElseThrow(() -> new SolrDocumentConversionException(disabledPersonDocument.getId()));

        return solrDisabledPersonMapper.toUgdSsrDisabledPerson(ugdSsrDisabledPerson, disabledPersonData);
    }
}
