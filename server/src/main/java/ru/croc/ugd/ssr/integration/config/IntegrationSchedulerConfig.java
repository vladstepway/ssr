package ru.croc.ugd.ssr.integration.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import ru.croc.ugd.ssr.integration.scheduler.SchedulerTask;

/**
 * Конфигурация планировщика по задачам интеграции.
 */
@Configuration
@EnableScheduling
public class IntegrationSchedulerConfig {

    /**
     * Создание бина задачи шедулера.
     *
     * @return бин
     */
    @Bean
    public SchedulerTask schedulerTask() {
        return new SchedulerTask();
    }

}
