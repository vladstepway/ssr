package ru.croc.ugd.ssr.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import ru.croc.ugd.ssr.service.bpm.BpmService;
import ru.croc.ugd.ssr.task.EmailNotificationTask;

/**
 * Класс с задачами для планировщика email уведомлений.
 */
public class EmailNotificationScheduler {

    private static final Logger LOG = LoggerFactory.getLogger(EmailNotificationScheduler.class);

    @Autowired
    private BpmService bpmService;

    @Autowired
    private EmailNotificationTask task;

    @Value("${schedulers.email-notification-dashboard.enable}")
    private Boolean enableScheduler;

    /**
     * Таск на уведомление пользователей о невыполненных задачах по дашборду (утро).
     */
    @Scheduled(cron = "${schedulers.email-notification-dashboard.morning}")
    public void morningNotificationDashboardTaskRequests() {
        if (enableScheduler) {
            LOG.info(
                "Началась запланированная задача на уведомление пользователей"
                    + " о невыполненных задачах по дашборду (утро)"
            );
            task.reportNewDashboardItem(bpmService.getGroupsByProcessDefinitionKey("ugdssr_dashboardRequest"));
            LOG.info(
                "Завершилась запланированная задача на уведомление пользователей"
                    + " о невыполненных задачах по дашборду (утро)"
            );
        } else {
            LOG.info("Задача на уведомление пользователей о невыполненных задачах по дашборду (утро) отключена");
        }
    }

    /**
     * Таск на уведомление пользователей о невыполненных задачах по дашборду (вечер).
     */
    @Scheduled(cron = "${schedulers.email-notification-dashboard.evening}")
    public void eveningNotificationDashboardTaskRequests() {
        if (enableScheduler) {
            LOG.info(
                "Началась запланированная задача на уведомление пользователей "
                    + "о невыполненных задачах по дашборду (вечер)"
            );
            task.reportNewDashboardItem(bpmService.getGroupsByProcessDefinitionKey("ugdssr_dashboardRequest"));
            LOG.info(
                "Завершилась запланированная задача на уведомление пользователей "
                    + "о невыполненных задачах по дашборду (вечер)"
            );
        } else {
            LOG.info("Задача на уведомление пользователей о невыполненных задачах по дашборду (вечер) отключена");
        }
    }

}
