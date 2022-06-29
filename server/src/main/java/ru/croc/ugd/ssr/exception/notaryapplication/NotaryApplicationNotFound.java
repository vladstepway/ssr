package ru.croc.ugd.ssr.exception.notaryapplication;

import ru.croc.ugd.ssr.exception.SsrException;

/**
 * Validation error when notary application not found.
 */
public class NotaryApplicationNotFound extends SsrException {

    /**
     * Creates NotaryApplicationNotFound.
     */
    public NotaryApplicationNotFound(final String eno) {
        super(String.format("Заявление на посещение нотариуса с номером %s не найдено", eno));
    }
}
