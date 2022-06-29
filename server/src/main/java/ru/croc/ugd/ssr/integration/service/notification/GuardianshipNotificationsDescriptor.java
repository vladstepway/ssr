package ru.croc.ugd.ssr.integration.service.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GuardianshipNotificationsDescriptor implements NotificationDescriptor {
    GUARDIANSHIP_ACCEPT(
        "Уведомление о готовности распоряжения о выдаче предварительного разрешения на совершение сделки",
        "templates/guardianship/emailGuardianshipConfirmed.html",
        "templates/guardianship/elkGuardianshipConfirmed.html",
        "Вы получили разрешение на совершение сделки",
        "Вы получили разрешение на совершение сделки",
        "Вы получили разрешение органов опеки на совершение сделки."
            + " Подробную информацию узнайте в личном кабинете на mos.ru:  https://my.mos.ru/my/#/settings/info",
        "880102"
    ),
    GUARDIANSHIP_DECLINE(
        "Уведомление о готовности распоряжения об отказе в выдаче предварительного разрешения на совершение сделки",
        "templates/guardianship/emailGuardianshipRejected.html",
        "templates/guardianship/elkGuardianshipRejected.html",
        "Вы получили отказ на совершение сделки",
        "Вы получили отказ на совершение сделки",
        "Вы получили отказ органов опеки на совершение сделки."
            + " Подробную информацию узнайте в личном кабинете на mos.ru:  https://my.mos.ru/my/#/settings/info",
        "880103"
    );

    private final String notificationTitle;
    private final String emailNotificationTemplatePath;
    private final String elkNotificationTemplatePath;
    private final String pushNotificationBody;
    private final String pushNotificationHeader;
    private final String smsNotification;
    private final String eventCode;
}
