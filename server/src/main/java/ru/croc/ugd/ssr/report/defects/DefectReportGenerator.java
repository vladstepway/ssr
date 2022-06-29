package ru.croc.ugd.ssr.report.defects;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.dto.apartmentdefectconfirmation.RestDefectDto;
import ru.croc.ugd.ssr.exception.WorkbookNotCreatedException;
import ru.croc.ugd.ssr.service.excel.model.print.Column;
import ru.croc.ugd.ssr.service.excel.model.print.PrintColumnSettings;
import ru.croc.ugd.ssr.service.excel.model.print.PrintSettings;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
@AllArgsConstructor
public class DefectReportGenerator {

    private final DefectExcelDataPrinter xssfExcelDataPrinter;

    public byte[] printDefectReport(
        final String unom, final List<RestDefectDto> restDefectDtos, final Map<String, String> generalValues
    ) {
        try (
            final ByteArrayOutputStream result = new ByteArrayOutputStream();
            final XSSFWorkbook workbookExtracted = getWorkbook(unom, restDefectDtos, generalValues)
        ) {
            if (workbookExtracted == null) {
                throw new WorkbookNotCreatedException();
            }
            workbookExtracted.write(result);
            return result.toByteArray();
        } catch (IOException e) {
            throw new WorkbookNotCreatedException();
        }
    }

    private XSSFWorkbook getWorkbook(
        final String unom, final List<RestDefectDto> restDefectDtos, final Map<String, String> generalValues
    ) {
        final AtomicReference<XSSFWorkbook> workbookRef = new AtomicReference<>();
        final PrintSettings<RestDefectDto> printSettings = PrintSettings.<RestDefectDto>builder()
            .printColumnSettings(getSharedColumnSettings())
            .sheetTitleName(unom)
            .build();
        workbookRef.set(
            xssfExcelDataPrinter.printData(restDefectDtos, printSettings, generalValues)
        );
        return workbookRef.get();
    }

    private static PrintColumnSettings<RestDefectDto> getSharedColumnSettings() {
        final PrintColumnSettings<RestDefectDto> printColumnSettings = PrintColumnSettings.<RestDefectDto>builder()
            .cellDefaultStyleFunction(workbook -> {
                CellStyle cellStyle = workbook.createCellStyle();
                cellStyle.setWrapText(true);
                return cellStyle;
            })
            .headerDefaultStyleFunction(workbook -> {
                CellStyle headerCellStyle = workbook.createCellStyle();
                Font cellFont = workbook.createFont();
                cellFont.setBold(true);
                headerCellStyle.setFont(cellFont);

                headerCellStyle.setWrapText(true);

                headerCellStyle.setBorderBottom(BorderStyle.MEDIUM);
                headerCellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
                headerCellStyle.setBorderTop(BorderStyle.MEDIUM);
                headerCellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());

                headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
                headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

                headerCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());

                return headerCellStyle;
            })
            .headerRowHeight(3)
            .build();

        Arrays.stream(DefectReportHeaderType.values())
            .forEach(headerType -> printColumnSettings.setColumn(
                headerType.getColumnIndex(),
                Column.<RestDefectDto>builder()
                    .headerTitle(headerType.getColumnName())
                    .width(headerType.getColumnWidth())
                    .dataExtractor(headerType.getDataExtractor())
                    .build()
            ));

        return printColumnSettings;
    }
}
