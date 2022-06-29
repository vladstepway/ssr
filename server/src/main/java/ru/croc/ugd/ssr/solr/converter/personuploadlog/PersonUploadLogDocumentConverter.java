package ru.croc.ugd.ssr.solr.converter.personuploadlog;

import static java.util.Optional.of;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.model.personuploadlog.PersonUploadLogDocument;
import ru.croc.ugd.ssr.personuploadlog.PersonUploadLog;
import ru.croc.ugd.ssr.personuploadlog.PersonUploadLogData;
import ru.croc.ugd.ssr.solr.UgdSsrPersonUploadLog;
import ru.croc.ugd.ssr.solr.converter.SolrDocumentConversionException;
import ru.croc.ugd.ssr.solr.SsrDocumentConverter;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.reinform.cdp.document.model.DocumentType;


/**
 * PersonUploadLogDocumentConverter.
 */
@Service
@RequiredArgsConstructor
public class PersonUploadLogDocumentConverter
    extends SsrDocumentConverter<PersonUploadLogDocument, UgdSsrPersonUploadLog> {

    private final SolrPersonUploadLogMapper solrPersonUploadLogMapper;

    @NotNull
    @Override
    public DocumentType<PersonUploadLogDocument> getDocumentType() {
        return SsrDocumentTypes.PERSON_UPLOAD_LOG;
    }

    @NotNull
    @Override
    public UgdSsrPersonUploadLog convertDocument(
        @NotNull final PersonUploadLogDocument personUploadLogDocument
    ) {
        final UgdSsrPersonUploadLog ugdSsrPersonUploadLog
            = createDocument(getAnyAccessType(), personUploadLogDocument.getId());

        final PersonUploadLogData personUploadLogData =
            of(personUploadLogDocument.getDocument())
                .map(PersonUploadLog::getPersonUploadLogData)
                .orElseThrow(() -> new SolrDocumentConversionException(personUploadLogDocument.getId()));

        return solrPersonUploadLogMapper.toUgdSsrPersonUploadLog(ugdSsrPersonUploadLog, personUploadLogData);
    }

}
