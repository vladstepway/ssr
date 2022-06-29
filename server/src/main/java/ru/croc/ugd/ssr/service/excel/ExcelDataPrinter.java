package ru.croc.ugd.ssr.service.excel;

import ru.croc.ugd.ssr.service.excel.model.print.PrintSettings;

import java.util.List;
import java.util.Map;

public interface ExcelDataPrinter<DATA_TYPE, TABLE_TYPE> {
    TABLE_TYPE printData(final List<DATA_TYPE> input, final PrintSettings<DATA_TYPE> settings);

    TABLE_TYPE printData(
        final List<DATA_TYPE> input, final PrintSettings<DATA_TYPE> settings, final Map<String, String> generalValues
    );

    TABLE_TYPE printDataToNewSheet(
        final TABLE_TYPE tableInput, final List<DATA_TYPE> input, final PrintSettings<DATA_TYPE> settings
    );
}
