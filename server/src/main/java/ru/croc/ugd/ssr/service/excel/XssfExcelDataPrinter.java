package ru.croc.ugd.ssr.service.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.service.excel.model.print.PrintColumnSettings;
import ru.croc.ugd.ssr.service.excel.model.print.PrintSettings;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

@Service
public class XssfExcelDataPrinter<DATA_TYPE> implements ExcelDataPrinter<DATA_TYPE, XSSFWorkbook> {
    protected CellStyle defaultHeaderStyle;
    protected CellStyle defaultRowCellStyle;

    private final AtomicInteger currentProcessingRowIndex = new AtomicInteger(0);

    @Override
    public XSSFWorkbook printData(
        final List<DATA_TYPE> input,
        final PrintSettings<DATA_TYPE> settings
    ) {
        return printData(input, settings, null);
    }

    @Override
    public XSSFWorkbook printData(
        final List<DATA_TYPE> input,
        final PrintSettings<DATA_TYPE> settings,
        final Map<String, String> generalValues
    ) {
        final XSSFWorkbook newWorkbook = new XSSFWorkbook();
        final Sheet sheet = newWorkbook.createSheet(settings.getSheetTitleName());
        return printDataInternal(newWorkbook, sheet, input, settings, generalValues);
    }

    @Override
    public XSSFWorkbook printDataToNewSheet(
        final XSSFWorkbook tableInput,
        final List<DATA_TYPE> input,
        final PrintSettings<DATA_TYPE> settings
    ) {
        final Sheet sheet = tableInput.createSheet(settings.getSheetTitleName());
        return printDataInternal(tableInput, sheet, input, settings, null);
    }

    protected void beforePrintSheet(
        final XSSFWorkbook workbook,
        final Sheet sheet,
        final List<DATA_TYPE> input,
        final Map<String, String> generalValues
    ) {
    }

    private void beforePrintSheetInternal(
        final XSSFWorkbook workbook,
        final Sheet sheet,
        final List<DATA_TYPE> input,
        final Map<String, String> generalValues
    ) {
        beforePrintSheet(workbook, sheet, input, generalValues);
        currentProcessingRowIndex.set(sheet.getLastRowNum() + 1);
    }

    private XSSFWorkbook printDataInternal(
        final XSSFWorkbook tableInput,
        final Sheet sheet,
        final List<DATA_TYPE> input,
        final PrintSettings<DATA_TYPE> settings,
        final Map<String, String> generalValues
    ) {
        currentProcessingRowIndex.set(sheet.getLastRowNum() + 1);
        initStyles(tableInput, settings.getPrintColumnSettings());
        beforePrintSheetInternal(tableInput, sheet, input, generalValues);
        createHeader(sheet, settings.getPrintColumnSettings());
        createRows(input, sheet, settings);
        return tableInput;
    }

    private void initStyles(
        final Workbook workbook,
        final PrintColumnSettings printColumnSettings
    ) {
        final Function<Workbook, CellStyle> workbookCellStyleFunction = printColumnSettings
            .getCellDefaultStyleFunction();
        final Function<Workbook, CellStyle> workbookHeaderStyleFunction = printColumnSettings
            .getHeaderDefaultStyleFunction();
        if (workbookCellStyleFunction != null) {
            this.defaultRowCellStyle = workbookCellStyleFunction.apply(workbook);
        }
        if (workbookHeaderStyleFunction != null) {
            this.defaultHeaderStyle = workbookHeaderStyleFunction.apply(workbook);
        }
    }

    private void createRows(
        final List<DATA_TYPE> input,
        final Sheet sheet,
        final PrintSettings<DATA_TYPE> printSettings
    ) {
        for (DATA_TYPE inputItem : input) {
            final Row dataRow = sheet.createRow(currentProcessingRowIndex.getAndIncrement());
            if (printSettings.getPrintColumnSettings().getTableRowHeight() != 0) {
                dataRow.setHeightInPoints(printSettings.getPrintColumnSettings().getTableRowHeight()
                    * sheet.getDefaultRowHeightInPoints());
            }
            for (int currentColumnIndex = 0;
                 currentColumnIndex <= printSettings.getPrintColumnSettings().getMaxRowIndex();
                 currentColumnIndex++) {
                final Cell currentRowCell = dataRow.createCell(currentColumnIndex);
                currentRowCell.setCellStyle(defaultRowCellStyle);
                currentRowCell.setCellValue(printSettings
                    .getPrintColumnSettings()
                    .getCellValue(currentColumnIndex, inputItem));
            }
        }
    }

    private void createHeader(
        final Sheet sheet,
        final PrintColumnSettings<DATA_TYPE> printColumnSettings
    ) {
        final Row headerRow = sheet.createRow(currentProcessingRowIndex.getAndIncrement());
        for (int currentHeaderRow = 0;
             currentHeaderRow <= printColumnSettings.getMaxRowIndex();
             currentHeaderRow++) {
            final int newColumnWidth = printColumnSettings.getCellWidth(currentHeaderRow);
            if (newColumnWidth > 0 && newColumnWidth > sheet.getColumnWidth(currentHeaderRow)) {
                sheet.setColumnWidth(currentHeaderRow, newColumnWidth);
            }
            if (printColumnSettings.getHeaderRowHeight() != 0) {
                headerRow.setHeightInPoints(printColumnSettings.getHeaderRowHeight()
                    * sheet.getDefaultRowHeightInPoints());
            }
            final Cell headerCell = headerRow.createCell(currentHeaderRow);
            headerCell.setCellStyle(defaultHeaderStyle);
            if (printColumnSettings.containsIndex(currentHeaderRow)) {
                headerCell.setCellValue(printColumnSettings
                    .getIndexColumnMap()
                    .get(currentHeaderRow)
                    .getHeaderTitle()
                );
            }
        }
    }
}
