package ru.croc.ugd.ssr.exception;

/**
 * Validation zip password does not exist.
 */
public class ArchivePasswordNotExistException extends SsrException {

    /**
     * Creates ArchivePasswordNotExistException.
     */
    public ArchivePasswordNotExistException() {
        super("Пароль к архиву отсутствует");
    }
}
