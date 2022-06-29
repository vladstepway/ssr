package ru.croc.ugd.ssr.exception;

/**
 * Validation error for missed slot identifier.
 */
public class MissedSlotIdentifier extends SsrException {

    /**
     * Creates MissedSlotIdentifier.
     */
    public MissedSlotIdentifier() {
        super("Идентификатор слота должен быть указан");
    }
}
