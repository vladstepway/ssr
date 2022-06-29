package ru.croc.ugd.ssr.dto.contractdigitalsign;

import static java.util.Optional.ofNullable;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Статусы по уведомлению 880141 для отправки в очередь.
 */
@Getter
@AllArgsConstructor
public enum ContractDigitalSignNotificationStatus {
    /**
     * Договор подписан некорректной подписью.
     */
    SIGNED_INCORRECTLY(
        1080,
        1,
        "Договор подписан некорректной подписью",
        "При подписании договора должна использоваться усиленная квалифицированная электронная подпись, "
            + "действующая на момент подписания, принадлежащая ее владельцу.",
        "880141"
    ),
    /**
     * Правообладатель подписал договор некорректной подписью.
     */
    SIGNED_INCORRECTLY_BY_OWNER(
        1080,
        2,
        "Правообладатель подписал договор некорректной подписью",
        "%s подписал договор некорректной электронной подписью.",
        "880141"
    ),
    /**
     * Срок подписания истек.
     */
    SIGNING_PERIOD_EXPIRED(
        1080,
        3,
        "Срок подписания истек",
        "Договор не подписан в установленный срок.",
        "880141"
    ),
    /**
     * Правообладатель подписал договор.
     */
    SIGNED_BY_OWNER(
        8021,
        null, // X - порядковый номер подписанта
        "Правообладатель подписал договор",
        "Правообладатель %s подписал договор на новое жилое помещение, находящееся по адресу %s.",
        "880141"
    ),
    /**
     * Подписанный договор принят.
     */
    ACCEPTED(
        1077,
        null,
        "Подписанный договор принят",
        "Получен подписанный вами договор на новое жилое помещение, находящееся по адресу %s.",
        "880141"
    ),
    /**
     * Подписанный договор отправлен.
     */
    SENT(
        8011,
        null,
        null,
        null,
        "880141"
    );

    private final Integer code;
    private final Integer subCode;
    private final String description;
    private final String statusText;
    private final String eventCode;

    public String getId() {
        return Stream.of(code, subCode)
            .map(ContractDigitalSignNotificationStatus::ofNullableInteger)
            .filter(Objects::nonNull)
            .collect(Collectors.joining("."));
    }

    private static String ofNullableInteger(final Integer integer) {
        return ofNullable(integer)
            .map(String::valueOf)
            .orElse(null);
    }
}
