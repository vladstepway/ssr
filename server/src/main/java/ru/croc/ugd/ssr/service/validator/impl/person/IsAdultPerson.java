package ru.croc.ugd.ssr.service.validator.impl.person;

import org.springframework.context.MessageSource;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.service.validator.AbstractExceptionBasedValidatorUnit;
import ru.croc.ugd.ssr.service.validator.model.ValidatablePersonData;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Проверка на возраст.
 * @param <T> тип валидируемого объекта
 */
public abstract class IsAdultPerson<T extends ValidatablePersonData> extends AbstractExceptionBasedValidatorUnit<T> {

    /**
     * наименование проперти с сообщением.
     * @return наименование проперти с сообщением
     */
    protected abstract String getMessageProperty();

    /**
     * возраст от которого человек считается взрослымю.
     * @return возраст от которого человек считается взрослым
     */
    protected abstract long getAdultAge();

    /**
     * Конструктор.
     * @param messageSource Сервис локализации сообщений
     */
    public IsAdultPerson(MessageSource messageSource) {
        super(messageSource);
    }

    @Override
    public void validate(final T personType) {
        if (personType.getPerson().getBirthDate() == null || getAge(personType.getPerson()) < getAdultAge()) {
            throw getValidationException();
        }
    }

    private long getAge(final PersonType personType) {
        return ChronoUnit.YEARS.between(personType.getBirthDate(), LocalDate.now());
    }

}
