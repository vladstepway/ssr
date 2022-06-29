package ru.croc.ugd.ssr.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * Настройки логирования запросов и ответов.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = RequestResponseLoggingProperties.PREFIX)
public class RequestResponseLoggingProperties {
    /**
     * Префикс конфигурации.
     */
    static final String PREFIX = "ugd.ssr.request-response-logging";
    /**
     * Флаг включения возможности логирования.
     */
    private boolean enabled;
    /**
     * Параметры фильтрации.
     */
    private UriFilter uriFilter;
}
