package ru.croc.ugd.ssr.service.trade.status.buyin;

import ru.croc.ugd.ssr.service.trade.status.StatusCalculateCommand;
import ru.croc.ugd.ssr.trade.BuyInStatus;
import ru.croc.ugd.ssr.trade.ClaimStatus;

public class BuyInStatusRejected extends BuyInStatusSharedUnit {

    public BuyInStatusRejected() {
        this.nextUnit = null;
    }

    protected BuyInStatus getUnitStatus() {
        return BuyInStatus.REJECTED;
    }

    protected boolean isConditionMatched(StatusCalculateCommand statusCalculateCommand) {
        final ClaimStatus claimStatus = statusCalculateCommand.getTradeAdditionType().getClaimStatus();

        return isBuyInTradeType(statusCalculateCommand) &&
            !ClaimStatus.ACTIVE.equals(claimStatus);
    }
}
