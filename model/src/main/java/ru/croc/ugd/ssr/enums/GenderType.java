package ru.croc.ugd.ssr.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum GenderType {

    Male(1, 'M', "Мужской", "Male"),
    Female(2, 'F', "Женский", "Female");

    private final int code;
    private final char charCode;
    private final String name;
    private final String englishName;

    public static GenderType fromCode(final int code) {
        return Arrays.stream(GenderType.values())
            .filter(source -> source.code == code)
            .findFirst()
            .orElse(null);
    }

    public static GenderType fromCharCode(final char charCode) {
        return Arrays.stream(GenderType.values())
            .filter(source -> source.charCode == charCode)
            .findFirst()
            .orElse(null);
    }

    public static GenderType fromName(final String name) {
        return Arrays.stream(GenderType.values())
            .filter(source -> source.name.equalsIgnoreCase(name))
            .findFirst()
            .orElse(null);
    }

    public static GenderType fromEnglishName(final String name) {
        return Arrays.stream(GenderType.values())
            .filter(source -> source.englishName.equalsIgnoreCase(name))
            .findFirst()
            .orElse(null);
    }
}
