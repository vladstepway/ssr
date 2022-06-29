package ru.croc.ugd.ssr.dto.notary;

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
public enum NotaryApplicationFlowStatus implements NotificationDescriptor {

    /**
     * Заявление доставлено нотариусу.
     */
    ACCEPTED(
        1040,
        null,
        "Заявление принято",
        "Заявление доставлено нотариусу",
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
     * Отказ в регистрации заявления.
     */
    DECLINED(
        1035,
        null,
        "Отказ в регистрации заявления",
        "Отказ в регистрации заявления",
        "Вам отказано в регистрации заявления в связи с %s.",
        null,
        null,
        null,
        null,
        null,
        null,
        null
    ),
    /**
     * Заявка зарегистрирована.
     */
    REGISTERED(
        1050,
        null,
        "На исполнении. Заявление зарегистрировано",
        "Заявление зарегистрировано",
        "Заявление зарегистрировано и принято к исполнению нотариусом %s. \n %s",
        "Уведомление о подаче заявления на посещение нотариуса",
        "templates/notary/emailNotaryRegistered.html",
        "templates/notary/elkNotaryRegistered.html",
        "Вы подали заявление на посещение нотариуса по программе реновации",
        "Вы подали заявление на посещение нотариуса по программе реновации",
        "Вы подали заявление на посещение нотариуса по программе реновации."
            + " Подробную информацию узнайте в личном кабинете на mos.ru: https://my.mos.ru/my/#/settings/info ",
        "880108"
    ),
    /**
     * Услуга приостановлена. Ожидание доукомплектации документов.
     */
    WAITING_DOCUMENTS(
        1060,
        null,
        "Ожидание документов",
        "Услуга приостановлена. Ожидание доукомплектации документов",
        "Представлен неполный комплект документов, необходимых для предоставления услуги. "
            + "Необходимо отправить требуемые документы в течение 14 дней. \n %s",
        null,
        null,
        null,
        null,
        null,
        null,
        null
    ),
    /**
     * Услуга возобновлена. Документы получены и переданы на рассмотрение.
     */
    DOCUMENTS_RECEIVED(
        1160,
        null,
        "Подтверждение документов",
        "Услуга возобновлена. Документы получены и переданы на рассмотрение",
        "Документы получены и переданы на рассмотрение нотариусу %s. "
            + "Ожидайте подтверждения готовности проекта договора",
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
    REFUSE_TO_PROVIDE_SERVICE(
        1080,
        1,
        "Отказ в предоставлении услуги",
        "Отказ в предоставлении услуги",
        "Вам отказано в предоставлении услуги в связи с %s",
        "Уведомление об отказе в предоставлении услуги",
        "templates/notary/emailNotaryRefuseToProvideService.html",
        "templates/notary/elkNotaryRefuseToProvideService.html",
        "Вам отказано в предоставление услуги нотариуса",
        "Вам отказано в предоставление услуги нотариуса",
        "Вам отказано в предоставление услуги нотариуса."
            + " Подробную информацию узнайте в личном кабинете на mos.ru:"
            + " https://my.mos.ru/my/#/settings/info ",
        "880115"
    ),
    /**
     * Отказ в предоставлении услуги.
     */
    REFUSE_NO_BOOKING(
        1080,
        2,
        "Отказ в предоставлении услуги",
        "Отказ в предоставлении услуги",
        "Отказ в предоставлении услуги. По заявлению не осуществлена запись на посещение нотариуса %s "
            + "в установленный 30-дневный срок.",
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
    REFUSE_NO_DOCUMENTS(
        1080,
        3,
        "Отказ в предоставлении услуги",
        "Отказ в предоставлении услуги",
        "Отказ в предоставлении услуги. По заявлению не осуществлена отправка требуемых документов "
            + "в установленный 14-дневный срок.",
        null,
        null,
        null,
        null,
        null,
        null,
        null
    ),
    /**
     * Проект договора готов.
     */
    DRAFT_CONTRACT_READY(
        1051,
        1,
        "Проект договора готов",
        "Проект договора подготовлен",
        "Проект договора подготовлен нотариусом %s. "
            + "Открыта запись на посещение нотариуса. Выберите удобное время приема. "
            + "В случае отсутствия возможности записи в ближайшие 7 дней, свяжитесь с нотариусом %s. \n %s",
        null,
        null,
        null,
        null,
        null,
        null,
        null
    ),
    /**
     * Документы от заявителя переданы в ведомство.
     */
    DOCUMENTS_TRANSFERRED(
        8011,
        1,
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
     * Заявка на бронирование времени отправлена в ведомство.
     */
    BOOKING_SENT(
        8011,
        2,
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
     * Запись на приём закрыта.
     */
    BOOKING_CLOSED(
        8031,
        1,
        null,
        "Запись на приём закрыта",
        "Возможность записи на посещение нотариуса %s закрыта в связи с истечением 7 доступных дней."
            + " Чтобы осуществить запись на посещение нотариуса, свяжитесь с нотариусом %s по телефону %s.",
        null,
        null,
        null,
        null,
        null,
        null,
        null
    ),
    /**
     * Заявка на повторное открытие записи принята.
     */
    RECOLLECT_DOCUMENTS(
        1051,
        2,
        "На исполнении. Подготовка справок",
        "Заявка на повторное открытие записи принята",
        "Заявка на повторное открытие записи принята нотариусом %s.",
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
    BOOKED_PORTAL(
        null,
        null,
        null,
        null,
        null,
        "Уведомление правообладателей о дате и времени приёма у нотариуса",
        "templates/notary/portal/emailNotaryInvite.html",
        "templates/notary/portal/elkNotaryInvite.html",
        null,
        null,
        null,
        "880104"
    ),
    BOOKED(
        8021,
        1,
        "Запись произведена",
        "Запись произведена",
        "Посещение нотариуса %s запланировано на %s "
            + "в %s по адресу %s. Если передумаете, отмените запись до "
            + "%s %s. Просим вас прибыть на прием к нотариусу в выбранное вами время. "
            + "В случае опоздания более чем на 15 минут, вам может быть отказано в приеме. <br>"
            + "<a href=\"%s\">Добавить в календарь</a>",
        "Уведомление о назначенной записи на прием к нотариусу",
        "templates/notary/emailNotaryInvite.html",
        "templates/notary/elkNotaryInvite.html",
        "Вы записаны на посещение нотариуса по программе реновации",
        "Вы записаны на посещение нотариуса по программе реновации",
        "Вы записаны на посещение нотариуса по программе реновации."
            + " Подробную информацию узнайте в личном кабинете на mos.ru:"
            + " https://my.mos.ru/my/#/settings/info ",
        "880109"
    ),
    BOOKED_OWNER(
        null,
        null,
        null,
        null,
        null,
        "Уведомление о назначенной записи на прием к нотариусу другим правообладателям",
        "templates/notary/owner/emailNotaryInvite.html",
        "templates/notary/owner/elkNotaryInvite.html",
        "Вы записаны на посещение нотариуса по программе реновации",
        "Вы записаны на посещение нотариуса по программе реновации",
        "Вы записаны на посещение нотариуса по программе реновации."
            + " Подробную информацию узнайте в личном кабинете на mos.ru:"
            + " https://my.mos.ru/my/#/settings/info ",
        "880116"
    ),
    /**
     * Запись на приём открыта.
     */
    APPOINTMENT_OPEN(
        1051,
        3,
        "Проект договора подготовлен",
        "Запись на приём открыта",
        "Повторно открыта запись на посещение нотариуса %s. Выберите удобное время приема. "
            + "В случае отсутствия возможности записи в ближайшие 7 дней, свяжитесь с нотариусом %s по телефону %s.",
        null,
        null,
        null,
        null,
        null,
        null,
        null
    ),
    /**
     * Запись отменена нотариусом.
     */
    CANCELED_BY_NOTARY(
        8021,
        2,
        null,
        "Запись отменена нотариусом",
        "Нотариус отменил запись на посещение по заявлению № %s. Пожалуйста, запишитесь на другое время. \n %s",
        "Уведомление об отмене записи на прием к нотариусу по инициативе нотариуса",
        "templates/notary/emailNotaryRejectedByNotary.html",
        "templates/notary/elkNotaryRejectedByNotary.html",
        "Запись на прием к нотариусу отменена",
        "Запись на прием к нотариусу отменена",
        "Запись на прием к нотариусу отменена."
            + " Подробную информацию узнайте в личном кабинете на mos.ru:"
            + " https://my.mos.ru/my/#/settings/info ",
        "880111"
    ),
    /**
     * Запись отменена нотариусом.
     */
    CANCELED_BY_NOTARY_OWNER(
        null,
        null,
        null,
        null,
        null,
        "Уведомление об отмене записи на прием к нотариусу по инициативе нотариуса другим правообладателям",
        "templates/notary/owner/emailNotaryRejectedByNotary.html",
        "templates/notary/owner/elkNotaryRejectedByNotary.html",
        "Запись на прием к нотариусу отменена",
        "Запись на прием к нотариусу отменена",
        "Запись на прием к нотариусу отменена."
            + " Подробную информацию узнайте в личном кабинете на mos.ru:"
            + " https://my.mos.ru/my/#/settings/info ",
        "880118"
    ),
    /**
     * Запись отменена нотариусом.
     */
    CANCELED_BY_NOTARY_PORTAL(
        null,
        null,
        null,
        null,
        null,
        "Уведомление правообладателей об отмене записи по инициативе нотариуса",
        "templates/notary/portal/emailNotaryRejectedByNotary.html",
        "templates/notary/portal/elkNotaryRejectedByNotary.html",
        null,
        null,
        null,
        "880106"
    ),
    /**
     * Напоминание о записи на приём.
     */
    APPOINTMENT_REMINDER(
        8021,
        3,
        null,
        "Напоминание о записи на прием к нотариусу",
        "Напоминаем, что вы записаны на посещение нотариуса %s по адресу %s на %s в %s. "
            + "Отменить запись можно не позднее %s.",
        "Напоминание о записи на прием к нотариусу",
        "templates/notary/emailNotaryReminder.html",
        "templates/notary/elkNotaryReminder.html",
        "Прием у нотариуса уже скоро",
        "Прием у нотариуса уже скоро",
        "Прием у нотариуса уже скоро."
            + " Подробную информацию узнайте в личном кабинете на mos.ru:"
            + " https://my.mos.ru/my/#/settings/info ",
        "880112"
    ),
    /**
     * Напоминание о записи на приём.
     */
    APPOINTMENT_REMINDER_OWNER(
        null,
        null,
        null,
        null,
        null,
        "Напоминание о записи на прием к нотариусу другим правообладателям",
        "templates/notary/owner/emailNotaryReminder.html",
        "templates/notary/owner/elkNotaryReminder.html",
        "Прием у нотариуса уже скоро",
        "Прием у нотариуса уже скоро",
        "Прием у нотариуса уже скоро."
            + " Подробную информацию узнайте в личном кабинете на mos.ru:"
            + " https://my.mos.ru/my/#/settings/info ",
        "880119"
    ),
    /**
     * Напоминание о записи на приём.
     */
    APPOINTMENT_REMINDER_PORTAL(
        null,
        null,
        null,
        null,
        null,
        "Уведомление с целью напомнить правообладателям"
            + " о времени записи к нотариусу",
        "templates/notary/portal/emailNotaryReminder.html",
        "templates/notary/portal/elkNotaryReminder.html",
        null,
        null,
        null,
        "880107"
    ),
    /**
     * Запись отменена заявителем.
     */
    CANCELED_BY_APPLICANT(
        1053,
        null,
        null,
        "Запись отменена по инициативе заявителя",
        "Вы отменили запись на посещение нотариуса (заявление № %s). Если это ошибка, "
            + "сообщите об этом в Информационно-справочную службу по оказанию "
            + "государственных услуг: +7 (495) 539-55-55.\n"
            + "Осуществить повторную запись возможно до %s, в ином случае свяжитесь с нотариусом %s по телефону %s",
        "Уведомление об отмене записи на прием к нотариусу по инициативе заявителя",
        "templates/notary/emailNotaryDeclined.html",
        "templates/notary/elkNotaryDeclined.html",
        "Запись на прием к нотариусу отменена",
        "Запись на прием к нотариусу отменена",
        "Запись на прием к нотариусу отменена."
            + " Подробную информацию узнайте в личном кабинете на mos.ru:"
            + " https://my.mos.ru/my/#/settings/info ",
        "880110"
    ),
    /**
     * Запись отменена заявителем.
     */
    CANCELED_BY_APPLICANT_OWNER(
        null,
        null,
        null,
        null,
        null,
        "Уведомление об отмене записи на прием к нотариусу по инициативе заявителя другим правообладателям",
        "templates/notary/owner/emailNotaryDeclined.html",
        "templates/notary/owner/elkNotaryDeclined.html",
        "Запись на прием к нотариусу отменена",
        "Запись на прием к нотариусу отменена",
        "Запись на прием к нотариусу отменена."
            + " Подробную информацию узнайте в личном кабинете на mos.ru:"
            + " https://my.mos.ru/my/#/settings/info ",
        "880117"
    ),
    /**
     * Запись отменена заявителем.
     */
    CANCELED_BY_APPLICANT_PORTAL(
        null,
        null,
        null,
        null,
        null,
        "Уведомление правообладателей об отмене записи к нотариусу по инициативе заявителя",
        "templates/notary/portal/emailNotaryDeclined.html",
        "templates/notary/portal/elkNotaryDeclined.html",
        null,
        null,
        null,
        "880105"
    ),
    /**
     * Получен запрос на отмену записи по инициативе заявителя.
     */
    CANCEL_BY_APPLICANT_REQUEST(
        1068,
        null,
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
     * Закрытие возможности отмены записи на приём.
     */
    CLOSE_APPOINTMENT_CANCELLATION(
        8031,
        2,
        null,
        "Закрытие возможности отмены записи",
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
        "Услуга оказана. Договор заключен",
        "Услуга оказана",
        "Услуга оказана. Договор заключен",
        "Уведомление о заключении договора",
        "templates/notary/emailNotaryServiceProvided.html",
        "templates/notary/elkNotaryServiceProvided.html",
        "Вы подписали договор на новую квартиру, предоставленную по программе реновации",
        "Вы подписали договор на новую квартиру, предоставленную по программе реновации",
        "Вы подписали договор на новую квартиру, предоставленную по программе реновации."
            + " Подробную информацию узнайте в личном кабинете на mos.ru: https://my.mos.ru/my/#/settings/info ",
        "880114"
    ),
    /**
     * Отмена записи невозможна.
     */
    UNABLE_TO_CANCEL(
        1168,
        null,
        null,
        "Отмена записи невозможна",
        "Отменить запись невозможно менее, чем за 24 часа до назначенного времени приема.",
        null,
        null,
        null,
        null,
        null,
        null,
        null
    ),
    /**
     * Требуется повторная запись на приём.
     */
    RETURN_VISIT_REQUIRED(
        1051,
        4,
        null,
        "Требуется повторная запись на приём",
        "Требуется повторная запись на посещение нотариуса. "
            + "Если Вы еще не записаны, свяжитесь с нотариусом %s по телефону %s",
        "Уведомление о необходимости повторной записи на прием к нотариусу",
        "templates/notary/emailNotaryReturnVisitRequired.html",
        "templates/notary/elkNotaryReturnVisitRequired.html",
        "Вам необходимо связяться с нотариусом для повторной записи на прием",
        "Вам необходимо связяться с нотариусом для повторной записи на прием",
        "Вам необходимо связяться с нотариусом для повторной записи на прием."
            + " Подробную информацию узнайте в личном кабинете на mos.ru:"
            + " https://my.mos.ru/my/#/settings/info ",
        "880113"
    ),
    /**
     * Технический сбой при регистрации заявления.
     */
    TECHNICAL_CRASH_REGISTRATION(
        103099,
        null,
        null,
        "Технический сбой",
        "Заявление не отправлено. Пожалуйста, повторите попытку. ",
        null,
        null,
        null,
        null,
        null,
        null,
        null
    ),
    /**
     * Технический сбой при получении документов.
     */
    TECHNICAL_CRASH_RECEIVED_DOCUMENTS(
        801199,
        1,
        null,
        "Технический сбой",
        "К сожалению, произошел технический сбой и сведения не могут быть доставлены в ведомство. Повторите попытку.",
        null,
        null,
        null,
        null,
        null,
        null,
        null
    ),
    /**
     * Технический сбой при бронировании слота.
     */
    TECHNICAL_CRASH_BOOKING(
        801199,
        2,
        null,
        "Технический сбой",
        "К сожалению, произошел технический сбой и сведения не могут быть доставлены в ведомство. Повторите попытку.",
        null,
        null,
        null,
        null,
        null,
        null,
        null
    ),
    /**
     * Технический сбой при отмене записи по инициативе заявителя.
     */
    TECHNICAL_CRASH_CANCEL_BY_APPLICANT(
        116899,
        null,
        null,
        "Технический сбой",
        "К сожалению, произошел технический сбой и запрос на изменение не доставлен в ведомство. Повторите попытку.",
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
            .map(NotaryApplicationFlowStatus::ofNullableInteger)
            .filter(Objects::nonNull)
            .collect(Collectors.joining("."));
    }

    private static String ofNullableInteger(final Integer integer) {
        return ofNullable(integer)
            .map(String::valueOf)
            .orElse(null);
    }

    public static NotaryApplicationFlowStatus of(final Integer code, final Integer subCode) {
        return Arrays.stream(NotaryApplicationFlowStatus.values())
            .filter(status -> Objects.equals(status.code, code))
            .filter(status -> Objects.equals(status.subCode, subCode))
            .findFirst()
            .orElse(null);
    }

    public static NotaryApplicationFlowStatus of(final String id) {
        return Arrays.stream(NotaryApplicationFlowStatus.values())
            .filter(status -> Objects.equals(status.getId(), id))
            .findFirst()
            .orElse(null);
    }

    public static Optional<NotaryApplicationFlowStatus> ofEventCode(final String eventCode) {
        return Arrays.stream(NotaryApplicationFlowStatus.values())
            .filter(notaryApplicationFlowStatus -> Objects.equals(notaryApplicationFlowStatus.eventCode, eventCode))
            .findFirst();
    }
}
