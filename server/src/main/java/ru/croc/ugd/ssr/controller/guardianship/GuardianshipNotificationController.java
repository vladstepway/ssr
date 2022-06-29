package ru.croc.ugd.ssr.controller.guardianship;

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
@RequestMapping("/guardianship-notification")
public class GuardianshipNotificationController {

    private final TestElkUserNotificationService testElkUserNotificationService;

    @ApiOperation(value = "880102")
    @PostMapping(value = "/880102")
    public void send880102(
        @ApiParam("ssoId")
        @RequestParam(value = "id", required = false) String ssoId,
        @ApiParam("personId")
        @RequestParam(value = "personId", required = false) String personId,
        @ApiParam("declineReason")
        @RequestParam(value = "declineReason", required = false) String declineReason
    ) {
        testElkUserNotificationService.sendGuardianshipNotification(
            true,
            ssoId,
            personId,
            declineReason
        );
    }

    @ApiOperation(value = "880103")
    @PostMapping(value = "/880103")
    public void send880103(
        @ApiParam("ssoId")
        @RequestParam(value = "id", required = false) String ssoId,
        @ApiParam("personId")
        @RequestParam(value = "personId", required = false) String personId,
        @ApiParam("declineReason")
        @RequestParam(value = "declineReason", required = false) String declineReason
    ) {
        testElkUserNotificationService.sendGuardianshipNotification(
            false,
            ssoId,
            personId,
            declineReason
        );
    }
}
