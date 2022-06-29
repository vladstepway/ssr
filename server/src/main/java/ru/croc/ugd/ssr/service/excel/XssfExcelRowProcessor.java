package ru.croc.ugd.ssr.service.excel;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import ru.croc.ugd.ssr.service.excel.model.process.ProcessParameters;
import ru.croc.ugd.ssr.service.excel.model.process.ProcessResult;
import ru.croc.ugd.ssr.service.excel.model.process.XssfCellProcessResult;
import ru.croc.ugd.ssr.service.excel.model.process.XssfCellValidateResult;
import ru.croc.ugd.ssr.service.excel.model.process.XssfRowProcessResult;
import ru.croc.ugd.ssr.service.excel.model.process.XssfRowValidateResult;
import ru.croc.ugd.ssr.service.validator.model.ValidationResult;

import java.io.FileOutputStream;

public abstract class XssfExcelRowProcessor implements ExcelProcessor<XSSFWorkbook> {
    protected ProcessParameters processParameters;
    protected final DataFormatter formatter;

    protected XssfExcelRowProcessor() {
        formatter = new DataFormatter();
        formatter.addFormat("m/d/yy", new java.text.SimpleDateFormat("dd.MM.yyyy"));
    }

    @Override
    public ProcessResult process(final XSSFWorkbook input, final ProcessParameters processParameters) {
        return process(input, processParameters, null);
    }

    @Override
    public ProcessResult process(final XSSFWorkbook input, final FileOutputStream logFos) {
        return process(input, null, logFos);
    }

    private ProcessResult process(
        final XSSFWorkbook input, final ProcessParameters processParameters, final FileOutputStream logFos
    ) {
        this.processParameters = processParameters;
        final ProcessResult processResult = new ProcessResult();
        this.before(input);

        final int numberOfSheets = input.getNumberOfSheets();
        for (int currentSheetIndex = 0; currentSheetIndex < numberOfSheets; currentSheetIndex++) {
            final XSSFSheet sheetToProcess = input.getSheetAt(currentSheetIndex);
            final ValidationResult validationResult = processSheet(sheetToProcess, logFos);
            processResult.putSheetValidationResult(currentSheetIndex, validationResult);
        }

        this.after(input);
        return processResult;
    }

    protected abstract XssfExcelCellProcessor getCellProcessor();

    protected void before(final XSSFWorkbook input) {
    }

    protected void after(final XSSFWorkbook input) {
    }

    protected void preProcessSheet(final XSSFSheet input) {
    }

    protected void processHeaderRow(final XSSFRow headerRow) {
    }

    protected void handleSkipEmptyRow(final XSSFRow headerRow) {
    }

    protected int getHeaderRowIndex(final XSSFSheet sheetToProcess) {
        return 0;
    }

    protected boolean skipProcessingIfInvalidRow() {
        return false;
    }

    /**
     * Take care of your columns amount. Physical number can be less then last index if first cells are null.
     */
    protected Integer getColumnsNumber(final XSSFRow rowToValidate, final XSSFRow headerRow) {
        return rowToValidate.getPhysicalNumberOfCells();
    }

    protected XssfRowValidateResult validateRow(final XSSFRow rowToValidate, final XSSFRow headerRow) {
        Assert.notNull(getCellProcessor(), "Cell processor is null");
        final XssfRowValidateResult xssfRowValidateResult = new XssfRowValidateResult();
        final int cellsNumber = getColumnsNumber(rowToValidate, headerRow);
        for (int cellIndex = 0; cellIndex < cellsNumber; cellIndex++) {
            final XSSFCell xssfCell = rowToValidate.getCell(cellIndex);
            final XssfCellValidateResult xssfCellValidateResult = getCellProcessor()
                .validateCell(xssfCell, cellIndex);
            xssfRowValidateResult.addValidatorUnitResults(xssfCellValidateResult);
        }
        return xssfRowValidateResult;
    }

    protected XssfRowProcessResult processRow(
        final XSSFRow rowToProcess,
        final XSSFRow headerRow,
        final XssfRowValidateResult xssfRowValidateResult,
        final FileOutputStream logFos
    ) {
        final XssfRowProcessResult xssfRowProcessResult = new XssfRowProcessResult();
        final int cellsNumber = getColumnsNumber(rowToProcess, headerRow);
        for (int cellIndex = 0; cellIndex < cellsNumber; cellIndex++) {
            final XSSFCell xssfCell = rowToProcess.getCell(cellIndex);
            if (xssfCell != null) {
                final XssfCellProcessResult xssfCellProcessResult = getCellProcessor()
                    .processCell(xssfCell, cellIndex, rowToProcess.getRowNum());
                xssfRowProcessResult.addXssfCellProcessResult(xssfCellProcessResult);
            }
        }
        return xssfRowProcessResult;
    }

    protected ValidationResult preValidateSheet(final XSSFSheet input) {
        final int numberOfRows = input.getPhysicalNumberOfRows();
        final int headerIndex = this.getHeaderRowIndex(input);
        if (headerIndex < 0) {
            return ValidationResult.fail("Не найден заголовок");
        }
        final int requiredMinNumberOfRows = headerIndex + 1;
        if (numberOfRows - requiredMinNumberOfRows <= 0) {
            return ValidationResult.fail("В таблице нет данных");
        }
        return ValidationResult.ok();
    }

    protected int getNumberOfActiveRows(final XSSFSheet sheet) {
        return NumberUtils.max(sheet.getLastRowNum(),
            sheet.getPhysicalNumberOfRows());
    }

    protected String getCellValue(final XSSFCell xssfCell) {
        return xssfCell == null ? null : formatter.formatCellValue(xssfCell);
    }

    protected XSSFRow getRow(final XSSFSheet sheetToProcess, final int rowIndex) {
        final XSSFRow processingRow = sheetToProcess.getRow(rowIndex);
        return processingRow;
    }

    private ValidationResult processSheet(final XSSFSheet sheetToProcess, final FileOutputStream logFos) {
        this.preProcessSheet(sheetToProcess);
        final ValidationResult preValidateSheetResult = this.preValidateSheet(sheetToProcess);
        if (!preValidateSheetResult.isValid()) {
            return preValidateSheetResult;
        }
        final int numberOfRows = getNumberOfActiveRows(sheetToProcess);
        final int headerRowIndex = getHeaderRowIndex(sheetToProcess);
        final XSSFRow headerRow = getRow(sheetToProcess, headerRowIndex);
        this.processHeaderRow(headerRow);
        for (int rowIndex = headerRowIndex + 1; rowIndex < numberOfRows; rowIndex++) {
            final XSSFRow processingRow = getRow(sheetToProcess, rowIndex);
            if (processingRow == null || areAllPhysicalValuesEmptyOrNull(processingRow)) {
                handleSkipEmptyRow(processingRow);
                continue;
            }
            final XssfRowValidateResult xssfRowValidateResult = this.validateRow(processingRow, headerRow);
            final boolean isValidRow = xssfRowValidateResult.isValid();
            if (isValidRow || !this.skipProcessingIfInvalidRow()) {
                this.processRow(processingRow, headerRow, xssfRowValidateResult, logFos);
            }
        }
        return preValidateSheetResult;
    }

    private boolean areAllPhysicalValuesEmptyOrNull(final XSSFRow cells) {
        final short allNumberCells = cells.getLastCellNum();
        boolean isAnyNotNull = false;
        for (int i = 0; i < allNumberCells; i++) {
            if (cells.getCell(i) != null && !StringUtils.isEmpty(getCellValue(cells.getCell(i)))) {
                isAnyNotNull = true;
            }
        }
        return !isAnyNotNull;
    }

    protected int retrieveAllRowsNumber(final XSSFWorkbook input) {
        final int numberOfSheets = input.getNumberOfSheets();
        int allRowsNumber = 0;
        for (int currentSheetIndex = 0; currentSheetIndex < numberOfSheets; currentSheetIndex++) {
            final XSSFSheet currentSheet = input.getSheetAt(currentSheetIndex);
            final int headerRows = getHeaderRowIndex(currentSheet) + 1;
            allRowsNumber += getNumberOfActiveRows(currentSheet) - headerRows;
        }
        return allRowsNumber;
    }

    protected void saveCell(final XSSFRow rowToUpdate, final Integer cellIndex, final String newValue) {
        XSSFCell cellToUpdate = rowToUpdate.getCell(cellIndex);
        if (cellToUpdate == null) {
            cellToUpdate = rowToUpdate.createCell(cellIndex);
        }
        cellToUpdate.setCellValue(newValue);
    }

    protected String getPageNameFromRow(final XSSFRow row) {
        if (row == null || row.getSheet() == null) {
            return null;
        }
        return row.getSheet().getSheetName();
    }
}
