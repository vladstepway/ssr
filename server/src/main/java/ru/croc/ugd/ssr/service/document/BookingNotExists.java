package ru.croc.ugd.ssr.service.document;

import ru.croc.ugd.ssr.exception.SsrException;

/**
 * Указанного бронирования не существует.
 */
public class BookingNotExists extends SsrException {

    /**
     * Creates BookingNotExists.
     */
    public BookingNotExists() {
        super("Указанного бронирования не существует.");
    }
}
