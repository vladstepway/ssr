package ru.croc.ugd.ssr.controller.tradeaddition;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.integration.service.notification.TestElkUserNotificationService;

@RestController
@AllArgsConstructor
@RequestMapping("/trade-addition-notification")
public class TradeAdditionNotificationController {
    private final TestElkUserNotificationService testElkUserNotificationService;

    @ApiOperation(value = "880046")
    @PostMapping(value = "/880046")
    public void send880046(
        @ApiParam("ssoId")
        @RequestParam(value = "id", defaultValue = "168ba8a0-ff62-4e2e-9b40-018bbde456b1") String ssoId,
        @ApiParam("personDocumentId")
        @RequestParam(value = "personDocumentId", required = false) String personDocumentId,
        @ApiParam("cipId")
        @RequestParam(value = "cipId", defaultValue = "c3464b61-141a-44dd-8918-ff3dc16977d5") String cipId
    ) {
        testElkUserNotificationService.testTradeAdditionElkRequest("880046", ssoId, personDocumentId, cipId);
    }

    @ApiOperation(value = "880040")
    @PostMapping(value = "/880040")
    public void send880040(
        @ApiParam("ssoId")
        @RequestParam(value = "id", defaultValue = "168ba8a0-ff62-4e2e-9b40-018bbde456b1") String ssoId,
        @ApiParam("personDocumentId")
        @RequestParam(value = "personDocumentId", required = false) String personDocumentId,
        @ApiParam("cipId")
        @RequestParam(value = "cipId", defaultValue = "c3464b61-141a-44dd-8918-ff3dc16977d5") String cipId
    ) {
        testElkUserNotificationService.testTradeAdditionElkRequest("880040", ssoId, personDocumentId, cipId);
    }

    @ApiOperation(value = "880055")
    @PostMapping(value = "/880055")
    public void send880055(
        @ApiParam("ssoId")
        @RequestParam(value = "id", defaultValue = "168ba8a0-ff62-4e2e-9b40-018bbde456b1") String ssoId,
        @ApiParam("personDocumentId")
        @RequestParam(value = "personDocumentId", required = false) String personDocumentId,
        @ApiParam("cipId")
        @RequestParam(value = "cipId", defaultValue = "c3464b61-141a-44dd-8918-ff3dc16977d5") String cipId,
        @ApiParam("chedId")
        @RequestParam(value = "chedId") String chedId
    ) {
        testElkUserNotificationService.testTradeAdditionElkRequest("880055", ssoId,
            personDocumentId, cipId, chedId);
    }

    @ApiOperation(value = "880056")
    @PostMapping(value = "/880056")
    public void send880056(
        @ApiParam("ssoId")
        @RequestParam(value = "id", defaultValue = "168ba8a0-ff62-4e2e-9b40-018bbde456b1") String ssoId,
        @ApiParam("personDocumentId")
        @RequestParam(value = "personDocumentId", required = false) String personDocumentId,
        @ApiParam("cipId")
        @RequestParam(value = "cipId", defaultValue = "c3464b61-141a-44dd-8918-ff3dc16977d5") String cipId
    ) {
        testElkUserNotificationService.testTradeAdditionElkRequest("880056", ssoId, personDocumentId, cipId);
    }

    @ApiOperation(value = "880044")
    @PostMapping(value = "/880044")
    public void send880044(
        @ApiParam("ssoId")
        @RequestParam(value = "id", defaultValue = "168ba8a0-ff62-4e2e-9b40-018bbde456b1") String ssoId,
        @ApiParam("personDocumentId")
        @RequestParam(value = "personDocumentId", required = false) String personDocumentId,
        @ApiParam("cipId")
        @RequestParam(value = "cipId", defaultValue = "c3464b61-141a-44dd-8918-ff3dc16977d5") String cipId
    ) {
        testElkUserNotificationService.testTradeAdditionElkRequest("880044", ssoId, personDocumentId, cipId);
    }

    @ApiOperation(value = "880047")
    @PostMapping(value = "/880047")
    public void send880047(
        @ApiParam("ssoId")
        @RequestParam(value = "id", defaultValue = "168ba8a0-ff62-4e2e-9b40-018bbde456b1") String ssoId,
        @ApiParam("personDocumentId")
        @RequestParam(value = "personDocumentId", required = false) String personDocumentId,
        @ApiParam("cipId")
        @RequestParam(value = "cipId", defaultValue = "c3464b61-141a-44dd-8918-ff3dc16977d5") String cipId
    ) {
        testElkUserNotificationService.testTradeAdditionElkRequest("880047", ssoId, personDocumentId, cipId);
    }

    @ApiOperation(value = "880048")
    @PostMapping(value = "/880048")
    public void send880048(
        @ApiParam("ssoId")
        @RequestParam(value = "id", defaultValue = "168ba8a0-ff62-4e2e-9b40-018bbde456b1") String ssoId,
        @ApiParam("personDocumentId")
        @RequestParam(value = "personDocumentId", required = false) String personDocumentId,
        @ApiParam("cipId")
        @RequestParam(value = "cipId", defaultValue = "c3464b61-141a-44dd-8918-ff3dc16977d5") String cipId
    ) {
        testElkUserNotificationService.testTradeAdditionElkRequest("880048", ssoId, personDocumentId, cipId);
    }


    @ApiOperation(value = "880081")
    @PostMapping(value = "/880081")
    public void send880081(
        @ApiParam("ssoId")
        @RequestParam(value = "id", defaultValue = "168ba8a0-ff62-4e2e-9b40-018bbde456b1") String ssoId,
        @ApiParam("personDocumentId")
        @RequestParam(value = "personDocumentId", required = false) String personDocumentId,
        @ApiParam("cipId")
        @RequestParam(value = "cipId", defaultValue = "c3464b61-141a-44dd-8918-ff3dc16977d5") String cipId
    ) {
        testElkUserNotificationService.testTradeAdditionElkRequest("880081", ssoId, personDocumentId, cipId);
    }


    @ApiOperation(value = "880082")
    @PostMapping(value = "/880082")
    public void send880082(
        @ApiParam("ssoId")
        @RequestParam(value = "id", defaultValue = "168ba8a0-ff62-4e2e-9b40-018bbde456b1") String ssoId,
        @ApiParam("personDocumentId")
        @RequestParam(value = "personDocumentId", required = false) String personDocumentId,
        @ApiParam("cipId")
        @RequestParam(value = "cipId", defaultValue = "c3464b61-141a-44dd-8918-ff3dc16977d5") String cipId
    ) {
        testElkUserNotificationService.testTradeAdditionElkRequest("880082", ssoId, personDocumentId, cipId);
    }


    @ApiOperation(value = "880095")
    @PostMapping(value = "/880095")
    public void send880095(
        @ApiParam("ssoId")
        @RequestParam(value = "id", defaultValue = "168ba8a0-ff62-4e2e-9b40-018bbde456b1") String ssoId,
        @ApiParam("personDocumentId")
        @RequestParam(value = "personDocumentId", required = false) String personDocumentId,
        @ApiParam("cipId")
        @RequestParam(value = "cipId", defaultValue = "c3464b61-141a-44dd-8918-ff3dc16977d5") String cipId
    ) {
        testElkUserNotificationService.testTradeAdditionElkRequest("880095", ssoId, personDocumentId, cipId);
    }


    @ApiOperation(value = "880058")
    @PostMapping(value = "/880058")
    public void send880058(
        @ApiParam("ssoId")
        @RequestParam(value = "id", defaultValue = "168ba8a0-ff62-4e2e-9b40-018bbde456b1") String ssoId,
        @ApiParam("personDocumentId")
        @RequestParam(value = "personDocumentId", required = false) String personDocumentId,
        @ApiParam("cipId")
        @RequestParam(value = "cipId", defaultValue = "c3464b61-141a-44dd-8918-ff3dc16977d5") String cipId
    ) {
        testElkUserNotificationService.testTradeAdditionElkRequest("880058", ssoId, personDocumentId, cipId);
    }


    @ApiOperation(value = "880059")
    @PostMapping(value = "/880059")
    public void send880059(
        @ApiParam("ssoId")
        @RequestParam(value = "id", defaultValue = "168ba8a0-ff62-4e2e-9b40-018bbde456b1") String ssoId,
        @ApiParam("personDocumentId")
        @RequestParam(value = "personDocumentId", required = false) String personDocumentId,
        @ApiParam("cipId")
        @RequestParam(value = "cipId", defaultValue = "c3464b61-141a-44dd-8918-ff3dc16977d5") String cipId
    ) {
        testElkUserNotificationService.testTradeAdditionElkRequest("880059", ssoId, personDocumentId, cipId);
    }

    @ApiOperation(value = "880050")
    @PostMapping(value = "/880050")
    public void send880050(
        @ApiParam("ssoId")
        @RequestParam(value = "id", defaultValue = "168ba8a0-ff62-4e2e-9b40-018bbde456b1") String ssoId,
        @ApiParam("personDocumentId")
        @RequestParam(value = "personDocumentId", required = false) String personDocumentId,
        @ApiParam("cipId")
        @RequestParam(value = "cipId", defaultValue = "c3464b61-141a-44dd-8918-ff3dc16977d5") String cipId
    ) {
        testElkUserNotificationService.testTradeAdditionElkRequest("880050", ssoId, personDocumentId, cipId);
    }


    @ApiOperation(value = "880060")
    @PostMapping(value = "/880060")
    public void send880060(
        @ApiParam("ssoId")
        @RequestParam(value = "id", defaultValue = "168ba8a0-ff62-4e2e-9b40-018bbde456b1") String ssoId,
        @ApiParam("personDocumentId")
        @RequestParam(value = "personDocumentId", required = false) String personDocumentId,
        @ApiParam("cipId")
        @RequestParam(value = "cipId", defaultValue = "c3464b61-141a-44dd-8918-ff3dc16977d5") String cipId
    ) {
        testElkUserNotificationService.testTradeAdditionElkRequest("880060", ssoId, personDocumentId, cipId);
    }
}
