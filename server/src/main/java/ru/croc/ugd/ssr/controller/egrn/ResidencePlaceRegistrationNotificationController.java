package ru.croc.ugd.ssr.controller.egrn;

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
@RequestMapping("/residence-place-registration-notification")
public class ResidencePlaceRegistrationNotificationController {

    private final TestElkUserNotificationService testElkUserNotificationService;

    @ApiOperation(value = "Send 880144 for person")
    @PostMapping("/person/880144")
    public void send880144(
        @ApiParam("personDocumentId")
        @RequestParam(value = "personDocumentId") String personDocumentId
    ) {
        testElkUserNotificationService.sendResidencePlaceRegistrationNotification(personDocumentId);
    }

    @ApiOperation(value = "Send 880144 triggered by egrn flat request")
    @PostMapping("/egrn-flat-request/880144")
    public void send880144ByEgrn(
        @ApiParam("egrnFlatRequestDocumentId")
        @RequestParam(value = "egrnFlatRequestDocumentId") String egrnFlatRequestDocumentId
    ) {
        testElkUserNotificationService.sendResidencePlaceRegistrationNotificationByEgrn(egrnFlatRequestDocumentId);
    }

}
