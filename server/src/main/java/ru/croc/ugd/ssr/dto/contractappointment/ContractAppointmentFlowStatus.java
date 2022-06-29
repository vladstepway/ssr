package ru.croc.ugd.ssr.dto.contractappointment;

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
public enum ContractAppointmentFlowStatus implements NotificationDescriptor {

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
        null,
        null
    ),
    /**
     * Услуга оказана. Договор заключен.
     */
    CONTRACT_SIGNED(
        1075,
        null,
        "Услуга оказана",
        "Договор на равнозначное жилое помещение заключен %s."
            + " Для просмотра и скачивания договора, перейдите по ссылке: %s.",
        "Услуга оказана. Договор заключен",
        "Уведомление о заключении договора",
        "templates/contractappointment/owner/emailContractAppointmentContractSigned.html",
        "templates/contractappointment/owner/elkContractAppointmentContractSigned.html",
        null,
        null,
        null,
        "880114",
        "880114-1"
    ),
    /**
     * Отказ в предоставлении услуги.
     */
    REFUSE_TO_PROVIDE_SERVICE(
        1080,
        null,
        "Отказ от предоставления услуги",
        "Услуга не оказана.",
        "Отказ в предоставлении услуги. Договор не заключен",
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
        "Вы отменили запись на прием по заключению договора (заявление № %s) по квартире,"
            + " находящейся по адресу %s. Если это ошибка, сообщите об этом в"
            + " Информационно-справочную службу по оказанию государственных услуг: +7 (495) 539-55-55.",
        "Запись отменена по инициативе заявителя",
        "Уведомление правообладателей об отмене записи по инициативе заявителя",
        "templates/contractappointment/owner/emailContractAppointmentCanceledByApplicant.html",
        "templates/contractappointment/owner/elkContractAppointmentCanceledByApplicant.html",
        null,
        null,
        null,
        "880134",
        null
    ),
    /**
     * Запись отменена по инициативе ведомства.
     */
    CANCELED_BY_OPERATOR(
        1095,
        null,
        "Запись отменена по инициативе ведомства",
        "Департамент городского имущества города Москвы отменил запись на прием по заключению договора "
            + "(заявление № %s) на %s в %s по причине: %s. Пожалуйста, запишитесь на другое время.",
        "Запись отменена по инициативе ведомства",
        "Уведомление правообладателей об отмене записи по инициативе ведомства",
        "templates/contractappointment/owner/emailContractAppointmentCanceledByOperator.html",
        "templates/contractappointment/owner/elkContractAppointmentCanceledByOperator.html",
        null,
        null,
        null,
        "880135",
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
        null,
        null
    ),
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
        "Заключение договора запланировано по квартире, находящейся по адресу %s"
            + " на %s в %s по адресу %s. Если Вы не сможете явиться в назначенное время"
            + ", пожалуйста, отмените запись. <br>"
            + "<a href=\"%s\">Добавить в календарь</a>",
        "Запись произведена",
        "Уведомление правообладателей о дате заключения договора",
        "templates/contractappointment/owner/emailContractAppointmentRegistered.html",
        "templates/contractappointment/owner/elkContractAppointmentRegistered.html",
        null,
        null,
        null,
        "880131",
        null
    ),
    /**
     * Запись перенесена по инициативе ведомства.
     */
    MOVED_BY_OPERATOR(
        8021,
        1,
        "Запись перенесена по инициативе ведомства",
        "Департамент городского имущества города Москвы перенес Вашу запись на прием по заключению договора"
            + " по квартире, находящейся по адресу %s, на %s в %s по адресу %s.<br/>"
            + "Вы можете отменить запись.<br/>"
            + "Если с Вами не согласовали перенос по обращению № %s,"
            + " обратитесь в Центр информирования по переселению вашего дома.<br/>"
            + "<a href=\"%s\">Добавить в календарь</a>",
        null,
        "Уведомление правообладателей о переносе записи по инициативе ведомства",
        "templates/contractappointment/owner/emailContractAppointmentMovedByOperator.html",
        "templates/contractappointment/owner/elkContractAppointmentMovedByOperator.html",
        null,
        null,
        null,
        "880132",
        null
    ),
    /**
     * Внутренний статус ССР. Сбор подписей.
     */
    COLLECT_SIGNATURES(
        999001,
        null,
        null,
        null,
        "Сбор подписей",
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
    private final String notificationCode;

    public String getId() {
        return Stream.of(code, subCode)
            .map(ContractAppointmentFlowStatus::ofNullableInteger)
            .filter(Objects::nonNull)
            .collect(Collectors.joining("."));
    }

    public String getNotificationCode() {
        return ofNullable(notificationCode)
            .orElse(eventCode);
    }

    private static String ofNullableInteger(final Integer integer) {
        return ofNullable(integer)
            .map(String::valueOf)
            .orElse(null);
    }

    public static ContractAppointmentFlowStatus of(final Integer code, final Integer subCode) {
        return Arrays.stream(ContractAppointmentFlowStatus.values())
            .filter(status -> Objects.equals(status.code, code))
            .filter(status -> Objects.equals(status.subCode, subCode))
            .findFirst()
            .orElse(null);
    }

    public static ContractAppointmentFlowStatus of(final String id) {
        return Arrays.stream(ContractAppointmentFlowStatus.values())
            .filter(status -> Objects.equals(status.getId(), id))
            .findFirst()
            .orElse(null);
    }

    public static ContractAppointmentFlowStatus ofEventCode(final String eventCode) {
        return Arrays.stream(ContractAppointmentFlowStatus.values())
            .filter(status -> Objects.equals(status.getEventCode(), eventCode))
            .findFirst()
            .orElse(null);
    }

    public static ContractAppointmentFlowStatus ofName(final String name) {
        return Arrays.stream(ContractAppointmentFlowStatus.values())
            .filter(status -> Objects.equals(status.name(), name))
            .findFirst()
            .orElse(null);
    }

}
