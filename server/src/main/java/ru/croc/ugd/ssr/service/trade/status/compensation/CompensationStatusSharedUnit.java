package ru.croc.ugd.ssr.service.trade.status.compensation;

import org.springframework.util.CollectionUtils;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.ResettlementHistory;
import ru.croc.ugd.ssr.service.trade.status.StatusCalculateCommand;
import ru.croc.ugd.ssr.service.trade.status.TradeAdditionAbstractStatusCalculatorUnit;
import ru.croc.ugd.ssr.trade.CompensationStatus;
import ru.croc.ugd.ssr.trade.TradeType;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

public abstract class CompensationStatusSharedUnit
        extends TradeAdditionAbstractStatusCalculatorUnit<CompensationStatus> {
    private final EnumSet ALLOWED_TRADE_TYPES = EnumSet.of(
            TradeType.COMPENSATION,
            TradeType.OUT_OF_DISTRICT);

    protected boolean isCompensationTradeType(StatusCalculateCommand statusCalculateCommand) {
        final TradeType tradeType = statusCalculateCommand.getTradeAdditionType().getTradeType();
        return ALLOWED_TRADE_TYPES.contains(tradeType);
    }

    protected boolean isResettlementStatusExist(final StatusCalculateCommand statusCalculateCommand,
                                                final String resettlementStatusToCheck) {
        final List<ResettlementHistory> resettlementHistories = statusCalculateCommand
                .getPersonTypes()
                .stream()
                .map(PersonType::getResettlementHistory)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(resettlementHistories)) {
            return resettlementHistories
                    .stream()
                    .map(ResettlementHistory::getStatus)
                    .collect(Collectors.toList())
                    .contains(resettlementStatusToCheck);
        }
        return false;
    }
}
