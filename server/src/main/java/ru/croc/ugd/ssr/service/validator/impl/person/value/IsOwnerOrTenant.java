package ru.croc.ugd.ssr.service.validator.impl.person.value;

import lombok.AllArgsConstructor;
import ru.croc.ugd.ssr.enums.StatusLiving;
import ru.croc.ugd.ssr.service.validator.ValueBasedValidatorUnit;
import ru.croc.ugd.ssr.service.validator.model.ValidatablePersonData;
import ru.croc.ugd.ssr.service.validator.model.ValidatorUnitResult;

import java.util.function.Function;

/**
 * Подать заявление может только собственник, наниматель или член семьи нанимателя квартиры.
 * @param <T> тип валидируемого объекта
 */
@AllArgsConstructor
public class IsOwnerOrTenant<T extends ValidatablePersonData>
    implements ValueBasedValidatorUnit<T> {

    private final Function<T, String> retrieveErrorMessage;

    @Override
    public ValidatorUnitResult validate(final T personType) {
        if (personType.getPerson().getStatusLiving() == null
            || !isStatusMatchingOwnersOrTenant(personType.getPerson().getStatusLiving())
        ) {
            return ValidatorUnitResult.fail(retrieveErrorMessage.apply(personType));
        }
        return ValidatorUnitResult.ok();
    }

    protected boolean isStatusMatchingOwnersOrTenant(final String statusLiving) {
        return StatusLiving.OWNER.value().toString().equals(statusLiving)
            || StatusLiving.TENANT.value().toString().equals(statusLiving)
            || StatusLiving.RESIDENT.value().toString().equals(statusLiving);
    }
}
