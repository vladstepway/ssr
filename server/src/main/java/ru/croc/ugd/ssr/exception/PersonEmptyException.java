package ru.croc.ugd.ssr.exception;

/**
 * Обработчик исключений по жильцу с незаполненными данными.
 */
public class PersonEmptyException extends SsrException {

    /**
     * Проброс исключения с сообщением.
     * @param message сообщение для исключения.
     */
    public PersonEmptyException(String message) {
        super(message);
    }
}
