package ru.croc.ugd.ssr.integration.service.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PersonalDocumentRequestNotificationsDescriptor implements NotificationDescriptor {

    REQUEST_CREATED(
        "Уведомление о дополнительной загрузке документов по запросу от ОИВ",
        "templates/personaldocument/emailPersonalDocumentRequestCreated.html",
        "templates/personaldocument/elkPersonalDocumentRequestCreated.html",
        null,
        null,
        null,
        "880139"
    );

    private final String notificationTitle;
    private final String emailNotificationTemplatePath;
    private final String elkNotificationTemplatePath;
    private final String pushNotificationBody;
    private final String pushNotificationHeader;
    private final String smsNotification;
    private final String eventCode;
}
