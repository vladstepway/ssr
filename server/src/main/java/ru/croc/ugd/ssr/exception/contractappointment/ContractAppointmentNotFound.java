package ru.croc.ugd.ssr.exception.contractappointment;

import ru.croc.ugd.ssr.exception.SsrException;

/**
 * Validation error when contract appointment not found.
 */
public class ContractAppointmentNotFound extends SsrException {

    /**
     * Creates ContractAppointmentNotFound.
     */
    public ContractAppointmentNotFound(final String eno) {
        super(String.format("Заявление на подписание договора с номером %s не найдено", eno));
    }

}
