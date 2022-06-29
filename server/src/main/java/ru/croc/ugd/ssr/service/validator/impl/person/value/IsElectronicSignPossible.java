package ru.croc.ugd.ssr.service.validator.impl.person.value;

import static java.util.Objects.nonNull;

import lombok.AllArgsConstructor;
import org.springframework.util.CollectionUtils;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.config.contractappointment.ContractAppointmentProperties;
import ru.croc.ugd.ssr.service.validator.ValueBasedNotEmptyValidatorUnit;
import ru.croc.ugd.ssr.service.validator.model.ValidatablePersonData;
import ru.croc.ugd.ssr.service.validator.model.ValidatorUnitResult;

@AllArgsConstructor
public class IsElectronicSignPossible<T extends ValidatablePersonData> implements ValueBasedNotEmptyValidatorUnit<T> {

    private final ContractAppointmentProperties contractAppointmentProperties;
    private final String errorMessage;

    @Override
    public ValidatorUnitResult validate(final T validatablePersonData) {
        if (isElectronicSignPossible(validatablePersonData.getPerson())) {
            return ValidatorUnitResult.ok();
        }
        return ValidatorUnitResult.fail(errorMessage);
    }

    private boolean isElectronicSignPossible(final PersonType person) {
        return nonNull(contractAppointmentProperties.getElectronicSign())
            && contractAppointmentProperties.getElectronicSign().isEnabled()
            && (person.isElectronicSignEnabled() || checkAffair(person));
    }

    private boolean checkAffair(final PersonType person) {
        return CollectionUtils.isEmpty(contractAppointmentProperties.getElectronicSign().getAffairs())
            || (nonNull(person) && contractAppointmentProperties.getElectronicSign().getAffairs()
            .stream()
            .anyMatch(affairId -> affairId.equals(person.getAffairId())));
    }
}
