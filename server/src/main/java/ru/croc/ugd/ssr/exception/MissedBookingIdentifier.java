package ru.croc.ugd.ssr.exception;

/**
 * Validation error for missed booking identifier.
 */
public class MissedBookingIdentifier extends SsrException {

    /**
     * Creates MissedBookingIdentifier.
     */
    public MissedBookingIdentifier() {
        super("Идентификатор бронирования должен быть указан");
    }
}
