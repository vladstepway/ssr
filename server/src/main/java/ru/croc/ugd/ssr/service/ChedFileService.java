package ru.croc.ugd.ssr.service;

import static java.util.Objects.isNull;
import static ru.croc.ugd.ssr.utils.StreamUtils.not;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.integration.config.IntegrationPropertyConfig;
import ru.croc.ugd.ssr.utils.StreamUtils;
import ru.reinform.cdp.ched.model.DownloadDocument;
import ru.reinform.cdp.ched.model.ResultDocument;
import ru.reinform.cdp.ched.model.UploadDocument;
import ru.reinform.cdp.ched.model.request.sync.Download;
import ru.reinform.cdp.ched.model.request.sync.Upload;
import ru.reinform.cdp.ched.rest.api.ChedRestApi;
import ru.reinform.types.ChedHeaderType;
import ru.reinform.types.FileChedType;
import ru.reinform.types.StoreType;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Сервис по работе с ЦХЭД.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChedFileService {

    @Value("${app.system:UGD}")
    private String appSystemCode;

    private final IntegrationPropertyConfig config;
    private final ChedRestApi chedRestApi;

    /**
     * Метод выполняет загрузку файла из (Р)ЦХЭД в alfresco.
     *
     * @param chedFileId идентификатор файла в (Р)ЦХЭД
     * @param folderId   id папки в альфреско
     * @param storeType  тип хранилища
     * @return идентификатор фала в alfresco
     */
    public String extractFileFromChed(final String chedFileId, final String folderId, final StoreType storeType) {
        final DownloadDocument downloadDocumentRequest = createDownloadDocumentRequest(chedFileId, folderId, storeType);

        final ResultDocument resultDocument = chedRestApi.syncDownload(downloadDocumentRequest);
        return resultDocument.getDocument().getFiles().getFilesResult().get(0).getFileAlfId();
    }

    /**
     * Метод выполняет загрузку файла из хранилища GU_DOCS (Р)ЦХЭД в alfresco.
     *
     * @param chedFileId идентификатор файла в (Р)ЦХЭД
     * @param folderId   id папки в альфреско
     * @return ид используемое в ссылках на файл в alfresco
     */
    public String extractFileFromChedAndGetFileLink(final String chedFileId, final String folderId) {
        final String alfFileId = extractFileFromChed(chedFileId, folderId, StoreType.GU_DOCS);
        if (isNull(alfFileId)) {
            return alfFileId;
        } else {
            return alfFileId.split(";")[0];
        }
    }

    /**
     * Метод выполняет загрузку файла из (Р)ЦХЭД в alfresco.
     *
     * @param chedFileId идентификатор файла в (Р)ЦХЭД
     * @param folderId   id папки в альфреско
     * @param storeType  тип хранилища
     * @return ид используемое в ссылках на файл в alfresco
     */
    public String extractFileFromChedAndGetFileLink(
        final String chedFileId, final String folderId, final StoreType storeType
    ) {
        final String alfFileId = extractFileFromChed(chedFileId, folderId, storeType);
        if (isNull(alfFileId)) {
            return alfFileId;
        } else {
            return alfFileId.split(";")[0];
        }
    }

    /**
     * Загрузить файл в (Р)ЦХЭД.
     *
     * @param alfrescoFileId    ид файла в альфреско
     * @param asgufCode         код документа в АС ГУФ
     * @param chedDocumentClass тип класса документа в ЦХЭД
     * @return id в (Р)ЦХЭД
     */
    public String uploadFileToChed(
        final String alfrescoFileId, final String asgufCode, final String chedDocumentClass
    ) {
        return chedSyncUpload(
            alfrescoFileId, asgufCode, chedDocumentClass
        ).getDocument().getFiles().getFilesResult().get(0).getFileChedId();
    }

    /**
     * Получить ссылку на файл в ЦХЭД.
     *
     * @param chedId chedId
     * @return ссылка на файл в ЦХЭД
     */
    public Optional<String> getChedFileLink(final String chedId) {
        if (StringUtils.isEmpty(config.getChedDownloadUrl())) {
            log.info("ChedDownloadUrl is empty");
            return Optional.empty();
        }
        return Optional.ofNullable(chedId)
            .map(String::trim)
            .filter(not(String::isEmpty))
            .map(id -> config.getChedDownloadUrl() + id);
    }

    private ResultDocument chedSyncUpload(final String fileId, final String asgufCode, final String chedDocumentClass) {
        final Upload upload = new Upload();

        final ChedHeaderType header = new ChedHeaderType();
        header.setSystem(appSystemCode);
        header.setDateRequest(LocalDateTime.now());
        upload.setDocHeader(header);

        final Upload.FilesUpload filesUpload = new Upload.FilesUpload();
        filesUpload.setStore(StoreType.GU_DOCS);
        final FileChedType fileChed = new FileChedType();
        fileChed.setFileId(fileId);
        fileChed.setChedDocumentClass(chedDocumentClass);
        fileChed.setAsgufCode(asgufCode);
        filesUpload.getFile().add(fileChed);
        upload.setFilesUpload(filesUpload);

        final UploadDocument uploadDocument = new UploadDocument();
        uploadDocument.setDocument(upload);

        return chedRestApi.syncUpload(uploadDocument);
    }

    private DownloadDocument createDownloadDocumentRequest(
        final String chedFileId, final String folderId, final StoreType storeType
    ) {
        final DownloadDocument downloadDocument = new DownloadDocument();
        final Download download = new Download();
        final ChedHeaderType header = new ChedHeaderType();
        header.setSystem(appSystemCode);
        header.setDateRequest(LocalDateTime.now());
        downloadDocument.setDocument(download);
        download.setDocHeader(header);
        final Download.FilesDownload filesDownload = new Download.FilesDownload();
        download.setFilesDownload(filesDownload);
        filesDownload.setFolderId(folderId);
        final FileChedType fileChedType = new FileChedType();
        fileChedType.setChedDocumentClass("Document");
        fileChedType.setFileId(chedFileId);
        filesDownload.setStore(storeType);
        filesDownload.getFile().add(fileChedType);
        return downloadDocument;
    }
}
