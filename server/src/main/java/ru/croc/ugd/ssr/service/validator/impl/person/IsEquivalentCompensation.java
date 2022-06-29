package ru.croc.ugd.ssr.service.validator.impl.person;

import org.springframework.context.MessageSource;
import org.springframework.util.CollectionUtils;
import ru.croc.ugd.ssr.PersonType.NewFlatInfo.NewFlat;
import ru.croc.ugd.ssr.exception.PersonNotValidForShippingException;
import ru.croc.ugd.ssr.service.validator.AbstractExceptionBasedValidatorUnit;
import ru.croc.ugd.ssr.service.validator.model.ValidatablePersonData;

import java.util.List;

/**
 * Выбрано равнозначное возмещение в денежной форме взамен квартиры.
 * @param <T> тип валидируемого объекта
 */
public class IsEquivalentCompensation<T extends ValidatablePersonData> extends AbstractExceptionBasedValidatorUnit<T> {

    /**
     * наименование проперти с сообщением.
     */
    public static final String EQUIVALENT_COMPENSATION = "validate.person.equivalentCompensation";
    public static final String EQUIVALENT_COMPENSATION_CODE = "no_right_to_apply";

    /**
     * 3 - Равноценное возмещение в денежной форме.
     */
    private static final String FLAT_EVENT_MONEY_COMP = "3";

    /**
     * Конструктор.
     * @param messageSource Сервис локализации сообщений
     */
    public IsEquivalentCompensation(MessageSource messageSource) {
        super(messageSource);
    }

    @Override
    public void validate(final T personType) {
        if (personType.getPerson().getNewFlatInfo() != null && personType.getPerson().getNewFlatInfo() != null
            && !CollectionUtils.isEmpty(personType.getPerson().getNewFlatInfo().getNewFlat())) {
            final List<NewFlat> newFlats = personType.getPerson().getNewFlatInfo().getNewFlat();
            newFlats.stream()
                .filter(this::isEquivalentCompensationForFlat)
                .findAny()
                .ifPresent(a -> {
                    throw getValidationException();
                });
        }
    }

    private boolean isEquivalentCompensationForFlat(final NewFlat newFlat) {
        return FLAT_EVENT_MONEY_COMP.equals(newFlat.getEvent());
    }

    public RuntimeException getValidationException() {
        return new PersonNotValidForShippingException(getMessage(EQUIVALENT_COMPENSATION),
            EQUIVALENT_COMPENSATION_CODE);
    }

}
