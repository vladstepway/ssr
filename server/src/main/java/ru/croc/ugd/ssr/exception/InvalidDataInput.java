package ru.croc.ugd.ssr.exception;

/**
 * Validation error for invalid request data.
 */
public class InvalidDataInput extends SsrException {

    /**
     * Creates InvalidDataInput.
     */
    public InvalidDataInput() {
        super("Данные запроса невалидны");
    }
}
