package ru.croc.ugd.ssr.controller.notarycompensation;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.integration.service.notification.TestElkUserNotificationService;

import java.time.LocalDateTime;

@RestController
@AllArgsConstructor
@RequestMapping("/notary-compensation-notification")
public class NotaryCompensationNotificationController {

    private final TestElkUserNotificationService testElkUserNotificationService;

    @ApiOperation(value = "880140")
    @PostMapping(value = "/880140")
    public void send880140(
        @ApiParam("ssoId")
        @RequestParam(value = "ssoId", required = false) String ssoId,
        @ApiParam("personDocumentId")
        @RequestParam(value = "personDocumentId", required = false) String personDocumentId
    ) {
        testElkUserNotificationService.sendNotaryCompensationNotification(
            ssoId,
            personDocumentId
        );
    }

    @ApiOperation(value = "880142")
    @PostMapping(value = "/880142")
    public void send880142(
        @ApiParam("notaryApplicationDocumentId")
        @RequestParam(value = "notaryApplicationDocumentId", required = false) String notaryApplicationDocumentId,
        @ApiParam("notaryDocumentId")
        @RequestParam(value = "notaryDocumentId", required = false) String notaryDocumentId,
        @ApiParam("ssoId")
        @RequestParam(value = "ssoId", required = false) String ssoId,
        @ApiParam("eno")
        @RequestParam(value = "eno", required = false) String eno,
        @ApiParam("personDocumentId")
        @RequestParam(value = "personDocumentId", required = false) String personDocumentId,
        @ApiParam("appointmentDateTime")
        @RequestParam(value = "appointmentDateTime", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime appointmentDateTime
    ) {
        testElkUserNotificationService.sendNotaryCompensationNotification(
            notaryApplicationDocumentId,
            notaryDocumentId,
            "880142",
            ssoId,
            personDocumentId,
            appointmentDateTime,
            eno
        );
    }
}
