package ru.croc.ugd.ssr.integration.service.notification;

public interface NotificationDescriptor {
    String getNotificationTitle();

    String getEmailNotificationTemplatePath();

    String getElkNotificationTemplatePath();

    String getPushNotificationBody();

    String getPushNotificationHeader();

    String getSmsNotification();

    String getEventCode();

    default String getNotificationCode() {
        return getEventCode();
    }
}
