package ru.croc.ugd.ssr.service.reporter;

import static java.util.Objects.isNull;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import ru.computel.common.filenet.client.FilenetFileBean;
import ru.croc.ugd.ssr.dto.apartmentinspection.RestFileDto;
import ru.croc.ugd.ssr.exception.SsrException;
import ru.croc.ugd.ssr.service.SsrFilestoreService;
import ru.reinform.reporter.model.FilenetDestination;
import ru.reinform.reporter.model.WordHtmlSubstitution;
import ru.reinform.reporter.model.rest.WordToPdfFilenetRequest;
import ru.reinform.reporter.service.Word2PdfRemoteService;
import ru.reinform.reporter.service.WordRemoteService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class SsrReporterService {

    private static final String DATE_FORMAT_PATTERN = "dd_MM_yyyy_hh_mm_ss";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN);

    private final WordRemoteService wordRemoteService;
    private final Word2PdfRemoteService word2PdfRemoteService;
    private final SsrFilestoreService ssrFilestoreService;

    public String wordInsertHtml(
        final String versionSeriesGuid,
        final String placeholder,
        final String html,
        final String destinationFolderId,
        final String destinationFileName
    ) {
        final WordHtmlSubstitution substitution = new WordHtmlSubstitution(placeholder, html);
        final FilenetDestination filenetDestination = new FilenetDestination(
            null,
            destinationFolderId,
            destinationFileName,
            "rtf",
            null
        );

        final FilenetFileBean fileBean = wordRemoteService
            .insertHtmlText2Filenet(versionSeriesGuid, substitution, filenetDestination);

        return fileBean.getVersionSeriesGuid();
    }

    public String convertWordToPdf(
        final String wordFileLink, final String destinationFolderId, final String destinationFileName
    ) {
        final WordToPdfFilenetRequest wordToPdfFilenetRequest = new WordToPdfFilenetRequest(
            wordFileLink,
            new FilenetDestination(
                null,
                destinationFolderId,
                destinationFileName,
                "pdf",
                null
            )
        );

        final FilenetFileBean fileBean = word2PdfRemoteService.convertWord2PdfFilenet(wordToPdfFilenetRequest);
        return fileBean.getVersionSeriesGuid();
    }

    public RestFileDto createPdfReport(
        final String fileNamePrefix,
        final String rtfTemplatePath,
        final Map<ReportFieldType, String> reportFields,
        final String folderId
    ) {
        final byte[] rtfTemplateContent = loadRtfTemplate(rtfTemplatePath);
        if (isNull(rtfTemplateContent)) {
            throw new SsrException("Файл шаблона не найден");
        }

        final String templateFileStoreId = ssrFilestoreService.createRtfFile(
            retrieveFileName("template", "rtf"),
            rtfTemplateContent,
            folderId
        );
        final List<WordHtmlSubstitution> substitutions = retrieveSubstitutions(reportFields);
        final String rtfFileLink = createRtfReport(
            templateFileStoreId,
            folderId,
            retrieveFileName("file", ".rtf"),
            substitutions
        );

        final String fileName = retrieveFileName(fileNamePrefix, "pdf");
        final String pdfFileLink = convertWordToPdf(rtfFileLink, folderId, fileName);
        final byte[] pdfFileContent = ssrFilestoreService.getFile(pdfFileLink);
        ssrFilestoreService.deleteFile(pdfFileLink);

        return RestFileDto.builder()
            .content(pdfFileContent)
            .fileName(fileName)
            .build();
    }

    private static byte[] loadRtfTemplate(final String templatePath) {
        final Resource templateResource = new ClassPathResource(templatePath);
        return readResource(templateResource);
    }

    private static byte[] readResource(final Resource templateResource) {
        try {
            return FileCopyUtils.copyToByteArray(templateResource.getInputStream());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    public String retrieveFileName(final String fileNamePrefix, final String fileType) {
        return fileNamePrefix + "_" + SsrReporterService.DATE_FORMATTER.format(LocalDateTime.now()) + "." + fileType;
    }

    private List<WordHtmlSubstitution> retrieveSubstitutions(final Map<ReportFieldType, String> reportFields) {
        return reportFields.entrySet()
            .stream()
            .map(reportField -> new WordHtmlSubstitution(
                reportField.getKey().getFieldName(), retrieveStrongHtmlText(reportField.getValue())
            ))
            .collect(Collectors.toList());
    }

    private String retrieveStrongHtmlText(final String value) {
        return "<strong style=\"font-size: 11px; font-family: 'Times New Roman', Times, serif;\">"
            + value
            + "</strong>";
    }

    private String createRtfReport(
        final String fileStoreId,
        final String folderId,
        final String fileName,
        final List<WordHtmlSubstitution> substitutions
    ) {
        final FilenetDestination filenetDestination = new FilenetDestination(
            fileStoreId,
            folderId,
            fileName,
            "rtf",
            null
        );
        final FilenetFileBean fileBean = wordRemoteService.insertHtmlTexts2Filenet(
            fileStoreId, substitutions, filenetDestination
        );
        return fileBean.getVersionSeriesGuid();
    }
}
