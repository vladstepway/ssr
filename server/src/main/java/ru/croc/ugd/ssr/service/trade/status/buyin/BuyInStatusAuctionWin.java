package ru.croc.ugd.ssr.service.trade.status.buyin;

import ru.croc.ugd.ssr.service.trade.status.StatusCalculateCommand;
import ru.croc.ugd.ssr.trade.BuyInStatus;
import ru.croc.ugd.ssr.trade.ClaimStatus;

public class BuyInStatusAuctionWin extends BuyInStatusSharedUnit {

    public BuyInStatusAuctionWin() {
        this.nextUnit = new BuyInStatusAuctionLost();
    }

    protected BuyInStatus getUnitStatus() {
        return BuyInStatus.AUCTION_WON;
    }

    protected boolean isConditionMatched(StatusCalculateCommand statusCalculateCommand) {
        final ClaimStatus claimStatus = statusCalculateCommand.getTradeAdditionType().getClaimStatus();

        return isBuyInTradeType(statusCalculateCommand) &&
                ClaimStatus.ACTIVE.equals(claimStatus) &&
                existsAuctionResult(statusCalculateCommand);
    }
}
