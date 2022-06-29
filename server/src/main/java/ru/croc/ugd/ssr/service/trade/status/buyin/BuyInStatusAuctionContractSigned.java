package ru.croc.ugd.ssr.service.trade.status.buyin;

import ru.croc.ugd.ssr.service.trade.status.StatusCalculateCommand;
import ru.croc.ugd.ssr.trade.BuyInStatus;
import ru.croc.ugd.ssr.trade.ClaimStatus;

import java.time.LocalDate;

public class BuyInStatusAuctionContractSigned extends BuyInStatusSharedUnit {

    public BuyInStatusAuctionContractSigned() {
        this.nextUnit = new BuyInStatusAuctionContractProvided();
    }

    protected BuyInStatus getUnitStatus() {
        return BuyInStatus.CONTRACT_SIGNED;
    }

    protected boolean isConditionMatched(StatusCalculateCommand statusCalculateCommand) {
        final LocalDate contractSignedDate = statusCalculateCommand.getTradeAdditionType()
            .getContractSignedDate();

        final ClaimStatus claimStatus = statusCalculateCommand.getTradeAdditionType().getClaimStatus();

        return isBuyInTradeType(statusCalculateCommand) &&
            ClaimStatus.ACTIVE.equals(claimStatus) &&
            contractSignedDate != null;
    }
}
