package ru.croc.ugd.ssr.service.validator.impl.person.value;

import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.service.validator.ValueBasedNotEmptyValidatorUnit;
import ru.croc.ugd.ssr.service.validator.model.ValidatablePersonData;
import ru.croc.ugd.ssr.service.validator.model.ValidatorUnitResult;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class IsAdultPerson<T extends ValidatablePersonData> implements ValueBasedNotEmptyValidatorUnit<T> {
    private String nonValidMessage;
    private long adultAge;

    public IsAdultPerson(String nonValidMessage, long adultAge) {
        this.nonValidMessage = nonValidMessage;
        this.adultAge = adultAge;
    }

    @Override
    public ValidatorUnitResult validate(ValidatablePersonData personType) {
        if (personType.getPerson().getBirthDate() == null || getAge(personType.getPerson()) < adultAge) {
            return ValidatorUnitResult.fail(nonValidMessage);
        }
        return ValidatorUnitResult.ok();
    }

    private long getAge(final PersonType personType) {
        return ChronoUnit.YEARS.between(personType.getBirthDate(), LocalDate.now());
    }
}
