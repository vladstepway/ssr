package ru.croc.ugd.ssr.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum DayScheduleEventType {

    CREATE_DAY("1"),
    UPDATE_DAY("2"),
    DELETE_DAY("3"),
    COPY_PERIOD("4"),
    DELETE_PERIOD("5");

    private final String code;

    /**
     * Returns source by value.
     * @param code code
     * @return source
     */
    public static DayScheduleEventType fromCode(final String code) {
        return Arrays.stream(DayScheduleEventType.values())
            .filter(source -> source.code.equals(code))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(code));
    }
}
