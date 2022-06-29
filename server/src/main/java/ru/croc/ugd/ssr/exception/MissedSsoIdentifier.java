package ru.croc.ugd.ssr.exception;

/**
 * Validation error for missed sso identifier.
 */
public class MissedSsoIdentifier extends SsrException {

    /**
     * Creates MissedSsoIdentifier.
     */
    public MissedSsoIdentifier() {
        super("Идентификатор sso должен быть указан");
    }
}
