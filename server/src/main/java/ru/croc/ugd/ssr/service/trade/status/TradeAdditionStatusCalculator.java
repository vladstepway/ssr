package ru.croc.ugd.ssr.service.trade.status;

import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.service.trade.status.buyin.BuyInStatusAuctionKeysIssued;
import ru.croc.ugd.ssr.service.trade.status.compensation.CompensationStatusKeysIssued;
import ru.croc.ugd.ssr.trade.BuyInStatus;
import ru.croc.ugd.ssr.trade.CompensationStatus;

@Service
public class TradeAdditionStatusCalculator {
    private TradeAdditionAbstractStatusCalculatorUnit<CompensationStatus> compensationStatusCalculator
            = new CompensationStatusKeysIssued();
    private TradeAdditionAbstractStatusCalculatorUnit<BuyInStatus> buyInStatusCalculator
            = new BuyInStatusAuctionKeysIssued();

    public BuyInStatus calculateBuyInStatus(final StatusCalculateCommand statusCalculateCommand) {
        return buyInStatusCalculator.getStatus(statusCalculateCommand);
    }

    public CompensationStatus calculateClaimStatus(final StatusCalculateCommand statusCalculateCommand) {
        return compensationStatusCalculator.getStatus(statusCalculateCommand);
    }
}
