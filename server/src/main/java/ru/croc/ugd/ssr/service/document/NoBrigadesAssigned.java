package ru.croc.ugd.ssr.service.document;

import ru.croc.ugd.ssr.exception.SsrException;

/**
 * Occurs when no brigades assigned to schedule configuration.
 */
public class NoBrigadesAssigned extends SsrException {

    /**
     * Creates NoBrigadesAssigned.
     */
    public NoBrigadesAssigned() {
        super("Отсутствует список бригад.");
    }
}
