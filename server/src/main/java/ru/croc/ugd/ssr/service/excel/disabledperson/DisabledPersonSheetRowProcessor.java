package ru.croc.ugd.ssr.service.excel.disabledperson;

import static ru.croc.ugd.ssr.service.disabledperson.DisabledPersonImportProcessor.DOCUMENT_ID_PROCESSING_KEY;
import static ru.croc.ugd.ssr.service.excel.disabledperson.model.DisabledPersonSheetConstants.COMMENT_COLUMN_INDEX;
import static ru.croc.ugd.ssr.service.excel.disabledperson.model.DisabledPersonSheetConstants.IS_ROW_RECORDED_INDEX;

import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.disabledperson.DisabledPersonImportData;
import ru.croc.ugd.ssr.model.disabledperson.DisabledPersonImportDocument;
import ru.croc.ugd.ssr.service.document.DisabledPersonImportDocumentService;
import ru.croc.ugd.ssr.service.excel.XssfExcelCellProcessor;
import ru.croc.ugd.ssr.service.excel.XssfExcelRowProcessor;
import ru.croc.ugd.ssr.service.excel.disabledperson.model.DisabledPersonSheetConstants;
import ru.croc.ugd.ssr.service.excel.model.process.XssfRowProcessResult;
import ru.croc.ugd.ssr.service.excel.model.process.XssfRowValidateResult;

import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.function.Consumer;

@Service
@AllArgsConstructor
public abstract class DisabledPersonSheetRowProcessor extends XssfExcelRowProcessor {

    private final DisabledPersonSheetCellProcessor disabledPersonSheetCellProcessor;
    private final DisabledPersonImportDocumentService disabledPersonImportDocumentService;

    @Override
    protected XssfExcelCellProcessor getCellProcessor() {
        return disabledPersonSheetCellProcessor;
    }

    @Override
    protected void before(final XSSFWorkbook input) {
        super.before(input);
        final int finalAllRowsNumber = retrieveAllRowsNumber(input);
        updateDisabledPersonImportDocument(disabledPersonImportDocument -> disabledPersonImportDocument.getDocument()
            .getDisabledPersonImportData()
            .setAllRowsNumber(finalAllRowsNumber)
        );
    }

    @Override
    protected void preProcessSheet(XSSFSheet input) {
        super.preProcessSheet(input);
        input.setColumnWidth(COMMENT_COLUMN_INDEX.getColumnIndex(), 16000);
        input.setColumnWidth(IS_ROW_RECORDED_INDEX.getColumnIndex(), 3000);
    }

    @Override
    protected void processHeaderRow(XSSFRow headerRow) {
        super.processHeaderRow(headerRow);
        createHeaderCell(COMMENT_COLUMN_INDEX, headerRow);
        createHeaderCell(IS_ROW_RECORDED_INDEX, headerRow);
    }

    private void createHeaderCell(
        final DisabledPersonSheetConstants disabledPersonSheetConstant, final XSSFRow headerRow
    ) {
        final XSSFCell cell = headerRow.createCell(disabledPersonSheetConstant.getColumnIndex());
        cell.setCellValue(disabledPersonSheetConstant.getColumnReadableName());
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
            .stream(DisabledPersonSheetConstants.values())
            .filter(DisabledPersonSheetConstants::getIsOriginalHeader)
            .count());
    }

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

        if (!xssfRowValidateResult.isValid()) {
            updateDisabledPersonImportReportInfo(false);
            saveRowCommentSection(rowToProcess, xssfRowValidateResult.getJoinedValidationMessage());
            saveIsRowRecorded(rowToProcess, false);
            return xssfRowProcessResult;
        }

        return saveRow(rowToProcess, xssfRowProcessResult);
    }

    @Override
    protected void handleSkipEmptyRow(XSSFRow headerRow) {
        super.handleSkipEmptyRow(headerRow);
        updateDisabledPersonImportReportInfo(false);
    }

    protected abstract XssfRowProcessResult saveRow(
        final XSSFRow rowToProcess, final XssfRowProcessResult xssfRowProcessResult
    );

    protected void updateDisabledPersonImportReportInfo(final boolean isProcessedOk) {
        updateDisabledPersonImportDocument(disabledPersonImportDocument -> {
            final DisabledPersonImportData disabledPersonImportData = disabledPersonImportDocument.getDocument()
                .getDisabledPersonImportData();
            final int allRowsNumber = disabledPersonImportData.getAllRowsNumber();
            int loadedOkRowsNumber = disabledPersonImportData.getLoadedOkRowsNumber();
            int loadedNokRowsNumber = disabledPersonImportData.getLoadedNokRowsNumber();
            if (isProcessedOk) {
                loadedOkRowsNumber++;
            } else {
                loadedNokRowsNumber++;
            }
            final int totalLoadedRowsNumber = loadedNokRowsNumber + loadedOkRowsNumber;
            final int currentPercentage = (100 * totalLoadedRowsNumber) / allRowsNumber;
            if (totalLoadedRowsNumber <= allRowsNumber) {
                disabledPersonImportData.setLoadedOkRowsNumber(loadedOkRowsNumber);
                disabledPersonImportData.setLoadedNokRowsNumber(loadedNokRowsNumber);
                disabledPersonImportData.setPercentageReady(currentPercentage);
            }
        });
    }

    protected void updateDisabledPersonImportDocument(
        final Consumer<DisabledPersonImportDocument> disabledPersonImportDocumentConsumer
    ) {
        final String documentId = processParameters.getProcessingParams().get(DOCUMENT_ID_PROCESSING_KEY);
        final DisabledPersonImportDocument disabledPersonImportDocument =
            disabledPersonImportDocumentService.fetchDocument(documentId);
        disabledPersonImportDocumentConsumer.accept(disabledPersonImportDocument);
        disabledPersonImportDocumentService.updateDocument(disabledPersonImportDocument, "updateBySheetRowProcessor");
    }

    protected void saveIsRowRecorded(XSSFRow rowToUpdate, boolean isRecorded) {
        saveCell(rowToUpdate, IS_ROW_RECORDED_INDEX.getColumnIndex(), isRecorded ? "Да" : "Нет");
    }

    protected void saveRowCommentSection(XSSFRow rowToUpdate, String newValue) {
        saveCell(rowToUpdate, COMMENT_COLUMN_INDEX.getColumnIndex(), newValue);
    }

    protected String getCommentRowValue(XSSFRow row) {
        return getCellValue(
            row.getCell(COMMENT_COLUMN_INDEX.getColumnIndex())
        );
    }

    private boolean isValidHeaderRow(final XSSFRow potentialHeaderRow) {
        if (potentialHeaderRow == null) {
            return false;
        }
        return Arrays.stream(DisabledPersonSheetConstants.values())
            .filter(DisabledPersonSheetConstants::getIsOriginalHeader)
            .allMatch(disabledPersonSheetConstant -> {
                final String cellValueToValidate = getCellValue(
                    potentialHeaderRow.getCell(disabledPersonSheetConstant.getColumnIndex())
                );
                return StringUtils.containsIgnoreCase(
                    cellValueToValidate, disabledPersonSheetConstant.getColumnOriginalPartName()
                );
            });
    }
}
