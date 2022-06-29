package ru.croc.ugd.ssr.service.validator.impl.person;

import static java.util.Optional.ofNullable;

import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import ru.croc.ugd.ssr.contractdigitalsign.ContractDigitalSignData;
import ru.croc.ugd.ssr.service.validator.ValueBasedNotEmptyValidatorUnit;
import ru.croc.ugd.ssr.service.validator.impl.contractdigitalsign.ContractDigitalSignPersonValidator;
import ru.croc.ugd.ssr.service.validator.model.ValidatablePersonData;
import ru.croc.ugd.ssr.service.validator.model.ValidatorUnitResult;

import java.time.LocalDate;
import java.util.Locale;

@AllArgsConstructor
public class IsContactSigningPeriodCorrect<T extends ValidatablePersonData>
    implements ValueBasedNotEmptyValidatorUnit<T> {

    private final MessageSource messageSource;
    private final String errorMessage;

    @Override
    public ValidatorUnitResult validate(final T validatablePersonData) {
        final ContractDigitalSignData contractDigitalSignData = validatablePersonData.getContractDigitalSignData();
        if (!isContactSigningPeriodCorrect(contractDigitalSignData)) {
            return ValidatorUnitResult.fail(getMessage(contractDigitalSignData));
        }
        return ValidatorUnitResult.ok();
    }

    private boolean isContactSigningPeriodCorrect(final ContractDigitalSignData contractDigitalSignData) {
        return LocalDate.now().equals(
            ofNullable(contractDigitalSignData)
                .map(ContractDigitalSignData::getAppointmentDate)
                .orElse(null)
        );
    }

    private String getMessage(final ContractDigitalSignData contractDigitalSignData) {
        return messageSource.getMessage(
            errorMessage,
            new Object[]{
                ofNullable(contractDigitalSignData)
                    .map(ContractDigitalSignData::getAppointmentDate)
                    .map(dateTime -> dateTime.format(ContractDigitalSignPersonValidator.DATE_FORMATTER))
                    .orElse(null)
            },
            Locale.getDefault()
        );
    }
}
