package ru.croc.ugd.ssr.dto.personaldocument;

import static java.util.Optional.ofNullable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.croc.ugd.ssr.integration.service.notification.NotificationDescriptor;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Статусы для отправки в очередь.
 */
@Getter
@AllArgsConstructor
public enum PersonalDocumentApplicationFlowStatus implements NotificationDescriptor {
    /**
     * Документы приняты.
     */
    ACCEPTED(
        1075,
        null,
        null,
        "Документы приняты",
        "Документы приняты Департаментом городского имущества.",
        null,
        null,
        null,
        null,
        null,
        null,
        null
    ),
    /**
     * Технический сбой.
     */
    TECHNICAL_CRASH_REGISTRATION(
        103099,
        null,
        null,
        "Технический сбой",
        "Заявка не отправлена. Пожалуйста, повторите попытку.",
        null,
        null,
        null,
        null,
        null,
        null,
        null
    );

    private final Integer code;
    private final Integer subCode;
    private final String ssrStatus;
    private final String description;
    private final String statusText;
    private final String notificationTitle;
    private final String emailNotificationTemplatePath;
    private final String elkNotificationTemplatePath;
    private final String pushNotificationBody;
    private final String pushNotificationHeader;
    private final String smsNotification;
    private final String eventCode;

    public String getId() {
        return Stream.of(code, subCode)
            .map(PersonalDocumentApplicationFlowStatus::ofNullableInteger)
            .filter(Objects::nonNull)
            .collect(Collectors.joining("."));
    }

    private static String ofNullableInteger(final Integer integer) {
        return ofNullable(integer)
            .map(String::valueOf)
            .orElse(null);
    }

    public static PersonalDocumentApplicationFlowStatus of(final Integer code, final Integer subCode) {
        return Arrays.stream(PersonalDocumentApplicationFlowStatus.values())
            .filter(status -> Objects.equals(status.code, code))
            .filter(status -> Objects.equals(status.subCode, subCode))
            .findFirst()
            .orElse(null);
    }

    public static PersonalDocumentApplicationFlowStatus of(final String id) {
        return Arrays.stream(PersonalDocumentApplicationFlowStatus.values())
            .filter(status -> Objects.equals(status.getId(), id))
            .findFirst()
            .orElse(null);
    }

    public static PersonalDocumentApplicationFlowStatus ofEventCode(final String eventCode) {
        return Arrays.stream(PersonalDocumentApplicationFlowStatus.values())
            .filter(status -> Objects.equals(status.getEventCode(), eventCode))
            .findFirst()
            .orElse(null);
    }
}
