package ru.croc.ugd.ssr.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.dto.ProcessInfo;
import ru.croc.ugd.ssr.dto.shipping.ShippingResultDto;
import ru.croc.ugd.ssr.model.ArmApplyRequestDocument;
import ru.croc.ugd.ssr.model.ArmIssueOfferLetterRequestDocument;
import ru.croc.ugd.ssr.model.ArmShowApartRequestDocument;
import ru.croc.ugd.ssr.model.DashboardRequestDocument;
import ru.croc.ugd.ssr.model.SoonResettlementRequestDocument;
import ru.croc.ugd.ssr.service.ResettlementRequestDocumentService;
import ru.croc.ugd.ssr.service.SoonResettlementRequestDocumentService;
import ru.croc.ugd.ssr.service.arm.ArmApplyRequestDocumentService;
import ru.croc.ugd.ssr.service.arm.ArmIssueOfferLetterRequestDocumentService;
import ru.croc.ugd.ssr.service.arm.ArmShowApartRequestDocumentService;
import ru.croc.ugd.ssr.service.bpm.BpmService;
import ru.croc.ugd.ssr.service.dashboard.DashboardRequestDocumentService;
import ru.croc.ugd.ssr.service.shipping.ShippingResultService;
import ru.reinform.cdp.bpm.model.FormProperty;

import java.util.Collections;
import javax.validation.Valid;

/**
 * Контроллер для работы с БП (бизнес-процессами).
 */
@RestController
@AllArgsConstructor
public class ProcessController {

    private final SoonResettlementRequestDocumentService soonResettlementRequestDocumentService;
    private final ResettlementRequestDocumentService resettlementRequestDocumentService;
    private final DashboardRequestDocumentService dashboardRequestDocumentService;
    private final ArmIssueOfferLetterRequestDocumentService armIssueOfferLetterRequestDocumentService;
    private final ArmShowApartRequestDocumentService armShowApartRequestDocumentService;
    private final ArmApplyRequestDocumentService armApplyRequestDocumentService;
    private final ShippingResultService shippingResultService;
    private final BpmService bpmService;


    /**
     * Запуск процесса скорого переселения ОН.
     *
     * @param notes комментарии к операции
     * @return документ-заявка на скорое переселние дома
     */
    @ApiOperation(value = "Создание заявки на скорое переселение ОН")
    @PostMapping(path = "/createSoonResettlementRequestDocumentWithProcess")
    public SoonResettlementRequestDocument startSoonResettlementProcess(
        @ApiParam(value = "Комментарий к операции") @RequestParam(required = false) String notes
    ) {
        return soonResettlementRequestDocumentService.createDocumentWithProcess(notes);
    }

    /**
     * Запуск процесса переселения ОН/квартир.
     *
     * @param notes заметки
     * @return taskId пользовательской задачи запущенного БП
     */
    @ApiOperation(value = "Запуск процесса переселения ОН/квартир")
    @GetMapping(path = "/createResettlementRequestDocumentWithProcess")
    public String createResettlementRequestDocumentWithProcess(
        @ApiParam(value = "Комментарий к операции") @RequestParam(required = false) String notes
    ) {
        return resettlementRequestDocumentService.createDocumentWithProcess(notes);
    }

    /**
     * Запуск процесса создания новой записи дашборда.
     *
     * @return новая запись дашборда
     */
    @ApiOperation(value = "Запуск процесса создания новой записи дашборда")
    @PostMapping(path = "/createDashboardRequestDocumentWithProcess")
    public DashboardRequestDocument createDashboardRequestDocumentWithProcess() {
        return dashboardRequestDocumentService.createDocumentWithProcess();
    }

    /**
     * Запуск процесса создания нового запроса на выдачу письма с предложением.
     *
     * @param personId id жителя-адресата письма
     * @return новая запись выдачи письма с предложением
     */
    @ApiOperation(value = "Запуск процесса создания нового запроса на выдачу письма с предложением")
    @PostMapping(path = "/createArmIssueOfferLetterRequestDocumentWithProcess/{personId}")
    public ArmIssueOfferLetterRequestDocument createArmIssueOfferLetterRequestDocumentWithProcess(
        @PathVariable String personId
    ) {
        return armIssueOfferLetterRequestDocumentService.createDocumentWithProcess(personId);
    }

    /**
     * Запуск процесса создания нового запроса на показ квартиры.
     *
     * @param personId id жителя, для которого производится показ
     * @return новая запись показа квартиры
     */
    @ApiOperation(value = "Запуск процесса создания нового запроса на показ квартиры")
    @PostMapping(path = "/createArmShowApartRequestDocumentWithProcess/{personId}")
    public ArmShowApartRequestDocument createArmShowApartRequestDocumentWithProcess(
        @PathVariable String personId
    ) {
        return armShowApartRequestDocumentService.createDocumentWithProcess(personId);
    }

    /**
     * Запуск процесса создания нового запроса на принятие/отказ.
     *
     * @param personId id жителя
     * @return новая запись принятия/отказа
     */
    @ApiOperation(value = "Запуск процесса создания нового запроса на принятие/отказ")
    @PostMapping(path = "/createArmApplyRequestDocumentWithProcess/{personId}")
    public ArmApplyRequestDocument createArmApplyRequestDocumentWithProcess(
        @PathVariable String personId
    ) {
        return armApplyRequestDocumentService.createDocumentWithProcess(personId);
    }

    /**
     * Фиксация результатов выполнения переезда.
     *
     * @param dto - результаты.
     */
    @ApiOperation(value = "Фиксация результатов выполнения переезда. "
        + "Обновляет данные документа, закрывает задачу по БП")
    @PostMapping(path = "/setShippingResult")
    public void setShippingResult(
        @ApiParam(value = "Результаты выполнения переезда")
        @RequestBody
        @Valid ShippingResultDto dto
    ) {
        shippingResultService.setShippingResult(dto);
    }

    /**
     * Подтверждение заявки с МПГУ.
     *
     * @param dto - идентификаторы процесса и задачи.
     */
    @ApiOperation(value = "Подтверждение заявки с МПГУ")
    @PostMapping(path = "/confirmMpguOrder")
    public void confirmMpguOrder(
        @ApiParam(value = "Идентификаторы процесса и задачи")
        @RequestBody
        @Valid ProcessInfo dto
    ) {
        bpmService.completeTaskViaForm(dto.getTaskId(),
            Collections.singletonList(new FormProperty("confirmation", "true")));
    }

}
