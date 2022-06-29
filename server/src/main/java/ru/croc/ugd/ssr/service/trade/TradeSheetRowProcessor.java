package ru.croc.ugd.ssr.service.trade;

import static ru.croc.ugd.ssr.service.trade.model.TradeAdditionSheetConstants.COMMENT_COLUMN_INDEX;
import static ru.croc.ugd.ssr.service.trade.model.TradeAdditionSheetConstants.DECODING_ADDRESS_NEW_COLUMN_INDEX;
import static ru.croc.ugd.ssr.service.trade.model.TradeAdditionSheetConstants.DECODING_ADDRESS_OLD_COLUMN_INDEX;
import static ru.croc.ugd.ssr.service.trade.model.TradeAdditionSheetConstants.DECODING_FIO_COLUMN_INDEX;
import static ru.croc.ugd.ssr.service.trade.model.TradeAdditionSheetConstants.IS_ROW_RECORDED_INDEX;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.model.trade.TradeDataBatchStatusDocument;
import ru.croc.ugd.ssr.service.document.TradeDataBatchStatusDocumentService;
import ru.croc.ugd.ssr.service.excel.XssfExcelCellProcessor;
import ru.croc.ugd.ssr.service.excel.XssfExcelRowProcessor;
import ru.croc.ugd.ssr.service.excel.model.process.XssfRowProcessResult;
import ru.croc.ugd.ssr.service.excel.model.process.XssfRowValidateResult;
import ru.croc.ugd.ssr.service.trade.model.TradeAdditionGeneralConstants;
import ru.croc.ugd.ssr.service.trade.model.TradeAdditionSheetConstants;
import ru.croc.ugd.ssr.trade.TradeDataBatchStatusType;

import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.function.Consumer;

/**
 * TradeSheetRowProcessor.
 */
@Slf4j
@Service
@AllArgsConstructor
public abstract class TradeSheetRowProcessor extends XssfExcelRowProcessor {
    private final TradeSheetCellProcessor tradeSheetCellProcessor;
    private final TradeDataBatchStatusDocumentService tradeDataBatchStatusDocumentService;
    protected final TradeAdditionValueDecoder tradeAdditionValueDecoder;

    @Override
    protected XssfExcelCellProcessor getCellProcessor() {
        return tradeSheetCellProcessor;
    }

    @Override
    protected void before(final XSSFWorkbook input) {
        super.before(input);
        final int finalAllRowsNumber = retrieveAllRowsNumber(input);
        updateTradeDataBatchStatus((tradeDataBatchStatusDocument) -> {
            tradeDataBatchStatusDocument.getDocument()
                .getTradeDataBatchStatusTypeData()
                .setAllRowsNumber(finalAllRowsNumber);
        });
    }

    protected void updateTradeDataBatchStatus(final Consumer<? super TradeDataBatchStatusDocument> handler) {
        final String batchDocumentId = processParameters
            .getProcessingParams()
            .get(TradeAdditionGeneralConstants.BATCH_DOCUMENT_ID_PROCESSING_KEY);
        final TradeDataBatchStatusDocument tradeDataBatchStatusDocument = tradeDataBatchStatusDocumentService
            .fetchDocument(batchDocumentId);
        handler.accept(tradeDataBatchStatusDocument);
        tradeDataBatchStatusDocumentService.updateDocument(batchDocumentId,
            tradeDataBatchStatusDocument,
            true,
            false,
            null);
    }

    @Override
    protected void preProcessSheet(XSSFSheet input) {
        super.preProcessSheet(input);
        input.setColumnWidth(COMMENT_COLUMN_INDEX.getColumnIndex(), 16000);
        input.setColumnWidth(DECODING_FIO_COLUMN_INDEX.getColumnIndex(), 16000);
        input.setColumnWidth(DECODING_ADDRESS_OLD_COLUMN_INDEX.getColumnIndex(), 16000);
        input.setColumnWidth(DECODING_ADDRESS_NEW_COLUMN_INDEX.getColumnIndex(), 16000);
        input.setColumnWidth(IS_ROW_RECORDED_INDEX.getColumnIndex(), 300);

        tradeAdditionValueDecoder.loadMappings();
    }

    @Override
    protected void processHeaderRow(XSSFRow headerRow) {
        super.processHeaderRow(headerRow);
        XSSFCell commentHeaderCell = headerRow.createCell(COMMENT_COLUMN_INDEX.getColumnIndex());
        commentHeaderCell.setCellValue(COMMENT_COLUMN_INDEX.getColumnReadableName());
        XSSFCell isRowRecordedHeaderCell = headerRow.createCell(IS_ROW_RECORDED_INDEX.getColumnIndex());
        isRowRecordedHeaderCell.setCellValue(IS_ROW_RECORDED_INDEX.getColumnReadableName());
        XSSFCell decodingFioHeaderCell = headerRow.createCell(DECODING_FIO_COLUMN_INDEX.getColumnIndex());
        decodingFioHeaderCell.setCellValue(DECODING_FIO_COLUMN_INDEX.getColumnReadableName());
        XSSFCell decodingAddressOldHeaderCell = headerRow
            .createCell(DECODING_ADDRESS_OLD_COLUMN_INDEX.getColumnIndex());
        decodingAddressOldHeaderCell.setCellValue(DECODING_ADDRESS_OLD_COLUMN_INDEX.getColumnReadableName());
        XSSFCell decodingAddressNewHeaderCell = headerRow
            .createCell(DECODING_ADDRESS_NEW_COLUMN_INDEX.getColumnIndex());
        decodingAddressNewHeaderCell.setCellValue(DECODING_ADDRESS_NEW_COLUMN_INDEX.getColumnReadableName());
    }

    @Override
    protected int getHeaderRowIndex(XSSFSheet sheetToProcess) {
        final int numberOfRows = sheetToProcess.getPhysicalNumberOfRows();
        for (int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {
            final XSSFRow potentialHeaderRow = getRow(sheetToProcess, rowIndex);
            if (isValidHeaderRow(potentialHeaderRow)) {
                return rowIndex;
            }
        }
        return -1;
    }

    @Override
    protected Integer getColumnsNumber(final XSSFRow rowToValidate, final XSSFRow headerRow) {
        return Math.toIntExact(Arrays
            .stream(TradeAdditionSheetConstants.values())
            .filter(tradeAdditionSheetConstants -> checkSheetConstant(headerRow, tradeAdditionSheetConstants))
            .count());
    }

    protected abstract XssfRowProcessResult saveRowAsTradeAddition(final XSSFRow rowToProcess,
                                                                   final XssfRowProcessResult xssfRowProcessResult);

    @Override
    protected XssfRowProcessResult processRow(
        final XSSFRow rowToProcess,
        final XSSFRow headerRow,
        final XssfRowValidateResult xssfRowValidateResult,
        final FileOutputStream logFos
    ) {
        final XssfRowProcessResult xssfRowProcessResult = super.processRow(
            rowToProcess, headerRow, xssfRowValidateResult, null
        );
        saveRowCommentSection(rowToProcess, StringUtils.EMPTY);
        saveRowDecodeFioSection(rowToProcess, StringUtils.EMPTY);
        saveRowDecodeAddressOldSection(rowToProcess, StringUtils.EMPTY);
        saveRowDecodeAddressNewSection(rowToProcess, StringUtils.EMPTY);

        if (!xssfRowValidateResult.isValid()) {
            updateTradeBatchReportInfo(false);
            saveRowCommentSection(rowToProcess, xssfRowValidateResult.getJoinedValidationMessage());
            saveIsRowRecorded(rowToProcess, false);
            return xssfRowProcessResult;
        }

        return saveRowAsTradeAddition(rowToProcess, xssfRowProcessResult);
    }

    @Override
    protected void handleSkipEmptyRow(XSSFRow headerRow) {
        super.handleSkipEmptyRow(headerRow);
        updateTradeBatchReportInfo(false);
    }

    protected void updateTradeBatchReportInfo(final boolean isProcessedOk) {
        updateTradeDataBatchStatus((tradeDataBatchStatusDocument) -> {
            final TradeDataBatchStatusType tradeDataBatchStatusType = tradeDataBatchStatusDocument.getDocument()
                .getTradeDataBatchStatusTypeData();
            final int allRowsNumber = tradeDataBatchStatusType.getAllRowsNumber();
            int loadedOkRowsNumber = tradeDataBatchStatusType.getLoadedOkRowsNumber();
            int loadedNokRowsNumber = tradeDataBatchStatusType.getLoadedNokRowsNumber();
            if (isProcessedOk) {
                loadedOkRowsNumber++;
            } else {
                loadedNokRowsNumber++;
            }
            final int totalLoadedRowsNumber = loadedNokRowsNumber + loadedOkRowsNumber;
            final int currentPercentage = (100 * totalLoadedRowsNumber) / allRowsNumber;
            if (totalLoadedRowsNumber <= allRowsNumber) {
                tradeDataBatchStatusType.setLoadedOkRowsNumber(loadedOkRowsNumber);
                tradeDataBatchStatusType.setLoadedNokRowsNumber(loadedNokRowsNumber);
                tradeDataBatchStatusType.setPercentageReady(currentPercentage);
            }
        });
    }

    protected void saveIsRowRecorded(XSSFRow rowToUpdate, boolean isRecorded) {
        saveCell(rowToUpdate, IS_ROW_RECORDED_INDEX.getColumnIndex(), isRecorded ? "Да" : "Нет");
    }

    protected void saveRowCommentSection(XSSFRow rowToUpdate, String newValue) {
        saveCell(rowToUpdate, COMMENT_COLUMN_INDEX.getColumnIndex(), newValue);
    }

    protected void saveRowDecodeFioSection(XSSFRow rowToUpdate, String newValue) {
        saveCell(rowToUpdate, DECODING_FIO_COLUMN_INDEX.getColumnIndex(), newValue);
    }

    protected void saveRowDecodeAddressOldSection(XSSFRow rowToUpdate, String newValue) {
        saveCell(rowToUpdate, DECODING_ADDRESS_OLD_COLUMN_INDEX.getColumnIndex(), newValue);
    }

    protected void saveRowDecodeAddressNewSection(XSSFRow rowToUpdate, String newValue) {
        saveCell(rowToUpdate, DECODING_ADDRESS_NEW_COLUMN_INDEX.getColumnIndex(), newValue);
    }

    protected String getCommentRowValue(XSSFRow row) {
        return getCellValue(row
            .getCell(COMMENT_COLUMN_INDEX.getColumnIndex()));
    }

    private boolean isValidHeaderRow(final XSSFRow potentialHeaderRow) {
        if (potentialHeaderRow == null) {
            return false;
        }
        return Arrays.stream(TradeAdditionSheetConstants.values())
            .filter(tradeAdditionSheetConstants -> checkSheetConstant(potentialHeaderRow, tradeAdditionSheetConstants))
            .allMatch(tradeAdditionSheetConstants -> {
                final String cellValueToValidate = getCellValue(potentialHeaderRow
                    .getCell(tradeAdditionSheetConstants.getColumnIndex()));
                return StringUtils.containsIgnoreCase(cellValueToValidate,
                    tradeAdditionSheetConstants.getColumnOriginalPartName());
            });
    }

    private boolean checkSheetConstant(
        final XSSFRow headerRow, final TradeAdditionSheetConstants tradeAdditionSheetConstants
    ) {
        return tradeAdditionSheetConstants.getIsOriginalHeader()
            && existsColumn(headerRow, tradeAdditionSheetConstants);
    }

    private boolean existsColumn(final XSSFRow headerRow, final TradeAdditionSheetConstants sheetConstant) {
        if (!sheetConstant.equals(TradeAdditionSheetConstants.SELL_ID_COLUMN_INDEX)) {
            return true;
        } else {
            final XSSFCell xssfCell = headerRow.getCell(sheetConstant.getColumnIndex());
            return sheetConstant.getColumnOriginalPartName().equals(getCellValue(xssfCell));
        }
    }
}
