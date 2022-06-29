package ru.croc.ugd.ssr.service.personaldocument;

import static java.util.Objects.nonNull;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.croc.ugd.ssr.dto.personaldocument.PersonalDocumentApplicationFlowStatus;
import ru.croc.ugd.ssr.integration.service.notification.PersonalDocumentApplicationElkNotificationService;
import ru.croc.ugd.ssr.model.personaldocument.PersonalDocumentApplicationDocument;
import ru.croc.ugd.ssr.model.personaldocument.PersonalDocumentDocument;
import ru.croc.ugd.ssr.model.personaldocument.PersonalDocumentRequestDocument;
import ru.croc.ugd.ssr.mq.listener.personaldocument.EtpPersonalDocumentMapper;
import ru.croc.ugd.ssr.personaldocument.DocumentType;
import ru.croc.ugd.ssr.personaldocument.PersonalDocumentApplicationType;
import ru.croc.ugd.ssr.personaldocument.PersonalDocumentFile;
import ru.croc.ugd.ssr.personaldocument.PersonalDocumentRequest;
import ru.croc.ugd.ssr.personaldocument.PersonalDocumentRequestType;
import ru.croc.ugd.ssr.personaldocument.PersonalDocumentType;
import ru.croc.ugd.ssr.personaldocument.TenantType;
import ru.croc.ugd.ssr.service.ChedFileService;
import ru.croc.ugd.ssr.service.SsrFilestoreService;
import ru.croc.ugd.ssr.service.bpm.BpmService;
import ru.croc.ugd.ssr.service.document.PersonalDocumentApplicationDocumentService;
import ru.croc.ugd.ssr.service.document.PersonalDocumentDocumentService;
import ru.croc.ugd.ssr.service.document.PersonalDocumentRequestDocumentService;
import ru.croc.ugd.ssr.service.personaldocument.type.SsrPersonalDocumentTypeService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Сервис для регистрации заявлений на предоставление документов.
 */
@Slf4j
@Service
@AllArgsConstructor
public class PersonalDocumentApplicationRegistrationService {

    private static final String PROCESS_ACCEPT_DOCUMENTS_KEY = "ugdssrDocument_acceptDocuments";

    private final PersonalDocumentApplicationDocumentService personalDocumentApplicationDocumentService;
    private final PersonalDocumentRequestDocumentService personalDocumentRequestDocumentService;
    private final PersonalDocumentDocumentService personalDocumentDocumentService;
    private final PersonalDocumentApplicationElkNotificationService personalDocumentApplicationElkNotificationService;
    private final ChedFileService chedFileService;
    private final SsrFilestoreService ssrFilestoreService;
    private final EtpPersonalDocumentMapper etpPersonalDocumentMapper;
    private final SsrPersonalDocumentTypeService ssrPersonalDocumentTypeService;
    private final BpmService bpmService;

    @Transactional
    public void processRegistration(
        final PersonalDocumentApplicationDocument personalDocumentApplicationDocument,
        final PersonalDocumentDocument personalDocumentDocument
    ) {
        final PersonalDocumentType personalDocument = personalDocumentDocument
            .getDocument()
            .getPersonalDocumentData();

        final PersonalDocumentApplicationType personalDocumentApplication = personalDocumentApplicationDocument
            .getDocument()
            .getPersonalDocumentApplicationData();

        final PersonalDocumentRequestDocument personalDocumentRequestDocument = ofNullable(personalDocumentApplication)
            .map(PersonalDocumentApplicationType::getRequestDocumentId)
            .flatMap(personalDocumentRequestDocumentService::fetchById)
            .orElse(null);

        personalDocumentDocumentService.createDocument(personalDocumentDocument, true, null);

        addFileStoreInformation(personalDocumentDocument);
        addFilesFromZip(personalDocumentDocument);
        addUnionFileStoreId(personalDocumentDocument);

        personalDocumentApplicationDocumentService.createDocument(personalDocumentApplicationDocument, true, null);

        personalDocument.setApplicationDocumentId(personalDocumentApplicationDocument.getId());

        ofNullable(personalDocumentRequestDocument)
            .map(document -> addPersonalDocumentApplicationDocumentId(
                document, personalDocumentApplicationDocument.getId()
            ))
            .ifPresent(document -> personalDocumentRequestDocumentService.updateDocument(
                document.getId(), document, true, true, null
            ));

        personalDocumentDocumentService.updateDocument(
            personalDocumentDocument.getId(), personalDocumentDocument, true, true, null
        );

        personalDocumentApplicationElkNotificationService.sendStatus(
            PersonalDocumentApplicationFlowStatus.ACCEPTED, personalDocumentApplicationDocument
        );

        if (nonNull(personalDocumentRequestDocument)) {
            createAcceptDocumentsProcess(personalDocumentApplicationDocument.getId(), personalDocumentRequestDocument)
                .ifPresent(personalDocumentApplication::setProcessInstanceId);
        }

        personalDocumentApplicationDocumentService.updateDocument(
            personalDocumentApplicationDocument.getId(),
            personalDocumentApplicationDocument,
            true,
            true,
            null
        );
    }

    private void addFileStoreInformation(final PersonalDocumentDocument personalDocumentDocument) {
        final PersonalDocumentType personalDocument = personalDocumentDocument.getDocument().getPersonalDocumentData();

        ofNullable(personalDocument.getDocuments())
            .map(PersonalDocumentType.Documents::getDocument)
            .ifPresent(documents -> addFileStoreInformation(documents, personalDocumentDocument.getFolderId()));

        ofNullable(personalDocument.getTenants())
            .map(PersonalDocumentType.Tenants::getTenant)
            .orElse(Collections.emptyList())
            .stream()
            .map(TenantType::getDocuments)
            .map(TenantType.Documents::getDocument)
            .forEach(documents -> addFileStoreInformation(documents, personalDocumentDocument.getFolderId()));
    }

    private void addFileStoreInformation(final List<DocumentType> documents, final String folderId) {
        ofNullable(documents)
            .orElse(Collections.emptyList())
            .stream()
            .map(DocumentType::getFiles)
            .forEach(files -> addFileStoreInformation(files, folderId));
    }

    private void addFileStoreInformation(final DocumentType.Files files, final String folderId) {
        ofNullable(files)
            .map(DocumentType.Files::getFile)
            .orElse(Collections.emptyList())
            .forEach(file -> file.setFileStoreId(
                chedFileService.extractFileFromChedAndGetFileLink(file.getChedFileId(), folderId)
            ));
    }

    private PersonalDocumentRequestDocument addPersonalDocumentApplicationDocumentId(
        final PersonalDocumentRequestDocument personalDocumentRequestDocument, final String applicationDocumentId
    ) {
        ofNullable(personalDocumentRequestDocument)
            .map(PersonalDocumentRequestDocument::getDocument)
            .map(PersonalDocumentRequest::getPersonalDocumentRequestData)
            .ifPresent(request -> request.setApplicationDocumentId(applicationDocumentId));

        return personalDocumentRequestDocument;
    }

    private void addFilesFromZip(final PersonalDocumentDocument personalDocumentDocument) {
        try {
            final PersonalDocumentType personalDocument = personalDocumentDocument.getDocument()
                .getPersonalDocumentData();

            ofNullable(personalDocument.getDocuments())
                .map(PersonalDocumentType.Documents::getDocument)
                .ifPresent(documents -> addFilesFromZip(documents, personalDocumentDocument.getFolderId()));

            ofNullable(personalDocument.getTenants())
                .map(PersonalDocumentType.Tenants::getTenant)
                .orElse(Collections.emptyList())
                .stream()
                .map(TenantType::getDocuments)
                .map(TenantType.Documents::getDocument)
                .forEach(documents -> addFilesFromZip(documents, personalDocumentDocument.getFolderId()));
        } catch (Exception e) {
            log.warn("Unable to add files from zip: {}", e.getMessage(), e);
            throw e;
        }
    }

    private void addFilesFromZip(final List<DocumentType> documents, final String folderId) {
        ofNullable(documents)
            .orElse(Collections.emptyList())
            .stream()
            .map(DocumentType::getFiles)
            .forEach(files -> addFilesFromZip(files, folderId));
    }

    private void addFilesFromZip(final DocumentType.Files files, final String folderId) {
        if (nonNull(files)) {
            final List<PersonalDocumentFile> zipFiles = files.getFile()
                .stream()
                .filter(file -> "zip".equals(file.getFileType()))
                .collect(Collectors.toList());

            zipFiles.forEach(zipFile -> files.getFile().addAll(retrieveFilesFromZip(zipFile, folderId)));
        }
    }

    private List<PersonalDocumentFile> retrieveFilesFromZip(
        final PersonalDocumentFile personalDocumentFile, final String folderId
    ) {
        final byte[] zipFileContent = ssrFilestoreService.getFile(personalDocumentFile.getFileStoreId());
        final List<Pair<String, String>> files = ssrFilestoreService.retrieveFilesFromZip(zipFileContent, folderId);
        return files.stream()
            .map(file -> etpPersonalDocumentMapper.toFile(file.getLeft(), file.getRight()))
            .collect(Collectors.toList());
    }

    private void addUnionFileStoreId(final PersonalDocumentDocument personalDocumentDocument) {
        try {
            final PersonalDocumentType personalDocument = personalDocumentDocument.getDocument()
                .getPersonalDocumentData();
            final List<String> fileIds = retrievePersonalDocumentFileIds(personalDocument);
            final byte[] unionFileContent = ssrFilestoreService.loadAndMergeFilesToSinglePdf(fileIds);

            final String fileName = "Единый файл документов " + LocalDateTime.now() + ".pdf";

            final String unionFileStoreId = ssrFilestoreService.createPdfFile(
                fileName, unionFileContent, personalDocumentDocument.getFolderId()
            );

            personalDocument.setUnionFileStoreId(unionFileStoreId);
        } catch (Exception e) {
            log.warn("Unable to create union file: {}", e.getMessage(), e);
            throw e;
        }
    }

    private List<String> retrievePersonalDocumentFileIds(final PersonalDocumentType personalDocument) {
        final List<String> fileIds = new ArrayList<>();

        ofNullable(personalDocument.getDocuments())
            .map(PersonalDocumentType.Documents::getDocument)
            .ifPresent(documents -> fileIds.addAll(retrieveFileIds(documents)));

        ofNullable(personalDocument.getTenants())
            .map(PersonalDocumentType.Tenants::getTenant)
            .ifPresent(tenants -> fileIds.addAll(retrieveTenantDocumentFileIds(tenants)));

        return fileIds;
    }

    private List<String> retrieveTenantDocumentFileIds(final List<TenantType> tenants) {
        return tenants.stream()
            .filter(tenant -> nonNull(tenant.getDocuments()))
            .map(tenant -> retrieveFileIds(tenant.getDocuments().getDocument()))
            .flatMap(List::stream)
            .collect(Collectors.toList());
    }

    private List<String> retrieveFileIds(final List<DocumentType> documents) {
        return documents.stream()
            .sorted(Comparator.comparing(
                document -> ssrPersonalDocumentTypeService.getSortOrderByCode(document.getTypeCode()),
                Comparator.nullsFirst(Integer::compareTo)
            ))
            .map(this::retrieveFileIds)
            .flatMap(List::stream)
            .collect(Collectors.toList());
    }

    private List<String> retrieveFileIds(final DocumentType document) {
        return ofNullable(document)
            .map(DocumentType::getFiles)
            .map(DocumentType.Files::getFile)
            .orElse(Collections.emptyList())
            .stream()
            .filter(file -> !"zip".equals(file.getFileType()))
            .map(PersonalDocumentFile::getFileStoreId)
            .collect(Collectors.toList());
    }

    private Optional<String> createAcceptDocumentsProcess(
        final String personalDocumentApplicationId,
        final PersonalDocumentRequestDocument personalDocumentRequestDocument
    ) {
        final Optional<String> initiatorLoginOptional = getInitiatorLogin(personalDocumentRequestDocument);

        if (!initiatorLoginOptional.isPresent()) {
            log.warn(
                "The initiator of document request was not found: applicationId {}", personalDocumentApplicationId
            );
            return Optional.empty();
        }

        final String initiatorLogin = initiatorLoginOptional.get();

        final Map<String, String> variablesMap = new HashMap<>();
        variablesMap.put(BpmService.PROCESS_DOCUMENT_KEY_ENTITY_ID_VAR, personalDocumentApplicationId);
        variablesMap.put(BpmService.PROCESS_DOCUMENT_KEY_CANDIDATE_USERS, initiatorLogin);

        return ofNullable(bpmService.startNewProcess(PROCESS_ACCEPT_DOCUMENTS_KEY, variablesMap));
    }

    private Optional<String> getInitiatorLogin(final PersonalDocumentRequestDocument personalDocumentRequestDocument) {
        return of(personalDocumentRequestDocument)
            .map(PersonalDocumentRequestDocument::getDocument)
            .map(PersonalDocumentRequest::getPersonalDocumentRequestData)
            .map(PersonalDocumentRequestType::getInitiatorLogin);
    }
}
