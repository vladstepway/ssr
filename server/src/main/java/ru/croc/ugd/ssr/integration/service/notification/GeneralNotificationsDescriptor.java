package ru.croc.ugd.ssr.integration.service.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GeneralNotificationsDescriptor implements NotificationDescriptor {
    CONTRACT_READY_FOR_ACQUAINTING(
        "Уведомление о готовности проекта договора для ознакомления",
        "templates/general/emailContractReadyForAcquainting.html",
        "templates/general/elkContractReadyForAcquainting.html",
        "Для Вас подготовлен проект договора для ознакомления на предложенную"
            + " квартиру по программе реновации",
        "Для Вас подготовлен проект договора для ознакомления на предложенную"
            + " квартиру по программе реновации",
        "Для Вас подготовлен проект договора для ознакомления на предложенную "
            + "квартиру по программе реновации. Подробную информацию узнайте "
            + "в личном кабинете на mos.ru: https://my.mos.ru/my/#/settings/info",
        "880101"
    ),

    OFFER_LETTER(
        "Уведомление с предложением равнозначной квартиры",
        "templates/general/emailOfferLetter.html",
        "templates/general/elkOfferLetter.html",
        "",//TODO
        "",//TODO
        "",//TODO
        "880029"
    );

    private final String notificationTitle;
    private final String emailNotificationTemplatePath;
    private final String elkNotificationTemplatePath;
    private final String pushNotificationBody;
    private final String pushNotificationHeader;
    private final String smsNotification;
    private final String eventCode;
}
