package ru.croc.ugd.ssr.exception.flatappointment;

import ru.croc.ugd.ssr.exception.SsrException;

/**
 * Validation error when flat appointment not found.
 */
public class FlatAppointmentNotFound extends SsrException {

    /**
     * Creates FlatAppointmentNotFound.
     */
    public FlatAppointmentNotFound(final String eno) {
        super(String.format("Заявление на осмотр квартиры с номером %s не найдено", eno));
    }
}
