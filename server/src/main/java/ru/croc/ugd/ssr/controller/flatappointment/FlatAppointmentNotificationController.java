package ru.croc.ugd.ssr.controller.flatappointment;

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
@RequestMapping("/flat-appointment-notification")
public class FlatAppointmentNotificationController {

    private final TestElkUserNotificationService testElkUserNotificationService;

    @ApiOperation(value = "880126")
    @PostMapping(value = "/880126")
    public void send880126(
        @ApiParam("flatAppointmentDocumentId")
        @RequestParam(value = "flatAppointmentDocumentId", required = false) String flatAppointmentDocumentId,
        @ApiParam("ssoId")
        @RequestParam(value = "ssoId", required = false) String ssoId,
        @ApiParam("personDocumentId")
        @RequestParam(value = "personDocumentId", required = false) String personDocumentId,
        @ApiParam("appointmentDateTime")
        @RequestParam(value = "appointmentDateTime", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime appointmentDateTime,
        @ApiParam("cipId")
        @RequestParam(value = "cipId", required = false) String cipId,
        @ApiParam("cancelReason")
        @RequestParam(value = "cancelReason", required = false) String cancelReason
    ) {
        testElkUserNotificationService.sendFlatAppointmentNotification(
            flatAppointmentDocumentId,
            "880126",
            ssoId,
            personDocumentId,
            cipId,
            cancelReason,
            appointmentDateTime
        );
    }

    @ApiOperation(value = "880130")
    @PostMapping(value = "/880130")
    public void send880130(
        @ApiParam("flatAppointmentDocumentId")
        @RequestParam(value = "flatAppointmentDocumentId", required = false) String flatAppointmentDocumentId,
        @ApiParam("ssoId")
        @RequestParam(value = "ssoId", required = false) String ssoId,
        @ApiParam("personDocumentId")
        @RequestParam(value = "personDocumentId", required = false) String personDocumentId,
        @ApiParam("appointmentDateTime")
        @RequestParam(value = "appointmentDateTime", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime appointmentDateTime,
        @ApiParam("cipId")
        @RequestParam(value = "cipId", required = false) String cipId,
        @ApiParam("cancelReason")
        @RequestParam(value = "cancelReason", required = false) String cancelReason
    ) {
        testElkUserNotificationService.sendFlatAppointmentNotification(
            flatAppointmentDocumentId,
            "880130",
            ssoId,
            personDocumentId,
            cipId,
            cancelReason,
            appointmentDateTime
        );
    }

    @ApiOperation(value = "880129")
    @PostMapping(value = "/880129")
    public void send880129(
        @ApiParam("flatAppointmentDocumentId")
        @RequestParam(value = "flatAppointmentDocumentId", required = false) String flatAppointmentDocumentId,
        @ApiParam("ssoId")
        @RequestParam(value = "ssoId", required = false) String ssoId,
        @ApiParam("personDocumentId")
        @RequestParam(value = "personDocumentId", required = false) String personDocumentId,
        @ApiParam("appointmentDateTime")
        @RequestParam(value = "appointmentDateTime", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime appointmentDateTime,
        @ApiParam("cipId")
        @RequestParam(value = "cipId", required = false) String cipId,
        @ApiParam("cancelReason")
        @RequestParam(value = "cancelReason", required = false) String cancelReason
    ) {
        testElkUserNotificationService.sendFlatAppointmentNotification(
            flatAppointmentDocumentId,
            "880129",
            ssoId,
            personDocumentId,
            cipId,
            cancelReason,
            appointmentDateTime
        );
    }

    @ApiOperation(value = "880127")
    @PostMapping(value = "/880127")
    public void send880127(
        @ApiParam("flatAppointmentDocumentId")
        @RequestParam(value = "flatAppointmentDocumentId", required = false) String flatAppointmentDocumentId,
        @ApiParam("ssoId")
        @RequestParam(value = "ssoId", required = false) String ssoId,
        @ApiParam("personDocumentId")
        @RequestParam(value = "personDocumentId", required = false) String personDocumentId,
        @ApiParam("appointmentDateTime")
        @RequestParam(value = "appointmentDateTime", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime appointmentDateTime,
        @ApiParam("cipId")
        @RequestParam(value = "cipId", required = false) String cipId,
        @ApiParam("cancelReason")
        @RequestParam(value = "cancelReason", required = false) String cancelReason
    ) {
        testElkUserNotificationService.sendFlatAppointmentNotification(
            flatAppointmentDocumentId,
            "880127",
            ssoId,
            personDocumentId,
            cipId,
            cancelReason,
            appointmentDateTime
        );
    }

}
