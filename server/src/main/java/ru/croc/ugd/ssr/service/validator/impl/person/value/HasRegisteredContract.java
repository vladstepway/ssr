package ru.croc.ugd.ssr.service.validator.impl.person.value;

import lombok.AllArgsConstructor;
import ru.croc.ugd.ssr.service.validator.ValueBasedNotEmptyValidatorUnit;
import ru.croc.ugd.ssr.service.validator.model.ValidatablePersonData;
import ru.croc.ugd.ssr.service.validator.model.ValidatorUnitResult;

@AllArgsConstructor
public class HasRegisteredContract<T extends ValidatablePersonData> implements ValueBasedNotEmptyValidatorUnit<T> {

    private final String nonValidMessage;

    @Override
    public ValidatorUnitResult validate(final T validatablePersonData) {
        // ToDo: Check the availability of registered contract in Rosreestr
        return ValidatorUnitResult.ok();
    }
}
