package ru.croc.ugd.ssr.exception;

import ru.croc.ugd.ssr.dto.commissioninspection.CommissionInspectionFlowStatus;

/**
 * InvalidRefusalStatus.
 */
public class InvalidRefusalStatus extends SsrException {

    /**
     * Creates InvalidRefusalStatus.
     *
     * @param refusalStatus refusalStatus
     */
    public InvalidRefusalStatus(final CommissionInspectionFlowStatus refusalStatus) {
        super("Неподдерживаемый статус отказа: " + refusalStatus.name());
    }
}
