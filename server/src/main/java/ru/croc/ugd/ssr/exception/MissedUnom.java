package ru.croc.ugd.ssr.exception;

/**
 * Validation error for missed unom.
 */
public class MissedUnom extends SsrException {

    /**
     * Creates MissedUnom.
     */
    public MissedUnom() {
        super("УНОМ должен быть указан");
    }
}
