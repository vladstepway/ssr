package ru.croc.ugd.ssr.service.trade.status.compensation;

import ru.croc.ugd.ssr.service.trade.status.StatusCalculateCommand;
import ru.croc.ugd.ssr.trade.ClaimStatus;
import ru.croc.ugd.ssr.trade.CompensationStatus;

public class CompensationStatusRejected extends CompensationStatusSharedUnit {

    public CompensationStatusRejected() {
        this.nextUnit = null;
    }

    @Override
    protected CompensationStatus getUnitStatus() {
        return CompensationStatus.REJECTED;
    }

    @Override
    protected boolean isConditionMatched(StatusCalculateCommand statusCalculateCommand) {
        final ClaimStatus claimStatus = statusCalculateCommand.getTradeAdditionType().getClaimStatus();

        return isCompensationTradeType(statusCalculateCommand) &&
            !ClaimStatus.ACTIVE.equals(claimStatus);
    }
}