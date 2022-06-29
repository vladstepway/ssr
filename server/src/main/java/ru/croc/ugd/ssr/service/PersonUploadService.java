package ru.croc.ugd.ssr.service;

import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;
import static ru.croc.ugd.ssr.builder.PersonBuilder.retrieveBirthday;
import static ru.croc.ugd.ssr.builder.PersonBuilder.retrieveValueByTagName;
import static ru.croc.ugd.ssr.integration.util.FileUtils.createDir;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.io.inputstream.ZipInputStream;
import net.lingala.zip4j.model.FileHeader;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ru.computel.common.filenet.client.FilenetFileBean;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.builder.PersonBuilder;
import ru.croc.ugd.ssr.config.SystemProperties;
import ru.croc.ugd.ssr.dto.PersonUploadMetadata;
import ru.croc.ugd.ssr.exception.SsrException;
import ru.croc.ugd.ssr.exception.ZipFileParsingException;
import ru.croc.ugd.ssr.integration.service.flows.RemovableStatusUpdateService;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.personuploadlog.PersonUploadLogDocument;
import ru.croc.ugd.ssr.parser.PersonXmlParser;
import ru.croc.ugd.ssr.parser.ZipFileExtractor;
import ru.croc.ugd.ssr.personuploadlog.PersonUploadLogData;
import ru.croc.ugd.ssr.personuploadlog.PersonUploadLogStatus;
import ru.croc.ugd.ssr.service.document.PersonUploadLogDocumentService;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.reinform.cdp.document.service.DocumentDataService;
import ru.reinform.cdp.filestore.model.FilestoreFolderAttrs;
import ru.reinform.cdp.filestore.model.FilestoreSourceRef;
import ru.reinform.cdp.filestore.model.remote.api.CreateFolderRequest;
import ru.reinform.cdp.filestore.service.FilestoreRemoteService;
import ru.reinform.cdp.filestore.service.FilestoreV2RemoteService;
import ru.reinform.cdp.search.service.SearchRemoteService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;

/**
 * PersonUploadService.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PersonUploadService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    private static final String LOG_RECORD_TEMPLATE = "%s - %s - %s \n";
    private static final String START_PERSON_UPLOAD_MSG_TEMPLATE =
        "Пользователем \"%s\" запущен процесс загрузки жителей из файла \"%s\".";
    private static final String END_PERSON_UPLOAD_MSG_TEMPLATE =
        "Загрузка файла с жителями \"%s\" завершена. Загружено: %s записей.";
    private static final String PROCESS_INTERRUPTED_MSG = "Процесс загрузки жителей был прерван.";
    private static final String LOG_FILE_NAME = "%s_%s_logs.txt";
    private static final String SUBSYSTEM_CODE = "UGD_SSR";

    private static final int PERSON_DOCUMENT_CACHE_SIZE = 100;
    private static final Map<String, PersonDocument> PERSON_DOCUMENTS_CACHE = new HashMap<>();

    @Value("${app.filestore.ssr.rootPath}")
    private String rootPath;

    @Value("${ugd.ssr.log.path}")
    private String logPath;

    private final PersonNotificationService personNotificationService;
    private final PersonBuilder personBuilder;
    private final PersonUploadLogDocumentService personUploadLogDocumentService;
    private final DocumentDataService documentDataService;
    private final PersonDocumentService personDocumentService;
    private final SearchRemoteService searchRemoteService;
    private final PersonXmlParser personXmlParser;
    private final ZipFileExtractor zipFileExtractor;
    private final FilestoreRemoteService filestoreRemoteService;
    private final SystemProperties systemProperties;
    private final RemovableStatusUpdateService flatStatus;
    private final FilestoreV2RemoteService remoteService;

    @PostConstruct
    public void recoverInterrupterDocuments() {
        personUploadLogDocumentService.fetchAllInProgressLogDocuments()
            .stream()
            .filter(Objects::nonNull)
            .forEach(this::recoverInterruptedDocument);
    }

    /**
     * Получить список документов из архива загруженного на filestore.
     *
     * @param fileId file id
     * @param password пароль
     * @return список документов
     */
    public List<Document> retrieveDocumentsByFileId(final String fileId, final String password) {
        final byte[] uploadedFileBytes = filestoreRemoteService.getFile(fileId, systemProperties.getSystem());

        try {
            return retrieveDocuments(fileId + ".zip", uploadedFileBytes, password);
        } catch (Exception e) {
            throw new ZipFileParsingException(e);
        }
    }

    /**
     * Получить список документов из архива.
     *
     * @param multipartFile архив жителей
     * @param password пароль
     * @return список документов
     */
    public List<Document> retrieveDocumentsFromZip(final MultipartFile multipartFile, final String password) {
        try {
            return retrieveDocuments(multipartFile.getOriginalFilename(), multipartFile.getBytes(), password);
        } catch (Exception e) {
            throw new ZipFileParsingException(e);
        }
    }

    private List<Document> retrieveDocuments(
        final String fileName, final byte[] fileBytes, final String password
    ) throws IOException {
        final String directoryPath = "./" + fileName.replace(".", "_") + "/";
        createDir(directoryPath);
        final Path filePath = Paths.get(directoryPath + fileName);

        final ZipFile zipFile = zipFileExtractor.parseFile(fileBytes, filePath, password);
        final List<FileHeader> fileHeaders = zipFile.getFileHeaders();
        final List<Document> documents = getDocumentFromArchive(zipFile, fileHeaders);
        FileUtils.deleteDirectory(new File(directoryPath));
        return documents;
    }

    /**
     * Получить документ из архива.
     *
     * @param multipartFile архив жителей
     * @return документ
     */
    public Document retrieveDocumentFromXml(final MultipartFile multipartFile) {
        final Path filepath = Paths.get("./", multipartFile.getOriginalFilename());
        try (final OutputStream os = Files.newOutputStream(filepath)) {
            os.write(multipartFile.getBytes());
            final InputStream is = new FileInputStream(filepath.toString());
            final Document document = personXmlParser.parserPersonXml(is);
            Files.delete(filepath);
            return document;
        } catch (Exception e) {
            throw new ZipFileParsingException(e);
        }
    }

    private List<Document> getDocumentFromArchive(final ZipFile zipFile, final List<FileHeader> fileHeaders)
        throws IOException {
        final List<Document> documents = new ArrayList<>();
        for (final FileHeader fileHeader : fileHeaders) {
            try (final ZipInputStream zis = zipFile.getInputStream(fileHeader)) {
                documents.add(personXmlParser.parserPersonXml(zis));
            }
        }

        return documents;
    }

    /**
     * Process persons from documents.
     * @param fileId file id
     * @param fileName file name
     * @param userName userName
     * @param documents documents
     */
    @Async
    public void processPersonsFromDocuments(
        final String fileId,
        final String fileName,
        final String userName,
        final List<Document> documents
    ) {
        String personUploadLogId = null;
        final String logFileName = String.format(LOG_FILE_NAME, Instant.now().toEpochMilli(), fileName);
        createDir(logPath);
        try (final FileOutputStream fos = new FileOutputStream(logPath + logFileName)) {

            personNotificationService.startPersonUpload(fileName, userName);
            personUploadLogId = ofNullable(personUploadLogDocumentService.startPersonUpload(fileId, fileName, userName))
                .map(PersonUploadLogDocument::getId)
                .orElse(null);

            writeInfoLog(fos, String.format(START_PERSON_UPLOAD_MSG_TEMPLATE, userName, fileName));

            final AtomicInteger counter = new AtomicInteger(0);
            final List<String> exceptions = new ArrayList<>();

            for (Document document : documents) {
                final PersonUploadMetadata metadata = processPersonsFromDocument(
                    fos,
                    document,
                    logFileName,
                    personUploadLogId
                );

                counter.addAndGet(metadata.getCount());
                exceptions.addAll(metadata.getExceptions());
            }

            personNotificationService.endPersonUpload(fileName, counter.get(), exceptions);

            writeInfoLog(fos, String.format(END_PERSON_UPLOAD_MSG_TEMPLATE, fileName, counter));
        } catch (Exception e) {
            log.warn("Unable to upload persons: {}", e.getMessage(), e);
        } finally {
            endPersonUploadLog(logFileName, personUploadLogId);
        }
    }

    private PersonUploadMetadata processPersonsFromDocument(
        final FileOutputStream fos,
        final Document document,
        final String logFileName,
        final String personUploadLogId
    ) {
        final List<String> exceptions = new ArrayList<>();
        final AtomicInteger counter = new AtomicInteger(0);

        try {
            final NodeList nodeList = document.getElementsByTagName("DATA_RECORD");
            if (nodeList == null) {
                return new PersonUploadMetadata(counter.get(), exceptions);
            }
            for (int i = 0; i < nodeList.getLength(); i++) {
                try {
                    writeInfoLog(fos, String.format("Начало обработки записи %s", i + 1));

                    processingNode(fos, nodeList.item(i));
                    counter.incrementAndGet();
                } catch (Exception e) {
                    final String exceptionMessage = String.format("Запись #%d - %s", i + 1, e.getMessage());
                    exceptions.add(exceptionMessage);
                    writeErrorLog(fos, exceptionMessage, e);
                } finally {
                    if (i % 150 == 0) {
                        tracePersonUploadLog(logFileName, personUploadLogId);
                    }
                }
            }
        } finally {
            savePersonDocumentToSolr();
        }
        return new PersonUploadMetadata(counter.get(), exceptions);
    }

    private void processingNode(final FileOutputStream fos, final Node node) {
        if (node == null) {
            return;
        }
        if (node.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
            final Element element = (Element) node;
            final PersonDocument personDocument = createOrUpdatePersonFromDocument(fos, element);
            cachePersonDocument(personDocument);
            updateFlatStatus(element);
        }
    }

    private PersonDocument createOrUpdatePersonFromDocument(final FileOutputStream fos, final Element element) {
        final String personId = retrieveValueByTagName(element, "PERSON_ID")
            .orElse(null);

        final String affairId = retrieveValueByTagName(element, "AFFAIR_ID")
            .orElse(null);

        if (StringUtils.isBlank(personId) || StringUtils.isBlank(affairId)) {
            throw new SsrException(
                String.format(
                    "Невозможно загрузить жителя без personId или affairId (personId=%s, affairId=%s)",
                    personId,
                    affairId
                )
            );
        }

        final String snils = retrieveValueByTagName(element, "SNILS")
            .orElse(null);

        final String lastName = retrieveValueByTagName(element, "LASTNAME")
            .orElse(null);

        final String firstName = retrieveValueByTagName(element, "FIRSTNAME")
            .orElse(null);

        final String middleName = retrieveValueByTagName(element, "MIDDLENAME")
            .orElse(null);

        final String fullName = Stream.of(lastName, firstName, middleName)
            .filter(Objects::nonNull)
            .collect(Collectors.joining(" "));

        final LocalDate birthday = retrieveBirthday(element)
            .orElse(null);

        final String unom = retrieveValueByTagName(element, "SNOS_UNOM")
            .orElse(null);

        final String flatNum = retrieveValueByTagName(element, "SNOS_FLAT_NUM")
            .orElse(null);

        final String flatId = personBuilder.retrieveFlatIdByUnomAndFlatNum(
            ofNullable(unom).map(BigInteger::new).orElse(null),
            flatNum
        ).orElse(null);

        final PersonDocument personDocument =
            ofNullable(affairId)
                .flatMap(existingAffairId -> personDocumentService
                    .fetchOneByPersonIdAndAffairId(personId, existingAffairId)
                )
                .orElseGet(() -> personDocumentService.fetchPersonByPersonIdAndUnomAndFlatId(personId, unom, flatId)
                    .orElseGet(() -> personDocumentService
                        .fetchPersonBySnilsAndUnomAndFlatId(snils, unom, flatId)
                        .orElseGet(() -> ofNullable(birthday)
                            .flatMap(bday -> personDocumentService
                                .fetchPersonByFullNameAndBirthdayAndUnomAndFlatId(fullName, bday, unom, flatId))
                            .orElseGet(() -> personDocumentService
                                .fetchPersonByFullNameAndUnomAndFlatId(fullName, unom, flatId)
                                .orElseGet(() -> personDocumentService
                                    .fetchPersonByFullNameAndUnom(fullName, unom)
                                    .orElse(null)
                                )
                            )
                        )
                    )
                );
        return ofNullable(personDocument)
            .map(document -> updatePersonDocument(fos, element, document))
            .orElseGet(() -> createPersonDocument(fos, element));
    }

    private void updateFlatStatus(final Element element) {
        final String flatNumber = retrieveValueByTagName(element, "APART_NUM_START").orElse(null);
        final String unom = retrieveValueByTagName(element, "UNOM_START").orElse(null);
        final String cadNum = retrieveValueByTagName(element, "CADR_NUM_START").orElse(null);

        flatStatus.updateRemovalStatus(flatNumber, new ArrayList<>(), unom, cadNum);
    }

    private PersonDocument updatePersonDocument(
        final FileOutputStream fos,
        final Element element,
        final PersonDocument personDocument
    ) {
        final PersonDocument updatedPersonDocument =
            personBuilder.updatePersonDocument(personDocument, element);
        final PersonDocument savedPersonDocument =
            personDocumentService.updateDocument(
                updatedPersonDocument.getId(),
                updatedPersonDocument,
                true,
                false,
                null);

        final String updatePersonMessage = String.format(
            "Обновлен житель с personId: %s; documentId: %s",
            savedPersonDocument.getDocument().getPersonData().getPersonID(),
            savedPersonDocument.getId()
        );
        writeInfoLog(fos, updatePersonMessage);

        return savedPersonDocument;
    }

    private PersonDocument createPersonDocument(final FileOutputStream fos, final Element element) {
        final PersonDocument createdPersonDocument = personBuilder.createPersonDocument(element);
        final PersonDocument savedPersonDocument = personDocumentService
            .createDocument(createdPersonDocument, false, null);

        final String createPersonMessage = String.format(
            "Создан житель с personId: %s, documentId: %s",
            savedPersonDocument.getDocument().getPersonData().getPersonID(),
            savedPersonDocument.getId()
        );

        writeInfoLog(fos, createPersonMessage);

        return savedPersonDocument;
    }

    private void cachePersonDocument(final PersonDocument personDocument) {
        if (personDocument == null) {
            return;
        }
        if (PERSON_DOCUMENTS_CACHE.size() == PERSON_DOCUMENT_CACHE_SIZE) {
            savePersonDocumentToSolr();
        }
        PERSON_DOCUMENTS_CACHE.put(personDocument.getFolderId(), personDocument);
    }

    private void savePersonDocumentToSolr() {
        final List<PersonDocument> personDocuments = new ArrayList<>(PERSON_DOCUMENTS_CACHE.values());
        reindexPersonDocumentsInSolr(personDocuments);
        PERSON_DOCUMENTS_CACHE.clear();
        log.info("Person document cache cleared");
    }

    private void reindexPersonDocumentsInSolr(final List<PersonDocument> personDocuments) {
        log.info("Start reindex person documents in solr");
        documentDataService.reindexDocuments(SsrDocumentTypes.PERSON, personDocuments);
        log.info("Finish reindex person documents in solr");
    }

    /**
     * Удаление загруженных жителей.
     */
    @Transactional
    public void deleteUploadedPerson() {
        final List<String> uploadedPersonIds = personDocumentService.getUploadedPersonIds();
        personDocumentService.deleteAllByIsUploaded();
        searchRemoteService.deleteDocumentsByTypeAndIds("UGD_SSR_PERSON", uploadedPersonIds);
        log.info("Uploaded persons were deleted from db and solr.");
    }

    private void writeInfoLog(final FileOutputStream fos, final String message) {
        log.info(message);
        writeLog(fos, message, "INFO");
    }

    private void writeErrorLog(final FileOutputStream fos, final String message, final Exception exception) {
        log.error(message, exception);
        writeLog(fos, message, "ERROR");
    }

    private void writeErrorLog(final FileOutputStream fos, final String message) {
        log.error(message);
        writeLog(fos, message, "ERROR");
    }

    private void writeLog(final FileOutputStream fos, final String message, final String status) {
        final String logRecord = createLogRecord(message, status);
        try {
            fos.write(logRecord.getBytes());
        } catch (Exception e) {
            log.warn("Unable to write log on person upload: {}", e.getMessage(), e);
        }
    }

    private String createLogRecord(final String message, final String level) {
        return String.format(
            LOG_RECORD_TEMPLATE,
            DATE_FORMATTER.format(LocalDateTime.now()),
            level,
            message
        );
    }

    private void endPersonUploadLog(
        final String logFileName,
        final String personUploadLogId
    ) {
        saveOrUpdatePersonUploadLogFile(logFileName).ifPresent(logFileId -> {
            personUploadLogDocumentService.endPersonUploadLog(
                personUploadLogId,
                logFileId
            );
        });
        final File logFile = new File(logPath + logFileName);
        if (logFile.exists()) {
            logFile.delete();
        }
    }

    private void tracePersonUploadLog(
        final String logFileName,
        final String personUploadLogId
    ) {
        saveOrUpdatePersonUploadLogFile(logFileName).ifPresent(logFileId -> {
            personUploadLogDocumentService.tracePersonUploadLog(
                personUploadLogId,
                logFileId
            );
        });

    }

    private Optional<String> saveOrUpdatePersonUploadLogFile(final String filename) {
        final String folderId = createPersonUploadLogFolder();
        final File logFile = new File(logPath + filename);
        try {
            final byte[] fileContent = Files.readAllBytes(logFile.toPath());
            return Optional.of(filestoreRemoteService.createOrUpdateFile(
                filename, null, fileContent, folderId,
                "txt", null, null, "UGD"));
        } catch (Exception e) {
            log.warn("Unable to save person upload log file: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    private String createPersonUploadLogFolder() {
        final CreateFolderRequest request = new CreateFolderRequest();
        request.setPath(rootPath + "/person-upload-logs");
        request.setErrorIfAlreadyExists(false);
        request.setAttrs(FilestoreFolderAttrs.builder()
            .folderTypeID("-")
            .folderEntityID("-")
            .folderSourceReference(FilestoreSourceRef.SERVICE.name())
            .build());
        return remoteService.createFolder(request, systemProperties.getSystem(), SUBSYSTEM_CODE).getId();
    }

    private void recoverInterruptedDocument(final PersonUploadLogDocument personUploadLogDocument) {
        final PersonUploadLogData personUploadLogData = personUploadLogDocument
            .getDocument()
            .getPersonUploadLogData();
        personUploadLogData.setStatus(PersonUploadLogStatus.FAILED);
        final String logFileId = personUploadLogData.getLogFileId();
        recoverLogFile(logFileId);
        personUploadLogDocumentService.updatePersonUploadLogDocument(personUploadLogDocument);
    }

    private void recoverLogFile(final String logFileId) {
        if (StringUtils.isEmpty(logFileId)) {
            return;
        }
        final byte[] logFileContent = filestoreRemoteService
            .getFile(logFileId, systemProperties.getSystem());
        final FilenetFileBean logFileInfo = filestoreRemoteService
            .getFileInfo(logFileId, systemProperties.getSystem());
        createDir(logPath);
        try (final FileOutputStream fos = new FileOutputStream(logPath + logFileInfo.getFileName())) {
            fos.write(logFileContent);
            writeErrorLog(fos, PROCESS_INTERRUPTED_MSG);
            saveOrUpdatePersonUploadLogFile(logFileInfo.getFileName());
        } catch (Exception e) {
            log.warn("Unable to recover document: {}", e.getMessage(), e);
        }
    }

    @Async
    public void mergeContractData() {
        log.info("Started mergeContractData");
        final List<PersonDocument> personDocuments = personDocumentService.fetchAllPersonsWithContractDuplicates();
        personDocuments.forEach(this::mergeContractData);
        log.info("Completed mergeContractData");
    }

    private void mergeContractData(final PersonDocument personDocument) {
        try {
            log.info("Started merge contracts for person id = {}", personDocument.getId());
            final PersonType personData = personDocument.getDocument().getPersonData();
            ofNullable(personData.getContracts())
                .map(PersonType.Contracts::getContract)
                .ifPresent(contracts -> mergeContractData(contracts, personDocument));
            log.info("Completed merge contracts for person id = {}", personDocument.getId());
        } catch (Exception e) {
            log.error("Unable to merge contracts for person id = {}: {}", personDocument.getId(), e.getMessage(), e);
        }
    }

    private void mergeContractData(
        final List<PersonType.Contracts.Contract> contracts, final PersonDocument personDocument
    ) {
        contracts.stream()
            .filter(contract -> StringUtils.isNotEmpty(contract.getOrderId()))
            .collect(Collectors.toMap(
                PersonType.Contracts.Contract::getOrderId,
                Arrays::asList,
                (a1, a2) -> Stream.concat(a1.stream(), a2.stream()).collect(Collectors.toList())
            ))
            .entrySet()
            .stream()
            .filter(entry -> entry.getValue().size() > 1)
            .forEach(entry -> removeEmptyContract(entry.getKey(), entry.getValue().size(), contracts, personDocument));
    }

    private void removeEmptyContract(
        final String orderId,
        final int size,
        final List<PersonType.Contracts.Contract> contracts,
        final PersonDocument personDocument
    ) {
        if (removeEmptyContract(orderId, size, contracts)) {
            personDocumentService.updateDocument(personDocument);
        }
    }

    private boolean removeEmptyContract(
        final String orderId,
        final int size,
        final List<PersonType.Contracts.Contract> contracts
    ) {
        final Map<Integer, PersonType.Contracts.Contract> deletionContracts = IntStream.range(0, contracts.size())
            .boxed()
            .collect(Collectors.toMap(Function.identity(), contracts::get))
            .entrySet()
            .stream()
            .filter(entry -> orderId.equals(entry.getValue().getOrderId())
                && entry.getValue().isIsDgiData()
                && isNull(entry.getValue().getContractNum())
                && isNull(entry.getValue().getContractSignDate())
                && isNull(entry.getValue().getContractDateEnd())
                && isNull(entry.getValue().getContractStatus())
                && isNull(entry.getValue().getContractType())
            )
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        if (deletionContracts.size() == 0) {
            return false;
        } else {
            if (deletionContracts.size() == size) {
                deletionContracts.keySet()
                    .stream()
                    .max(Integer::compareTo)
                    .map(deletionContracts::remove);
            }
            deletionContracts.forEach((key, value) -> contracts.remove((int) key));
            return true;
        }
    }
}
