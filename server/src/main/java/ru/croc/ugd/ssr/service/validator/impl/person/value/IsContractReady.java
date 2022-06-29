package ru.croc.ugd.ssr.service.validator.impl.person.value;

import lombok.AllArgsConstructor;
import ru.croc.ugd.ssr.service.validator.ValueBasedNotEmptyValidatorUnit;
import ru.croc.ugd.ssr.service.validator.model.ValidatablePersonData;
import ru.croc.ugd.ssr.service.validator.model.ValidatorUnitResult;
import ru.croc.ugd.ssr.utils.PersonUtils;

import java.util.Optional;
import java.util.function.Function;

/**
 * Ошибка если нет документа для подписи.
 * @param <T> тип валидируемого объекта
 */
@AllArgsConstructor
public class IsContractReady<T extends ValidatablePersonData> implements ValueBasedNotEmptyValidatorUnit<T> {

    private final Function<T, String> retrieveErrorMessage;

    @Override
    public ValidatorUnitResult validate(final T personType) {
        final Optional<String> orderIdOp = PersonUtils.getOrderIdByNewFlat(personType.getPerson(),
            personType.getNewFlat());
        if (!orderIdOp.isPresent()) {
            return ValidatorUnitResult.fail(retrieveErrorMessage.apply(personType));
        }
        return ValidatorUnitResult.ok();
    }

}
