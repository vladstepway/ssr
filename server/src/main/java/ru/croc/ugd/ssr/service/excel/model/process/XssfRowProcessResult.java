package ru.croc.ugd.ssr.service.excel.model.process;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Result of xssf row processing.
 */
@Data
public class XssfRowProcessResult {
    private List<XssfCellProcessResult> xssfCellProcessResults;

    /**
     * add another process result.
     * @param xssfCellProcessResult xssfCellProcessResult.
     */
    public void addXssfCellProcessResult(final XssfCellProcessResult xssfCellProcessResult) {
        if (xssfCellProcessResults == null) {
            xssfCellProcessResults = new ArrayList<>();
        }
        xssfCellProcessResults.add(xssfCellProcessResult);
    }

    /**
     * find process result by col index.
     * @param index index
     * @return XssfCellProcessResult
     */
    public XssfCellProcessResult findByIndex(final Integer index) {
        if (xssfCellProcessResults == null) {
            return null;
        }
        return xssfCellProcessResults
                .stream()
                .filter(xssfCellProcessResult -> Objects.equals(index, xssfCellProcessResult.getCollIndex()))
                .findFirst()
                .orElse(null);
    }
}
