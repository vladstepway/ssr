package ru.croc.ugd.ssr.service.validator.impl.person;

import org.springframework.context.MessageSource;
import ru.croc.ugd.ssr.exception.PersonNotValidForCommissionInspectionException;
import ru.croc.ugd.ssr.service.validator.model.ValidatablePersonData;

/**
 * Услуга доступна для лиц старше 18 лет.
 * @param <T> тип валидируемого объекта
 */
public class IsAdultPerson18<T extends ValidatablePersonData> extends IsAdultPerson<T> {
    /**
     * наименование проперти с сообщением.
     */
    public static final String NOT_ADULT_MESSAGE = "validate.person.notAdult18";
    /**
     * Возраст.
     */
    private static final long ADULT_AGE = 18L;

    /**
     * Конструктор.
     * @param messageSource Сервис локализации сообщений
     */
    public IsAdultPerson18(MessageSource messageSource) {
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
        return new PersonNotValidForCommissionInspectionException(getMessage(getMessageProperty()));
    }

}
