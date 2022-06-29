package ru.croc.ugd.ssr.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Системные свойства.
 */
@ConfigurationProperties(SystemProperties.PREFIX)
@Getter
@Setter
public class SystemProperties {

    /**
     * Префикс конфигурации.
     */
    static final String PREFIX = "app";

    private String system;
    private String domain;
}
