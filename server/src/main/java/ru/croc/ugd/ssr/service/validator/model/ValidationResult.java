package ru.croc.ugd.ssr.service.validator.model;


/**
 * ValidationResult for validators.
 */
public class ValidationResult extends GenericValidationResult<ValidatorUnitResult> {
    /**
     * get ok result.
     * @return ok result.
     */
    public static ValidationResult ok() {
        return new ValidationResult();
    }

    /**
     * Get failed result with message.
     * @param message fail message
     * @return failed validation result
     */
    public static ValidationResult fail(final String message) {
        final ValidationResult validationResult = new ValidationResult();
        validationResult.addValidatorUnitResults(
            new ValidatorUnitResult(false, message)
        );
        return validationResult;
    }
}

