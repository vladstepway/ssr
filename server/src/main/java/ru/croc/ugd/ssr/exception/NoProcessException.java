package ru.croc.ugd.ssr.exception;

/**
 * Validation error for missed process.
 */
public class NoProcessException extends SsrException {

    /**
     * Create NoProcess.
     */
    public NoProcessException() {
        super("Не найден процесс");
    }
}
