package ru.croc.ugd.ssr.exception;

/**
 * Validation error when slots have been already booked.
 */
public class SlotsAlreadyBooked extends SsrException {

    /**
     * Creates SlotsAlreadyBooked.
     */
    public SlotsAlreadyBooked() {
        super("Слот был забронирован");
    }
}
