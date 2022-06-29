package ru.croc.ugd.ssr.service.validator;

import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.util.StringUtils;
import ru.croc.ugd.ssr.service.validator.model.ValidationResult;
import ru.croc.ugd.ssr.service.validator.model.ValidatorUnitResult;

import java.util.Locale;

public abstract class AbstractValueBasedValidator<VALIDATABLE_OBJECT>
    implements ValueBasedValidator<VALIDATABLE_OBJECT> {
    protected void addValidationResult(
        final ValueBasedValidatorUnit<VALIDATABLE_OBJECT> validatorUnit,
        final VALIDATABLE_OBJECT validatableObject,
        final ValidationResult validationResult
    ) {
        if (validatorUnit instanceof ValueBasedNotEmptyValidatorUnit) {
            if (validatableObject == null) {
                return;
            }
            if (validatableObject instanceof String && StringUtils.isEmpty(validatableObject)) {
                return;
            }
        }
        validationResult
            .addValidatorUnitResults(validateObject(validatableObject, validatorUnit));
    }

    private ValidatorUnitResult validateObject(
        final VALIDATABLE_OBJECT validatableObject, final ValueBasedValidatorUnit<VALIDATABLE_OBJECT> validatorUnit
    ) {
        final ValidatorUnitResult validatorUnitResult = validatorUnit.validate(validatableObject);
        validatorUnitResult.setValidationMessage(
            translateMessageIfNeeded(validatorUnitResult.getValidationMessage()));
        return validatorUnitResult;
    }


    protected MessageSource getMessageSource() {
        return null;
    }

    protected String translateMessageIfNeeded(final String message) {
        final MessageSource messageSource = getMessageSource();
        if (messageSource == null) {
            return message;
        }
        try {
            return messageSource.getMessage(message, null, Locale.getDefault());
        } catch (NoSuchMessageException ex) {
            return message;
        }
    }
}
