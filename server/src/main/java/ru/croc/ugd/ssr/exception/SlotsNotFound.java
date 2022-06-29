package ru.croc.ugd.ssr.exception;

/**
 * Validation error when slots not found.
 */
public class SlotsNotFound extends SsrException {

    /**
     * Creates SlotsNotFound.
     */
    public SlotsNotFound() {
        super("Отсутствуют слоты относящиеся к переданному идентификатору");
    }
}
