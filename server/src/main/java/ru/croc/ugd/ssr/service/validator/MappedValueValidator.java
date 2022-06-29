package ru.croc.ugd.ssr.service.validator;

import io.jsonwebtoken.lang.Collections;
import org.springframework.util.CollectionUtils;
import ru.croc.ugd.ssr.service.validator.model.ValidationResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class MappedValueValidator<VALIDATABLE_OBJECT> extends AbstractValueBasedValidator<VALIDATABLE_OBJECT> {

    private static final String NULL_OBJECT_MESSAGE = "Validatable object not found";
    private final Map<Object, List<ValueBasedValidatorUnit<VALIDATABLE_OBJECT>>> abstractValidatorUnitsMap =
        new HashMap<>();

    protected void registerValidatorInMap(
        final Object key, final ValueBasedValidatorUnit<VALIDATABLE_OBJECT>... abstractValidatorUnit
    ) {
        abstractValidatorUnitsMap.put(key, Collections.arrayToList(abstractValidatorUnit));
    }

    public abstract Object getKeyForValidator(VALIDATABLE_OBJECT validatableObject);

    public ValidationResult validate(final VALIDATABLE_OBJECT validatableObject) {
        if (validatableObject == null) {
            return ValidationResult.fail(NULL_OBJECT_MESSAGE);
        }
        final List<ValueBasedValidatorUnit<VALIDATABLE_OBJECT>> abstractValidatorUnits =
            abstractValidatorUnitsMap.get(getKeyForValidator(validatableObject));

        if (CollectionUtils.isEmpty(abstractValidatorUnits)) {
            return ValidationResult.ok();
        }
        final ValidationResult validationResult = new ValidationResult();
        abstractValidatorUnits
            .forEach(validatorUnit -> addValidationResult(validatorUnit, validatableObject, validationResult));
        return validationResult;
    }
}
