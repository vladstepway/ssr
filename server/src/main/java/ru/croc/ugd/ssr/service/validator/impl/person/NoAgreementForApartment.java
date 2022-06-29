package ru.croc.ugd.ssr.service.validator.impl.person;

import static ru.croc.ugd.ssr.utils.PersonUtils.getAcceptedAgreement;
import static ru.croc.ugd.ssr.utils.PersonUtils.hasAreaFixedAgreement;
import static ru.croc.ugd.ssr.utils.PersonUtils.isContractSigned;

import lombok.AllArgsConstructor;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.service.validator.ValueBasedValidatorUnit;
import ru.croc.ugd.ssr.service.validator.model.ValidatablePersonData;
import ru.croc.ugd.ssr.service.validator.model.ValidatorUnitResult;

/**
 * Только при заселении и дозаселении.
 * Проверка, что получено согласие на квартиру или получено положительное решение комиссии по докупке.
 * @param <T> тип валидируемого объекта
 */
@AllArgsConstructor
public class NoAgreementForApartment<T extends ValidatablePersonData> implements ValueBasedValidatorUnit<T> {

    private final String errorMessage;

    @Override
    public ValidatorUnitResult validate(final T personToValidate) {

        if (personToValidate.getTradeAddition() != null) {
            return ValidatorUnitResult.ok();
        }

        final PersonType person = personToValidate.getPerson();

        if (isContractSigned(personToValidate.getPerson(), personToValidate.getNewFlat())) {
            return ValidatorUnitResult.ok();
        }

        if (getAcceptedAgreement(person, personToValidate.getOfferLetter()).isPresent()
            || hasAreaFixedAgreement(person, personToValidate.getOfferLetter())
        ) {
            return ValidatorUnitResult.ok();
        }

        return ValidatorUnitResult.fail(errorMessage);
    }

}
