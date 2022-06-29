package ru.croc.ugd.ssr.service.trade.status.buyin;

import ru.croc.ugd.ssr.service.trade.status.StatusCalculateCommand;
import ru.croc.ugd.ssr.trade.BuyInStatus;
import ru.croc.ugd.ssr.trade.ClaimStatus;
import ru.croc.ugd.ssr.trade.CommissionDecisionResult;

public class BuyInStatusAuction extends BuyInStatusSharedUnit {

    public BuyInStatusAuction() {
        this.nextUnit = new BuyInStatusNegativeDecisionOfCommission();
    }

    protected BuyInStatus getUnitStatus() {
        return BuyInStatus.AUCTION;
    }

    protected boolean isConditionMatched(StatusCalculateCommand statusCalculateCommand) {
        final ClaimStatus claimStatus = statusCalculateCommand.getTradeAdditionType().getClaimStatus();
        final CommissionDecisionResult commissionDecisionResult = statusCalculateCommand.getTradeAdditionType()
                .getCommissionDecisionResult();

        return isBuyInTradeType(statusCalculateCommand) &&
                ClaimStatus.ACTIVE.equals(claimStatus) &&
                CommissionDecisionResult.AUCTION.equals(commissionDecisionResult);
    }
}
