package ru.croc.ugd.ssr.service.validator.impl.person.value;

import lombok.AllArgsConstructor;
import ru.croc.ugd.ssr.service.validator.ValueBasedNotEmptyValidatorUnit;
import ru.croc.ugd.ssr.service.validator.model.ValidatablePersonData;
import ru.croc.ugd.ssr.service.validator.model.ValidatorUnitResult;
import ru.croc.ugd.ssr.utils.PersonUtils;

@AllArgsConstructor
public class HasOfferLetter<T extends ValidatablePersonData> implements ValueBasedNotEmptyValidatorUnit<T> {

    private final String nonValidMessage;

    @Override
    public ValidatorUnitResult validate(T validatablePersonData) {
        if (!PersonUtils.getLastOfferLetterIfNotDeclined(validatablePersonData.getPerson()).isPresent()) {
            return ValidatorUnitResult.fail(nonValidMessage);
        }
        return ValidatorUnitResult.ok();
    }
}
