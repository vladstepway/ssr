package ru.croc.ugd.ssr.service.trade.status.buyin;

import ru.croc.ugd.ssr.service.trade.status.StatusCalculateCommand;
import ru.croc.ugd.ssr.trade.BuyInStatus;
import ru.croc.ugd.ssr.trade.ClaimStatus;

public class BuyInStatusRequestApplied extends BuyInStatusSharedUnit {

    public BuyInStatusRequestApplied() {
        this.nextUnit = new BuyInStatusRejected();
    }

    protected BuyInStatus getUnitStatus() {
        return BuyInStatus.REQUEST_APPLIED;
    }

    protected boolean isConditionMatched(StatusCalculateCommand statusCalculateCommand) {
        final ClaimStatus claimStatus = statusCalculateCommand.getTradeAdditionType().getClaimStatus();

        return isBuyInTradeType(statusCalculateCommand) &&
                ClaimStatus.ACTIVE.equals(claimStatus);
    }
}
