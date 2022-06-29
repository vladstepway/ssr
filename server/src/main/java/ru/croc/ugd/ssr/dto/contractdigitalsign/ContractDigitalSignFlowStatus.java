package ru.croc.ugd.ssr.dto.contractdigitalsign;

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
public enum ContractDigitalSignFlowStatus implements NotificationDescriptor {

    /**
     * Запись произведена.
     */
    REGISTERED(
        1050,
        null,
        "Запись произведена",
        "Заключение договора в электронной форме запланировано по квартире, находящейся по адресу %s"
            + " на %s. В день заключения договора у всех правообладателей должны быть действующие"
            + " сертификаты квалифицированной электронной подписи.<br>"
            + "<a href=\"%s\">Добавить в календарь</a>",
        "Запись произведена",
        "Уведомление правообладателей о дате заключения договора",
        "templates/contractappointment/contractDigitalSign/emailContractDigitalSignRegistered.html",
        "templates/contractappointment/contractDigitalSign/elkContractDigitalSignRegistered.html",
        null,
        null,
        null,
        "880131",
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
        "templates/contractappointment/contractDigitalSign/emailContractDigitalSignContractSigned.html",
        "templates/contractappointment/contractDigitalSign/elkContractDigitalSignContractSigned.html",
        null,
        null,
        null,
        "880114",
        "880114-1"
    ),
    /**
     * Отказ в предоставлении услуги по причине неподписи правообладателем.
     */
    REFUSE_TO_PROVIDE_SERVICE_BY_APPLICANT(
        1080,
        1,
        "Отказ от предоставления услуги",
        "Договор не заключен, так как при подписании договора вы использовали некорректную электронную подпись.",
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
     * Отказ в предоставлении услуги по причине неподписи правообладателем.
     */
    REFUSE_TO_PROVIDE_SERVICE_BY_OWNER(
        1080,
        2,
        "Отказ от предоставления услуги",
        "Договор не заключен, так как при подписании договора "
            + "правообладатель %s использовал некорректную электронную подпись.",
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
     * Отказ в предоставлении услуги по причине неподписи правообладателем.
     */
    REFUSE_TO_PROVIDE_SERVICE_DUE_TO_OWNER(
        1080,
        3,
        "Отказ от предоставления услуги",
        "Договор не заключен, так как %s договор",
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
     * Отказ в предоставлении услуги по причине неподписи сотрудником ДГИ.
     */
    REFUSE_TO_PROVIDE_SERVICE_DUE_TO_EMPLOYEE(
        1080,
        4,
        "Отказ от предоставления услуги",
        "Договор не заключен по следующей причине: истек срок подписания договора",
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
        "templates/contractappointment/contractDigitalSign/emailContractDigitalSignCanceledByApplicant.html",
        "templates/contractappointment/contractDigitalSign/elkContractDigitalSignCanceledByApplicant.html",
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
            + "(заявление № %s) на %s по причине: %s. Пожалуйста, запишитесь на другую дату.",
        "Запись отменена по инициативе ведомства",
        "Уведомление правообладателей об отмене записи по инициативе ведомства",
        "templates/contractappointment/contractDigitalSign/emailContractDigitalSignCanceledByOperator.html",
        "templates/contractappointment/contractDigitalSign/elkContractDigitalSignCanceledByOperator.html",
        null,
        null,
        null,
        "880135",
        null
    ),
    /**
     * Запись перенесена по инициативе ведомства.
     */
    MOVED_BY_OPERATOR(
        8021,
        1,
        "Запись перенесена по инициативе ведомства",
        "Департамент городского имущества города Москвы перенес Вашу запись на подписание договора "
            + "по квартире, находящейся по адресу %s, на %s. <br/>"
            + "При подписании у вас должен быть действующий сертификат "
            + "усиленной квалифицированной электронной подписи.<br/>"
            + "Вы можете отменить запись."
            + "<a href=\"%s\">Добавить в календарь</a>",
        null,
        "Уведомление правообладателей о переносе записи по инициативе ведомства",
        "templates/contractappointment/contractDigitalSign/emailContractDigitalSignMovedByOperator.html",
        "templates/contractappointment/contractDigitalSign/elkContractDigitalSignMovedByOperator.html",
        null,
        null,
        null,
        "880132",
        null
    ),
    /**
     * Требуется подписать договор для правообладателя.
     */
    COLLECT_SIGNATURES_OWNER(
        null,
        null,
        null,
        null,
        null,
        "Уведомление о необходимости подписать договор",
        "templates/contractappointment/contractDigitalSign/emailContractDigitalSignCollectSignaturesOwner.html",
        "templates/contractappointment/contractDigitalSign/elkContractDigitalSignCollectSignaturesOwner.html",
        null,
        null,
        null,
        "880141",
        null
    ),
    /**
     * Требуется подписать договор для заявителя.
     */
    COLLECT_SIGNATURES_APPLICANT(
        null,
        null,
        null,
        null,
        null,
        "Уведомление о необходимости подписать договор",
        "templates/contractappointment/contractDigitalSign/emailContractDigitalSignCollectSignaturesApplicant.html",
        "templates/contractappointment/contractDigitalSign/elkContractDigitalSignCollectSignaturesApplicant.html",
        null,
        null,
        null,
        "880141",
        null
    ),
    /**
     * Информирование о возможности получить УКЭП.
     */
    DIGITAL_SIGN_OPPORTUNITY(
        null,
        null,
        null,
        null,
        null,
        "Уведомление о возможности получения УКЭП",
        "templates/contractappointment/contractDigitalSign/emailContractDigitalSignOpportunity.html",
        "templates/contractappointment/contractDigitalSign/elkContractDigitalSignOpportunity.html",
        null,
        null,
        null,
        "880147",
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
            .map(ContractDigitalSignFlowStatus::ofNullableInteger)
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

    public static ContractDigitalSignFlowStatus of(final Integer code, final Integer subCode) {
        return Arrays.stream(ContractDigitalSignFlowStatus.values())
            .filter(status -> Objects.equals(status.code, code))
            .filter(status -> Objects.equals(status.subCode, subCode))
            .findFirst()
            .orElse(null);
    }

    public static ContractDigitalSignFlowStatus of(final String id) {
        return Arrays.stream(ContractDigitalSignFlowStatus.values())
            .filter(status -> Objects.equals(status.getId(), id))
            .findFirst()
            .orElse(null);
    }

    public static ContractDigitalSignFlowStatus ofEventCode(final String eventCode) {
        return Arrays.stream(ContractDigitalSignFlowStatus.values())
            .filter(status -> Objects.equals(status.getEventCode(), eventCode))
            .findFirst()
            .orElse(null);
    }

    public static ContractDigitalSignFlowStatus ofName(final String name) {
        return Arrays.stream(ContractDigitalSignFlowStatus.values())
            .filter(status -> Objects.equals(status.name(), name))
            .findFirst()
            .orElse(null);
    }
}
