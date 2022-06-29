package ru.croc.ugd.ssr.service.trade.model;

import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import ru.croc.ugd.ssr.trade.EstateInfoType;
import ru.croc.ugd.ssr.trade.TradeAdditionType;

import java.util.Map;

@Slf4j
public class TradeAdditionGeneralConstants {
    static {
        try {
            TRADE_ADDITION_PROPERTY_TO_PRINT_MAP = ImmutableMap.<String, String>builder()
                .put(TradeAdditionType.class.getDeclaredField("recordNumber").getName(),
                        "Номер строки")
                .put(TradeAdditionType.class.getDeclaredField("attachedFileName").getName(),
                        "Название файла")
                .put(TradeAdditionType.class.getDeclaredField("newEstates").getName(),
                        "Заселяемая недвижимость")
                .put(EstateInfoType.class.getDeclaredField("cadNumber").getName(),
                        "Кадастровый номер")
                .put(EstateInfoType.class.getDeclaredField("address").getName(),
                        "Адрес")
                .put(EstateInfoType.class.getDeclaredField("flatNumber").getName(),
                        "Номер квартиры")
                .put(EstateInfoType.class.getDeclaredField("rooms").getName(),
                        "Комнаты")
                .put(TradeAdditionType.class.getDeclaredField("tradeType").getName(),
                        "Вид сделки")
                .put(TradeAdditionType.class.getDeclaredField("offerLetterDate").getName(),
                        "Дата письма с предложением квартиры")
                .put(TradeAdditionType.class.getDeclaredField("applicationDate").getName(),
                        "Дата подачи заявления")
                .put(TradeAdditionType.class.getDeclaredField("tradeResult").getName(),
                        "Результат сделки при виде сделки 4-5")
                .put(TradeAdditionType.class.getDeclaredField("commissionDecisionDate").getName(),
                        "Дата решения комиссии")
                .put(TradeAdditionType.class.getDeclaredField("commissionDecisionResult").getName(),
                        "Результат решения Комиссии")
                .put(TradeAdditionType.class.getDeclaredField("auctionDate").getName(),
                        "Дата проведения аукциона")
                .put(TradeAdditionType.class.getDeclaredField("auctionResult").getName(),
                        "Итог аукциона")
                .put(TradeAdditionType.class.getDeclaredField("contractReadinessDate").getName(),
                        "Дата готовности проекта договора")
                .put(TradeAdditionType.class.getDeclaredField("contractSignedDate").getName(),
                        "Дата подписания договора")
                .put(TradeAdditionType.class.getDeclaredField("contractNumber").getName(),
                        "Номер договора")
                .put(TradeAdditionType.class.getDeclaredField("keysIssueDate").getName(),
                        "Дата выдачи ключей")
                .put(TradeAdditionType.class.getDeclaredField("claimStatus").getName(),
                        "Статус заявления")
            .build();
        } catch (NoSuchFieldException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public static final String UPLOADED_FILE_PROCESSING_KEY = "UPLOADED_FILE_PROCESSING_KEY";
    public static final String BATCH_DOCUMENT_ID_PROCESSING_KEY = "BATCH_DOCUMENT_ID_PROCESSING_KEY";
    public static final String DATE_VALUE_FORMAT = "dd.MM.yyyy";
    public static final Map<String, String> TRADE_ADDITION_PROPERTY_TO_PRINT_MAP;
}
