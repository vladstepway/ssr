package ru.croc.ugd.ssr.integration.service.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResidencePlaceRegistrationNotificationsDescriptor implements NotificationDescriptor {

    OPTION_AVAILABLE(
        "Информирование о возможности зарегистрироваться по месту жительства",
        "templates/residenceplaceregistration/elkEmailResidencePlaceRegistrationIsAvailable.html",
        "templates/residenceplaceregistration/elkEmailResidencePlaceRegistrationIsAvailable.html",
        null,
        null,
        null,
        "880144"
    );

    private final String notificationTitle;
    private final String emailNotificationTemplatePath;
    private final String elkNotificationTemplatePath;
    private final String pushNotificationBody;
    private final String pushNotificationHeader;
    private final String smsNotification;
    private final String eventCode;
}
