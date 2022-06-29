package ru.croc.ugd.ssr.service.excel.model.print;

import lombok.Builder;
import lombok.Data;

import java.util.function.Function;

@Data
@Builder
public class Column<DATA_TYPE> {
    private final String headerTitle;
    private final Function<DATA_TYPE, String> dataExtractor;
    private final int width;
}
