package ru.croc.ugd.ssr.report.defects;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.dto.apartmentdefectconfirmation.RestDefectDto;
import ru.croc.ugd.ssr.service.excel.XssfExcelDataPrinter;

import java.util.List;
import java.util.Map;

@Service
public class DefectExcelDataPrinter extends XssfExcelDataPrinter<RestDefectDto> {

    public static final String ADDRESS = "address";
    public static final String AREA_AND_DISTRICT = "areaAndDistrict";

    @Override
    protected void beforePrintSheet(
        final XSSFWorkbook workbook,
        final Sheet sheet,
        final List<RestDefectDto> input,
        final Map<String, String> generalValues
    ) {
        final int lastRowIndex = sheet.getLastRowNum();
        final Row titleRow = sheet.createRow(lastRowIndex + 1);
        final Row addressRow = sheet.createRow(lastRowIndex + 2);
        final Row areaAndDistrictRow = sheet.createRow(lastRowIndex + 3);
        sheet.createRow(lastRowIndex + 4);

        final Cell titleCell = titleRow.createCell(0);
        final Cell addressCell = addressRow.createCell(0);
        final Cell areaAndDistrictCell = areaAndDistrictRow.createCell(0);

        prepareStyleForTitleCell(workbook, titleCell);
        prepareStyleForAddressCell(workbook, addressCell);

        titleCell.setCellValue("Перечень дефектов");
        addressCell.setCellValue(getValue(generalValues, ADDRESS));
        generalValues.remove(ADDRESS);
        areaAndDistrictCell.setCellValue(getValue(generalValues, AREA_AND_DISTRICT));
        generalValues.remove(AREA_AND_DISTRICT);

        if (!generalValues.isEmpty()) {
            generalValues.forEach((key, value) -> {
                final Row additionalRow = sheet.createRow(sheet.getLastRowNum() + 1);

                final Cell keyCell = additionalRow.createCell(0);
                keyCell.setCellValue(key);

                additionalRow.createCell(1);

                final Cell valueCell = additionalRow.createCell(2);
                valueCell.setCellValue(value);
            });
            sheet.createRow(sheet.getLastRowNum() + 1);
        }
    }

    private String getValue(final Map<String, String> generalValues, final String key) {
        try {
            return generalValues.get(key);
        } catch (Exception e) {
            return null;
        }
    }

    private void prepareStyleForTitleCell(final XSSFWorkbook workbook, final Cell cell) {
        final XSSFCellStyle cellStyle = workbook.createCellStyle();
        Font cellFont = workbook.createFont();
        cellFont.setBold(true);
        cellFont.setFontHeightInPoints((short) 13);
        cellStyle.setFont(cellFont);
        cell.setCellStyle(cellStyle);
    }

    private void prepareStyleForAddressCell(final XSSFWorkbook workbook, final Cell cell) {
        final XSSFCellStyle cellStyle = workbook.createCellStyle();
        Font cellFont = workbook.createFont();
        cellFont.setBold(true);
        cellStyle.setFont(cellFont);
        cell.setCellStyle(cellStyle);
    }
}
