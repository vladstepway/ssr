package ru.croc.ugd.ssr.service.validator;

import ru.croc.ugd.ssr.service.validator.model.ValidationResult;

/**
 * General validator that returns validationResults.
 * @param <VALIDATABLE_OBJECT> Validatable object
 */
public interface ValueBasedValidator<VALIDATABLE_OBJECT> {
    ValidationResult validate(final VALIDATABLE_OBJECT validatableObject);
}
