package ru.croc.ugd.ssr.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import ru.croc.ugd.ssr.scheduler.EmailNotificationScheduler;
import ru.croc.ugd.ssr.task.DashboardProcessTask;

/**
 * Конфигурация шедулеров.
 */
@Configuration
@EnableScheduling
public class SchedulerConfig {

    @Bean
    public ThreadPoolTaskScheduler configureTasks() {
        final ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setThreadNamePrefix("ssr-scheduler-");
        taskScheduler.setPoolSize(10);
        taskScheduler.initialize();

        return taskScheduler;
    }

    /**
     * Создание задачи на создание еженедельного дашборда.
     *
     * @return задача.
     */
    @Bean
    public DashboardProcessTask dashboardProcessTask() {
        return new DashboardProcessTask();
    }

    /**
     * Создание задачи на рассылку email-уведомлений.
     *
     * @return задача.
     */
    @Bean
    public EmailNotificationScheduler emailNotificationScheduler() {
        return new EmailNotificationScheduler();
    }

}
