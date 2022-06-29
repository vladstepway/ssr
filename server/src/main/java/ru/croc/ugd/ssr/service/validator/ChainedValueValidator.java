package ru.croc.ugd.ssr.service.validator;

import lombok.AllArgsConstructor;
import org.springframework.util.CollectionUtils;
import ru.croc.ugd.ssr.service.validator.model.ValidationResult;

import java.util.ArrayList;
import java.util.List;


@AllArgsConstructor
public class ChainedValueValidator<VALIDATABLE_OBJECT> extends AbstractValueBasedValidator<VALIDATABLE_OBJECT> {

    private final List<ValueBasedValidatorUnit<VALIDATABLE_OBJECT>> abstractValidatorUnits = new ArrayList<>();

    protected void registerValidatorInChain(
        final ValueBasedValidatorUnit<VALIDATABLE_OBJECT> abstractValidatorUnit
    ) {
        abstractValidatorUnits.add(abstractValidatorUnit);
    }

    public ValidationResult validate(final VALIDATABLE_OBJECT validatableObject) {
        if (CollectionUtils.isEmpty(abstractValidatorUnits)) {
            return ValidationResult.ok();
        }
        final ValidationResult validationResult = new ValidationResult();
        abstractValidatorUnits
            .forEach(validatorUnit -> addValidationResult(validatorUnit, validatableObject, validationResult));
        return validationResult;
    }
}
