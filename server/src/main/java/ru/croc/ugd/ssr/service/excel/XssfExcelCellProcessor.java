package ru.croc.ugd.ssr.service.excel;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFCell;
import ru.croc.ugd.ssr.service.excel.model.process.XssfCellProcessResult;
import ru.croc.ugd.ssr.service.excel.model.process.XssfCellValidateResult;

/**
 * XssfExcelCellProcessor.
 */
public abstract class XssfExcelCellProcessor {
    /**
     * formatter.
     */
    protected final DataFormatter formatter;

    protected XssfExcelCellProcessor() {
        formatter = new DataFormatter();
        formatter.addFormat("m/d/yy", new java.text.SimpleDateFormat("dd.MM.yyyy"));
    }

    /**
     * validateCell.
     * @param xssfCell xssfCell
     * @param cellIndex cellIndex
     * @return ru.croc.ugd.ssr.service.excel.model.XssfCellValidateResult
     */
    protected abstract XssfCellValidateResult validateCell(final XSSFCell xssfCell,
                                                           final Integer cellIndex);

    /**
     * process table cell.
     * @param xssfCell xssfCell
     * @param cellIndex cellIndex
     * @param rowIndex rowIndex
     * @return processing result.
     */
    protected XssfCellProcessResult processCell(final XSSFCell xssfCell,
                                                final Integer cellIndex,
                                                final Integer rowIndex) {
        return XssfCellProcessResult.builder()
                .collIndex(xssfCell == null ? cellIndex : xssfCell.getColumnIndex())
                .rowIndex(xssfCell == null ? rowIndex : xssfCell.getRowIndex())
                .cellRef(xssfCell)
                .cellRawValue(getCellValue(xssfCell))
                .build();
    }

    /**
     * get formatted string value.
     * @param xssfCell xssfCell
     * @return string value.
     */
    protected String getCellValue(final XSSFCell xssfCell) {
        return xssfCell == null ? null : formatter.formatCellValue(xssfCell);
    }
}
