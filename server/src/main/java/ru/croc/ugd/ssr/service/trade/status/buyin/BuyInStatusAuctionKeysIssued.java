package ru.croc.ugd.ssr.service.trade.status.buyin;

import ru.croc.ugd.ssr.service.trade.status.StatusCalculateCommand;
import ru.croc.ugd.ssr.trade.BuyInStatus;
import ru.croc.ugd.ssr.trade.ClaimStatus;

import java.time.LocalDate;

public class BuyInStatusAuctionKeysIssued extends BuyInStatusSharedUnit {

    public BuyInStatusAuctionKeysIssued() {
        this.nextUnit = new BuyInStatusAuctionContractSigned();
    }

    protected BuyInStatus getUnitStatus() {
        return BuyInStatus.KEYS_ISSUED;
    }

    protected boolean isConditionMatched(StatusCalculateCommand statusCalculateCommand) {
        final LocalDate keysIssueDate = statusCalculateCommand.getTradeAdditionType()
            .getKeysIssueDate();

        final ClaimStatus claimStatus = statusCalculateCommand.getTradeAdditionType().getClaimStatus();

        return isBuyInTradeType(statusCalculateCommand) &&
            ClaimStatus.ACTIVE.equals(claimStatus) &&
            keysIssueDate != null;
    }
}
