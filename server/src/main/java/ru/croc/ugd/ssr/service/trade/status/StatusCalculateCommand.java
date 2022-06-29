package ru.croc.ugd.ssr.service.trade.status;

import lombok.Builder;
import lombok.Data;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.trade.TradeAdditionType;

import java.util.List;

@Data
@Builder
public class StatusCalculateCommand {
    private TradeAdditionType tradeAdditionType;
    private List<PersonType> personTypes;
}
