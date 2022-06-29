package ru.croc.ugd.ssr.service.validator.impl.person;

import org.springframework.context.MessageSource;
import ru.croc.ugd.ssr.exception.PersonNotValidForShippingException;
import ru.croc.ugd.ssr.service.validator.model.ValidatablePersonData;

/**
 * Проверка на возраст.
 * @param <T> тип валидируемого объекта
 */
public class IsAdultPerson16<T extends ValidatablePersonData> extends IsAdultPerson<T> {
    /**
     * наименование проперти с сообщением.
     */
    public static final String NOT_ADULT_MESSAGE = "validate.person.notAdult16";
    /**
     * Возраст.
     */
    private static final long ADULT_AGE = 16L;

    /**
     * Конструктор.
     * @param messageSource Сервис локализации сообщений
     */
    public IsAdultPerson16(MessageSource messageSource) {
        super(messageSource);
    }

    @Override
    protected String getMessageProperty() {
        return NOT_ADULT_MESSAGE;
    }

    @Override
    protected long getAdultAge() {
        return ADULT_AGE;
    }

    @Override
    public RuntimeException getValidationException() {
        return new PersonNotValidForShippingException(getMessage(getMessageProperty()));
    }

}
