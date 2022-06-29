package ru.croc.ugd.ssr.service.trade.utils;

import static ru.croc.ugd.ssr.service.trade.model.TradeAdditionSheetConstants.AGREEMENT_TYPE_COLUMN_INDEX;

import ru.croc.ugd.ssr.service.excel.model.process.XssfCellProcessResult;
import ru.croc.ugd.ssr.service.excel.model.process.XssfRowProcessResult;
import ru.croc.ugd.ssr.trade.TradeType;

import java.util.Objects;

/**
 * TradeTypeUtils.
 */
public class TradeTypeUtils {
    public static boolean is4or5TradeType(final XssfRowProcessResult xssfRowProcessResult) {
        final XssfCellProcessResult agreementTypeResult = xssfRowProcessResult
                .findByIndex(AGREEMENT_TYPE_COLUMN_INDEX.getColumnIndex());
        if (agreementTypeResult == null) {
            return false;
        }
        return Objects.equals(agreementTypeResult.getCellRawValue(),
                TradeType.COMPENSATION.value())
                || Objects.equals(agreementTypeResult.getCellRawValue(),
                TradeType.OUT_OF_DISTRICT.value());
    }

    public static boolean is4or5TradeType(final TradeType tradeType) {
        return Objects.equals(tradeType, TradeType.COMPENSATION)
                || Objects.equals(tradeType, TradeType.OUT_OF_DISTRICT);
    }
}
