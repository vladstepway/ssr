package ru.croc.ugd.ssr.service.validator.model;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * GenericValidationResult.
 * @param <VALIDATE_UNIT> VALIDATE_UNIT
 */
@Getter
@Setter
public class GenericValidationResult<VALIDATE_UNIT extends ValidatorUnitResultContact> {
    /**
     * list of validator units.
     */
    protected List<VALIDATE_UNIT> validatorUnitResults;

    /**
     * are all results valid.
     * @return are valid
     */
    public boolean isValid() {
        if (CollectionUtils.isEmpty(validatorUnitResults)) {
            return true;
        }
        return validatorUnitResults
            .stream()
            .allMatch(validatorUnitResult -> validatorUnitResult.isValid());
    }

    /**
     * Add unit result.
     * @param validatorUnitResult validatorUnitResult
     */
    public void addValidatorUnitResults(final VALIDATE_UNIT validatorUnitResult) {
        Assert.notNull(validatorUnitResult, "validatorUnitResult can't be null");
        if (CollectionUtils.isEmpty(validatorUnitResults)) {
            validatorUnitResults = new ArrayList<>();
        }
        validatorUnitResults.add(validatorUnitResult);
    }

    /**
     * Get common message for all validators.
     * @return common message.
     */
    public String getJoinedValidationMessage() {
        if (CollectionUtils.isEmpty(validatorUnitResults)) {
            return StringUtils.EMPTY;
        }
        return validatorUnitResults
            .stream()
            .filter(xssfCellValidateResult -> StringUtils.isNotEmpty(xssfCellValidateResult.getValidationMessage()))
            .map(xssfCellValidateResult -> xssfCellValidateResult.getValidationMessage())
            .collect(Collectors.joining("\n"))
            .trim()
            .replaceAll("\n+", "\n");
    }

    /**
     * Get the first validation message.
     * @return common message.
     */
    public String getFirstValidationMessage() {
        return Optional.ofNullable(validatorUnitResults)
            .map(List::stream)
            .orElse(Stream.empty())
            .filter(e -> !e.isValid())
            .findFirst()
            .map(ValidatorUnitResultContact::getValidationMessage)
            .orElse(null);
    }

}
