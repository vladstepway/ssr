package ru.croc.ugd.ssr.service.excel.model.print;

import lombok.Builder;
import lombok.Data;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Builder
@Data
public class PrintColumnSettings<DATA_TYPE> {
    private final Map<Integer, Column<DATA_TYPE>> indexColumnMap = new HashMap<>();

    private Function<Workbook, CellStyle> headerDefaultStyleFunction;
    private Function<Workbook, CellStyle> cellDefaultStyleFunction;

    /**
     * Multiplies default height.
     */
    private final int headerRowHeight;
    private final int tableRowHeight;

    public PrintColumnSettings<DATA_TYPE> setColumn(final int index, final Column<DATA_TYPE> column) {
        this.indexColumnMap.put(index, column);
        return this;
    }

    public int getMaxRowIndex() {
        return indexColumnMap
                .keySet()
                .stream()
                .max(Integer::compareTo)
                .orElse(0);
    }

    public boolean containsIndex(final int index) {
        return indexColumnMap.containsKey(index);
    }

    public int getCellWidth(final int index) {
        if (containsIndex(index)) {
            return indexColumnMap.get(index).getWidth();
        }
        return -1;
    }

    public String getCellValue(final int index, final DATA_TYPE dataType) {
        if (containsIndex(index)) {
            return indexColumnMap
                .get(index)
                .getDataExtractor()
                .apply(dataType);
        }
        return null;
    }
}
