package ru.croc.ugd.ssr.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Настройки уведомлений о загрузке жителей ssr.
 */
@ConfigurationProperties(prefix = PersonNotificationProperties.PREFIX)
@Getter
@Setter
public class PersonNotificationProperties {
    /**
     * Префикс конфигурации.
     */
    static final String PREFIX = "ugd.ssr.notification.person-upload";

    private String recipientRole;

}
