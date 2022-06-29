package ru.croc.ugd.ssr.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * Настройки отчетов по уведомлениям ssr.
 */
@ConfigurationProperties(prefix = SentMessagesStatisticsReportProperties.PREFIX)
@Getter
@Setter
public class SentMessagesStatisticsReportProperties {
    /**
     * Префикс конфигурации.
     */
    static final String PREFIX = "ugd.ssr.sent-messages-statistics-report";

    private boolean enabled;

    private String schedule;

    private List<String> emails;
}
