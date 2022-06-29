package ru.croc.ugd.ssr.integration.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.integration.service.notification.TestElkUserNotificationService;

/**
 * Тестовый контроллер для запуска сервисов по интеграции с ЕЛК.
 */
@RestController(value = "/test-elk-user-notification")
@AllArgsConstructor
@Api(description = "Тестовый контроллер для запуска сервисов по интеграции с ЕЛК.")
public class TestElkUserNotificationController {

    private static final Logger LOG = LoggerFactory.getLogger(TestElkUserNotificationController.class);

    private final TestElkUserNotificationService testElkUserNotificationService;

    /**
     * Тестовый запрос в ЕЛК (начало переселения).
     *
     * @param ssoId ssoId
     * @param cipId cipId
     * @return статус
     */
    @ApiOperation(value = "Тестовый запрос в ЕЛК (начало переселения)")
    @GetMapping(value = "/testStartRenovationElkRequest")
    public ResponseEntity<String> testStartRenovationElkRequest(
        @ApiParam("ssoId")
        @RequestParam(value = "ssoId", defaultValue = "168ba8a0-ff62-4e2e-9b40-018bbde456b1") String ssoId,
        @ApiParam("personId")
        @RequestParam(value = "personId", required = false) String personId,
        @ApiParam("cipId")
        @RequestParam(value = "cipId", defaultValue = "c3464b61-141a-44dd-8918-ff3dc16977d5") String cipId
    ) {
        LOG.info("Тестовый запрос в ЕЛК (начало переселения) по ssoId: {}, cipId: {}", ssoId, cipId);
        testElkUserNotificationService.testStartRenovationElkRequest(ssoId, personId, cipId);
        return ResponseEntity.ok("OK");
    }

    /**
     * Тестовый запрос в ЕЛК (письмо с предложением).
     *
     * @param ssoId ssoId
     * @param personId personId
     * @param cipId cipId
     * @param letterId letterId
     * @return статус
     */
    @ApiOperation(value = "Тестовый запрос в ЕЛК (письмо с предложением)")
    @GetMapping(value = "/testOfferLetterElkRequest")
    public ResponseEntity<String> testOfferLetterElkRequest(
        @ApiParam("ssoId")
        @RequestParam(value = "id", defaultValue = "168ba8a0-ff62-4e2e-9b40-018bbde456b1") String ssoId,
        @ApiParam("personId")
        @RequestParam(value = "personId", required = false) String personId,
        @ApiParam("cipId")
        @RequestParam(value = "cipId", defaultValue = "c3464b61-141a-44dd-8918-ff3dc16977d5") String cipId,
        @ApiParam("letterId")
        @RequestParam(value = "letterId") String letterId
    ) {
        LOG.info(
            "Тестовый запрос в ЕЛК (письмо с предложением) по ssoId: {}, cipId: {}, letterId: {}",
            ssoId,
            cipId,
            letterId
        );
        testElkUserNotificationService.testOfferLetterElkRequest(ssoId, personId, cipId, letterId);
        return ResponseEntity.ok("OK");
    }

    /**
     * Тестовый запрос в ЕЛК (согласие на квартиру).
     *
     * @param ssoId ssoId
     * @param cipId cipId
     * @return статус
     */
    @ApiOperation(value = "Тестовый запрос в ЕЛК (согласие на квартиру)")
    @GetMapping(value = "/testFlatAgreementElkRequest")
    public ResponseEntity<String> testFlatAgreementElkRequest(
        @ApiParam("ssoId")
        @RequestParam(value = "id", defaultValue = "168ba8a0-ff62-4e2e-9b40-018bbde456b1") String ssoId,
        @ApiParam("personId")
        @RequestParam(value = "personId", required = false) String personId,
        @ApiParam("cipId")
        @RequestParam(value = "cipId", defaultValue = "c3464b61-141a-44dd-8918-ff3dc16977d5") String cipId
    ) {
        LOG.info("Тестовый запрос в ЕЛК (согласие на квартиру) по ssoId: {}, cipId: {}", ssoId, cipId);
        testElkUserNotificationService.testFlatAgreementElkRequest(ssoId, personId, cipId);
        return ResponseEntity.ok("OK");
    }

    /**
     * Тестовый запрос в ЕЛК (отказ от квартиры).
     *
     * @param ssoId ssoId
     * @param cipId cipId
     * @return статус
     */
    @ApiOperation(value = "Тестовый запрос в ЕЛК (отказ от квартиры)")
    @GetMapping(value = "/testFlatRefusalElkRequest")
    public ResponseEntity<String> testFlatRefusalElkRequest(
        @ApiParam("ssoId")
        @RequestParam(value = "id", defaultValue = "168ba8a0-ff62-4e2e-9b40-018bbde456b1") String ssoId,
        @ApiParam("personId")
        @RequestParam(value = "personId", required = false) String personId,
        @ApiParam("cipId")
        @RequestParam(value = "cipId", defaultValue = "c3464b61-141a-44dd-8918-ff3dc16977d5") String cipId
    ) {
        LOG.info("Тестовый запрос в ЕЛК (отказ от квартиры) по ssoId: {}, cipId: {}", ssoId, cipId);
        testElkUserNotificationService.testFlatRefusalElkRequest(ssoId, personId, cipId);
        return ResponseEntity.ok("OK");
    }

    /**
     * Тестовый запрос в ЕЛК (после осмотра квартиры).
     *
     * @param ssoId ssoId
     * @param cipId cipId
     * @return статус
     */
    @ApiOperation(value = "Тестовый запрос в ЕЛК (после осмотра квартиры)")
    @GetMapping(value = "/testFlatInspectionElkRequest")
    public ResponseEntity<String> testFlatInspectionElkRequest(
        @ApiParam("ssoId")
        @RequestParam(value = "id", defaultValue = "168ba8a0-ff62-4e2e-9b40-018bbde456b1") String ssoId,
        @ApiParam("personId")
        @RequestParam(value = "personId", required = false) String personId,
        @ApiParam("cipId")
        @RequestParam(value = "cipId", defaultValue = "c3464b61-141a-44dd-8918-ff3dc16977d5") String cipId
    ) {
        LOG.info("Тестовый запрос в ЕЛК (после осмотра квартиры) по ssoId: {}, cipId: {}", ssoId, cipId);
        testElkUserNotificationService.testFlatInspectionElkRequest(ssoId, personId, cipId);
        return ResponseEntity.ok("OK");
    }

    /**
     * Тестовый запрос в ЕЛК (о подписанном договоре).
     *
     * @param ssoId  ssoId
     * @param cipId  cipId
     * @param flatId flatId
     * @return статус
     */
    @ApiOperation(value = "Тестовый запрос в ЕЛК (о подписанном договоре)")
    @GetMapping(value = "/testSignedContractElkRequest")
    public ResponseEntity<String> testSignedContractElkRequest(
        @ApiParam("ssoId")
        @RequestParam(value = "id", defaultValue = "168ba8a0-ff62-4e2e-9b40-018bbde456b1") String ssoId,
        @ApiParam("personId")
        @RequestParam(value = "personId", required = false) String personId,
        @ApiParam("cipId")
        @RequestParam(value = "cipId", defaultValue = "c3464b61-141a-44dd-8918-ff3dc16977d5") String cipId,
        @ApiParam("flatId")
        @RequestParam(value = "flatId", defaultValue = "648961455") String flatId
    ) {
        LOG.info(
            "Тестовый запрос в ЕЛК (о подписанном договоре) по ssoId: {}, cipId: {}, flatId: {}", ssoId, cipId, flatId
        );
        testElkUserNotificationService.testSignedContractElkRequest(ssoId, personId, cipId, flatId);
        return ResponseEntity.ok("OK");
    }


    /**
     * Тестовый запрос в ЕЛК (о готовности договора).
     *
     * @param ssoId ssoId
     * @param cipId cipId
     * @return статус
     */
    @ApiOperation(value = "Тестовый запрос в ЕЛК (о готовности договора)")
    @GetMapping(value = "/testContractReadyElkRequest")
    public ResponseEntity<String> testContractReadyElkRequest(
        @ApiParam("ssoId")
        @RequestParam(value = "id", defaultValue = "168ba8a0-ff62-4e2e-9b40-018bbde456b1") String ssoId,
        @ApiParam("personId")
        @RequestParam(value = "personId", required = false) String personId,
        @ApiParam("cipId")
        @RequestParam(value = "cipId", defaultValue = "c3464b61-141a-44dd-8918-ff3dc16977d5") String cipId
    ) {
        LOG.info("Тестовый запрос в ЕЛК (о готовности договора) по ssoId: {}, cipId: {}", ssoId, cipId);
        testElkUserNotificationService.testContractReadyElkRequest(ssoId, personId, cipId);
        return ResponseEntity.ok("OK");
    }


    /**
     * Тестовый запрос в ЕЛК (о готовности договора).
     *
     * @param ssoId ssoId
     * @param cipId cipId
     * @return статус
     */
    @ApiOperation(value = "Тестовый запрос в ЕЛК (о готовности договора)")
    @GetMapping(value = "/testContractReadyForAcquaintingRequest")
    public ResponseEntity<String> testContractReadyForAcquaintingRequest(
        @ApiParam("ssoId")
        @RequestParam(value = "id", defaultValue = "168ba8a0-ff62-4e2e-9b40-018bbde456b1") String ssoId,
        @ApiParam("personId")
        @RequestParam(value = "personId", required = false) String personId,
        @ApiParam("cipId")
        @RequestParam(value = "cipId", defaultValue = "c3464b61-141a-44dd-8918-ff3dc16977d5") String cipId
    ) {
        LOG.info("Тестовый запрос в ЕЛК (о готовности договора) по ssoId: {}, cipId: {}", ssoId, cipId);
        testElkUserNotificationService.sendContractReadyForAcquaintingNotification(ssoId, personId, cipId);
        return ResponseEntity.ok("OK");
    }
}
