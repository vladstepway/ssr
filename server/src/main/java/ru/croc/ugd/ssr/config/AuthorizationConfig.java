package ru.croc.ugd.ssr.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Настройки авторизации.
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = AuthorizationConfig.PREFIX)
public class AuthorizationConfig {
    /**
     * Префикс конфигурации.
     */
    static final String PREFIX = "ugd.ssr.auth";
    /**
     * Группа, дающая право доступа к функционалу, связанному с жителем.
     */
    private String personGroup;
}
