package ru.croc.ugd.ssr.controller;

import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.service.FirstFlowErrorAnalyticsService;
import ru.croc.ugd.ssr.service.bpm.BpmHandlerService;

/**
 * Контроллер для обработки скриптовых тасков БП.
 * WARNING - спамим АС УР - просто так не использовать
 */
@RestController
@AllArgsConstructor
@RequestMapping("/bpmHandler")
public class BpmHandlerController {

    private final BpmHandlerService bpmHandlerService;
    private final FirstFlowErrorAnalyticsService firstFlowErrorAnalyticsService;

    /**
     * Обогащает дома из SoonResettlementRequest данными.
     *
     * @param id    идентификатор SoonResettlementRequest
     * @param login логин пользователя, от имени которого вносятся изменения
     */
    @GetMapping("/updateRealEstateFromEzd/{id}/{login}")
    public void soonResettle(
        @ApiParam(value = "soonResettlementId", required = true) @PathVariable String id,
        @ApiParam(value = "login", required = true) @PathVariable String login
    ) {
        bpmHandlerService.soonResettle(id, login);
    }

    /**
     * Переселяет дома/квартиры из ResettlementRequest.
     *
     * @param id идентификатор ResettlementRequest
     */
    @GetMapping("/updateRealEstateFromEtp/{id}")
    public void resettle(
        @ApiParam(value = "resettlementId", required = true) @PathVariable String id
    ) {
        bpmHandlerService.resettle(id);
    }

    /**
     * Сохраняет информацию из запроса ДГИ в сам дашборд.
     *
     * @param id    идентификатор DashboardRequestDocument
     * @param login логин пользователя, от имени которого вносятся изменения
     */
    @GetMapping("/saveDashboardDgi/{id}/{login}")
    public void saveDashboardDgi(
        @ApiParam(value = "dashboardRequestId", required = true) @PathVariable String id,
        @ApiParam(value = "login", required = true) @PathVariable String login
    ) {
        bpmHandlerService.saveDashboardDgi(id, login);
    }

    /**
     * Сохраняет информацию из запроса ДГП в сам дашборд.
     *
     * @param id    идентификатор DashboardRequestDocument
     * @param login логин пользователя, от имени которого вносятся изменения
     */
    @GetMapping("/saveDashboardDgp/{id}/{login}")
    public void saveDashboardDgp(
        @ApiParam(value = "dashboardRequestId", required = true) @PathVariable String id,
        @ApiParam(value = "login", required = true) @PathVariable String login
    ) {
        bpmHandlerService.saveDashboardDgp(id, login);
    }

    /**
     * Сохраняет информацию из запроса Фонда в сам дашборд.
     *
     * @param id    идентификатор DashboardRequestDocument
     * @param login логин пользователя, от имени которого вносятся изменения
     */
    @GetMapping("/saveDashboardFond/{id}/{login}")
    public void saveDashboardFond(
        @ApiParam(value = "dashboardRequestId", required = true) @PathVariable String id,
        @ApiParam(value = "login", required = true) @PathVariable String login
    ) {
        bpmHandlerService.saveDashboardFond(id, login);
    }

    /**
     * Сохраняет информацию из запроса ДС в сам дашборд.
     *
     * @param id    идентификатор DashboardRequestDocument
     * @param login логин пользователя, от имени которого вносятся изменения
     */
    @GetMapping("/saveDashboardDs/{id}/{login}")
    public void saveDashboardDs(
        @ApiParam(value = "dashboardRequestId", required = true) @PathVariable String id,
        @ApiParam(value = "login", required = true) @PathVariable String login
    ) {
        bpmHandlerService.saveDashboardDs(id, login);
    }

    /**
     * Публикует запись дашборда.
     *
     * @param id идентификатор DashboardRequestDocument
     */
    @GetMapping("/publishDashboard/{id}")
    public void publishDashboard(
        @ApiParam(value = "dashboardRequestId", required = true) @PathVariable String id
    ) {
        bpmHandlerService.publishDashboard(id);
    }

    /**
     * Сохраняет в истории выдачу письма с предложением.
     *
     * @param id    идентификатор ArmIssueOfferLetterRequest
     * @param login логин пользователя, завершившего задачу
     */
    @GetMapping("/saveArmIssueOfferRequest/{id}/{login}")
    public void saveArmIssueOfferRequest(
        @ApiParam(value = "issueOfferRequestId", required = true) @PathVariable String id,
        @ApiParam(value = "login", required = true) @PathVariable String login
    ) {
        bpmHandlerService.saveArmIssueOfferRequest(id, login);
    }

    /**
     * Сохраняет в истории показ квартиры.
     *
     * @param id    идентификатор armShowApartRequest
     * @param login логин пользователя, завершившего задачу
     */
    @GetMapping("/saveArmShowApartRequest/{id}/{login}")
    public void saveArmShowApartRequest(
        @ApiParam(value = "showApartRequestId", required = true) @PathVariable String id,
        @ApiParam(value = "login", required = true) @PathVariable String login
    ) {
        bpmHandlerService.saveArmShowApartRequest(id, login);
    }

    /**
     * Сохраняет в истории принятие/отказ.
     *
     * @param id    идентификатор armApplyRequest
     * @param login логин пользователя, завершившего задачу
     */
    @GetMapping("/saveArmApplyRequest/{id}/{login}")
    public void saveArmApplyRequest(
        @ApiParam(value = "applyRequestId", required = true) @PathVariable String id,
        @ApiParam(value = "login", required = true) @PathVariable String login
    ) {
        bpmHandlerService.saveArmApplyRequest(id, login);
    }

    /**
     * Обрабатывает разбор задач с ошибками по первому потоку.
     *
     * @param id идентификатор firstFlowErrorAnalytics
     */
    @GetMapping("/handleFirstFlowErrorAnalytics/{id}")
    public void handleFirstFlowErrorAnalytics(
        @ApiParam(value = "firstFlowErrorAnalyticsId", required = true) @PathVariable String id
    ) {
        firstFlowErrorAnalyticsService.handleDocument(id);
    }

    /**
     * Отправляет уведомления жителям после инициации процесса переселения.
     *
     * @param id идентификатор objectResettlementRequest
     */
    @GetMapping("/sendNotificationsAfterResettlement/{id}")
    public void sendNotificationsAfterResettlement(
        @ApiParam(value = "objectResettlementRequestId", required = true) @PathVariable String id
    ) {
        bpmHandlerService.sendNotificationsAfterResettlement(id);
    }
}
