package ru.croc.ugd.ssr.service.trade.utils;

import ru.croc.ugd.ssr.trade.BuyInStatus;
import ru.croc.ugd.ssr.trade.ClaimStatus;
import ru.croc.ugd.ssr.trade.CompensationStatus;
import ru.croc.ugd.ssr.trade.TradeType;

import java.util.EnumMap;
import java.util.Map;

/**
 * TradeAdditionEnumUtils.
 */
public class TradeAdditionEnumTranslations {
    /**
     * TradeType translations.
     */
    public static final Map<TradeType, String> TRADE_TYPE_TRANSLATIONS;
    /**
     * ClaimStatus translations.
     */
    public static final Map<ClaimStatus, String> CLAIM_STATUS_TRANSLATIONS;
    /**
     * BuyInStatus translations.
     */
    public static final Map<BuyInStatus, String> BUY_IN_STATUS_TRANSLATIONS;
    /**
     * CompensationStatus translations.
     */
    public static final Map<CompensationStatus, String> COMPENSATION_STATUS_TRANSLATIONS;

    static {
        TRADE_TYPE_TRANSLATIONS = new EnumMap<>(TradeType.class);
        TRADE_TYPE_TRANSLATIONS.put(TradeType.SIMPLE_TRADE, "Докупка");
        TRADE_TYPE_TRANSLATIONS.put(TradeType.TRADE_WITH_COMPENSATION, "Докупка с компенсацией");
        TRADE_TYPE_TRANSLATIONS.put(TradeType.TRADE_IN_TWO_YEARS, "Покупка в течение 2-х лет");
        TRADE_TYPE_TRANSLATIONS.put(TradeType.COMPENSATION, "Компенсация");
        TRADE_TYPE_TRANSLATIONS.put(TradeType.OUT_OF_DISTRICT, "Вне района");

        CLAIM_STATUS_TRANSLATIONS = new EnumMap<>(ClaimStatus.class);
        CLAIM_STATUS_TRANSLATIONS.put(ClaimStatus.ACTIVE, "Активное");
        CLAIM_STATUS_TRANSLATIONS.put(ClaimStatus.RECALLED, "Отзыв (ф.л.)");
        CLAIM_STATUS_TRANSLATIONS.put(ClaimStatus.REJECTED_BY_COMMISSION, "Отказ (Комиссия)");
        CLAIM_STATUS_TRANSLATIONS.put(ClaimStatus.REJECTED_WINNER_AUCTION, "Отказ первому (Аукцион)");
        CLAIM_STATUS_TRANSLATIONS.put(ClaimStatus.REJECTED_BY_PRIORITY, "Отказ приоритет");
        CLAIM_STATUS_TRANSLATIONS.put(ClaimStatus.AUCTION_LOST, "Аукцион проигран");
        CLAIM_STATUS_TRANSLATIONS.put(ClaimStatus.REJECTED, "Отказ");
        CLAIM_STATUS_TRANSLATIONS.put(ClaimStatus.REJECTED_COMPENSATION, "Отказ по компенсации");

        BUY_IN_STATUS_TRANSLATIONS = new EnumMap<>(BuyInStatus.class);
        BUY_IN_STATUS_TRANSLATIONS.put(BuyInStatus.REQUEST_APPLIED, "Подано заявление");
        BUY_IN_STATUS_TRANSLATIONS.put(BuyInStatus.POSITIVE_DECISION_OF_COMMISSION,
                "Положительное решение Комиссии Фонда");
        BUY_IN_STATUS_TRANSLATIONS.put(BuyInStatus.NEGATIVE_DECISION_OF_COMMISSION,
                "Отрицательное решение Комиссии Фонда");
        BUY_IN_STATUS_TRANSLATIONS.put(BuyInStatus.AUCTION, "Аукцион");
        BUY_IN_STATUS_TRANSLATIONS.put(BuyInStatus.AUCTION_REJECTED, "Аукцион отказ");
        BUY_IN_STATUS_TRANSLATIONS.put(BuyInStatus.AUCTION_LOST, "Аукцион проигран");
        BUY_IN_STATUS_TRANSLATIONS.put(BuyInStatus.AUCTION_WON, "Определен победитель");
        BUY_IN_STATUS_TRANSLATIONS.put(BuyInStatus.CONTRACT_PROVIDED, "Получен проект договора");
        BUY_IN_STATUS_TRANSLATIONS.put(BuyInStatus.CONTRACT_SIGNED, "Подписан договор");
        BUY_IN_STATUS_TRANSLATIONS.put(BuyInStatus.KEYS_ISSUED, "Получены ключи от новой квартиры");
        BUY_IN_STATUS_TRANSLATIONS.put(BuyInStatus.REJECTED, "Отказ");

        COMPENSATION_STATUS_TRANSLATIONS = new EnumMap<>(CompensationStatus.class);
        COMPENSATION_STATUS_TRANSLATIONS.put(CompensationStatus.LETTER_ACCEPTED, "Получено письмо с предложением");
        COMPENSATION_STATUS_TRANSLATIONS.put(CompensationStatus.INSPECTION_COMPETED, "Проведен осмотр");
        COMPENSATION_STATUS_TRANSLATIONS.put(CompensationStatus.AGREEMENT_APPLIED, "Подано согласие");
        COMPENSATION_STATUS_TRANSLATIONS.put(CompensationStatus.REJECTION_APPLIED, "Подан отказ");
        COMPENSATION_STATUS_TRANSLATIONS.put(CompensationStatus.CONTRACT_PROVIDED, "Получен проект договора");
        COMPENSATION_STATUS_TRANSLATIONS.put(CompensationStatus.CONTRACT_SIGNED, "Договор подписан");
        COMPENSATION_STATUS_TRANSLATIONS.put(CompensationStatus.KEYS_ISSUED, "Выданы ключи от новой квартиры");
        COMPENSATION_STATUS_TRANSLATIONS.put(CompensationStatus.APARTMENT_VACATED, "Квартира освобождена");
        COMPENSATION_STATUS_TRANSLATIONS.put(CompensationStatus.REJECTED, "Отказ");
    }
}
