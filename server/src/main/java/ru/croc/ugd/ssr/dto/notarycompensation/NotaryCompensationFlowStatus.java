package ru.croc.ugd.ssr.dto.notarycompensation;

import static java.util.Optional.ofNullable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.croc.ugd.ssr.integration.service.notification.NotificationDescriptor;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Статусы для отправки в очередь.
 */
@Getter
@AllArgsConstructor
public enum NotaryCompensationFlowStatus implements NotificationDescriptor {

    /**
     * Заявление доставлено в ведомство.
     */
    ACCEPTED(
        1040,
        null,
        "Заявление доставлено в ведомство",
        null,
        "Заявление принято",
        "Уведомление третьим лицам о согласии/отказе на возмещение оплаты услуг нотариуса",
        "templates/notarycompensation/emailNotaryCompensationConsentRequest.html",
        "templates/notarycompensation/elkNotaryCompensationConsentRequest.html",
        null,
        null,
        null,
        "880142"
    ),
    /**
     * Требуется получение согласия правообладателей.
     */
    OWNER_CONSENT_REQUIRED(
        1040,
        null,
        "Требуется получение согласия правообладателей",
        null,
        "Требуется получение согласия правообладателей",
        null,
        null,
        null,
        null,
        null,
        null,
        null
    ),
    /**
     * Заявление принято к рассмотрению.
     */
    REGISTERED(
        1050,
        null,
        "Заявление на рассмотрении",
        "Заявление принято к рассмотрению",
        "Заявление на рассмотрении",
        null,
        null,
        null,
        null,
        null,
        null,
        null
    ),

    /**
     * Отказано в предоставлении услуги.
     */
    REJECTED(
        1080,
        null,
        "Отказ в предоставлении услуги",
        "Заявление не принято по следующей причине:\n%s",
        "Отказ в предоставлении услуги",
        null,
        null,
        null,
        null,
        null,
        null,
        null
    ),

    /**
     * Услуга оказана. Решение положительное.
     */
    CONFIRMED(
        1075,
        null,
        "Услуга оказана. Решение положительное",
        "Возмещение оплаты услуг нотариуса будет произведено в течение 1 календарного месяца"
            + " по реквизитам, указанным в заявлении.",
        "Услуга оказана. Решение положительное",
        null,
        null,
        null,
        null,
        null,
        null,
        null
    ),

    /**
     * Денежные средства не выплачены.
     */
    MONEY_NOT_PAYED(
        8021,
        3,
        "Денежные средства не выплачены",
        //todo Konstantin: aslu said that currently mos.ru is not providing this
        // service and nobody knows actual phone number for this case
        "Невозможно выплатить денежные средства по следующей причине:\n"
            + " %s.\n"
            + "За подробной информацией просьба обратиться по телефону UNKNOWN_PHONE_NUMBER_MUST_BE_REPLACED.",
        "Денежные средства не выплачены",
        null,
        null,
        null,
        null,
        null,
        null,
        null
    ),
    /**
     * Технический сбой при регистрации заявления.
     */
    TECHNICAL_CRASH_REGISTRATION(
        103099,
        null,
        "Технический сбой",
        "Заявка не отправлена. Пожалуйста, повторите попытку.",
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null
    ),
    /**
     * Уведомление о возможности получения возмещения оплаты услуг нотариуса.
     */
    COMPENSATION_OPPORTUNITY(
        null,
        null,
        null,
        null,
        null,
        "Уведомление о возможности получения возмещения оплаты услуг нотариуса",
        "templates/notarycompensation/emailNotaryCompensationOpportunity.html",
        "templates/notarycompensation/elkNotaryCompensationOpportunity.html",
        null,
        null,
        null,
        "880140"
    );

    private final Integer code;
    private final Integer subCode;
    private final String description;
    private final String statusText;
    private final String ssrStatus;
    private final String notificationTitle;
    private final String emailNotificationTemplatePath;
    private final String elkNotificationTemplatePath;
    private final String pushNotificationBody;
    private final String pushNotificationHeader;
    private final String smsNotification;
    private final String eventCode;

    public String getId() {
        return Stream.of(code, subCode)
            .map(NotaryCompensationFlowStatus::ofNullableInteger)
            .filter(Objects::nonNull)
            .collect(Collectors.joining("."));
    }

    private static String ofNullableInteger(final Integer integer) {
        return ofNullable(integer)
            .map(String::valueOf)
            .orElse(null);
    }

    public static NotaryCompensationFlowStatus of(final Integer code, final Integer subCode) {
        return Arrays.stream(NotaryCompensationFlowStatus.values())
            .filter(status -> Objects.equals(status.code, code))
            .filter(status -> Objects.equals(status.subCode, subCode))
            .findFirst()
            .orElse(null);
    }

    public static NotaryCompensationFlowStatus of(final String id) {
        return Arrays.stream(NotaryCompensationFlowStatus.values())
            .filter(status -> Objects.equals(status.getId(), id))
            .findFirst()
            .orElse(null);
    }

    public static Optional<NotaryCompensationFlowStatus> ofEventCode(final String eventCode) {
        return Arrays.stream(NotaryCompensationFlowStatus.values())
            .filter(notaryCompensationFlowStatus -> Objects.equals(notaryCompensationFlowStatus.eventCode, eventCode))
            .findFirst();
    }
}
