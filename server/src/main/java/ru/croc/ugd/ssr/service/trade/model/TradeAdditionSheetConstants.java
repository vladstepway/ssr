package ru.croc.ugd.ssr.service.trade.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * TradeAdditionSheetConstants.
 */
@Getter
@AllArgsConstructor
public enum TradeAdditionSheetConstants {

    NUMBER_COLUMN_INDEX(0, "Номер строки", "№",
            true),
    FILENAME_COLUMN_INDEX(1, "Название файла", "Название файла",
            true),
    UNOM_OLD_3_COLUMN_INDEX(2, "UNOM расселяемого дома при виде сделки 3",
            "При виде сделки 3", true),
    UNOM_OLD_COLUMN_INDEX(3, "UNOM расселяемого дома",
            "UNOM расселяемого дома", true),
    FLAT_OLD_NUM_COLUMN_INDEX(4, "Номер расселяемой квартиры",
            "Номер расселяемой квартиры", true),
    ROOM_OLD_NUMBER_COLUMN_INDEX(5, "Номер расселяемой комнаты",
            "Номер расселяемой комнаты", true),
    UNOM_NEW_COLUMN_INDEX(6, "UNOM новостройки",
            "UNOM новостройки", true),
    FLAT_NUM_NEW_COLUMN_INDEX(7, "Номер квартиры в новостройке",
            "Номер квартиры в новостройке", true),
    PERSON_ID_COLUMN_INDEX(8, "person_id", "person_id",
            true),
    AFFAIR_ID_COLUMN_INDEX(9, "affair_id", "affair_id",
            true),
    AGREEMENT_TYPE_COLUMN_INDEX(10, "Вид сделки", "Вид сделки",
            true),
    REQUEST_STATUS_COLUMN_INDEX(11, "Статус заявления", "Статус заявления",
            true),
    LETTER_DATE_COLUMN_INDEX(12, "Дата письма с предложением квартиры",
            "Дата письма с предложением квартиры", true),
    REQUEST_DATE_COLUMN_INDEX(13, "Дата подачи заявления",
            "Дата подачи заявления", true),
    DEAL_4_5_COLUMN_INDEX(14, "При виде сделки 4-5",
            "При виде сделки 4-5", true),
    COMMISSION_DATE_COLUMN_INDEX(15, "Дата решения комиссии",
            "Дата решения комиссии", true),
    COMMISSION_DECISION_COLUMN_INDEX(16, "Результат решения комиссии",
            "Результат решения Комиссии", true),
    AUCTION_DATE_COLUMN_INDEX(17, "Дата проведения аукциона",
            "Дата проведения аукциона", true),
    AUCTION_RESULT_COLUMN_INDEX(18, "Итог аукциона", "Итог аукциона",
            true),
    CONTRACT_READINESS_DATE_COLUMN_INDEX(19, "Дата готовности проекта договора",
            "Дата готовности проекта договора", true),
    SIGNED_CONTRACT_DATE_COLUMN_INDEX(20, "Дата подписания договора",
            "Дата подписания договора", true),
    CONTRACT_NUMBER_COLUMN_INDEX(21, "Номер договора",
            "Номер договора", true),
    KEY_ISSUE_DATE_COLUMN_INDEX(22, "Дата выдачи ключей",
            "Дата выдачи ключей", true),
    SELL_ID_COLUMN_INDEX(23, "Уникальный номер реализации",
        "Уникальный номер реализации", true),
    COMMENT_COLUMN_INDEX(24, "Комментарий",
            "Комментарий", false),
    DECODING_FIO_COLUMN_INDEX(25, "ФИО",
            "ФИО", false),
    DECODING_ADDRESS_OLD_COLUMN_INDEX(26, "Адрес отселяемый",
            "Адрес отселяемый", false),
    DECODING_ADDRESS_NEW_COLUMN_INDEX(27, "Адрес заселяемый",
            "Адрес заселяемый", false),
    IS_ROW_RECORDED_INDEX(28, "Загружено","Загружено", false);

    private final Integer columnIndex;
    private final String columnReadableName;
    private final String columnOriginalPartName;
    private final Boolean isOriginalHeader;

    public static TradeAdditionSheetConstants findByIndex(final Integer columnIndex) {
        for (TradeAdditionSheetConstants v : values()) {
            if (Objects.equals(columnIndex, v.columnIndex)) {
                return v;
            }
        }
        return null;
    }
}
