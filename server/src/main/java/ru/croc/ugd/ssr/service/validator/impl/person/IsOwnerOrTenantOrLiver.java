package ru.croc.ugd.ssr.service.validator.impl.person;

import org.springframework.context.MessageSource;
import ru.croc.ugd.ssr.exception.PersonNotValidForShippingException;
import ru.croc.ugd.ssr.service.validator.AbstractExceptionBasedValidatorUnit;
import ru.croc.ugd.ssr.service.validator.model.ValidatablePersonData;
import ru.croc.ugd.ssr.utils.PersonUtils;

/**
 * Подать заявление может только собственник, наниматель или член семьи нанимателя квартиры.
 * @param <T> тип валидируемого объекта
 */
public class IsOwnerOrTenantOrLiver<T extends ValidatablePersonData> extends AbstractExceptionBasedValidatorUnit<T> {

    /**
     * наименование проперти с сообщением.
     */
    public static final String NOT_OWNER_MESSAGE = "validate.person.notOwnerOrTenantOrLiver";
    public static final String NOT_OWNER_MESSAGE_CODE = "user_not_owner_or_tenant";

    /**
     * Конструктор.
     * @param messageSource Сервис локализации сообщений
     */
    public IsOwnerOrTenantOrLiver(MessageSource messageSource) {
        super(messageSource);
    }

    @Override
    public void validate(final T personType) {
        if (personType.getPerson().getStatusLiving() == null
            || !PersonUtils.isOwnersOrTenant(personType.getPerson().getStatusLiving())) {
            throw getValidationException();
        }
    }


    public RuntimeException getValidationException() {
        return new PersonNotValidForShippingException(getMessage(NOT_OWNER_MESSAGE), NOT_OWNER_MESSAGE_CODE);
    }

}
