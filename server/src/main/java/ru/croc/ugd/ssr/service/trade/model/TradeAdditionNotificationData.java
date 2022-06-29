package ru.croc.ugd.ssr.service.trade.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import ru.croc.ugd.ssr.integration.service.notification.NotificationDescriptor;
import ru.croc.ugd.ssr.trade.BuyInStatus;
import ru.croc.ugd.ssr.trade.ClaimStatus;
import ru.croc.ugd.ssr.trade.CompensationStatus;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * ЕДП статусы для отправки в очередь.
 */
@Getter
@AllArgsConstructor
public enum TradeAdditionNotificationData implements NotificationDescriptor {
    REQUEST_APPLIED_BUY_IN(
        "Уведомление о поданном заявлении на докупку",
        "templates/tradeAddition/emailTradeAdditionApplicationBuyIn.html",
        "templates/tradeAddition/elkTradeAdditionApplicationBuyIn.html",
        "Вы подали заявление о приобретении квартиры за доплату по программе реновации",
        "Вы подали заявление о приобретении квартиры за доплату по программе реновации",
        "Вы подали заявление о приобретении квартиры за доплату по программе реновации." +
            " Подробную информацию узнайте в личном кабинете на mos.ru:" +
            " https://my.mos.ru/my/#/settings/info",
        "880046",
        BuyInStatus.REQUEST_APPLIED,
        200,
        null
    ),

    LETTER_ACCEPTED_COMPENSATION(
        "Уведомление с предложением квартиры меньшей жилой площади с компенсацией / квартиры в другом районе",
        "templates/tradeAddition/emailTradeAdditionOfferCompensation.html",
        "templates/tradeAddition/elkTradeAdditionOfferCompensation.html",
        "Для Вас подготовлено письмо с предложением новой квартиры",
        "Для Вас подготовлено письмо с предложением новой квартиры",
        "Для Вас подготовлено письмо с предложением новой квартиры." +
            " Подробную информацию узнайте в личном кабинете на mos.ru:" +
            " https://my.mos.ru/my/#/settings/info",
        "880040",
        CompensationStatus.LETTER_ACCEPTED,
        300,
        null
    ),

    INSPECTION_COMPETED_COMPENSATION(
        "Уведомление о необходимости подписать заявление на согласие/отказ с компенсацией после просмотра квартиры",
        "templates/tradeAddition/emailTradeAdditionSignApplicationRequestCompensation.html",
        "templates/tradeAddition/elkTradeAdditionSignApplicationRequestCompensation.html",
        "Напоминаем о необходимости подписать заявление о согласии" +
            " (или об отказе) на переселение в предложенную квартиру",
        "Вам необходимо подать заявление о согласии (или об отказе) на переселение в предложенную квартиру",
        "Напоминаем о необходимости подписать заявление о согласии/отказе на переселение в предложенную квартиру." +
            " Подробную информацию узнайте в личном кабинете на mos.ru:" +
            " https://my.mos.ru/my/#/settings/info",
        "880055",
        CompensationStatus.INSPECTION_COMPETED,
        350,
        null
    ),

    CONSENT_COMPENSATION(
        "Уведомление о поданном согласии",
        "templates/tradeAddition/emailTradeAdditionConsentCompensation.html",
        "templates/tradeAddition/elkTradeAdditionConsentCompensation.html",
        "Благодарим Вас за выраженное согласие на предложенную квартиру по программе реновации",
        "Благодарим Вас за выраженное согласие на предложенную квартиру по программе реновации",
        "Благодарим Вас за выраженное согласие на предложенную квартиру по программе реновации." +
            " Подробную информацию узнайте в личном кабинете на mos.ru:" +
            " https://my.mos.ru/my/#/settings/info ",
        "880056",
        CompensationStatus.AGREEMENT_APPLIED,
        400,
        null
    ),

    REFUSE_COMPENSATION(
        "Уведомление о поданном отказе",
        "templates/tradeAddition/emailTradeAdditionRefuseCompensation.html",
        "templates/tradeAddition/elkTradeAdditionRefuseCompensation.html",
        "Вы подали заявление об отказе от предложенной квартиры по программе реновации",
        "Вы подали заявление об отказе от предложенной квартиры по программе реновации",
        "Вы подали заявление об отказе от предложенной квартиры по программе реновации." +
            " Подробную информацию узнайте в личном кабинете на mos.ru:" +
            " https://my.mos.ru/my/#/settings/info",
        "880044",
        CompensationStatus.REJECTION_APPLIED,
        400,
        null
    ),

    DECISION_POSITIVE_BUY_IN(
        "Уведомление о положительном решении по докупке",
        "templates/tradeAddition/emailTradeAdditionDecisionPositiveBuyIn.html",
        "templates/tradeAddition/elkTradeAdditionDecisionPositiveBuyIn.html",
        "По Вашему заявлению вынесено положительное решение о заключении договора" +
            " о приобретении квартиры за доплату",
        "Вынесено положительное решение о заключении договора о приобретении квартиры за доплату",
        "По Вашему заявлению вынесено положительное решение о заключении договора о" +
            " приобретении квартиры за доплату. Подробную информацию узнайте в" +
            " личном кабинете на mos.ru: https://my.mos.ru/my/#/settings/info",
        "880047",
        BuyInStatus.POSITIVE_DECISION_OF_COMMISSION,
        700,
        null
    ),

    DECISION_NEGATIVE_BUY_IN(
        "Уведомление об отрицательном решении по докупке",
        "templates/tradeAddition/emailTradeAdditionDecisionNegativeBuyIn.html",
        "templates/tradeAddition/elkTradeAdditionDecisionNegativeBuyIn.html",
        "По Вашему заявлению вынесено отрицательное решение о" +
            " заключении договора о приобретении квартиры за доплату",
        "Вынесено отрицательное решение о заключении договора о приобретении квартиры за доплату",
        "По Вашему заявлению вынесено отрицательное решение о" +
            " заключении договора о приобретении квартиры за доплату." +
            " Подробную информацию узнайте в личном кабинете на mos.ru: https://my.mos.ru/my/#/settings/info",
        "880048",
        BuyInStatus.NEGATIVE_DECISION_OF_COMMISSION,
        700,
        null
    ),

    AUCTION_BUY_IN(
        "Уведомление с решением о проведении аукциона",
        "templates/tradeAddition/emailTradeAdditionAuctionBuyIn.html",
        "templates/tradeAddition/elkTradeAdditionAuctionBuyIn.html",
        "Вынесено решение о проведении аукциона на новую квартиру за доплату",
        "Вынесено решение о проведении аукциона на новую квартиру за доплату",
        "Вынесено решение о проведении аукциона на новую квартиру за доплату." +
            " Подробную информацию узнайте в личном кабинете на mos.ru:" +
            " https://my.mos.ru/my/#/settings/info",
        "880081",
        BuyInStatus.AUCTION,
        750,
        null
    ),

    AUCTION_BUY_IN_FOR_REJECTED(
        "Уведомление с решением о проведении аукциона",
        "templates/tradeAddition/emailTradeAdditionAuctionBuyIn.html",
        "templates/tradeAddition/elkTradeAdditionAuctionBuyIn.html",
        "Вынесено решение о проведении аукциона на новую квартиру за доплату",
        "Вынесено решение о проведении аукциона на новую квартиру за доплату",
        "Вынесено решение о проведении аукциона на новую квартиру за доплату." +
            " Подробную информацию узнайте в личном кабинете на mos.ru:" +
            " https://my.mos.ru/my/#/settings/info",
        "880081",
        BuyInStatus.AUCTION_REJECTED,
        750,
        ClaimStatus.REJECTED_WINNER_AUCTION
    ),

    AUCTION_RESULT_POSITIVE_BUY_IN(
        "Уведомление о результатах проведения аукциона (положительное)",
        "templates/tradeAddition/emailTradeAdditionAuctionResultPositiveBuyIn.html",
        "templates/tradeAddition/elkTradeAdditionAuctionResultPositiveBuyIn.html",
        "По результатам аукциона за Вами признано право на заключение договора" +
            " о приобретении квартиры за доплату",
        "За Вами признано право на заключение договора о приобретении квартиры за доплату",
        "По результатам аукциона за Вами признано право на заключение договора о " +
            "приобретении квартиры за доплату. Подробную информацию узнайте в " +
            "личном кабинете на mos.ru: https://my.mos.ru/my/#/settings/info",
        "880082",
        BuyInStatus.AUCTION_WON,
        800,
        null
    ),

    AUCTION_RESULT_NEGATIVE_BUY_IN(
        "Уведомление о результатах проведения аукциона (отрицательное)",
        "templates/tradeAddition/emailTradeAdditionAuctionResultNegativeBuyIn.html",
        "templates/tradeAddition/elkTradeAdditionAuctionResultNegativeBuyIn.html",
        "Вынесено отрицательное решение по результатам проведения аукциона",
        "Вынесено отрицательное решение по результатам проведения аукциона",
        "Вынесено отрицательное решение по результатам проведения аукциона." +
            " Подробную информацию узнайте в личном кабинете на mos.ru: https://my.mos.ru/my/#/settings/info",
        "880095",
        BuyInStatus.AUCTION_LOST,
        800,
        ClaimStatus.AUCTION_LOST
    ),

    AUCTION_RESULT_NEGATIVE_BUY_IN_FOR_REJECTED(
        "Уведомление о результатах проведения аукциона (отрицательное)",
        "templates/tradeAddition/emailTradeAdditionAuctionResultNegativeBuyIn.html",
        "templates/tradeAddition/elkTradeAdditionAuctionResultNegativeBuyIn.html",
        "Вынесено отрицательное решение по результатам проведения аукциона",
        "Вынесено отрицательное решение по результатам проведения аукциона",
        "Вынесено отрицательное решение по результатам проведения аукциона." +
            " Подробную информацию узнайте в личном кабинете на mos.ru: https://my.mos.ru/my/#/settings/info",
        "880095",
        BuyInStatus.AUCTION_REJECTED,
        800,
        ClaimStatus.REJECTED_BY_PRIORITY
    ),

    CONTRACT_PROVIDED_BUY_IN(
        "Уведомление о готовности проекта договора с приглашением на подписание договора",
        "templates/tradeAddition/emailTradeAdditionSignContractRequestBuyIn.html",
        "templates/tradeAddition/elkTradeAdditionSignContractRequestBuyIn.html",
        "Подготовлен договор о приобретении квартиры за доплату",
        "Подготовлен договор о приобретении квартиры за доплату",
        "Подготовлен договор о приобретении квартиры за доплату." +
            " Подробную информацию узнайте в личном кабинете на mos.ru:" +
            " https://my.mos.ru/my/#/settings/info",
        "880059",
        BuyInStatus.CONTRACT_PROVIDED,
        900,
        null
    ),

    CONTRACT_PROVIDED_COMPENSATION(
        "Уведомление о готовности проекта договора с приглашением на подписание договора",
        "templates/tradeAddition/emailTradeAdditionSignContractRequestCompensation.html",
        "templates/tradeAddition/elkTradeAdditionSignContractRequestCompensation.html",
        "Для Вас подготовлен договор на предложенную квартиру по программе реновации",
        "Для Вас подготовлен договор на предложенную квартиру по программе реновации",
        "Для Вас подготовлен проект договора на предложенную квартиру по программе реновации." +
            " Подробную информацию узнайте в личном кабинете на mos.ru:" +
            " https://my.mos.ru/my/#/settings/info",
        "880058",
        CompensationStatus.CONTRACT_PROVIDED,
        900,
        null
    ),

    KEYS_ISSUED_COMPENSATION(
        "Уведомление о полученных ключах с предложением воспользоваться помощью при переезде",
        "templates/tradeAddition/emailTradeAdditionReceiptOfKeys.html",
        "templates/tradeAddition/elkTradeAdditionReceiptOfKeys.html",
        "Вы получили ключи от новой квартиры и теперь можете заниматься организацией переезда",
        "Вы получили ключи от новой квартиры и теперь можете заниматься организацией переезда",
        "Вы получили ключи от новой квартиры и теперь можете заниматься организацией переезда." +
            " Подробную информацию узнайте в личном кабинете на mos.ru: https://my.mos.ru/my/#/settings/info",
        "880050",
        CompensationStatus.KEYS_ISSUED,
        1100,
        null
    ),

    KEYS_ISSUED_BUY_IN(
        "Уведомление о полученных ключах с предложением воспользоваться помощью при переезде",
        "templates/tradeAddition/emailTradeAdditionReceiptOfKeys.html",
        "templates/tradeAddition/elkTradeAdditionReceiptOfKeys.html",
        "Вы получили ключи от новой квартиры и теперь можете заниматься организацией переезда",
        "Вы получили ключи от новой квартиры и теперь можете заниматься организацией переезда",
        "Вы получили ключи от новой квартиры и теперь можете заниматься организацией переезда." +
            " Подробную информацию узнайте в личном кабинете на mos.ru: https://my.mos.ru/my/#/settings/info ",
        "880060",
        BuyInStatus.KEYS_ISSUED,
        1100,
        null
    );

    private final String notificationTitle;
    private final String emailNotificationTemplatePath;
    private final String elkNotificationTemplatePath;
    private final String pushNotificationBody;
    private final String pushNotificationHeader;
    private final String smsNotification;
    private final String eventCode;
    private final Enum requiredStatus;
    private final int priority;
    private final ClaimStatus claimStatus;

    /**
     * Returns -1 if not found
     * @param eventCode eventCode
     * @return found priority
     */
    public static int getPriorityByEventCode(final String eventCode) {
        return Arrays.asList(TradeAdditionNotificationData.values())
            .stream()
            .filter(tradeAdditionNotificationData ->
                StringUtils.equals(tradeAdditionNotificationData.getEventCode(), eventCode))
            .findFirst()
            .map(TradeAdditionNotificationData::getPriority)
            .orElse(-1);
    }

    public static Optional<TradeAdditionNotificationData> findByEventCode(final String eventCode) {
        return Arrays.asList(TradeAdditionNotificationData
                .values())
            .stream()
            .filter(tradeAdditionNotificationData -> Objects.equals(tradeAdditionNotificationData.eventCode, eventCode))
            .findFirst();
    }
}
