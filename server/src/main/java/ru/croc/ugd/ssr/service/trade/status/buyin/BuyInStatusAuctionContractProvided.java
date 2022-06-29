package ru.croc.ugd.ssr.service.trade.status.buyin;

import ru.croc.ugd.ssr.service.trade.status.StatusCalculateCommand;
import ru.croc.ugd.ssr.trade.BuyInStatus;
import ru.croc.ugd.ssr.trade.ClaimStatus;

import java.time.LocalDate;

public class BuyInStatusAuctionContractProvided extends BuyInStatusSharedUnit {

    public BuyInStatusAuctionContractProvided() {
        this.nextUnit = new BuyInStatusAuctionWin();
    }

    protected BuyInStatus getUnitStatus() {
        return BuyInStatus.CONTRACT_PROVIDED;
    }

    protected boolean isConditionMatched(StatusCalculateCommand statusCalculateCommand) {
        final LocalDate contractReadinessDate = statusCalculateCommand.getTradeAdditionType()
            .getContractReadinessDate();

        final ClaimStatus claimStatus = statusCalculateCommand.getTradeAdditionType().getClaimStatus();

        return isBuyInTradeType(statusCalculateCommand) &&
            ClaimStatus.ACTIVE.equals(claimStatus) &&
            contractReadinessDate != null;
    }
}
