package ru.croc.ugd.ssr.dto.flatappointment;

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
public enum FlatAppointmentFlowStatus implements NotificationDescriptor {

    /**
     * Заявление доставлено в ведомство.
     */
    ACCEPTED(
        1040,
        null,
        "Заявление доставлено в ведомство",
        null,
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
     * Запись произведена.
     */
    REGISTERED(
        1050,
        null,
        "Запись произведена",
        "Осмотр предложенной квартиры запланирован на %s в %s по адресу %s. "
            + "Если вы не сможете явиться в назначенное время, отмените запись.<br>"
            + "Для осуществления осмотра квартиры потребуется документ, удостоверяющий личность "
            + "и письмо с предложением квартиры.<br>"
            + "<a href=\"%s\">Добавить в календарь</a>",
        "Запись произведена",
        "Уведомление правообладателей о дате и времени осмотра квартиры",
        "templates/flatappointment/owner/emailFlatAppointmentRegistered.html",
        "templates/flatappointment/owner/elkFlatAppointmentRegistered.html",
        null,
        null,
        null,
        "880126"
    ),
    /**
     * Получен запрос на отмену записи заявителем.
     */
    CANCEL_BY_APPLICANT_REQUEST(
        1069,
        null,
        "Получен запрос на отмену записи заявителем",
        null,
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
     * Отмена записи невозможна.
     */
    UNABLE_TO_CANCEL(
        10190,
        null,
        "Отмена записи невозможна",
        null,
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
     * Запись отменена по инициативе заявителя.
     */
    CANCELED_BY_APPLICANT(
        1090,
        null,
        "Запись отменена по инициативе заявителя",
        "Вы отменили запись на осмотр предложенной квартиры (заявление № %s). "
            + "Если это ошибка, сообщите об этом в информационно-справочную службу "
            + "по оказанию государственных услуг: +7 (495) 539-55-55.",
        "Запись отменена по инициативе заявителя",
        "Уведомление правообладателей об отмене записи по инициативе заявителя",
        "templates/flatappointment/owner/emailFlatAppointmentCanceledByApplicant.html",
        "templates/flatappointment/owner/elkFlatAppointmentCanceledByApplicant.html",
        null,
        null,
        null,
        "880130"
    ),
    /**
     * Запись отменена по инициативе ведомства.
     */
    CANCELED_BY_OPERATOR(
        1095,
        null,
        "Запись отменена по инициативе ведомства",
        "Департамент городского имущества города Москвы отменил запись на осмотр предложенной квартиры"
            + " по заявлению № %s по технической причине. Пожалуйста, запишитесь на другое время.",
        "Запись отменена по инициативе ГКУ МЦН",
        "Уведомление правообладателей об отмене записи по инициативе ведомства",
        "templates/flatappointment/owner/emailFlatAppointmentCanceledByOperator.html",
        "templates/flatappointment/owner/elkFlatAppointmentCanceledByOperator.html",
        null,
        null,
        null,
        "880129"
    ),
    /**
     * Запись перенесена по инициативе ведомства.
     */
    MOVED_BY_OPERATOR(
        8021,
        1,
        "Запись перенесена по инициативе ведомства",
        "Департамент городского имущества города Москвы перенес запись на осмотр "
            + "предложенной квартиры на %s в %s по адресу %s.<br>"
            + "Вы можете отменить запись.<br>"
            + "Если с Вами не согласовали перенос по заявлению № %s, обратитесь в "
            + "Центр информирования по переселению вашего дома.<br>"
            + "<a href=\"%s\">Добавить в календарь</a>",
        null,
        "Уведомление правообладателей о переносе записи по инициативе ведомства",
        "templates/flatappointment/owner/emailFlatAppointmentMovedByOperator.html",
        "templates/flatappointment/owner/elkFlatAppointmentMovedByOperator.html",
        null,
        null,
        null,
        "880127"
    ),
    /**
     * Услуга оказана. Осмотр произведен.
     */
    INSPECTION_PERFORMED(
        1075,
        null,
        "Услуга оказана",
        "Осмотр произведен.",
        "Услуга оказана. Осмотр произведен",
        null,
        null,
        null,
        null,
        null,
        null,
        null
    ),
    /**
     * Отказ в предоставлении услуги.
     */
    INSPECTION_NOT_PERFORMED(
        1080,
        null,
        "Отказ от предоставления услуги",
        "Осмотр квартиры не состоялся.",
        "Отказ в предоставлении услуги. Осмотр квартиры не состоялся",
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
        "Технический сбой",
        "Запись не произведена. Пожалуйста, повторите попытку.",
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
     * Технический сбой при отмене заявления.
     */
    TECHNICAL_CRASH_CANCELLATION_REJECTED(
        116999,
        null,
        "Технический сбой",
        "Запись не отменена. Пожалуйста, повторите попытку.",
        null,
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
            .map(FlatAppointmentFlowStatus::ofNullableInteger)
            .filter(Objects::nonNull)
            .collect(Collectors.joining("."));
    }

    private static String ofNullableInteger(final Integer integer) {
        return ofNullable(integer)
            .map(String::valueOf)
            .orElse(null);
    }

    public static FlatAppointmentFlowStatus of(final Integer code, final Integer subCode) {
        return Arrays.stream(FlatAppointmentFlowStatus.values())
            .filter(status -> Objects.equals(status.code, code))
            .filter(status -> Objects.equals(status.subCode, subCode))
            .findFirst()
            .orElse(null);
    }

    public static FlatAppointmentFlowStatus of(final String id) {
        return Arrays.stream(FlatAppointmentFlowStatus.values())
            .filter(status -> Objects.equals(status.getId(), id))
            .findFirst()
            .orElse(null);
    }

    public static FlatAppointmentFlowStatus ofEventCode(final String eventCode) {
        return Arrays.stream(FlatAppointmentFlowStatus.values())
            .filter(status -> Objects.equals(status.getEventCode(), eventCode))
            .findFirst()
            .orElse(null);
    }

    public static FlatAppointmentFlowStatus ofName(final String name) {
        return Arrays.stream(FlatAppointmentFlowStatus.values())
            .filter(status -> Objects.equals(status.name(), name))
            .findFirst()
            .orElse(null);
    }

}
