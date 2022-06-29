package ru.croc.ugd.ssr.service.validator.impl.person.value;

import static java.util.Optional.ofNullable;

import lombok.AllArgsConstructor;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.service.validator.ValueBasedNotEmptyValidatorUnit;
import ru.croc.ugd.ssr.service.validator.model.ValidatablePersonData;
import ru.croc.ugd.ssr.service.validator.model.ValidatorUnitResult;

import java.util.Collections;
import java.util.Objects;

@AllArgsConstructor
public class HasResettlementHistoryWithoutSignedContract<T extends ValidatablePersonData>
    implements ValueBasedNotEmptyValidatorUnit<T> {

    private final String nonValidMessage;

    @Override
    public ValidatorUnitResult validate(T validatablePersonData) {
        if (validatablePersonData.getPerson().getResettlementHistory().isEmpty()
            || existsSignedContract(validatablePersonData.getPerson())) {
            return ValidatorUnitResult.fail(nonValidMessage);
        }
        return ValidatorUnitResult.ok();
    }

    private boolean existsSignedContract(final PersonType person) {
        return ofNullable(person.getContracts())
            .map(PersonType.Contracts::getContract)
            .orElse(Collections.emptyList())
            .stream()
            .map(PersonType.Contracts.Contract::getContractSignDate)
            .anyMatch(Objects::nonNull);
    }
}
