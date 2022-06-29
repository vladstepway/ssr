package ru.croc.ugd.ssr.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * Настройки отчетов ssr.
 */
@ConfigurationProperties(prefix = ReportProperties.PREFIX)
@Getter
@Setter
public class ReportProperties {
    /**
     * Префикс конфигурации.
     */
    static final String PREFIX = "ugd.ssr.report";

    private String schedule = "30 21 * * 1,2,3,4,5 ?";

    private String reportRole = "UGD_SSR_REPORT";

    private List<String> flowErrorEmails;

    private boolean enableFlowErrorSchedule;
}
