package ru.croc.ugd.ssr.service.validator.impl.excel;

import static java.util.Objects.isNull;
import static ru.croc.ugd.ssr.utils.DateTimeUtils.getDateFromString;

import org.apache.commons.lang.StringUtils;
import ru.croc.ugd.ssr.service.excel.model.ExtractedCellValue;
import ru.croc.ugd.ssr.service.validator.ValueBasedNotEmptyValidatorUnit;
import ru.croc.ugd.ssr.service.validator.model.ValidatorUnitResult;

import java.time.LocalDate;

public class DateValidator<T extends ExtractedCellValue> implements ValueBasedNotEmptyValidatorUnit<T> {

    private static final String DATE_VALUE_MESSAGE = "Поле %s не является датой";

    @Override
    public ValidatorUnitResult validate(T extractedCellValue) {
        if (StringUtils.isEmpty(extractedCellValue.getRawValue())) {
            return ValidatorUnitResult.ok();
        }
        LocalDate localDate = getDateFromString(extractedCellValue.getRawValue());
        if (isNull(localDate)) {
            return ValidatorUnitResult.fail(
                String.format(DATE_VALUE_MESSAGE, extractedCellValue.getColHeaderName())
            );
        }
        return ValidatorUnitResult.ok();
    }
}
