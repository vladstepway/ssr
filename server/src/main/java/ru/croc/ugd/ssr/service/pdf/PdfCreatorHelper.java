package ru.croc.ugd.ssr.service.pdf;

import static ru.croc.ugd.ssr.integration.util.FileUtils.createDir;

import com.google.common.io.ByteStreams;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.FileHeader;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.parser.ZipFileExtractor;
import ru.croc.ugd.ssr.service.pdf.model.InputFileFormat;
import ru.croc.ugd.ssr.service.pdf.model.MergeToPdfInput;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
public class PdfCreatorHelper {
    private final ZipFileExtractor zipFileExtractor;

    private static final int A4_PDF_WIDTH = 595;
    private static final int A4_PDF_HEIGHT = 842;

    public List<InputStream> convertInputToPdfInputStream(
        final MergeToPdfInput mergeToPdfInput
    ) throws IOException, DocumentException {
        final InputFileFormat inputFileFormat = mergeToPdfInput.getFileFormat();
        switch (inputFileFormat) {
            case GIF:
            case JPG:
            case PNG:
            case JPEG:
                final ByteArrayOutputStream byteArrayOutputStream =
                    convertImageToPdfOutputStream(mergeToPdfInput.getSource());
                return retrievePdfSingletonList(byteArrayOutputStream.toByteArray());
            case ZIP:
                return extractAndConvertZipFiles(mergeToPdfInput);
            case PDF:
                return retrievePdfSingletonList(mergeToPdfInput.getSource());
            default:
                return Collections.emptyList();
        }
    }

    private List<InputStream> extractAndConvertZipFiles(
        final MergeToPdfInput mergeToPdfInput
    ) throws IOException, DocumentException {
        final List<InputStream> resultInputStreams = new ArrayList<>();
        final byte[] zipFileBytes = mergeToPdfInput.getSource();
        final String fileName = UUID.randomUUID().toString() + ".zip";
        final String directoryPath = "./" + fileName.replace(".", "_") + "/";
        createDir(directoryPath);
        final Path tempPath = Paths.get(directoryPath + fileName);
        final ZipFile zipFile = zipFileExtractor.parseFile(zipFileBytes, tempPath, StringUtils.EMPTY);
        final List<FileHeader> zipFilesHeaders = zipFile.getFileHeaders();
        for (final FileHeader fileHeader : zipFilesHeaders) {
            final InputStream zipInnerFileInputStream = zipFile.getInputStream(fileHeader);
            final String zipInnerFileName = fileHeader.getFileName();
            try {
                final InputFileFormat fileFormat = InputFileFormat
                    .valueOf(com.google.common.io.Files.getFileExtension(zipInnerFileName).toUpperCase());
                if (InputFileFormat.PDF.equals(fileFormat)) {
                    resultInputStreams.add(zipInnerFileInputStream);
                } else {
                    byte[] zipInnerFileBytes = ByteStreams.toByteArray(zipInnerFileInputStream);
                    final MergeToPdfInput zipInnerFileMergePdfInput = MergeToPdfInput
                        .builder()
                        .source(zipInnerFileBytes)
                        .fileFormat(fileFormat)
                        .build();
                    resultInputStreams.addAll(convertInputToPdfInputStream(zipInnerFileMergePdfInput));
                }
            } catch (IllegalArgumentException ex) {
                log.warn(ex.getMessage(), ex);
                continue;
            }
        }
        try {
            FileUtils.deleteDirectory(new File(directoryPath));
        } catch (IOException ex) {
            log.warn("Couldn't delete file", ex.getMessage(), ex);
        }
        return resultInputStreams;
    }

    private ByteArrayOutputStream convertImageToPdfOutputStream(
        byte[] source
    ) throws DocumentException, IOException {
        final ByteArrayOutputStream outputResultStream = new ByteArrayOutputStream();

        final Image inputImage = Image.getInstance(source);
        inputImage.scaleToFit(A4_PDF_WIDTH, A4_PDF_HEIGHT);
        inputImage.setAbsolutePosition(0, 0);

        final Document resultDocument = new Document(
            PageSize.A4, 0, 0, 0, 0);

        PdfWriter.getInstance(resultDocument, outputResultStream);
        resultDocument.open();
        inputImage.setAbsolutePosition(0, PageSize.A4.getHeight() - inputImage.getScaledHeight());

        resultDocument.add(inputImage);
        resultDocument.close();
        return outputResultStream;
    }

    private List<InputStream> retrievePdfSingletonList(final byte[] fileContent) {
        return isPdfDocument(fileContent)
            ? Collections.singletonList(new ByteArrayInputStream(fileContent))
            : Collections.emptyList();
    }

    private boolean isPdfDocument(final byte[] fileContent) {
        try {
            PDDocument.load(fileContent);
            return true;
        } catch (IOException e) {
            log.warn("PdfCreatorHelper.isPdfDocument: file is not pdf", e.getMessage(), e);
        }
        return false;
    }
}
