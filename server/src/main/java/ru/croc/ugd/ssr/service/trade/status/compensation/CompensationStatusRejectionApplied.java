package ru.croc.ugd.ssr.service.trade.status.compensation;

import ru.croc.ugd.ssr.service.trade.status.StatusCalculateCommand;
import ru.croc.ugd.ssr.trade.ClaimStatus;
import ru.croc.ugd.ssr.trade.CompensationStatus;
import ru.croc.ugd.ssr.trade.TradeResult;

public class CompensationStatusRejectionApplied extends CompensationStatusSharedUnit {

    public CompensationStatusRejectionApplied() {
        this.nextUnit = new CompensationStatusAgreementApplied();
    }

    @Override
    protected CompensationStatus getUnitStatus() {
        return CompensationStatus.REJECTION_APPLIED;
    }

    @Override
    protected boolean isConditionMatched(StatusCalculateCommand statusCalculateCommand) {
        final ClaimStatus claimStatus = statusCalculateCommand.getTradeAdditionType().getClaimStatus();
        final TradeResult tradeResult = statusCalculateCommand.getTradeAdditionType().getTradeResult();

        return isCompensationTradeType(statusCalculateCommand) &&
                !ClaimStatus.ACTIVE.equals(claimStatus) &&
                TradeResult.REFUSED.equals(tradeResult);
    }
}
