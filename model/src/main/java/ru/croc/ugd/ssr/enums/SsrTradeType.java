package ru.croc.ugd.ssr.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum SsrTradeType {

    EQUAL_TRADE("Предлагаемая квартира", "Равнозначное предложение"),
    ADDITIONAL("Ваш выбор по докупке", "Докупка");

    private final String commissionInspectionTypeName;
    private final String apartmentInspectionTypeName;

    public static SsrTradeType ofApartmentInspectionTypeName(final String apartmentInspectionTypeName) {
        return Arrays.stream(values())
            .filter(source -> source.getApartmentInspectionTypeName().equalsIgnoreCase(apartmentInspectionTypeName))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(apartmentInspectionTypeName));
    }

    public static SsrTradeType ofCommissionInspectionTypeName(final String commissionInspectionTypeName) {
        return Arrays.stream(values())
            .filter(source -> source.getCommissionInspectionTypeName().equalsIgnoreCase(commissionInspectionTypeName))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(commissionInspectionTypeName));
    }
}
