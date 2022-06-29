package ru.croc.ugd.ssr.service.excel.person;

import lombok.AllArgsConstructor;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.service.excel.XssfExcelCellProcessor;
import ru.croc.ugd.ssr.service.excel.model.ExtractedCellValue;
import ru.croc.ugd.ssr.service.excel.model.process.XssfCellValidateResult;
import ru.croc.ugd.ssr.service.excel.person.model.PersonLetterAndContractSheetConstants;
import ru.croc.ugd.ssr.service.validator.model.ValidationResult;

@Service
@AllArgsConstructor
public class PersonLetterAndContractSheetCellProcessor extends XssfExcelCellProcessor {

    private final PersonLetterAndContractSheetDataValidator<ExtractedCellValue>
        personLetterAndContractSheetDataValidator;

    @Override
    protected XssfCellValidateResult validateCell(final XSSFCell xssfCell, final Integer cellIndex) {
        final ExtractedCellValue extractedCellValue = getExtractedCellValue(xssfCell, cellIndex);
        final ValidationResult validationResult = personLetterAndContractSheetDataValidator.validate(
            extractedCellValue
        );
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
        final PersonLetterAndContractSheetConstants columnNameEnum =
            PersonLetterAndContractSheetConstants.findByIndex(cellIndex);

        return ExtractedCellValue.builder()
            .colHeaderName(columnNameEnum == null ? "" : columnNameEnum.getColumnReadableName())
            .colIndex(xssfCell == null ? cellIndex : xssfCell.getColumnIndex())
            .rawValue(getCellValue(xssfCell))
            .build();
    }
}
