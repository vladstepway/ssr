package ru.croc.ugd.ssr.service.validator.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Результат проверки.
 * @param <T> тип проверяемого объекта
 */
@Getter
@AllArgsConstructor
public class ValidationObjectResult<T> extends GenericValidationResult<ValidatorUnitResult> {

    private final T validatedObject;
    private final String code;

    /**
     * Получить положительный результат проверки.
     * @param validatedObject валидируемый объект
     * @param <T> тип валидируемого объекта
     * @return ok result.
     */
    public static <T> ValidationObjectResult<T> ok(T validatedObject) {
        return new ValidationObjectResult<T>(validatedObject, null);
    }

    /**
     * получить отрицательный результат проверки.
     * @param message валидационное сообщение
     * @param validatedObject валидируемый объект
     * @param <T> тип валидируемого объекта
     * @return отрицательный результат проверки
     */
    public static <T> ValidationObjectResult<T> fail(final String message, T validatedObject, String code) {
        final ValidationObjectResult<T> validationResult = new ValidationObjectResult<T>(validatedObject, code);
        validationResult.addValidatorUnitResults(
            new ValidatorUnitResult(false, message)
        );
        return validationResult;
    }
}

