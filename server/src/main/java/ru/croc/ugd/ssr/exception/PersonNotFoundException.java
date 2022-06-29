package ru.croc.ugd.ssr.exception;

/**
 * Обработчик исключений по жильцу, по которому в БД нет данных.
 */
public class PersonNotFoundException extends SsrException {

    /**
     * Проброс исключения с сообщением.
     * @param message сообщение для исключения.
     */
    public PersonNotFoundException(String message) {
        super(message);
    }
}
