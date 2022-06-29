package ru.croc.ugd.ssr.service.trade.status.buyin;

import ru.croc.ugd.ssr.service.trade.status.StatusCalculateCommand;
import ru.croc.ugd.ssr.trade.BuyInStatus;

public class BuyInStatusAuctionReject extends BuyInStatusSharedUnit {

    public BuyInStatusAuctionReject() {
        this.nextUnit = new BuyInStatusAuction();
    }

    protected BuyInStatus getUnitStatus() {
        return BuyInStatus.AUCTION_REJECTED;
    }

    protected boolean isConditionMatched(StatusCalculateCommand statusCalculateCommand) {
        return isBuyInTradeType(statusCalculateCommand) &&
            isRejectedClaimStatus(statusCalculateCommand);
    }
}
