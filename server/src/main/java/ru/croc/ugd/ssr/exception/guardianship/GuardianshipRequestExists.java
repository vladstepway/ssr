package ru.croc.ugd.ssr.exception.guardianship;

import ru.croc.ugd.ssr.exception.SsrException;

/**
 * Guardianship request exists.
 */
public class GuardianshipRequestExists extends SsrException {

    /**
     * Create GuardianshipRequestExists.
     */
    public GuardianshipRequestExists() {
        super("Запрос для данной семьи уже сформирован");
    }
}
