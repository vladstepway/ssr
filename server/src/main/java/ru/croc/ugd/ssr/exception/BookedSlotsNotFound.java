package ru.croc.ugd.ssr.exception;

/**
 * Validation error when no booked slots found.
 */
public class BookedSlotsNotFound extends SsrException {

    /**
     * Creates NoBookingSlotsFound.
     */
    public BookedSlotsNotFound() {
        super("Слоты относящиеся к идентификатору бронирования отсутствуют");
    }
}
