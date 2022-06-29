package ru.croc.ugd.ssr.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum CommissionInspectionOrganisationType {

    PREFECTURE(1, "Префектура", "Префектурой"),
    RENOVATION_FUND(2, "Фонд реновации", "Фондом реновации"),
    HOT_LINE(3, "Горячая линия", "Горячей линией");

    private final int typeValue;
    private final String ssrValue;
    private final String elkValue;

    public static CommissionInspectionOrganisationType ofTypeValue(final int typeValue) {
        return Arrays.stream(values())
            .filter(source -> source.getTypeValue() == typeValue)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(String.valueOf(typeValue)));
    }
}
