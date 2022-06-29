package ru.croc.ugd.ssr.exception.commissioninspection;

import ru.croc.ugd.ssr.exception.SsrException;

/**
 * Validation error when inspection move date request is rejected.
 */
public class MoveDateRequestRejected extends SsrException {

    /**
     * InspectionCanNotMoved.
     * @param reason reason
     */
    public MoveDateRequestRejected(final String reason) {
        super(String.format("Осмотр не может быть перенесен: %s", reason));
    }

}
