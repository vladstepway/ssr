package ru.croc.ugd.ssr.service.excel.disabledperson;

import lombok.AllArgsConstructor;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.service.excel.XssfExcelCellProcessor;
import ru.croc.ugd.ssr.service.excel.disabledperson.model.DisabledPersonSheetConstants;
import ru.croc.ugd.ssr.service.excel.model.ExtractedCellValue;
import ru.croc.ugd.ssr.service.excel.model.process.XssfCellValidateResult;
import ru.croc.ugd.ssr.service.validator.model.ValidationResult;

/**
 * DisabledPersonSheetCellProcessor.
 */
@Service
@AllArgsConstructor
public class DisabledPersonSheetCellProcessor extends XssfExcelCellProcessor {

    private final DisabledPersonSheetDataValidator<ExtractedCellValue> disabledPersonSheetDataValidator;

    @Override
    protected XssfCellValidateResult validateCell(final XSSFCell xssfCell, final Integer cellIndex) {
        final ExtractedCellValue extractedCellValue = getExtractedCellValue(xssfCell, cellIndex);
        final ValidationResult validationResult = disabledPersonSheetDataValidator.validate(extractedCellValue);
        final XssfCellValidateResult xssfCellValidateResult = XssfCellValidateResult.builder()
            .cellRawValue(extractedCellValue.getRawValue())
            .cellRef(xssfCell)
            .build();
        xssfCellValidateResult.setValid(validationResult.isValid());
        xssfCellValidateResult.setValidationMessage(validationResult.getJoinedValidationMessage());
        return xssfCellValidateResult;
    }

    private ExtractedCellValue getExtractedCellValue(
        final XSSFCell xssfCell, final Integer cellIndex
    ) {
        final DisabledPersonSheetConstants columnNameEnum = DisabledPersonSheetConstants.findByIndex(cellIndex);

        return ExtractedCellValue.builder()
            .colHeaderName(columnNameEnum == null ? "" : columnNameEnum.getColumnReadableName())
            .colIndex(xssfCell == null ? cellIndex : xssfCell.getColumnIndex())
            .rawValue(getCellValue(xssfCell))
            .build();
    }
}
