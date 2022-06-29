package ru.croc.ugd.ssr.service.trade.status.buyin;

import ru.croc.ugd.ssr.service.trade.status.StatusCalculateCommand;
import ru.croc.ugd.ssr.trade.BuyInStatus;
import ru.croc.ugd.ssr.trade.ClaimStatus;
import ru.croc.ugd.ssr.trade.CommissionDecisionResult;

public class BuyInStatusNegativeDecisionOfCommission extends BuyInStatusSharedUnit {

    public BuyInStatusNegativeDecisionOfCommission() {
        this.nextUnit = new BuyInStatusPositiveDecisionOfCommission();
    }

    protected BuyInStatus getUnitStatus() {
        return BuyInStatus.NEGATIVE_DECISION_OF_COMMISSION;
    }

    protected boolean isConditionMatched(StatusCalculateCommand statusCalculateCommand) {
        final ClaimStatus claimStatus = statusCalculateCommand.getTradeAdditionType().getClaimStatus();
        final CommissionDecisionResult commissionDecisionResult = statusCalculateCommand.getTradeAdditionType()
                .getCommissionDecisionResult();

        return isBuyInTradeType(statusCalculateCommand) &&
                ClaimStatus.REJECTED_BY_COMMISSION.equals(claimStatus) &&
                CommissionDecisionResult.NEGATIVE.equals(commissionDecisionResult);
    }
}
