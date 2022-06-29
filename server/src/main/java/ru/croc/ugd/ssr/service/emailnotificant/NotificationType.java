package ru.croc.ugd.ssr.service.emailnotificant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum NotificationType {
    CONTRACT_APPOINTMENT("contractAppointment"),
    FLAT_APPOINTMENT("flatAppointment"),
    GUARDIANSHIP("guardianship");

    private final String value;

    public static NotificationType fromValue(final String value) {
        return Arrays.stream(NotificationType.values())
            .filter(source -> source.value.equals(value))
            .findFirst()
            .orElse(null);
    }
}
