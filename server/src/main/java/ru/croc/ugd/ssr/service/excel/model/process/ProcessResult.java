package ru.croc.ugd.ssr.service.excel.model.process;

import lombok.Getter;
import org.apache.commons.collections4.MapUtils;
import ru.croc.ugd.ssr.service.validator.model.ValidationResult;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Result of processor.
 */
@Getter
public class ProcessResult {
    private Map<Integer, ValidationResult> sheetValidationResult;

    /**
     * Add new validation result.
     * @param sheetIndex sheetIndex.
     * @param validationResult validationResult.
     */
    public void putSheetValidationResult(Integer sheetIndex, ValidationResult validationResult) {
        if (sheetValidationResult == null) {
            sheetValidationResult = new HashMap<>();
        }
        sheetValidationResult.put(sheetIndex, validationResult);
    }

    public boolean isAnyInvalid() {
        return MapUtils.emptyIfNull(sheetValidationResult)
                .values()
                .stream()
                .anyMatch(validationResult -> !validationResult.isValid());
    }

    public String getPrintedResults() {
        return MapUtils.emptyIfNull(sheetValidationResult)
                .entrySet()
                .stream()
                .filter(integerValidationResultEntry -> !integerValidationResultEntry.getValue().isValid())
                .map(integerValidationResultEntry ->
                        String.format("Таблица %s: %s", integerValidationResultEntry.getKey() + 1,
                                integerValidationResultEntry
                                        .getValue()
                                        .getJoinedValidationMessage()))
                .collect(Collectors.joining("\n"));
    }
}
