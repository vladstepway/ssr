package ru.croc.ugd.ssr.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Настройки уведомлений об расмотрении заявления в ДТСЗН.
 */
@ConfigurationProperties(prefix = GuardianshipProperties.PREFIX)
@Getter
@Setter
public class GuardianshipProperties {
    /**
     * Префикс конфигурации.
     */
    static final String PREFIX = "ugd.ssr.guardianship";

    private String portalUrl;

}
