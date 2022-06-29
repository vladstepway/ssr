package ru.croc.ugd.ssr.service.document;

import ru.croc.ugd.ssr.exception.SsrException;

/**
 * Occurs when no windows assigned to schedule configuration.
 */
public class NoWindowsAssigned extends SsrException {

    /**
     * Creates NoWindowsAssigned.
     */
    public NoWindowsAssigned() {
        super("Отсутствует список окон.");
    }
}
