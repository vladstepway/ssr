package ru.croc.ugd.ssr.task;

import static java.util.Objects.isNull;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Scheduled;
import ru.croc.ugd.ssr.model.DashboardDocument;
import ru.croc.ugd.ssr.service.dashboard.DashboardDocumentService;
import ru.croc.ugd.ssr.service.dashboard.DashboardRequestDocumentService;
import ru.reinform.cdp.security.utils.RiAuthenticationUtils;
import ru.reinform.cdp.utils.rest.utils.SendRestServiceAuthUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;

/**
 * Класс с задачей для запуска процесса создания новой записи дашборда.
 */
@Slf4j
public class DashboardProcessTask {

    @Value("${mdm.url}")
    private String mdmUrl;

    @Value("${schedulers.autoDashboard.enable:false}")
    private boolean autoDashboardEnable;

    @Value("${schedulers.dashboard.enable:false}")
    private boolean dashboardEnable;

    @Autowired
    private DashboardDocumentService dashboardDocumentService;

    @Autowired
    private DashboardRequestDocumentService dashboardRequestDocumentService;

    @Autowired
    private RiAuthenticationUtils riAuthenticationUtils;

    @Autowired
    private SendRestServiceAuthUtils sendRestUtils;

    /**
     * Таск на создание новой записи дашборда.
     * Запускаем каждый час (в 20мин), на случай ошибки в предыдущий раз.
     */
    @Scheduled(cron = "${schedulers.autoDashboard.period}")
    public void startDashboardProcessTask() {
        if (dashboardEnable) {
            try {
                riAuthenticationUtils.setSecurityContextByServiceuser();

                String weekDay = getWeekDay();
                if (weekDay == null || weekDay.isEmpty()) {
                    log.warn("В справочнике Settings не найдена настройка ugd_ssr_new_dashboard_week_day");

                    return;
                }
                if (DayOfWeek.valueOf(weekDay) == LocalDate.now().getDayOfWeek()) {
                    DashboardDocument todayActiveDocument = dashboardDocumentService.getTodayActiveDocument();

                    if (isNull(todayActiveDocument)) {
                        log.info("Запуск формирования задач по внесению статистической информации.");

                        dashboardRequestDocumentService.createDocumentWithProcess();

                        log.info("Запущен БП по созданию новой записи дашборда");
                    }
                }
            } catch (RuntimeException e) {
                log.error("Ошибка формирования задач по внесению статистической информации!");
                log.error(e.toString());
            }
        }
    }

    /**
     * Таск на создание новой автоматической записи дашборда.
     * Запускаем каждый час (в 40мин), на случай ошибки в предыдущий раз.
     */
    @Scheduled(cron = "${schedulers.autoDashboard.period}")
    public void startAutoDashboardProcessTask() {
        if (autoDashboardEnable) {
            try {
                riAuthenticationUtils.setSecurityContextByServiceuser();

                DashboardDocument todayActiveAutoDocument = dashboardDocumentService.getTodayActiveAutoDocument();

                if (isNull(todayActiveAutoDocument)) {
                    log.info("Запуск задачи на формирование автоматической записи дашборда.");

                    String id = dashboardDocumentService.createAutoDashboardDocument();

                    log.info("Сформирована автоматическая запись с id {}", id);
                }
            } catch (RuntimeException e) {
                log.error("Ошибка формирования автоматической записи статистической информации!");
                log.error(e.toString());
            }
        }
    }

    /**
     * Возвращает значение настройки ugd_ssr_new_dashboard_week_day (из справочника Settings).
     *
     * @return день недели из справочника
     */
    private String getWeekDay() {
        String json = "{\n"
            + "  \"nickAttr\": \"code\",\n"
            + "  \"values\": [\n"
            + "    \"ugd_ssr_new_dashboard_week_day\"\n"
            + "  ]\n"
            + "}";
        String result = sendRestUtils.sendJsonRequest(
            mdmUrl + "/api/v2/dictionary/dto/Settings", HttpMethod.POST, json, String.class
        );
        JSONArray jsonArray = new JSONArray(result);
        if (jsonArray.length() > 0) {
            return jsonArray.getJSONObject(0).getString("value");
        }
        return null;
    }

}
