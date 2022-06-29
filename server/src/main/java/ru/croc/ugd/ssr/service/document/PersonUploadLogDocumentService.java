package ru.croc.ugd.ssr.service.document;

import static java.util.Optional.ofNullable;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.db.dao.PersonUploadLogDao;
import ru.croc.ugd.ssr.exception.personuploadlog.InvalidPersonUploadLog;
import ru.croc.ugd.ssr.model.personuploadlog.PersonUploadLogDocument;
import ru.croc.ugd.ssr.personuploadlog.PersonUploadLog;
import ru.croc.ugd.ssr.personuploadlog.PersonUploadLogData;
import ru.croc.ugd.ssr.personuploadlog.PersonUploadLogStatus;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.reinform.cdp.document.model.DocumentType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * PersonUploadLogDocumentService.
 */
@Slf4j
@Service
@AllArgsConstructor
public class PersonUploadLogDocumentService extends SsrAbstractDocumentService<PersonUploadLogDocument> {
    private final PersonUploadLogDao personUploadLogDao;

    @NotNull
    @Override
    public DocumentType<PersonUploadLogDocument> getDocumentType() {
        return SsrDocumentTypes.PERSON_UPLOAD_LOG;
    }

    /**
     * Save log when person upload started.
     *
     * @param fileId file id
     * @param filename file name
     * @param username username
     * @return personUploadLogDocument
     */
    public PersonUploadLogDocument startPersonUpload(
        final String fileId,
        final String filename,
        final String username
    ) {
        final PersonUploadLogData personUploadLogData = new PersonUploadLogData();
        personUploadLogData.setFileId(fileId);
        personUploadLogData.setFilename(filename);
        personUploadLogData.setUsername(username);
        personUploadLogData.setStartDateTime(LocalDateTime.now());
        personUploadLogData.setStatus(PersonUploadLogStatus.IN_PROGRESS);

        final PersonUploadLog personUploadLog = new PersonUploadLog();
        personUploadLog.setPersonUploadLogData(personUploadLogData);

        final PersonUploadLogDocument document = new PersonUploadLogDocument();
        document.setDocument(personUploadLog);

        return createDocument(document, true, null);
    }

    /**
     * Update log when person upload ended.
     *
     * @param id log id
     * @param logFileId logFileId
     * @return personUploadLogDocument
     */
    public PersonUploadLogDocument endPersonUploadLog(
        final String id,
        final String logFileId
    ) {
        final PersonUploadLogDocument personUploadLogDocument =
            populateLogAndEndDate(id, logFileId);
        personUploadLogDocument.getDocument()
            .getPersonUploadLogData()
            .setStatus(PersonUploadLogStatus.UPLOADED);
        return updatePersonUploadLogDocument(personUploadLogDocument);
    }

    public PersonUploadLogDocument tracePersonUploadLog(
        final String id,
        final String logFileId
    ) {
        final PersonUploadLogDocument personUploadLogDocument =
            populateLogAndEndDate(id, logFileId);
        return updatePersonUploadLogDocument(personUploadLogDocument);
    }

    public PersonUploadLogDocument updatePersonUploadLogDocument(
        final PersonUploadLogDocument personUploadLogDocument
    ) {
        return updateDocument(
            personUploadLogDocument.getId(),
            personUploadLogDocument,
            true,
            true,
            null);
    }

    private PersonUploadLogDocument populateLogAndEndDate(
        final String id,
        final String logFileId
    ) {
        final PersonUploadLogDocument personUploadLogDocument = fetchDocument(id);

        final PersonUploadLogData updatedLog =
            ofNullable(personUploadLogDocument)
                .map(PersonUploadLogDocument::getDocument)
                .map(PersonUploadLog::getPersonUploadLogData)
                .orElseThrow(InvalidPersonUploadLog::new);
        updatedLog.setEndDateTime(LocalDateTime.now());
        updatedLog.setLogFileId(logFileId);
        return personUploadLogDocument;
    }

    public List<PersonUploadLogDocument> fetchAllInProgressLogDocuments() {
        return personUploadLogDao.findAllInProgressDocuments()
            .stream()
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
    }
}
