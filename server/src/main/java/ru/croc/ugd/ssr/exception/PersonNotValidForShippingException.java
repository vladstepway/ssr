package ru.croc.ugd.ssr.exception;

/**
 * Обработчик исключений по жильцу, который не прошел проверку.
 */
public class PersonNotValidForShippingException extends SsrException {

    /**
     * Проброс исключения с сообщением.
     * @param message message.
     */
    public PersonNotValidForShippingException(String message) {
        super(message);
    }

    /**
     * Проброс исключения с сообщением.
     * @param message message.
     * @param errorCode errorCode.
     */
    public PersonNotValidForShippingException(String message, String errorCode) {
        super(message, errorCode);
    }
}
