package ru.croc.ugd.ssr.integration.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.integration.service.IntegrationService;
import ru.croc.ugd.ssr.integration.service.notification.ElkUserNotificationService;

import java.util.List;

/**
 * Контроллер для запуска сервисов по интеграции с ЕЛК.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/elk-user-notification")
public class ElkUserNotificationController {

    private static final Logger LOG = LoggerFactory.getLogger(ElkUserNotificationController.class);

    private final ElkUserNotificationService notificationService;

    private final IntegrationService integrationService;

    /**
     * Отправка сообщения в ЕЛК (начало переселения) по всем жителям дома.
     *
     * @param id    Id дома
     * @param cipId id ЦИП
     * @return статус
     */
    @ApiOperation(value = "Отправка сообщения в ЕЛК (начало переселения) по всем жителям дома")
    @GetMapping(value = "sendRequestToElkByRealEstateId/{id}")
    public ResponseEntity<String> sendRequestToElkByRealEstateId(
        @ApiParam(value = "Id дома", required = true) @PathVariable String id,
        @ApiParam("id ЦИП") @RequestParam String cipId) {
        return integrationService.sendRequestToElkByRealEstateId(id, cipId);
    }

    /**
     * Отправка сообщения в ЕЛК (начало переселения) по всем жителям квартиры.
     *
     * @param id    Id квартиры
     * @param cipId id ЦИП
     * @return статус
     */
    @ApiOperation(value = "Отправка сообщения в ЕЛК (начало переселения) по всем жителям квартиры")
    @GetMapping(value = "sendRequestToElkByFlatId/{id}")
    public ResponseEntity<String> sendRequestToElkByFlatId(
        @ApiParam(value = "Id квартиры", required = true) @PathVariable String id,
        @ApiParam("id ЦИП") @RequestParam String cipId) {
        return integrationService.sendRequestToElkByFlatId(id, cipId);
    }

    /**
     * Отправка сообщения в ЕЛК (начало переселения) жителю по id.
     *
     * @param id    Id жителя
     * @param cipId id ЦИП
     * @return статус
     */
    @ApiOperation(value = "Отправка сообщения в ЕЛК (начало переселения) жителю по id")
    @GetMapping(value = "sendRequestToElkStartRenovationByPersonId/{id}")
    public ResponseEntity<String> sendRequestToElkStartRenovationByPersonId(
        @ApiParam(value = "Id жителя", required = true) @PathVariable String id,
        @ApiParam("id ЦИП") @RequestParam String cipId) {
        return notificationService.sendRequestToElkStartRenovationByPersonId(id, cipId);
    }

    /**
     * Отправка запроса в ЕЛК об отказе на квартиру.
     *
     * @param cipId    Id ЦИП
     * @param letterId ид письма с предложением
     * @param ids      Ids жителей
     */
    @ApiOperation(value = "Отправка запроса в ЕЛК об отказе на квартиру")
    @PostMapping(value = "sendNotificationFlatRefusal")
    public void sendNotificationFlatRefusal(
        @ApiParam(value = "Id ЦИП", required = true) @RequestParam String cipId,
        @ApiParam(value = "ид письма с предложением") @RequestParam(required = false) String letterId,
        @ApiParam(value = "Ids жителей", required = true) @RequestBody List<String> ids
    ) {
        notificationService.sendNotificationFlatRefusalByPersonIds(ids, cipId, letterId);
    }

    /**
     * Отправка запроса в ЕЛК о согласии на квартиру.
     *
     * @param letterId ид письма с предложением
     * @param ids      Ids жителей
     */
    @ApiOperation(value = "Отправка запроса в ЕЛК о согласии на квартиру")
    @PostMapping(value = "sendNotificationFlatAgreement")
    public void sendNotificationFlatAgreement(
        @ApiParam(value = "ид письма с предложением") @RequestParam(required = false) String letterId,
        @ApiParam(value = "Ids жителей", required = true) @RequestBody List<String> ids
    ) {
        notificationService.sendNotificationFlatAgreementByPersonIds(ids, letterId);
    }

    /**
     * Отправка запроса в ЕЛК после осмотра квартиры.
     *
     * @param cipId    Id ЦИП
     * @param ids      Ids жителей
     * @param letterId Id письма
     */
    @ApiOperation(value = "Отправка запроса в ЕЛК после осмотра квартиры")
    @PostMapping(value = "sendNotificationFlatInspection")
    public void sendNotificationFlatInspection(
        @ApiParam(value = "Id ЦИП", required = true) @RequestParam String cipId,
        @ApiParam(value = "Ids жителей", required = true) @RequestBody List<String> ids,
        @ApiParam(value = "Id письма", required = true) @RequestParam String letterId) {
        notificationService.sendNotificationFlatInspectionByPersonIds(ids, cipId, letterId);
    }

    /**
     * Сформировать XML для отправки в ЕЛК (начало переселения).
     *
     * @param id Id жителя.
     * @return xml
     */
    @ApiOperation(value = "Сформировать XML для отправки в ЕЛК (начало переселения)")
    @GetMapping(value = "/createStartRenovationXmlElkRequest")
    public ResponseEntity<String> createStartRenovationXmlElkRequest(
        @ApiParam("Id жителя") @RequestParam(value = "id") String id) {
        return ResponseEntity.ok(notificationService.createStartRenovationXmlElkRequest(id));
    }

    /**
     * Сформировать XML для отправки в ЕЛК (письмо с предложением).
     *
     * @param id Id жителя
     * @return xml
     */
    @ApiOperation(value = "Сформировать XML для отправки в ЕЛК (письмо с предложением)")
    @GetMapping(value = "/createOfferLetterXmlElkRequest")
    public ResponseEntity<String> createOfferLetterXmlElkRequest(
        @ApiParam("Id жителя") @RequestParam(value = "id") String id) {
        return ResponseEntity.ok(notificationService.createOfferLetterXmlElkRequest(id));
    }

    /**
     * Сформировать XML для отправки в ЕЛК (согласие по квартире).
     *
     * @param id Id жителя
     * @return xml
     */
    @ApiOperation(value = "Сформировать XML для отправки в ЕЛК (согласие по квартире)")
    @GetMapping(value = "/createFlatAgreementXmlElkRequest")
    public ResponseEntity<String> createFlatAgreementXmlElkRequest(
        @ApiParam("Id жителя") @RequestParam(value = "id") String id) {
        return ResponseEntity.ok(notificationService.createFlatAgreementXmlElkRequest(id));
    }

    /**
     * Сформировать XML для отправки в ЕЛК (отказ от квартиры).
     *
     * @param id Id жителя
     * @return xml
     */
    @ApiOperation(value = "Сформировать XML для отправки в ЕЛК (отказ от квартиры)")
    @GetMapping(value = "/createFlatRefusalXmlElkRequest")
    public ResponseEntity<String> createFlatRefusalXmlElkRequest(
        @ApiParam("Id жителя") @RequestParam(value = "id") String id) {
        return ResponseEntity.ok(notificationService.createFlatRefusalXmlElkRequest(id));
    }

    /**
     * Сформировать XML для отправки в ЕЛК (о подписанном договоре).
     *
     * @param id Id жителя
     * @return xml
     */
    @ApiOperation(value = "Сформировать XML для отправки в ЕЛК (о подписанном договоре)")
    @GetMapping(value = "/createSignedContractXmlElkRequest")
    public ResponseEntity<String> createSignedContractXmlElkRequest(
        @ApiParam("Id жителя") @RequestParam(value = "id") String id) {
        return ResponseEntity.ok(notificationService.createSignedContractXmlElkRequest(id));
    }

    /**
     * Сформировать XML для отправки в ЕЛК (о готовности проекта договора).
     *
     * @param id Id жителя
     * @return xml
     */
    @ApiOperation(value = "Сформировать XML для отправки в ЕЛК (о готовности проекта договора)")
    @GetMapping(value = "/createContractReadyXmlElkRequest")
    public ResponseEntity<String> createContractReadyXmlElkRequest(
        @ApiParam("Id жителя") @RequestParam(value = "id") String id) {
        return ResponseEntity.ok(notificationService.createContractReadyXmlElkRequest(id));
    }

    /**
     * Сформировать XML для отправки в ЕЛК (после осмотра квартиры).
     *
     * @param id Id жителя
     * @return xml
     */
    @ApiOperation(value = "Сформировать XML для отправки в ЕЛК (после осмотра квартиры)")
    @GetMapping(value = "/createFlatInspectionXmlElkRequest")
    public ResponseEntity<String> createFlatInspectionXmlElkRequest(
        @ApiParam("Id жителя") @RequestParam(value = "id") String id) {
        return ResponseEntity.ok(notificationService.createFlatInspectionXmlElkRequest(id));
    }

    /**
     * Запрос на парсинг сообщения от ЕЛК (ЕТП МВ).
     *
     * @param message message
     * @return status
     */
    @ApiOperation(value = "Запрос на парсинг сообщения от ЕЛК (ЕТП МВ)")
    @PostMapping(value = "/parseMessageFromElk")
    public ResponseEntity<String> parseMessageFromElk(@RequestBody String message) {
        integrationService.parseMessageFromElk(message);
        return ResponseEntity.ok("ОК");
    }

    /**
     * Отправка сообщения в ЕЛК (дефекты устранены) жителю по id.
     *
     * @param personId personId
     * @param cipId cipId
     * @param apartmentInspectionId apartmentInspectionId
     * @return статус
     */
    @ApiOperation(value = "Отправка сообщения в ЕЛК (дефекты устранены) жителю по id")
    @PostMapping(value = "sendRequestToElkFixDefectsByPersonId")
    public ResponseEntity<String> sendRequestToElkFixDefectsByPersonId(
        @ApiParam(value = "Id ЦИП") @RequestParam String cipId,
        @ApiParam(value = "Id жителя", required = true) @RequestBody String personId,
        @ApiParam(value = "Id акта устранения дефектов", required = true)
        @RequestParam String apartmentInspectionId) {
        return notificationService.sendNotificationFixDefects(personId, cipId, apartmentInspectionId);
    }
}
