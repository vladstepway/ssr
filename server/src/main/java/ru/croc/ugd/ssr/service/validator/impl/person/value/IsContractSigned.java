package ru.croc.ugd.ssr.service.validator.impl.person.value;

import lombok.AllArgsConstructor;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.validator.ValueBasedNotEmptyValidatorUnit;
import ru.croc.ugd.ssr.service.validator.model.ValidatablePersonData;
import ru.croc.ugd.ssr.service.validator.model.ValidatorUnitResult;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Ошибка если уже заключен.
 * @param <T> тип валидируемого объекта
 */
@AllArgsConstructor
public class IsContractSigned<T extends ValidatablePersonData> implements ValueBasedNotEmptyValidatorUnit<T> {
    private final String message;
    private final PersonDocumentService personDocumentService;

    @Override
    public ValidatorUnitResult validate(final T personType) {
        final Optional<LocalDate> signDate = personDocumentService.getContractSignDate(personType.getPerson());
        if (signDate.isPresent()) {
            return ValidatorUnitResult.fail(message);
        }

        return ValidatorUnitResult.ok();
    }

}
