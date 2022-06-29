package ru.croc.ugd.ssr.service.validator.impl.excel;

import static java.util.Objects.isNull;

import org.apache.commons.lang.StringUtils;
import ru.croc.ugd.ssr.service.excel.model.ExtractedCellValue;
import ru.croc.ugd.ssr.service.validator.ValueBasedValidatorUnit;
import ru.croc.ugd.ssr.service.validator.model.ValidatorUnitResult;

public class MandatoryValidator<T extends ExtractedCellValue> implements ValueBasedValidatorUnit<T> {

    private static final String MANDATORY_VALUE_MESSAGE = "Не заполнено обязательное поле %s";

    @Override
    public ValidatorUnitResult validate(T extractedCellValue) {
        if (isNull(extractedCellValue) || StringUtils.isEmpty(extractedCellValue.getRawValue())) {
            return ValidatorUnitResult.fail(
                String.format(MANDATORY_VALUE_MESSAGE, extractedCellValue.getColHeaderName())
            );
        }
        return ValidatorUnitResult.ok();
    }
}
