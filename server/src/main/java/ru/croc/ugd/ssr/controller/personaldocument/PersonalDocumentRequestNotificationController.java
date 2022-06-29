package ru.croc.ugd.ssr.controller.personaldocument;

import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.integration.service.notification.TestElkUserNotificationService;

@RestController
@AllArgsConstructor
@RequestMapping("/personal-document-request-notification")
public class PersonalDocumentRequestNotificationController {

    private final TestElkUserNotificationService testElkUserNotificationService;

    @PostMapping("/880139")
    public void send880139(
        @ApiParam("personalDocumentRequestDocumentId")
        @RequestParam(value = "personalDocumentRequestDocumentId") String personalDocumentRequestDocumentId,
        @ApiParam("ssoId")
        @RequestParam(value = "ssoId", required = false) String ssoId,
        @ApiParam("personDocumentId")
        @RequestParam(value = "personDocumentId", required = false) String personDocumentId
    ) {
        testElkUserNotificationService.sendPersonalDocumentRequestNotification(
            ssoId, personDocumentId, personalDocumentRequestDocumentId
        );
    }
}
