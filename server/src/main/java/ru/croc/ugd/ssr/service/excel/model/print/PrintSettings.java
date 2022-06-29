package ru.croc.ugd.ssr.service.excel.model.print;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PrintSettings<DATA_TYPE> {
    private final PrintColumnSettings<DATA_TYPE> printColumnSettings;
    private final String sheetTitleName;

}
