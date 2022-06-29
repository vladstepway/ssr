package ru.croc.ugd.ssr.dto.shipping;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.croc.ugd.ssr.integration.service.notification.NotificationDescriptor;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * ЕДП статусы для отправки в очередь.
 */
@Getter
@AllArgsConstructor
public enum ShippingFlowStatus implements NotificationDescriptor {

    /**
     * Ваш переезд в новую квартиру запланирован на <дата записи в формате дд.мм.гггг> в <время
     * записи в формате чч:мм>. Если передумаете, отмените запись до  <время записи в формате чч:мм>
     * <дата записи в формате дд.мм.гггг>.
     */
    RECORD_ADDED(
        1050,
        null,
        "Заявление зарегистрировано",
        "Ваш переезд из старой квартиры по адресу %s в новую квартиру по адресу %s запланирован на %s в %s."
            + " Если передумаете, отмените запись до %s %s.",
        "Вы записаны на переезд",
        "templates/shipping/emailShippingScheduledTemplate.html",
        "templates/shipping/elkShippingScheduledTemplate.html",
        "Вы произвели запись на услугу по организации переезда",
        "Вы произвели запись на услугу по организации переезда",
        "Вы произвели запись на услугу по организации переезда." +
            " Подробную информацию узнайте в личном кабинете на mos.ru:" +
            "  https://my.mos.ru/my/#/settings/info\n",
        "880069"
    ),

    RECALL_AVAILABLE(
        10090,
        null,
        "Отзыв заявления возможен",
        "Доступен отзыв заявления.",
        null,
        null,
        null,
        null,
        null,
        null,
        null
    ),

    /**
     * Префектура АО перенесла вашу запись на помощь в переезде на <новая дата записи в формате
     * дд.мм.гггг> в <новое время записи в формате чч:мм>. Отменить запись можно не позднее <дата
     * записи – 1 день в формате дд.мм.гггг>. Если с Вами не согласовали перенос по заявлению №
     * <Номер заявления>, обратитесь в Центр информирования по переселению вашего дома.
     */
    RECORD_RESCHEDULED(
        8021,
        "1",
        "Запись перенесена по инициативе префектуры АО",
        "Префектура АО перенесла вашу запись на помощь в переезде на %s в %s.\n"
            + "Отменить запись можно не позднее %s.\n"
            + "Если с Вами не согласовали перенос по заявлению № %s,"
            + " обратитесь в Центр информирования по переселению вашего дома.\n",
        "Ваша запись на переезд перенесена",
        "templates/shipping/emailShippingMovedTemplate.html",
        "templates/shipping/elkShippingMovedTemplate.html",
        "Ваша запись на переезд перенесена",
        "Ваша запись на переезд перенесена",
        "Ваша запись на услугу по организации переезда была перенесена." +
            " Подробную информацию узнайте в личном кабинете на mos.ru:" +
            " https://my.mos.ru/my/#/settings/info",
        "880070"
    ),

    /**
     * Напоминаем, что вы заказали машину для перевозки вещей на <дата записи в формате дд.мм.гггг>
     * в <время записи в формате чч:мм>. Отменить запись можно не позднее <дата записи – 1д ень в
     * формате дд.мм.гггг>.
     */
    SHIPPING_REMINDING(
        8021,
        "2",
        "Напоминание о переезде",
        "Напоминаем, что вы заказали машину для перевозки вещей на %s в %s."
            + " Отменить запись можно не позднее %s.",
        "Ваш переезд в новую квартиру уже скоро",
        "templates/shipping/emailShippingReminderTemplate.html",
        "templates/shipping/elkShippingReminderTemplate.html",
        "Ваш переезд в новую квартиру уже скоро",
        "Ваш переезд в новую квартиру уже скоро",
        "Скоро Вы переезжаете по программе реновации!" +
            " Подробную информацию узнайте в личном кабинете на mos.ru:" +
            "  https://my.mos.ru/my/#/settings/info",
        "880072"
    ),

    /**
     * Отзыв заявления невозможен.
     */
    RECALL_NOT_POSSIBLE(
        10190,
        null,
        "Отзыв заявления невозможен",
        "Отзыв заявления недоступен.",
        null,
        null,
        null,
        null,
        null,
        null,
        null
    ),

    /**
     * Услуга оказана.
     */
    SHIPPING_COMPLETE(
        1075,
        null,
        "Услуга оказана",
        "Услуга оказана. Ваши вещи – в новом доме.",
        null,
        null,
        null,
        null,
        null,
        null,
        null
    ),

    /**
     * Префектура АО отменила запись на перевозку вещей по заявлению
     * № <Номер заявления> по технической причине. Пожалуйста, запишитесь на другое время.
     */
    SHIPPING_DECLINED_BY_PREFECTURE(
        1080,
        "1",
        "Заявление отозвано по инициативе префектуры АО",
        "Префектура АО отменила запись на перевозку вещей по заявлению № %s по технической причине."
            + " Пожалуйста, запишитесь на другое время.",
        "Ваша запись на переезд отменена",
        "templates/shipping/emailShippingCancelledTemplate.html",
        "templates/shipping/elkShippingCancelledTemplate.html",
        "Ваша запись на переезд отменена",
        "Ваша запись на переезд отменена",
        "Ваша запись на услугу по организации переезда была отменена." +
            " Подробную информацию узнайте в личном кабинете на mos.ru:" +
            "  https://my.mos.ru/my/#/settings/info",
        "880071"
    ),

    /**
     * Отказ от предоставления услуги.
     */
    SHIPPING_REJECTED(
        1080,
        "2",
        "Отказ от предоставления услуги",
        "Услуга не оказана. Вы отказались от услуги помощи в переезде.",
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
    TECHNICAL_CRASH_SEND(
        103099,
        null,
        "Технический сбой",
        "Заявление не отправлено. Пожалуйста, повторите попытку.",
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
    TECHNICAL_CRASH_RECALL(
        106999,
        null,
        "Технический сбой",
        "Заявление не отозвано. Пожалуйста, повторите попытку.",
        null,
        null,
        null,
        null,
        null,
        null,
        null
    ),

    /**
     * Получен запрос на отзыв заявления заявителем.
     */
    DECLINE_SHIPPING_REQUEST(
        1069,
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
     * Отмена предоставления услуги.
     */
    SHIPPING_DECLINED(
        1090,
        null,
        "Заявление отозвано по инициативе заявителя.",
        "Вы отменили запись на перевозку вещей (заявление № %s). Если это ошибка,"
            + " сообщите об этом в Информационно-справочную службу по оказанию государственных услуг:"
            + " +7 (495) 539-55-55.",
        null,
        null,
        null,
        null,
        null,
        null,
        null
    );

    private final Integer code;
    private final String subCode;
    private final String description;
    private final String statusText;
    private final String notificationTitle;
    private final String emailNotificationTemplatePath;
    private final String elkNotificationTemplatePath;
    private final String pushNotificationBody;
    private final String pushNotificationHeader;
    private final String smsNotification;
    private final String eventCode;

    public static Optional<ShippingFlowStatus> ofEventCode(final String eventCode) {
        return Arrays.stream(ShippingFlowStatus.values())
            .filter(shippingFlowStatus -> Objects.equals(shippingFlowStatus.eventCode, eventCode))
            .findFirst();
    }
}
