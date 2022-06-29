package ru.croc.ugd.ssr.service.excel.person;

import static ru.croc.ugd.ssr.service.excel.person.PersonLetterAndContractLogUtils.writeWarnLog;

import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.service.excel.XssfExcelCellProcessor;
import ru.croc.ugd.ssr.service.excel.XssfExcelRowProcessor;
import ru.croc.ugd.ssr.service.excel.model.process.XssfRowProcessResult;
import ru.croc.ugd.ssr.service.excel.model.process.XssfRowValidateResult;
import ru.croc.ugd.ssr.service.excel.person.model.PersonLetterAndContractSheetConstants;

import java.io.FileOutputStream;
import java.util.Arrays;

@Service
@AllArgsConstructor
public abstract class PersonLetterAndContractSheetRowProcessor extends XssfExcelRowProcessor {

    private final PersonLetterAndContractSheetCellProcessor personLetterAndContractSheetCellProcessor;

    @Override
    protected XssfExcelCellProcessor getCellProcessor() {
        return personLetterAndContractSheetCellProcessor;
    }

    @Override
    protected int getHeaderRowIndex(XSSFSheet sheetToProcess) {
        final int numberOfRows = sheetToProcess.getPhysicalNumberOfRows();
        for (int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {
            final XSSFRow potentialHeaderRow = getRow(sheetToProcess, rowIndex);
            if (isValidHeaderRow(potentialHeaderRow)) {
                return rowIndex;
            }
        }
        return -1;
    }

    @Override
    protected Integer getColumnsNumber(final XSSFRow rowToValidate, final XSSFRow headerRow) {
        return (int) rowToValidate.getLastCellNum();
    }

    @Override
    protected XssfRowProcessResult processRow(
        final XSSFRow rowToProcess,
        final XSSFRow headerRow,
        final XssfRowValidateResult xssfRowValidateResult,
        final FileOutputStream logFos
    ) {
        final XssfRowProcessResult xssfRowProcessResult = super.processRow(
            rowToProcess, headerRow, xssfRowValidateResult, logFos
        );

        if (!xssfRowValidateResult.isValid()) {
            writeWarnLog(
                logFos,
                String.format(
                    "Ошибка загрузки строки, номер строки = %d: %s",
                    rowToProcess.getRowNum(),
                    xssfRowValidateResult.getJoinedValidationMessage()
                ),
                String.format(
                    "PersonLetterAndContract. Unable to upload row, rowNum = %d: %s",
                    rowToProcess.getRowNum(),
                    xssfRowValidateResult.getJoinedValidationMessage()
                )
            );
            return xssfRowProcessResult;
        }

        saveRowAsOfferLetterOrContract(rowToProcess, xssfRowProcessResult, logFos);
        return xssfRowProcessResult;
    }

    private boolean isValidHeaderRow(final XSSFRow potentialHeaderRow) {
        if (potentialHeaderRow == null) {
            return false;
        }
        return Arrays.stream(PersonLetterAndContractSheetConstants.values())
            .allMatch(offerLetterSheetConstant -> {
                final String cellValueToValidate = getCellValue(
                    potentialHeaderRow.getCell(offerLetterSheetConstant.getColumnIndex())
                );
                return StringUtils.containsIgnoreCase(
                    cellValueToValidate, offerLetterSheetConstant.getColumnOriginalPartName()
                );
            });
    }

    protected abstract void saveRowAsOfferLetterOrContract(
        final XSSFRow rowToProcess, final XssfRowProcessResult xssfRowProcessResult, final FileOutputStream logFos
    );
}
