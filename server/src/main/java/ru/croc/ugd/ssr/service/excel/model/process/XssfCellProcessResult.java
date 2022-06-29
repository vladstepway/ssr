package ru.croc.ugd.ssr.service.excel.model.process;

import lombok.Builder;
import lombok.Data;
import org.apache.poi.xssf.usermodel.XSSFCell;

/**
 * Result of xssf cell processing.
 */
@Data
@Builder
public class XssfCellProcessResult {
    private String cellRawValue;
    private int rowIndex;
    private Integer collIndex;
    private XSSFCell cellRef;
}
