package ru.croc.ugd.ssr.service.disabledperson;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.mapper.DisabledPersonImportMapper;
import ru.croc.ugd.ssr.model.disabledperson.DisabledPersonImportDocument;
import ru.croc.ugd.ssr.service.SsrFilestoreService;
import ru.croc.ugd.ssr.service.document.DisabledPersonImportDocumentService;
import ru.croc.ugd.ssr.service.excel.disabledperson.DisabledPersonRowSaverProcessor;
import ru.croc.ugd.ssr.service.excel.model.process.ProcessParameters;
import ru.croc.ugd.ssr.service.excel.model.process.ProcessResult;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Service
@AllArgsConstructor
public class DisabledPersonImportProcessor {

    public static final String DOCUMENT_ID_PROCESSING_KEY = "DOCUMENT_ID_PROCESSING_KEY";

    private final DisabledPersonRowSaverProcessor disabledPersonRowSaverProcessor;
    private final DisabledPersonImportDocumentService disabledPersonImportDocumentService;
    private final DisabledPersonImportMapper disabledPersonImportMapper;
    private final SsrFilestoreService ssrFilestoreService;

    @Async
    public void performFileParsing(
        final DisabledPersonImportDocument disabledPersonImportDocument,
        final byte[] fileToProcess,
        final ProcessParameters processParameters
    ) {
        log.info(
            "DisabledPersonImportProcessor: started for disabledPersonImportDocumentId = {}",
            disabledPersonImportDocument.getId()
        );
        try (final InputStream inputStream = new ByteArrayInputStream(fileToProcess);
             final XSSFWorkbook sheetWorkbook = new XSSFWorkbook(inputStream)) {
            final ProcessResult processResult = disabledPersonRowSaverProcessor.process(
                sheetWorkbook, processParameters
            );
            log.info(
                "DisabledPersonImportProcessor: table parsed for disabledPersonImportDocumentId = {}",
                disabledPersonImportDocument.getId()
            );
            saveImportResult(disabledPersonImportDocument, processResult, sheetWorkbook);
            log.info(
                "DisabledPersonImportProcessor: completed for disabledPersonImportDocumentId = {}",
                disabledPersonImportDocument.getId()
            );
        } catch (Exception e) {
            log.error(
                "DisabledPersonImportProcessor: failed to perform file parsing "
                    + "(disabledPersonImportDocumentId = {}): {}",
                disabledPersonImportDocument.getId(),
                e.getMessage(),
                e
            );
            setErrorStatus(disabledPersonImportDocument.getId(), e);
        }
    }

    private void saveImportResult(
        final DisabledPersonImportDocument disabledPersonImportDocument,
        final ProcessResult processResult,
        final XSSFWorkbook sheetWorkbook
    ) throws IOException {
        final String processedFileId = ssrFilestoreService.createXlsxFile(
            sheetWorkbook, disabledPersonImportDocument.getFolderId()
        );
        setCompletedStatus(disabledPersonImportDocument.getId(), processedFileId, processResult);
    }

    private void setCompletedStatus(
        final String disabledPersonImportDocumentId,
        final String processedFileId,
        final ProcessResult processResult
    ) {
        final DisabledPersonImportDocument disabledPersonImportDocument =
            disabledPersonImportDocumentService.fetchDocument(disabledPersonImportDocumentId);
        final DisabledPersonImportDocument updatedDisabledPersonImportDocument =
            disabledPersonImportMapper.toDocumentWithCompletedStatus(
                disabledPersonImportDocument, processedFileId, processResult
            );
        disabledPersonImportDocumentService.updateDocument(updatedDisabledPersonImportDocument, "setCompletedStatus");
    }

    private void setErrorStatus(
        final String disabledPersonImportDocumentId, final Exception exception
    ) {
        final DisabledPersonImportDocument disabledPersonImportDocument =
            disabledPersonImportDocumentService.fetchDocument(disabledPersonImportDocumentId);
        final DisabledPersonImportDocument updatedDisabledPersonImportDocument =
            disabledPersonImportMapper.toDocumentWithErrorStatus(disabledPersonImportDocument, exception);
        disabledPersonImportDocumentService.updateDocument(updatedDisabledPersonImportDocument, "setErrorStatus");
    }
}
