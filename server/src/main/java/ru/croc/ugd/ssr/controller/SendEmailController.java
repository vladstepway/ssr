package ru.croc.ugd.ssr.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.service.SendEmailService;
import ru.croc.ugd.ssr.service.bpm.BpmService;
import ru.croc.ugd.ssr.service.flowerrorreport.FlowErrorReportScheduler;
import ru.croc.ugd.ssr.task.EmailNotificationTask;

import java.util.Arrays;

/**
 * Контроллер по отправке email сообщений.
 */
@RestController
@AllArgsConstructor
public class SendEmailController {

    private static final Logger LOG = LoggerFactory.getLogger(SendEmailController.class);

    private final BpmService bpmService;
    private final EmailNotificationTask task;
    private final SendEmailService service;

    /**
     * Отправить email от платформы пользователям по группам о новой записи дашборда.
     *
     * @param groups Список групп через запятую
     */
    @ApiOperation(value = "Отправить email от платформы пользователям по группам о новой записи дашборда")
    @GetMapping("/sendEmailFromSmart")
    public void sendEmailFromSmart(
            @ApiParam(value = "Список групп через запятую")
            @RequestParam(value = "groups") String groups
    ) {
        task.reportNewDashboardItem(Arrays.asList(groups.split(",")));
    }

    /**
     * Отправить email с сообщением по скорому переезду.
     *
     * @param emailTo Адресат
     */
    @ApiOperation(value = "Отправить email с сообщением по скорому переезду")
    @GetMapping("/sendEmailStartRenovation")
    public void sendEmailStartRenovation(
            @ApiParam(value = "Адресат")
            @RequestParam(value = "emailTo") String emailTo
    ) {
        service.sendEmailStartRenovation(emailTo);
    }

    /**
     * Отправить email с сообщением о получении письма с предложением.
     *
     * @param emailTo Адресат
     */
    @ApiOperation(value = "Отправить email с сообщением о получении письма с предложением")
    @GetMapping("/sendEmailOfferLetter")
    public void sendEmailOfferLetter(
            @ApiParam(value = "Адресат")
            @RequestParam(value = "emailTo") String emailTo
    ) {
        service.sendEmailOfferLetter(emailTo);
    }

    /**
     * Отправить email с сообщением о согласии на квартиру.
     *
     * @param emailTo Адресат
     */
    @ApiOperation(value = "Отправить email с сообщением о согласии на квартиру")
    @GetMapping("/sendEmailFlatAgreement")
    public void sendEmailFlatAgreement(
            @ApiParam(value = "Адресат")
            @RequestParam(value = "emailTo") String emailTo
    ) {
        service.sendEmailFlatAgreement(emailTo);
    }

    /**
     * Отправить email с сообщением об отказе на квартиру.
     *
     * @param emailTo Адресат
     */
    @ApiOperation(value = "Отправить email с сообщением об отказе на квартиру")
    @GetMapping("/sendEmailFlatRefusal")
    public void sendEmailFlatRefusal(
            @ApiParam(value = "Адресат")
            @RequestParam(value = "emailTo") String emailTo
    ) {
        service.sendEmailFlatRefusal(emailTo);
    }

    /**
     * Отправить email с сообщением после осмотра квартиры.
     *
     * @param emailTo Адресат
     * @param fileUrl Ссылка на файл в ЦХЭД
     */
    @ApiOperation(value = "Отправить email с сообщением после осмотра квартиры")
    @GetMapping("/sendEmailFlatInspection")
    public void sendEmailFlatInspection(
            @ApiParam(value = "Адресат")
            @RequestParam(value = "emailTo") String emailTo,
            @ApiParam(
                    value = "Ссылка на файл в ЦХЭД",
                    defaultValue = "https://doc-upload.mos.ru/uform3.0/service/getcontent?os=GU_DOCS&id=8fbc8b87-1c8a-4a8c-b7aa-13681f08db4c"
            )
            @RequestParam(value = "fileUrl", required = false) String fileUrl
    ) {
        service.sendEmailFlatInspection(emailTo, fileUrl);
    }

    /**
     * Отправить email с сообщением о подписанном договоре.
     *
     * @param emailTo Адресат
     */
    @ApiOperation(value = "Отправить email с сообщением о подписанном договоре")
    @GetMapping("/sendEmailSignedContract")
    public void sendEmailSignedContract(
        @ApiParam(value = "Адресат")
        @RequestParam(value = "emailTo") String emailTo
    ) {
        service.sendEmailSignedContract(emailTo);
    }

    /**
     * Отправить email с сообщением о готовности договора.
     *
     * @param emailTo Адресат
     */
    @ApiOperation(value = "Отправить email с сообщением о готовности договора")
    @GetMapping("/sendEmailContractReady")
    public void sendEmailContractReady(
        @ApiParam(value = "Адресат")
        @RequestParam(value = "emailTo") String emailTo
    ) {
        service.sendEmailContractReady(emailTo);
    }

    /**
     * Отправить уведомления пользователям о невыполненных задачах по дашборду.
     */
    @ApiOperation(value = "Отправить уведомления пользователям о невыполненных задачах по дашборду")
    @GetMapping("/sendNotificationByDashboardTasks")
    public void sendNotificationByDashboardTasks() {
        LOG.info("Началась запланированная задача на уведомление пользователей о невыполненных задачах по дашборду");
        task.reportNewDashboardItem(bpmService.getGroupsByProcessDefinitionKey("ugdssr_dashboardRequest"));
        LOG.info("Завершилась запланированная задача на уведомление пользователей о невыполненных задачах по дашборду");
    }

    /**
     * Отправить email с отчетом обработки ошибок загрузки данных из ДГИ в ЕВД.
     *
     * @param id      ID документа-задачи на разбор ошибок
     * @param fileId  ID отчета в альфреске (если он уже сформирован)
     */
    @ApiOperation(value = "Отправить email с отчетом обработки ошибок загрузки данных из ДГИ в ЕВД")
    @GetMapping("/sendEmailFirstFlowErrorAnalyticsReport/{id}")
    public void sendEmailFirstFlowErrorAnalyticsReport(
            @ApiParam(value = "ID документа-задачи на разбор ошибок")
            @PathVariable(value = "id") String id,
            @ApiParam(value = "fileId")
            @RequestParam(value = "fileId", required = false) String fileId
    ) {
        LOG.info("Отправка email с отчетом по обработке ошибок загрузки данных из ДГИ в ЕВД");
        service.sendEmailFirstFlowErrorAnalyticsReport(id, fileId);
    }

}
