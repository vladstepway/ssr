package ru.croc.ugd.ssr.exception;

/**
 * Validation error when zip parsing.
 */
public class ZipFileParsingException extends SsrException {

    /**
     * Creates ZipFileParsingException.
     */
    public ZipFileParsingException(Throwable cause) {
        super("Ошибка при парсинге архива", cause);
    }
}
