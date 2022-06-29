package ru.croc.ugd.ssr.exception;

/**
 * Validation error for missed snils.
 */
public class MissedSnils extends SsrException {

    /**
     * Create MissedSnils.
     */
    public MissedSnils() {
        super("СНИЛС должен быть указан");
    }
}
