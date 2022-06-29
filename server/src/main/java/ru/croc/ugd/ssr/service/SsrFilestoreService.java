package ru.croc.ugd.ssr.service;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

import com.google.common.io.Files;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.io.inputstream.ZipInputStream;
import net.lingala.zip4j.model.LocalFileHeader;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.computel.common.filenet.client.FilenetFileBean;
import ru.computel.common.filenet.client.FilenetFolderBean;
import ru.croc.ugd.ssr.config.SystemProperties;
import ru.croc.ugd.ssr.service.pdf.DefaultPdfHandlerService;
import ru.croc.ugd.ssr.service.pdf.model.InputFileFormat;
import ru.croc.ugd.ssr.service.pdf.model.MergeToPdfInput;
import ru.reinform.cdp.document.model.DocumentAbstract;
import ru.reinform.cdp.document.model.DocumentType;
import ru.reinform.cdp.document.service.CommonFilestoreSubfoldersService;
import ru.reinform.cdp.filestore.service.FilestoreRemoteService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class SsrFilestoreService {
    private final SystemProperties systemProperties;
    private final FilestoreRemoteService filestoreRemoteService;
    private final DefaultPdfHandlerService pdfCreatorService;
    private final CommonFilestoreSubfoldersService commonFilestoreSubfoldersService;

    public SsrFilestoreService(
        SystemProperties systemProperties,
        FilestoreRemoteService filestoreRemoteService,
        @Qualifier("defaultPdfHandlerService") DefaultPdfHandlerService pdfCreatorService,
        CommonFilestoreSubfoldersService commonFilestoreSubfoldersService
    ) {
        this.systemProperties = systemProperties;
        this.filestoreRemoteService = filestoreRemoteService;
        this.pdfCreatorService = pdfCreatorService;
        this.commonFilestoreSubfoldersService = commonFilestoreSubfoldersService;
    }

    public byte[] getFile(final String fileId) {
        return filestoreRemoteService.getFile(fileId, systemProperties.getSystem());
    }

    public boolean deleteFile(final String fileId) {
        return filestoreRemoteService.deleteFile(fileId, systemProperties.getSystem());
    }

    public FilenetFileBean getFileInfo(final String fileId) {
        return filestoreRemoteService.getFileInfo(fileId, systemProperties.getSystem());
    }

    public byte[] loadAndMergeFilesToSinglePdf(final List<String> fileIds) {
        final List<MergeToPdfInput> mergeToPdfInputs = fileIds
            .stream()
            .map(fileId -> {
                final byte[] fileSource = getFile(fileId);
                final FilenetFileBean fileInfo = getFileInfo(fileId);
                final InputFileFormat fileFormat = InputFileFormat
                    .valueOf(Files.getFileExtension(fileInfo.getFileName()).toUpperCase());
                return MergeToPdfInput
                    .builder()
                    .source(fileSource)
                    .fileFormat(fileFormat)
                    .build();
            }).collect(Collectors.toList());
        return pdfCreatorService.mergeToSinglePdf(mergeToPdfInputs);
    }

    public List<Pair<String, String>> retrieveFilesFromZip(final byte[] zipFileContent, final String folderId) {
        final List<Pair<String, String>> files = new ArrayList<>();
        try (final ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(zipFileContent))) {
            byte[] buffer = new byte[4096];
            LocalFileHeader localFileHeader;
            while ((localFileHeader = zipInputStream.getNextEntry()) != null) {
                if (!localFileHeader.isDirectory()) {
                    try (final ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                        int len;
                        while ((len = zipInputStream.read(buffer)) > 0) {
                            outputStream.write(buffer, 0, len);
                        }
                        final byte[] fileContent = outputStream.toByteArray();
                        String fileName = localFileHeader.getFileName();
                        if (fileName.contains("/")) {
                            fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
                        }
                        // Скрытые файлы не должны обрабатываться
                        if (!fileName.startsWith("._")) {
                            if (checkFileType(fileName.toLowerCase())) {
                                // декодирование в связи с возможным наличием кириллицы в именах файлов
                                final String decodedFileName = new String(fileName.getBytes("IBM437"));
                                if (Pattern.matches(".*\\p{InCyrillic}.*", decodedFileName)) {
                                    fileName = decodedFileName;
                                } else {
                                    fileName = new String(fileName.getBytes("IBM437"), Charset.forName("IBM866"));
                                }
                                final String fileType = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
                                final String fileContentType = retrieveFileContentType(fileType);
                                fileName = fileName.substring(0, fileName.lastIndexOf("."))
                                    + "_" + LocalDateTime.now() + "." + fileType;
                                final String fileStoreId = filestoreRemoteService.createFile(
                                    fileName,
                                    fileContentType,
                                    fileContent,
                                    folderId,
                                    fileType,
                                    null,
                                    null,
                                    systemProperties.getSystem()
                                );
                                files.add(Pair.of(fileStoreId, fileType));
                            } else if (checkZipArchive(fileName.toLowerCase())) {
                                files.addAll(retrieveFilesFromZip(fileContent, folderId));
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Unable to parse zip: {}", e.getMessage(), e);
        }
        return files;
    }

    public String createPdfFile(
        final String unionFileId, final List<Integer> pageNumbers, final String fileName, final String folderId
    ) {
        final byte[] unionFileContent = getFile(unionFileId);
        final byte[] fileContent = pdfCreatorService.retrievePdf(unionFileContent, pageNumbers);
        return createPdfFile(fileName, fileContent, folderId);
    }

    public String createPdfFile(final String fileName, final byte[] fileContent, final String folderId) {
        return filestoreRemoteService.createFile(
            fileName, "application/pdf", fileContent, folderId, "pdf", null, null, systemProperties.getSystem()
        );
    }

    public String createRtfFile(final String fileName, final byte[] fileContent, final String folderId) {
        return filestoreRemoteService.createFile(
            fileName, "application/rtf", fileContent, folderId, "rtf", null, null, systemProperties.getSystem()
        );
    }

    private boolean checkFileType(final String fileName) {
        return fileName.matches(".+\\.((pdf)|(gif)|(jpeg)|(jpg)|(png)|(xml))$");
    }

    private boolean checkZipArchive(final String fileName) {
        return fileName.matches(".+\\.zip");
    }

    private String retrieveFileContentType(final String fileType) {
        switch (fileType) {
            case "pdf":
                return "application/pdf";
            case "gif":
                return "image/gif";
            case "jpeg":
            case "jpg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "xml":
                return "application/xml";
            default:
                return null;
        }
    }

    public List<String> retrieveFileStoreIdsFromSubfoldersByType(final String folderId, final String type) {
        final FilenetFolderBean filenetFolderBean = filestoreRemoteService
            .getFolderContent(folderId, true, systemProperties.getSystem());

        return ofNullable(filenetFolderBean.getFolderContentFolders())
            .map(List::stream)
            .orElse(Stream.empty())
            .map(FilenetFolderBean::getFolderContentFiles)
            .flatMap(List::stream)
            .filter(filenetFileBean -> nonNull(filenetFileBean.getFileName()))
            .filter(filenetFileBean -> filenetFileBean.getFileName().endsWith("." + type))
            .map(FilenetFileBean::getVersionSeriesGuid)
            .collect(Collectors.toList());
    }

    public <T extends DocumentAbstract> void createFolderIfNeeded(
        final DocumentType<T> documentType, final T document
    ) {
        if (isNull(document.getFolderId())) {
            commonFilestoreSubfoldersService.createFolder(documentType, document);
        }
    }

    public String createXlsxFile(final XSSFWorkbook sheetWorkbook, final String folderId) throws IOException {
        try (final ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            sheetWorkbook.write(bos);
            return filestoreRemoteService.createFile(
                UUID.randomUUID() + ".xlsx", "text/xlsx",
                bos.toByteArray(),
                folderId,
                "xlsx",
                "SSR",
                "SSR",
                systemProperties.getSystem()
            );
        }
    }
}
