package ru.croc.ugd.ssr.service.excel.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class ExtractedCellValue {
    private String rawValue;
    private Integer colIndex;
    private String colHeaderName;
}
