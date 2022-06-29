package ru.croc.ugd.ssr.service.validator.impl.person.value;

import lombok.AllArgsConstructor;
import ru.croc.ugd.ssr.service.validator.ValueBasedNotEmptyValidatorUnit;
import ru.croc.ugd.ssr.service.validator.model.ValidatablePersonData;
import ru.croc.ugd.ssr.service.validator.model.ValidatorUnitResult;
import ru.croc.ugd.ssr.utils.PersonUtils;

import java.util.function.Function;

/**
 * Ошибка если уже заключен.
 * @param <T> тип валидируемого объекта
 */
@AllArgsConstructor
public class IsContractForFlatSigned<T extends ValidatablePersonData> implements ValueBasedNotEmptyValidatorUnit<T> {

    private final Function<T, String> retrieveErrorMessage;

    @Override
    public ValidatorUnitResult validate(final T personType) {
        if (PersonUtils.isContractSigned(personType.getPerson(), personType.getNewFlat())) {
            return ValidatorUnitResult.fail(retrieveErrorMessage.apply(personType));
        }
        return ValidatorUnitResult.ok();
    }

}
