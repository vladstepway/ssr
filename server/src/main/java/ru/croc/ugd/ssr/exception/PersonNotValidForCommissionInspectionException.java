package ru.croc.ugd.ssr.exception;

/**
 * Обработчик исключений по жильцу, который не прошел проверку.
 */
public class PersonNotValidForCommissionInspectionException extends SsrException {

    /**
     * Проброс исключения с сообщением.
     * @param message сообщение для исключения.
     */
    public PersonNotValidForCommissionInspectionException(String message) {
        super(message);
    }
}
