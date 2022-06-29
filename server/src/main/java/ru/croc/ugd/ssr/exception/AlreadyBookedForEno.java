package ru.croc.ugd.ssr.exception;

/**
 * Validation error when eno already booken.
 */
public class AlreadyBookedForEno extends SsrException {

    /**
     * Creates AlreadyBookedForEno.
     */
    public AlreadyBookedForEno() {
        super("Для данного заявления бронирование уже было сделано");
    }
}
