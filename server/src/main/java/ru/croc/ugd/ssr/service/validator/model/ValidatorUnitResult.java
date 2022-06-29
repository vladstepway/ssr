package ru.croc.ugd.ssr.service.validator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValidatorUnitResult implements ValidatorUnitResultContact {

    private boolean isValid;
    private String validationMessage;

    public static ValidatorUnitResult fail(final String message) {
        return new ValidatorUnitResult(false, message);
    }

    public static ValidatorUnitResult ok() {
        return new ValidatorUnitResult(true, null);
    }
}
