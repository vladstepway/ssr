package ru.croc.ugd.ssr.service.flowerrorreport.print;

import static ru.croc.ugd.ssr.service.flowerrorreport.FlowErrorReportService.NOT_FOUND_ERROR_TYPE;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import ru.croc.ugd.ssr.flowreportederror.FlowReportedErrorData;
import ru.croc.ugd.ssr.service.excel.XssfExcelDataPrinter;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class FlowErrorExcelDataPrinter extends XssfExcelDataPrinter<FlowReportedErrorData> {
    protected void beforePrintSheet(
        final XSSFWorkbook workbook,
        final Sheet sheet,
        final List<FlowReportedErrorData> input,
        final Map<String, String> generalValues
    ) {
        final int lastRowIndex = sheet.getLastRowNum();
        final Row dataRowFirst = sheet.createRow(lastRowIndex + 1);
        final Row dataRowSecond = sheet.createRow(lastRowIndex + 2);
        sheet.addMergedRegion(CellRangeAddress.valueOf("A" + (dataRowFirst.getRowNum() + 1)
            + getLastColumnToMergeForPreHeader(input)
            + (dataRowFirst.getRowNum() + 1)));
        sheet.addMergedRegion(CellRangeAddress.valueOf("A" + (dataRowSecond.getRowNum() + 1)
            + getLastColumnToMergeForPreHeader(input)
            + (dataRowSecond.getRowNum() + 1)));

        final Cell cellFirstRow = dataRowFirst.createCell(0);
        final Cell cellSecondRow = dataRowSecond.createCell(0);

        prepareStylesForPreHeader(workbook, cellFirstRow, cellSecondRow);

        if (!CollectionUtils.isEmpty(input)) {
            if (input.get(0).getReportedDate() != null) {
                cellFirstRow.setCellValue("Отчет за "
                    + input.get(0).getReportedDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                    + " об ошибках получения данных из ДГИ");
            }
            cellSecondRow.setCellValue("Тип ошибки: " + input.get(0).getErrorType());
        }
    }

    private void prepareStylesForPreHeader(
        final XSSFWorkbook workbook,
        final Cell cellFirstRow,
        final Cell cellSecondRow
    ) {
        final XSSFCellStyle cellFirstStyle = workbook.createCellStyle();
        final XSSFCellStyle cellSecondStyle = workbook.createCellStyle();


        Font cellFirstFont = workbook.createFont();
        cellFirstFont.setBold(true);
        cellFirstFont.setFontHeightInPoints((short) 25);
        cellFirstStyle.setFont(cellFirstFont);
        cellFirstStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellFirstStyle.setAlignment(HorizontalAlignment.CENTER);

        Font cellSecondFont = workbook.createFont();
        cellSecondFont.setBold(true);
        cellSecondFont.setFontHeightInPoints((short) 20);
        cellSecondStyle.setFont(cellSecondFont);
        cellSecondStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellSecondStyle.setAlignment(HorizontalAlignment.CENTER);

        cellFirstRow.setCellStyle(cellFirstStyle);
        cellSecondRow.setCellStyle(cellSecondStyle);
    }

    private String getLastColumnToMergeForPreHeader(
        final List<FlowReportedErrorData> input) {
        if (CollectionUtils.isEmpty(input)) {
            return ":G";
        }
        if (NOT_FOUND_ERROR_TYPE.equals(input.get(0).getErrorType())) {
            return ":C";
        }
        return ":G";
    }
}
