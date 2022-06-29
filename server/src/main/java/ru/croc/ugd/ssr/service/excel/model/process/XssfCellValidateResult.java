package ru.croc.ugd.ssr.service.excel.model.process;

import lombok.Builder;
import lombok.Data;
import org.apache.poi.xssf.usermodel.XSSFCell;
import ru.croc.ugd.ssr.service.validator.model.ValidatorUnitResult;

/**
 * Result of xssf cell validation.
 */
@Data
@Builder
public class XssfCellValidateResult extends ValidatorUnitResult {
    private String cellRawValue;
    private XSSFCell cellRef;
}
