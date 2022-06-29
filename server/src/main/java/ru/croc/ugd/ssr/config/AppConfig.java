package ru.croc.ugd.ssr.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import ru.croc.ugd.ssr.config.contractappointment.ContractAppointmentProperties;

/**
 * Базовая конфигурация приложения.
 */
@Configuration
@EnableConfigurationProperties({
    SystemProperties.class,
    SoapMosruProperties.class,
    ReportProperties.class,
    SentMessagesStatisticsReportProperties.class,
    ApartmentInspectionProperties.class,
    PersonNotificationProperties.class,
    GuardianshipProperties.class,
    RequestResponseLoggingProperties.class
})
public class AppConfig {

}
