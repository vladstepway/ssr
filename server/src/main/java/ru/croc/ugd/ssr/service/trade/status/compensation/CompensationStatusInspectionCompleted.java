package ru.croc.ugd.ssr.service.trade.status.compensation;

import ru.croc.ugd.ssr.service.trade.status.StatusCalculateCommand;
import ru.croc.ugd.ssr.trade.CompensationStatus;

public class CompensationStatusInspectionCompleted extends CompensationStatusSharedUnit {
    private final String FLAT_DEMOED_PERSON_STATUS_CODE = "3";

    public CompensationStatusInspectionCompleted() {
        this.nextUnit = new CompensationStatusLetterAccepted();
    }

    @Override
    protected CompensationStatus getUnitStatus() {
        return CompensationStatus.INSPECTION_COMPETED;
    }

    @Override
    protected boolean isConditionMatched(StatusCalculateCommand statusCalculateCommand) {
        return isResettlementStatusExist(statusCalculateCommand, FLAT_DEMOED_PERSON_STATUS_CODE);
    }
}
