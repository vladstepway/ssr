package ru.croc.ugd.ssr.service.validator;

import ru.croc.ugd.ssr.service.validator.model.ValidatorUnitResult;

public interface ValueBasedValidatorUnit<VALIDATABLE_OBJECT> {
    ValidatorUnitResult validate(final VALIDATABLE_OBJECT object);
}
