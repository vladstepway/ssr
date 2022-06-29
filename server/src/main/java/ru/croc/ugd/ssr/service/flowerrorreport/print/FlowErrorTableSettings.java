package ru.croc.ugd.ssr.service.flowerrorreport.print;

import static java.util.Optional.ofNullable;
import static ru.croc.ugd.ssr.service.flowerrorreport.FlowErrorReportService.FOUND_SEVERAL_ERROR_TYPE;
import static ru.croc.ugd.ssr.service.flowerrorreport.FlowErrorReportService.NOT_FOUND_ERROR_TYPE;
import static ru.croc.ugd.ssr.service.flowerrorreport.FlowErrorReportService.WRONG_COMMUNAL_LIVER_ERROR_TYPE;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import ru.croc.ugd.ssr.flowreportederror.FlowReportedErrorData;
import ru.croc.ugd.ssr.flowreportederror.PersonInfoType;
import ru.croc.ugd.ssr.service.excel.model.print.Column;
import ru.croc.ugd.ssr.service.excel.model.print.PrintColumnSettings;
import ru.croc.ugd.ssr.service.excel.model.print.PrintSettings;

import java.util.function.Function;

public class FlowErrorTableSettings {
    public static PrintSettings<FlowReportedErrorData> getPrintSettingForData(
        final FlowReportedErrorData flowReportedErrorData) {
        if (FOUND_SEVERAL_ERROR_TYPE.equals(flowReportedErrorData.getErrorType())) {
            return getDuplicatePrintSetting();
        }
        if (NOT_FOUND_ERROR_TYPE.equals(flowReportedErrorData.getErrorType())) {
            return getNotFoundPrintSetting();
        }
        if (WRONG_COMMUNAL_LIVER_ERROR_TYPE.equals(flowReportedErrorData.getErrorType())) {
            return getWrongCommunalPrintSetting();
        }
        return getSharedPrintSetting("Отчет");
    }

    private static PrintSettings<FlowReportedErrorData> getSharedPrintSetting(final String sheetTitleName) {
        return PrintSettings
            .<FlowReportedErrorData>builder()
            .printColumnSettings(getSharedColumnSettings())
            .sheetTitleName(sheetTitleName)
            .build();
    }

    private static PrintSettings<FlowReportedErrorData> getDuplicatePrintSetting() {
        final PrintSettings<FlowReportedErrorData> printSettings = getSharedPrintSetting(FOUND_SEVERAL_ERROR_TYPE);
        printSettings
            .getPrintColumnSettings()
            .setColumn(3, buildColumn(
                "ФИО жителя №1",
                15000,
                getExtractPersonFioLinkFunction(FlowReportedErrorData::getPersonFirst)))
            .setColumn(4, buildColumn(
                "Адрес отселяемого дома жителя №1",
                12000,
                flowReportedErrorData -> ofNullable(flowReportedErrorData)
                    .map(FlowReportedErrorData::getPersonFirst)
                    .map(PersonInfoType::getAddressFrom)
                    .orElse(null)))
            .setColumn(5, buildColumn(
                "ФИО жителя №2",
                15000,
                getExtractPersonFioLinkFunction(FlowReportedErrorData::getPersonSecond)))
            .setColumn(6, buildColumn(
                "Адрес отселяемого дома жителя №2",
                12000,
                flowReportedErrorData -> ofNullable(flowReportedErrorData)
                    .map(FlowReportedErrorData::getPersonSecond)
                    .map(PersonInfoType::getAddressFrom)
                    .orElse(null)));
        return printSettings;
    }

    private static PrintSettings<FlowReportedErrorData> getNotFoundPrintSetting() {
        final PrintSettings<FlowReportedErrorData> printSettings = getSharedPrintSetting(NOT_FOUND_ERROR_TYPE);
        printSettings
            .getPrintColumnSettings()
            .setColumn(0, buildColumn(
                "Вид полученных сведений \n(№ потока)",
                25000,
                FlowReportedErrorData::getFlowType));
        return printSettings;
    }

    private static PrintSettings<FlowReportedErrorData> getWrongCommunalPrintSetting() {
        final PrintSettings<FlowReportedErrorData> printSettings =
            getSharedPrintSetting(WRONG_COMMUNAL_LIVER_ERROR_TYPE);
        printSettings
            .getPrintColumnSettings()
            .setColumn(3, buildColumn(
                "ФИО жителя",
                15000,
                getExtractPersonFioLinkFunction(FlowReportedErrorData::getPersonFirst)))
            .setColumn(4, buildColumn(
                "Адрес отселяемого дома",
                12000,
                flowReportedErrorData -> ofNullable(flowReportedErrorData)
                    .map(FlowReportedErrorData::getPersonFirst)
                    .map(PersonInfoType::getAddressFrom)
                    .orElse(null)))
            .setColumn(5, buildColumn(
                "Номер \nкомнаты",
                4000,
                flowReportedErrorData -> ofNullable(flowReportedErrorData)
                    .map(FlowReportedErrorData::getPersonFirst)
                    .map(PersonInfoType::getRoomsNumber)
                    .orElse("-")))
            .setColumn(6, buildColumn(
                "Признак \nкоммунальной \nквартиры",
                4000,
                flowReportedErrorData -> ofNullable(flowReportedErrorData)
                    .map(FlowReportedErrorData::getPersonFirst)
                    .map(personInfo -> personInfo.isIsCommunal() ? "да" : "нет")
                    .orElse(null)));
        return printSettings;
    }

    private static PrintColumnSettings<FlowReportedErrorData> getSharedColumnSettings() {
        return
            PrintColumnSettings.<FlowReportedErrorData>
                builder()
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
                .tableRowHeight(4)
                .build()
                .setColumn(0, buildColumn(
                    "Вид полученных сведений \n(№ потока)",
                    10000,
                    FlowReportedErrorData::getFlowType))
                .setColumn(1, buildColumn(
                    "AffairId",
                    5000,
                    getExtractAffairIdFunction()))
                .setColumn(2, buildColumn(
                    "PersonId",
                    5000,
                    getExtractPersonIdFunction()));
    }

    private static Function<FlowReportedErrorData, String> getExtractAffairIdFunction() {
        return flowReportedErrorData -> ofNullable(flowReportedErrorData)
            .map(FlowReportedErrorData::getPersonFirst)
            .map(PersonInfoType::getAffairId)
            .orElse(null);
    }

    private static Function<FlowReportedErrorData, String> getExtractPersonIdFunction() {
        return flowReportedErrorData -> ofNullable(flowReportedErrorData)
            .map(FlowReportedErrorData::getPersonFirst)
            .map(PersonInfoType::getPersonId)
            .orElse(null);
    }

    private static Function<FlowReportedErrorData, String> getExtractPersonFioLinkFunction(
        Function<FlowReportedErrorData, PersonInfoType> getPerson
    ) {
        return flowReportedErrorData -> ofNullable(flowReportedErrorData)
            .map(getPerson)
            .map(personInfoType -> {
                final String fio = personInfoType.getFio();
                final String link = personInfoType.getLink();
                return String.format("%s \n%s", fio, link);
            })
            .orElse(null);
    }

    private static Column<FlowReportedErrorData> buildColumn(
        final String title,
        final int width,
        final Function<FlowReportedErrorData, String> extractor
    ) {
        return Column.<FlowReportedErrorData>builder()
            .headerTitle(title)
            .width(width)
            .dataExtractor(extractor)
            .build();
    }
}
