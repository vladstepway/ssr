package ru.croc.ugd.ssr.service.trade.batch;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.model.trade.TradeDataBatchStatusDocument;
import ru.croc.ugd.ssr.service.SsrFilestoreService;
import ru.croc.ugd.ssr.service.document.TradeDataBatchStatusDocumentService;
import ru.croc.ugd.ssr.service.excel.model.process.ProcessParameters;
import ru.croc.ugd.ssr.service.excel.model.process.ProcessResult;
import ru.croc.ugd.ssr.service.trade.TradeAdditionRowSaverProcessor;
import ru.croc.ugd.ssr.service.trade.model.TradeAdditionGeneralConstants;
import ru.croc.ugd.ssr.trade.BatchProcessingStatus;
import ru.croc.ugd.ssr.trade.ErrorDescription;
import ru.croc.ugd.ssr.trade.TradeDataBatchStatusType;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;

/**
 * TradeAdditionBatchProcessor.
 */
@Service
@Slf4j
@AllArgsConstructor
public class TradeAdditionBatchProcessor {
    private final TradeAdditionRowSaverProcessor tradeSheetRowProcessor;
    private final TradeDataBatchStatusDocumentService tradeDataBatchStatusDocumentService;
    private final SsrFilestoreService ssrFilestoreService;

    /**
     * Process batch.
     *
     * @param fileToProcess                fileToProcess.
     * @param processParameters            processParameters.
     */
    @Async
    public void processBatchAsync(final TradeDataBatchStatusDocument tradeDataBatchStatusDocument,
                                  final byte[] fileToProcess,
                                  final ProcessParameters processParameters) {
        final String batchDocumentId = getBatchIdFromParams(processParameters);
        log.info("TradeAdditionBatchProcessor.processBatchAsync: started for batchId: " + batchDocumentId);
        try (final XSSFWorkbook sheetWorkbook = this.createSheetWorkbook(fileToProcess)) {
            log.info("TradeAdditionBatchProcessor.processBatchAsync:"
                + " table parsed, start processing  or batchId: " + batchDocumentId);
            final ProcessResult processResult = this.tradeSheetRowProcessor.process(sheetWorkbook, processParameters);
            log.info("TradeAdditionBatchProcessor.processBatchAsync: table parsed for batchId: " + batchDocumentId);
            handleProcessResult(processResult, sheetWorkbook, processParameters);
            log.info("TradeAdditionBatchProcessor.processBatchAsync: completed for batchId: " + batchDocumentId);
        } catch (Exception e) {
            log.error("Failed processing batch: " + tradeDataBatchStatusDocument.getId(), e);
            updateBatchWithFailure(e, tradeDataBatchStatusDocument);
        }
    }

    private void handleProcessResult(
        final ProcessResult processResult,
        final XSSFWorkbook sheetWorkbook,
        final ProcessParameters processParameters
    ) throws IOException {
        final String batchDocumentId = getBatchIdFromParams(processParameters);

        final TradeDataBatchStatusDocument tradeDataBatchStatusDocument = tradeDataBatchStatusDocumentService
            .fetchDocument(batchDocumentId);
        if (processResult.isAnyInvalid()) {
            addProcessResultWarning(tradeDataBatchStatusDocument, processResult);
        }
        final String fileResultId = ssrFilestoreService.createXlsxFile(
            sheetWorkbook, tradeDataBatchStatusDocument.getFolderId()
        );
        updateBatchWithComplete(
            tradeDataBatchStatusDocument,
            processParameters
                .getProcessingParams()
                .get(TradeAdditionGeneralConstants.UPLOADED_FILE_PROCESSING_KEY),
            null,
            fileResultId);
    }

    private String getBatchIdFromParams(final ProcessParameters processParameters) {
        return processParameters
            .getProcessingParams()
            .get(TradeAdditionGeneralConstants.BATCH_DOCUMENT_ID_PROCESSING_KEY);
    }

    private void addProcessResultWarning(final TradeDataBatchStatusDocument tradeDataBatchStatusDocument,
                                         final ProcessResult processResult) {
        tradeDataBatchStatusDocument
            .getDocument()
            .getTradeDataBatchStatusTypeData()
            .setWarnings(processResult.getPrintedResults());
    }

    private void updateBatchWithComplete(final TradeDataBatchStatusDocument tradeDataBatchStatusDocument,
                                         final String uploadedFileId,
                                         final String processId,
                                         final String fileResultId) {
        final TradeDataBatchStatusType tradeDataBatchStatusType = tradeDataBatchStatusDocument.getDocument()
            .getTradeDataBatchStatusTypeData();
        tradeDataBatchStatusType.setUploadedFileId(uploadedFileId);
        tradeDataBatchStatusType.setStatus(BatchProcessingStatus.COMPLETED);
        tradeDataBatchStatusType.setProcessedFileId(fileResultId);
        tradeDataBatchStatusType.setStartedProcessId(processId);
        tradeDataBatchStatusDocumentService.updateDocument(tradeDataBatchStatusDocument.getId(),
            tradeDataBatchStatusDocument,
            true, false, null);
    }

    private void updateBatchWithFailure(final Exception ex,
                                        final TradeDataBatchStatusDocument tradeDataBatchStatusDocument) {
        final ErrorDescription errorDescription = new ErrorDescription();
        errorDescription.setDateTime(LocalDateTime.now());
        errorDescription.setStacktrace(ExceptionUtils.getStackTrace(ex));
        errorDescription.setDescription(ex.getMessage());
        tradeDataBatchStatusDocument.getDocument()
            .getTradeDataBatchStatusTypeData()
            .setErrorDetails(errorDescription);
        tradeDataBatchStatusDocument.getDocument()
            .getTradeDataBatchStatusTypeData()
            .setStatus(BatchProcessingStatus.ERROR);
        tradeDataBatchStatusDocumentService.updateDocument(tradeDataBatchStatusDocument.getId(),
            tradeDataBatchStatusDocument,
            true, false, null);
    }

    private XSSFWorkbook createSheetWorkbook(final byte[] inputFileBytes) throws IOException {
        final InputStream inputStream = new ByteArrayInputStream(inputFileBytes);
        final XSSFWorkbook sheetWorkbook = new XSSFWorkbook(inputStream);
        return sheetWorkbook;
    }
}
