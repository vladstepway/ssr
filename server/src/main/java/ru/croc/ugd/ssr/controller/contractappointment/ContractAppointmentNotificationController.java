package ru.croc.ugd.ssr.controller.contractappointment;

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
@RequestMapping("/contract-appointment-notification")
public class ContractAppointmentNotificationController {

    private final TestElkUserNotificationService testElkUserNotificationService;

    @ApiOperation(value = "880134")
    @PostMapping(value = "/880134")
    public void send880134(
        @ApiParam("contactAppointmentDocumentId")
        @RequestParam(value = "contactAppointmentDocumentId", required = false) String contactAppointmentDocumentId,
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
        @RequestParam(value = "cancelReason", required = false) String cancelReason,
        @ApiParam("addressesTo")
        @RequestParam(value = "addressesTo", required = false) String addressesTo
    ) {
        testElkUserNotificationService.sendContractAppointmentNotification(
            contactAppointmentDocumentId,
            "880134",
            ssoId,
            personDocumentId,
            cipId,
            cancelReason,
            addressesTo,
            appointmentDateTime
        );
    }

    @ApiOperation(value = "880135")
    @PostMapping(value = "/880135")
    public void send880135(
        @ApiParam("contactAppointmentDocumentId")
        @RequestParam(value = "contactAppointmentDocumentId", required = false) String contactAppointmentDocumentId,
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
        @RequestParam(value = "cancelReason", required = false) String cancelReason,
        @ApiParam("addressesTo")
        @RequestParam(value = "addressesTo", required = false) String addressesTo
    ) {
        testElkUserNotificationService.sendContractAppointmentNotification(
            contactAppointmentDocumentId,
            "880135",
            ssoId,
            personDocumentId,
            cipId,
            cancelReason,
            addressesTo,
            appointmentDateTime
        );
    }

    @ApiOperation(value = "880131")
    @PostMapping(value = "/880131")
    public void send880131(
        @ApiParam("contactAppointmentDocumentId")
        @RequestParam(value = "contactAppointmentDocumentId", required = false) String contactAppointmentDocumentId,
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
        @RequestParam(value = "cancelReason", required = false) String cancelReason,
        @ApiParam("addressesTo")
        @RequestParam(value = "addressesTo", required = false) String addressesTo
    ) {
        testElkUserNotificationService.sendContractAppointmentNotification(
            contactAppointmentDocumentId,
            "880131",
            ssoId,
            personDocumentId,
            cipId,
            cancelReason,
            addressesTo,
            appointmentDateTime
        );
    }

    @ApiOperation(value = "880132")
    @PostMapping(value = "/880132")
    public void send880132(
        @ApiParam("contactAppointmentDocumentId")
        @RequestParam(value = "contactAppointmentDocumentId", required = false) String contactAppointmentDocumentId,
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
        @RequestParam(value = "cancelReason", required = false) String cancelReason,
        @ApiParam("addressesTo")
        @RequestParam(value = "addressesTo", required = false) String addressesTo
    ) {
        testElkUserNotificationService.sendContractAppointmentNotification(
            contactAppointmentDocumentId,
            "880132",
            ssoId,
            personDocumentId,
            cipId,
            cancelReason,
            addressesTo,
            appointmentDateTime
        );
    }

    @ApiOperation(value = "880114")
    @PostMapping(value = "/880114")
    public void send880114(
        @ApiParam("contactAppointmentDocumentId")
        @RequestParam(value = "contactAppointmentDocumentId", required = false) String contactAppointmentDocumentId,
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
        @RequestParam(value = "cancelReason", required = false) String cancelReason,
        @ApiParam("addressesTo")
        @RequestParam(value = "addressesTo", required = false) String addressesTo
    ) {
        testElkUserNotificationService.sendContractAppointmentNotification(
            contactAppointmentDocumentId,
            "880114",
            ssoId,
            personDocumentId,
            cipId,
            cancelReason,
            addressesTo,
            appointmentDateTime
        );
    }
}
