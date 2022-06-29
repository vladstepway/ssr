package ru.croc.ugd.ssr.service.excel.disabledperson.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public enum DisabledPersonSheetConstants {

    LAST_NAME_COLUMN_INDEX(0, "Фамилия", "Фамилия", true),
    FIRST_NAME_COLUMN_INDEX(1, "Имя", "Имя", true),
    MIDDLE_NAME_COLUMN_INDEX(2, "Отчество", "Отчество", true),
    BIRTH_DATE_COLUMN_INDEX(3, "Дата рождения", "Дата рождения", true),
    ADDRESS_FROM_COLUMN_INDEX(4, "Адрес отселяемого дома", "Адрес отселяемого дома ", true),
    PHONE_COLUMN_INDEX(5, "Телефон", "Телефон", true),
    DISTRICT_COLUMN_INDEX(6, "Район", "Район", true),
    AREA_COLUMN_INDEX(7, "Округ", "Округ", true),
    USING_WHEELCHAIR_COLUMN_INDEX(8, "Использование кресла-коляски", "Использование кресла-коляски (да/нет)", true),
    UNOM_COLUMN_INDEX(9, "UNOM", "UNOM дома", true),
    COMMENT_COLUMN_INDEX(10, "Комментарий", "Комментарий", false),
    IS_ROW_RECORDED_INDEX(11, "Загружено", "Загружено", false);

    private final Integer columnIndex;
    private final String columnReadableName;
    private final String columnOriginalPartName;
    private final Boolean isOriginalHeader;

    public static DisabledPersonSheetConstants findByIndex(final Integer columnIndex) {
        for (DisabledPersonSheetConstants v : values()) {
            if (Objects.equals(columnIndex, v.columnIndex)) {
                return v;
            }
        }
        return null;
    }
}
