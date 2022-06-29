package ru.croc.ugd.ssr.service.trade.status.buyin;

import ru.croc.ugd.ssr.service.trade.status.StatusCalculateCommand;
import ru.croc.ugd.ssr.service.trade.status.TradeAdditionAbstractStatusCalculatorUnit;
import ru.croc.ugd.ssr.trade.AuctionResult;
import ru.croc.ugd.ssr.trade.BuyInStatus;
import ru.croc.ugd.ssr.trade.ClaimStatus;
import ru.croc.ugd.ssr.trade.TradeType;

import java.util.EnumSet;

public abstract class BuyInStatusSharedUnit extends TradeAdditionAbstractStatusCalculatorUnit<BuyInStatus> {
    private final EnumSet ALLOWED_TRADE_TYPES = EnumSet.of(
        TradeType.SIMPLE_TRADE,
        TradeType.TRADE_WITH_COMPENSATION,
        TradeType.TRADE_IN_TWO_YEARS);
    private final EnumSet REJECTED_CLAIM_STATUSES = EnumSet.of(
        ClaimStatus.REJECTED_WINNER_AUCTION,
        ClaimStatus.REJECTED_BY_PRIORITY);
    private final EnumSet AUCTION_RESULTS = EnumSet.of(
        AuctionResult.HELD,
        AuctionResult.FAILED);

    protected boolean isBuyInTradeType(StatusCalculateCommand statusCalculateCommand) {
        final TradeType tradeType = statusCalculateCommand.getTradeAdditionType().getTradeType();
        return ALLOWED_TRADE_TYPES.contains(tradeType);
    }

    protected boolean isRejectedClaimStatus(StatusCalculateCommand statusCalculateCommand) {
        final ClaimStatus claimStatus = statusCalculateCommand.getTradeAdditionType().getClaimStatus();
        return REJECTED_CLAIM_STATUSES.contains(claimStatus);
    }

    protected boolean existsAuctionResult(StatusCalculateCommand statusCalculateCommand) {
        final AuctionResult auctionResult = statusCalculateCommand.getTradeAdditionType().getAuctionResult();
        return AUCTION_RESULTS.contains(auctionResult);
    }
}
