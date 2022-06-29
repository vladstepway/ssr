package ru.croc.ugd.ssr.integration.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Конфиг по интеграции.
 */
@Configuration
@EnableConfigurationProperties(IntegrationProperties.class)
public class IntegrationConfig {
}
