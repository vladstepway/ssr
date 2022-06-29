package ru.croc.ugd.ssr.exception;

/**
 * Validation error when unknown status code received.
 */
public class UnknownStatusCodeReceived extends SsrException {

    /**
     * UnknownStatusCodeReceived.
     */
    public UnknownStatusCodeReceived() {
        super("Получен невалидный статус код");
    }

}
