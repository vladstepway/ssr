package ru.croc.ugd.ssr.controller.notary;

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
@RequestMapping("/notary-notification")
public class NotaryNotificationController {

    private final TestElkUserNotificationService testElkUserNotificationService;

    @ApiOperation(value = "880108")
    @PostMapping(value = "/880108")
    public void send880108(
        @ApiParam("notaryApplicationDocumentId")
        @RequestParam(value = "notaryApplicationDocumentId", required = false) String notaryApplicationDocumentId,
        @ApiParam("notaryDocumentId")
        @RequestParam(value = "notaryDocumentId", required = false) String notaryDocumentId,
        @ApiParam("ssoId")
        @RequestParam(value = "ssoId", required = false) String ssoId,
        @ApiParam("personDocumentId")
        @RequestParam(value = "personDocumentId", required = false) String personDocumentId,
        @ApiParam("appointmentDateTime")
        @RequestParam(value = "appointmentDateTime", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime appointmentDateTime,
        @ApiParam("notaryFullName")
        @RequestParam(value = "notaryFullName", required = false) String notaryFullName,
        @ApiParam("eno")
        @RequestParam(value = "eno", required = false) String eno,
        @ApiParam("notaryPhones")
        @RequestParam(value = "notaryPhones", required = false) String notaryPhones,
        @ApiParam("oldAddress")
        @RequestParam(value = "oldAddress", required = false) String oldAddress,
        @ApiParam("newAddresses")
        @RequestParam(value = "newAddresses", required = false) String newAddresses
    ) {
        testElkUserNotificationService.sendNotaryNotification(
            notaryApplicationDocumentId,
            notaryDocumentId,
            "880108",
            ssoId,
            personDocumentId,
            notaryFullName,
            notaryPhones,
            eno,
            oldAddress,
            newAddresses,
            appointmentDateTime
        );
    }

    @ApiOperation(value = "880115")
    @PostMapping(value = "/880115")
    public void send880115(
        @ApiParam("notaryApplicationDocumentId")
        @RequestParam(value = "notaryApplicationDocumentId", required = false) String notaryApplicationDocumentId,
        @ApiParam("notaryDocumentId")
        @RequestParam(value = "notaryDocumentId", required = false) String notaryDocumentId,
        @ApiParam("ssoId")
        @RequestParam(value = "ssoId", required = false) String ssoId,
        @ApiParam("personDocumentId")
        @RequestParam(value = "personDocumentId", required = false) String personDocumentId,
        @ApiParam("appointmentDateTime")
        @RequestParam(value = "appointmentDateTime", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime appointmentDateTime,
        @ApiParam("notaryFullName")
        @RequestParam(value = "notaryFullName", required = false) String notaryFullName,
        @ApiParam("eno")
        @RequestParam(value = "eno", required = false) String eno,
        @ApiParam("notaryPhones")
        @RequestParam(value = "notaryPhones", required = false) String notaryPhones,
        @ApiParam("oldAddress")
        @RequestParam(value = "oldAddress", required = false) String oldAddress,
        @ApiParam("newAddresses")
        @RequestParam(value = "newAddresses", required = false) String newAddresses
    ) {
        testElkUserNotificationService.sendNotaryNotification(
            notaryApplicationDocumentId,
            notaryDocumentId,
            "880115",
            ssoId,
            personDocumentId,
            notaryFullName,
            notaryPhones,
            eno,
            oldAddress,
            newAddresses,
            appointmentDateTime
        );
    }

    @ApiOperation(value = "880104")
    @PostMapping(value = "/880104")
    public void send880104(
        @ApiParam("notaryApplicationDocumentId")
        @RequestParam(value = "notaryApplicationDocumentId", required = false) String notaryApplicationDocumentId,
        @ApiParam("notaryDocumentId")
        @RequestParam(value = "notaryDocumentId", required = false) String notaryDocumentId,
        @ApiParam("ssoId")
        @RequestParam(value = "ssoId", required = false) String ssoId,
        @ApiParam("personDocumentId")
        @RequestParam(value = "personDocumentId", required = false) String personDocumentId,
        @ApiParam("appointmentDateTime")
        @RequestParam(value = "appointmentDateTime", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime appointmentDateTime,
        @ApiParam("notaryFullName")
        @RequestParam(value = "notaryFullName", required = false) String notaryFullName,
        @ApiParam("eno")
        @RequestParam(value = "eno", required = false) String eno,
        @ApiParam("notaryPhones")
        @RequestParam(value = "notaryPhones", required = false) String notaryPhones,
        @ApiParam("oldAddress")
        @RequestParam(value = "oldAddress", required = false) String oldAddress,
        @ApiParam("newAddresses")
        @RequestParam(value = "newAddresses", required = false) String newAddresses
    ) {
        testElkUserNotificationService.sendNotaryNotification(
            notaryApplicationDocumentId,
            notaryDocumentId,
            "880104",
            ssoId,
            personDocumentId,
            notaryFullName,
            notaryPhones,
            eno,
            oldAddress,
            newAddresses,
            appointmentDateTime
        );
    }

    @ApiOperation(value = "880109")
    @PostMapping(value = "/880109")
    public void send880109(
        @ApiParam("notaryApplicationDocumentId")
        @RequestParam(value = "notaryApplicationDocumentId", required = false) String notaryApplicationDocumentId,
        @ApiParam("notaryDocumentId")
        @RequestParam(value = "notaryDocumentId", required = false) String notaryDocumentId,
        @ApiParam("ssoId")
        @RequestParam(value = "ssoId", required = false) String ssoId,
        @ApiParam("personDocumentId")
        @RequestParam(value = "personDocumentId", required = false) String personDocumentId,
        @ApiParam("appointmentDateTime")
        @RequestParam(value = "appointmentDateTime", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime appointmentDateTime,
        @ApiParam("notaryFullName")
        @RequestParam(value = "notaryFullName", required = false) String notaryFullName,
        @ApiParam("eno")
        @RequestParam(value = "eno", required = false) String eno,
        @ApiParam("notaryPhones")
        @RequestParam(value = "notaryPhones", required = false) String notaryPhones,
        @ApiParam("oldAddress")
        @RequestParam(value = "oldAddress", required = false) String oldAddress,
        @ApiParam("newAddresses")
        @RequestParam(value = "newAddresses", required = false) String newAddresses
    ) {
        testElkUserNotificationService.sendNotaryNotification(
            notaryApplicationDocumentId,
            notaryDocumentId,
            "880109",
            ssoId,
            personDocumentId,
            notaryFullName,
            notaryPhones,
            eno,
            oldAddress,
            newAddresses,
            appointmentDateTime
        );
    }

    @ApiOperation(value = "880116")
    @PostMapping(value = "/880116")
    public void send880116(
        @ApiParam("notaryApplicationDocumentId")
        @RequestParam(value = "notaryApplicationDocumentId", required = false) String notaryApplicationDocumentId,
        @ApiParam("notaryDocumentId")
        @RequestParam(value = "notaryDocumentId", required = false) String notaryDocumentId,
        @ApiParam("ssoId")
        @RequestParam(value = "ssoId", required = false) String ssoId,
        @ApiParam("personDocumentId")
        @RequestParam(value = "personDocumentId", required = false) String personDocumentId,
        @ApiParam("appointmentDateTime")
        @RequestParam(value = "appointmentDateTime", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime appointmentDateTime,
        @ApiParam("notaryFullName")
        @RequestParam(value = "notaryFullName", required = false) String notaryFullName,
        @ApiParam("eno")
        @RequestParam(value = "eno", required = false) String eno,
        @ApiParam("notaryPhones")
        @RequestParam(value = "notaryPhones", required = false) String notaryPhones,
        @ApiParam("oldAddress")
        @RequestParam(value = "oldAddress", required = false) String oldAddress,
        @ApiParam("newAddresses")
        @RequestParam(value = "newAddresses", required = false) String newAddresses
    ) {
        testElkUserNotificationService.sendNotaryNotification(
            notaryApplicationDocumentId,
            notaryDocumentId,
            "880116",
            ssoId,
            personDocumentId,
            notaryFullName,
            notaryPhones,
            eno,
            oldAddress,
            newAddresses,
            appointmentDateTime
        );
    }

    @ApiOperation(value = "880111")
    @PostMapping(value = "/880111")
    public void send880111(
        @ApiParam("notaryApplicationDocumentId")
        @RequestParam(value = "notaryApplicationDocumentId", required = false) String notaryApplicationDocumentId,
        @ApiParam("notaryDocumentId")
        @RequestParam(value = "notaryDocumentId", required = false) String notaryDocumentId,
        @ApiParam("ssoId")
        @RequestParam(value = "ssoId", required = false) String ssoId,
        @ApiParam("personDocumentId")
        @RequestParam(value = "personDocumentId", required = false) String personDocumentId,
        @ApiParam("appointmentDateTime")
        @RequestParam(value = "appointmentDateTime", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime appointmentDateTime,
        @ApiParam("notaryFullName")
        @RequestParam(value = "notaryFullName", required = false) String notaryFullName,
        @ApiParam("eno")
        @RequestParam(value = "eno", required = false) String eno,
        @ApiParam("notaryPhones")
        @RequestParam(value = "notaryPhones", required = false) String notaryPhones,
        @ApiParam("oldAddress")
        @RequestParam(value = "oldAddress", required = false) String oldAddress,
        @ApiParam("newAddresses")
        @RequestParam(value = "newAddresses", required = false) String newAddresses
    ) {
        testElkUserNotificationService.sendNotaryNotification(
            notaryApplicationDocumentId,
            notaryDocumentId,
            "880111",
            ssoId,
            personDocumentId,
            notaryFullName,
            notaryPhones,
            eno,
            oldAddress,
            newAddresses,
            appointmentDateTime
        );
    }

    @ApiOperation(value = "880118")
    @PostMapping(value = "/880118")
    public void send880118(
        @ApiParam("notaryApplicationDocumentId")
        @RequestParam(value = "notaryApplicationDocumentId", required = false) String notaryApplicationDocumentId,
        @ApiParam("notaryDocumentId")
        @RequestParam(value = "notaryDocumentId", required = false) String notaryDocumentId,
        @ApiParam("ssoId")
        @RequestParam(value = "ssoId", required = false) String ssoId,
        @ApiParam("personDocumentId")
        @RequestParam(value = "personDocumentId", required = false) String personDocumentId,
        @ApiParam("appointmentDateTime")
        @RequestParam(value = "appointmentDateTime", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime appointmentDateTime,
        @ApiParam("notaryFullName")
        @RequestParam(value = "notaryFullName", required = false) String notaryFullName,
        @ApiParam("eno")
        @RequestParam(value = "eno", required = false) String eno,
        @ApiParam("notaryPhones")
        @RequestParam(value = "notaryPhones", required = false) String notaryPhones,
        @ApiParam("oldAddress")
        @RequestParam(value = "oldAddress", required = false) String oldAddress,
        @ApiParam("newAddresses")
        @RequestParam(value = "newAddresses", required = false) String newAddresses
    ) {
        testElkUserNotificationService.sendNotaryNotification(
            notaryApplicationDocumentId,
            notaryDocumentId,
            "880118",
            ssoId,
            personDocumentId,
            notaryFullName,
            notaryPhones,
            eno,
            oldAddress,
            newAddresses,
            appointmentDateTime
        );
    }

    @ApiOperation(value = "880106")
    @PostMapping(value = "/880106")
    public void send880106(
        @ApiParam("notaryApplicationDocumentId")
        @RequestParam(value = "notaryApplicationDocumentId", required = false) String notaryApplicationDocumentId,
        @ApiParam("notaryDocumentId")
        @RequestParam(value = "notaryDocumentId", required = false) String notaryDocumentId,
        @ApiParam("ssoId")
        @RequestParam(value = "ssoId", required = false) String ssoId,
        @ApiParam("personDocumentId")
        @RequestParam(value = "personDocumentId", required = false) String personDocumentId,
        @ApiParam("appointmentDateTime")
        @RequestParam(value = "appointmentDateTime", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime appointmentDateTime,
        @ApiParam("notaryFullName")
        @RequestParam(value = "notaryFullName", required = false) String notaryFullName,
        @ApiParam("eno")
        @RequestParam(value = "eno", required = false) String eno,
        @ApiParam("notaryPhones")
        @RequestParam(value = "notaryPhones", required = false) String notaryPhones,
        @ApiParam("oldAddress")
        @RequestParam(value = "oldAddress", required = false) String oldAddress,
        @ApiParam("newAddresses")
        @RequestParam(value = "newAddresses", required = false) String newAddresses
    ) {
        testElkUserNotificationService.sendNotaryNotification(
            notaryApplicationDocumentId,
            notaryDocumentId,
            "880106",
            ssoId,
            personDocumentId,
            notaryFullName,
            notaryPhones,
            eno,
            oldAddress,
            newAddresses,
            appointmentDateTime
        );
    }

    @ApiOperation(value = "880112")
    @PostMapping(value = "/880112")
    public void send880112(
        @ApiParam("notaryApplicationDocumentId")
        @RequestParam(value = "notaryApplicationDocumentId", required = false) String notaryApplicationDocumentId,
        @ApiParam("notaryDocumentId")
        @RequestParam(value = "notaryDocumentId", required = false) String notaryDocumentId,
        @ApiParam("ssoId")
        @RequestParam(value = "ssoId", required = false) String ssoId,
        @ApiParam("personDocumentId")
        @RequestParam(value = "personDocumentId", required = false) String personDocumentId,
        @ApiParam("appointmentDateTime")
        @RequestParam(value = "appointmentDateTime", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime appointmentDateTime,
        @ApiParam("notaryFullName")
        @RequestParam(value = "notaryFullName", required = false) String notaryFullName,
        @ApiParam("eno")
        @RequestParam(value = "eno", required = false) String eno,
        @ApiParam("notaryPhones")
        @RequestParam(value = "notaryPhones", required = false) String notaryPhones,
        @ApiParam("oldAddress")
        @RequestParam(value = "oldAddress", required = false) String oldAddress,
        @ApiParam("newAddresses")
        @RequestParam(value = "newAddresses", required = false) String newAddresses
    ) {
        testElkUserNotificationService.sendNotaryNotification(
            notaryApplicationDocumentId,
            notaryDocumentId,
            "880112",
            ssoId,
            personDocumentId,
            notaryFullName,
            notaryPhones,
            eno,
            oldAddress,
            newAddresses,
            appointmentDateTime
        );
    }

    @ApiOperation(value = "880119")
    @PostMapping(value = "/880119")
    public void send880119(
        @ApiParam("notaryApplicationDocumentId")
        @RequestParam(value = "notaryApplicationDocumentId", required = false) String notaryApplicationDocumentId,
        @ApiParam("notaryDocumentId")
        @RequestParam(value = "notaryDocumentId", required = false) String notaryDocumentId,
        @ApiParam("ssoId")
        @RequestParam(value = "ssoId", required = false) String ssoId,
        @ApiParam("personDocumentId")
        @RequestParam(value = "personDocumentId", required = false) String personDocumentId,
        @ApiParam("appointmentDateTime")
        @RequestParam(value = "appointmentDateTime", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime appointmentDateTime,
        @ApiParam("notaryFullName")
        @RequestParam(value = "notaryFullName", required = false) String notaryFullName,
        @ApiParam("eno")
        @RequestParam(value = "eno", required = false) String eno,
        @ApiParam("notaryPhones")
        @RequestParam(value = "notaryPhones", required = false) String notaryPhones,
        @ApiParam("oldAddress")
        @RequestParam(value = "oldAddress", required = false) String oldAddress,
        @ApiParam("newAddresses")
        @RequestParam(value = "newAddresses", required = false) String newAddresses
    ) {
        testElkUserNotificationService.sendNotaryNotification(
            notaryApplicationDocumentId,
            notaryDocumentId,
            "880119",
            ssoId,
            personDocumentId,
            notaryFullName,
            notaryPhones,
            eno,
            oldAddress,
            newAddresses,
            appointmentDateTime
        );
    }

    @ApiOperation(value = "880107")
    @PostMapping(value = "/880107")
    public void send880107(
        @ApiParam("notaryApplicationDocumentId")
        @RequestParam(value = "notaryApplicationDocumentId", required = false) String notaryApplicationDocumentId,
        @ApiParam("notaryDocumentId")
        @RequestParam(value = "notaryDocumentId", required = false) String notaryDocumentId,
        @ApiParam("ssoId")
        @RequestParam(value = "ssoId", required = false) String ssoId,
        @ApiParam("personDocumentId")
        @RequestParam(value = "personDocumentId", required = false) String personDocumentId,
        @ApiParam("appointmentDateTime")
        @RequestParam(value = "appointmentDateTime", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime appointmentDateTime,
        @ApiParam("notaryFullName")
        @RequestParam(value = "notaryFullName", required = false) String notaryFullName,
        @ApiParam("eno")
        @RequestParam(value = "eno", required = false) String eno,
        @ApiParam("notaryPhones")
        @RequestParam(value = "notaryPhones", required = false) String notaryPhones,
        @ApiParam("oldAddress")
        @RequestParam(value = "oldAddress", required = false) String oldAddress,
        @ApiParam("newAddresses")
        @RequestParam(value = "newAddresses", required = false) String newAddresses
    ) {
        testElkUserNotificationService.sendNotaryNotification(
            notaryApplicationDocumentId,
            notaryDocumentId,
            "880107",
            ssoId,
            personDocumentId,
            notaryFullName,
            notaryPhones,
            eno,
            oldAddress,
            newAddresses,
            appointmentDateTime
        );
    }

    @ApiOperation(value = "880110")
    @PostMapping(value = "/880110")
    public void send880110(
        @ApiParam("notaryApplicationDocumentId")
        @RequestParam(value = "notaryApplicationDocumentId", required = false) String notaryApplicationDocumentId,
        @ApiParam("notaryDocumentId")
        @RequestParam(value = "notaryDocumentId", required = false) String notaryDocumentId,
        @ApiParam("ssoId")
        @RequestParam(value = "ssoId", required = false) String ssoId,
        @ApiParam("personDocumentId")
        @RequestParam(value = "personDocumentId", required = false) String personDocumentId,
        @ApiParam("appointmentDateTime")
        @RequestParam(value = "appointmentDateTime", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime appointmentDateTime,
        @ApiParam("notaryFullName")
        @RequestParam(value = "notaryFullName", required = false) String notaryFullName,
        @ApiParam("eno")
        @RequestParam(value = "eno", required = false) String eno,
        @ApiParam("notaryPhones")
        @RequestParam(value = "notaryPhones", required = false) String notaryPhones,
        @ApiParam("oldAddress")
        @RequestParam(value = "oldAddress", required = false) String oldAddress,
        @ApiParam("newAddresses")
        @RequestParam(value = "newAddresses", required = false) String newAddresses
    ) {
        testElkUserNotificationService.sendNotaryNotification(
            notaryApplicationDocumentId,
            notaryDocumentId,
            "880110",
            ssoId,
            personDocumentId,
            notaryFullName,
            notaryPhones,
            eno,
            oldAddress,
            newAddresses,
            appointmentDateTime
        );
    }

    @ApiOperation(value = "880117")
    @PostMapping(value = "/880117")
    public void send880117(
        @ApiParam("notaryApplicationDocumentId")
        @RequestParam(value = "notaryApplicationDocumentId", required = false) String notaryApplicationDocumentId,
        @ApiParam("notaryDocumentId")
        @RequestParam(value = "notaryDocumentId", required = false) String notaryDocumentId,
        @ApiParam("ssoId")
        @RequestParam(value = "ssoId", required = false) String ssoId,
        @ApiParam("personDocumentId")
        @RequestParam(value = "personDocumentId", required = false) String personDocumentId,
        @ApiParam("appointmentDateTime")
        @RequestParam(value = "appointmentDateTime", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime appointmentDateTime,
        @ApiParam("notaryFullName")
        @RequestParam(value = "notaryFullName", required = false) String notaryFullName,
        @ApiParam("eno")
        @RequestParam(value = "eno", required = false) String eno,
        @ApiParam("notaryPhones")
        @RequestParam(value = "notaryPhones", required = false) String notaryPhones,
        @ApiParam("oldAddress")
        @RequestParam(value = "oldAddress", required = false) String oldAddress,
        @ApiParam("newAddresses")
        @RequestParam(value = "newAddresses", required = false) String newAddresses
    ) {
        testElkUserNotificationService.sendNotaryNotification(
            notaryApplicationDocumentId,
            notaryDocumentId,
            "880117",
            ssoId,
            personDocumentId,
            notaryFullName,
            notaryPhones,
            eno,
            oldAddress,
            newAddresses,
            appointmentDateTime
        );
    }

    @ApiOperation(value = "880105")
    @PostMapping(value = "/880105")
    public void send880105(
        @ApiParam("notaryApplicationDocumentId")
        @RequestParam(value = "notaryApplicationDocumentId", required = false) String notaryApplicationDocumentId,
        @ApiParam("notaryDocumentId")
        @RequestParam(value = "notaryDocumentId", required = false) String notaryDocumentId,
        @ApiParam("ssoId")
        @RequestParam(value = "ssoId", required = false) String ssoId,
        @ApiParam("personDocumentId")
        @RequestParam(value = "personDocumentId", required = false) String personDocumentId,
        @ApiParam("appointmentDateTime")
        @RequestParam(value = "appointmentDateTime", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime appointmentDateTime,
        @ApiParam("notaryFullName")
        @RequestParam(value = "notaryFullName", required = false) String notaryFullName,
        @ApiParam("eno")
        @RequestParam(value = "eno", required = false) String eno,
        @ApiParam("notaryPhones")
        @RequestParam(value = "notaryPhones", required = false) String notaryPhones,
        @ApiParam("oldAddress")
        @RequestParam(value = "oldAddress", required = false) String oldAddress,
        @ApiParam("newAddresses")
        @RequestParam(value = "newAddresses", required = false) String newAddresses
    ) {
        testElkUserNotificationService.sendNotaryNotification(
            notaryApplicationDocumentId,
            notaryDocumentId,
            "880105",
            ssoId,
            personDocumentId,
            notaryFullName,
            notaryPhones,
            eno,
            oldAddress,
            newAddresses,
            appointmentDateTime
        );
    }

    @ApiOperation(value = "880114")
    @PostMapping(value = "/880114")
    public void send880114(
        @ApiParam("notaryApplicationDocumentId")
        @RequestParam(value = "notaryApplicationDocumentId", required = false) String notaryApplicationDocumentId,
        @ApiParam("notaryDocumentId")
        @RequestParam(value = "notaryDocumentId", required = false) String notaryDocumentId,
        @ApiParam("ssoId")
        @RequestParam(value = "ssoId", required = false) String ssoId,
        @ApiParam("personDocumentId")
        @RequestParam(value = "personDocumentId", required = false) String personDocumentId,
        @ApiParam("appointmentDateTime")
        @RequestParam(value = "appointmentDateTime", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime appointmentDateTime,
        @ApiParam("notaryFullName")
        @RequestParam(value = "notaryFullName", required = false) String notaryFullName,
        @ApiParam("eno")
        @RequestParam(value = "eno", required = false) String eno,
        @ApiParam("notaryPhones")
        @RequestParam(value = "notaryPhones", required = false) String notaryPhones,
        @ApiParam("oldAddress")
        @RequestParam(value = "oldAddress", required = false) String oldAddress,
        @ApiParam("newAddresses")
        @RequestParam(value = "newAddresses", required = false) String newAddresses
    ) {
        testElkUserNotificationService.sendNotaryNotification(
            notaryApplicationDocumentId,
            notaryDocumentId,
            "880114",
            ssoId,
            personDocumentId,
            notaryFullName,
            notaryPhones,
            eno,
            oldAddress,
            newAddresses,
            appointmentDateTime
        );
    }

    @ApiOperation(value = "880113")
    @PostMapping(value = "/880113")
    public void send880113(
        @ApiParam("notaryApplicationDocumentId")
        @RequestParam(value = "notaryApplicationDocumentId", required = false) String notaryApplicationDocumentId,
        @ApiParam("notaryDocumentId")
        @RequestParam(value = "notaryDocumentId", required = false) String notaryDocumentId,
        @ApiParam("ssoId")
        @RequestParam(value = "ssoId", required = false) String ssoId,
        @ApiParam("personDocumentId")
        @RequestParam(value = "personDocumentId", required = false) String personDocumentId,
        @ApiParam("appointmentDateTime")
        @RequestParam(value = "appointmentDateTime", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime appointmentDateTime,
        @ApiParam("notaryFullName")
        @RequestParam(value = "notaryFullName", required = false) String notaryFullName,
        @ApiParam("eno")
        @RequestParam(value = "eno", required = false) String eno,
        @ApiParam("notaryPhones")
        @RequestParam(value = "notaryPhones", required = false) String notaryPhones,
        @ApiParam("oldAddress")
        @RequestParam(value = "oldAddress", required = false) String oldAddress,
        @ApiParam("newAddresses")
        @RequestParam(value = "newAddresses", required = false) String newAddresses
    ) {
        testElkUserNotificationService.sendNotaryNotification(
            notaryApplicationDocumentId,
            notaryDocumentId,
            "880113",
            ssoId,
            personDocumentId,
            notaryFullName,
            notaryPhones,
            eno,
            oldAddress,
            newAddresses,
            appointmentDateTime
        );
    }
}
