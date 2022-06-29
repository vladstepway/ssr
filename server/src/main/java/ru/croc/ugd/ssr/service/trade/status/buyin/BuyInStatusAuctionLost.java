package ru.croc.ugd.ssr.service.trade.status.buyin;

import ru.croc.ugd.ssr.service.trade.status.StatusCalculateCommand;
import ru.croc.ugd.ssr.trade.BuyInStatus;
import ru.croc.ugd.ssr.trade.ClaimStatus;

public class BuyInStatusAuctionLost extends BuyInStatusSharedUnit {

    public BuyInStatusAuctionLost() {
        this.nextUnit = new BuyInStatusAuctionReject();
    }

    protected BuyInStatus getUnitStatus() {
        return BuyInStatus.AUCTION_LOST;
    }

    protected boolean isConditionMatched(StatusCalculateCommand statusCalculateCommand) {
        final ClaimStatus claimStatus = statusCalculateCommand.getTradeAdditionType().getClaimStatus();

        return isBuyInTradeType(statusCalculateCommand) &&
            ClaimStatus.AUCTION_LOST.equals(claimStatus);
    }
}
