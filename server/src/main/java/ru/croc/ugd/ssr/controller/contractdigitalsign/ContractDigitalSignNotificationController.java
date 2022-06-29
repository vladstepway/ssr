package ru.croc.ugd.ssr.controller.contractdigitalsign;

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
@RequestMapping("/contract-digital-sign-notification")
public class ContractDigitalSignNotificationController {

    private final TestElkUserNotificationService testElkUserNotificationService;

    @ApiOperation(value = "Send 880141 to owner")
    @PostMapping(value = "/owner/880141")
    public void send880141Owners(
        @ApiParam("ssoId")
        @RequestParam(value = "ssoId", required = false) String ssoId,
        @ApiParam("notifiablePersonDocumentId")
        @RequestParam(value = "notifiablePersonDocumentId", required = false) String notifiablePersonDocumentId,
        @ApiParam("requesterPersonDocumentId")
        @RequestParam(value = "requesterPersonDocumentId", required = false) String requesterPersonDocumentId,
        @ApiParam("contractDigitalSignDocumentId")
        @RequestParam(value = "contractDigitalSignDocumentId", required = false) String contractDigitalSignDocumentId
    ) {
        testElkUserNotificationService.sendContractDigitalSignRequestNotificationToOwner(
            ssoId, notifiablePersonDocumentId, requesterPersonDocumentId, contractDigitalSignDocumentId
        );
    }

    @ApiOperation(value = "Send 880141 to applicant")
    @PostMapping(value = "/applicant/880141")
    public void send880141Applicant(
        @ApiParam("ssoId")
        @RequestParam(value = "ssoId", required = false) String ssoId,
        @ApiParam("notifiablePersonDocumentId")
        @RequestParam(value = "notifiablePersonDocumentId", required = false) String notifiablePersonDocumentId,
        @ApiParam("contractDigitalSignDocumentId")
        @RequestParam(value = "contractDigitalSignDocumentId", required = false) String contractDigitalSignDocumentId
    ) {
        testElkUserNotificationService.sendContractDigitalSignRequestNotificationToApplicant(
            ssoId, notifiablePersonDocumentId, contractDigitalSignDocumentId
        );
    }

    @ApiOperation(value = "880131")
    @PostMapping(value = "/880131")
    public void send880131(
        @ApiParam("ssoId")
        @RequestParam(value = "ssoId", required = false) String ssoId,
        @ApiParam("notifiablePersonDocumentId")
        @RequestParam(value = "notifiablePersonDocumentId", required = false) String notifiablePersonDocumentId,
        @ApiParam("requesterPersonDocumentId")
        @RequestParam(value = "requesterPersonDocumentId", required = false) String requesterPersonDocumentId,
        @ApiParam("contractDigitalSignDocumentId")
        @RequestParam(value = "contractDigitalSignDocumentId", required = false) String contractDigitalSignDocumentId
    ) {
        testElkUserNotificationService.sendContractDigitalSignRequestNotification(
            ssoId, notifiablePersonDocumentId, requesterPersonDocumentId, contractDigitalSignDocumentId, "880131"
        );
    }

    @ApiOperation(value = "880132")
    @PostMapping(value = "/880132")
    public void send880132(
        @ApiParam("ssoId")
        @RequestParam(value = "ssoId", required = false) String ssoId,
        @ApiParam("notifiablePersonDocumentId")
        @RequestParam(value = "notifiablePersonDocumentId", required = false) String notifiablePersonDocumentId,
        @ApiParam("requesterPersonDocumentId")
        @RequestParam(value = "requesterPersonDocumentId", required = false) String requesterPersonDocumentId,
        @ApiParam("contractDigitalSignDocumentId")
        @RequestParam(value = "contractDigitalSignDocumentId", required = false) String contractDigitalSignDocumentId
    ) {
        testElkUserNotificationService.sendContractDigitalSignRequestNotification(
            ssoId, notifiablePersonDocumentId, requesterPersonDocumentId, contractDigitalSignDocumentId, "880132"
        );
    }

    @ApiOperation(value = "880134")
    @PostMapping(value = "/880134")
    public void send880134(
        @ApiParam("ssoId")
        @RequestParam(value = "ssoId", required = false) String ssoId,
        @ApiParam("notifiablePersonDocumentId")
        @RequestParam(value = "notifiablePersonDocumentId", required = false) String notifiablePersonDocumentId,
        @ApiParam("requesterPersonDocumentId")
        @RequestParam(value = "requesterPersonDocumentId", required = false) String requesterPersonDocumentId,
        @ApiParam("contractDigitalSignDocumentId")
        @RequestParam(value = "contractDigitalSignDocumentId", required = false) String contractDigitalSignDocumentId
    ) {
        testElkUserNotificationService.sendContractDigitalSignRequestNotification(
            ssoId, notifiablePersonDocumentId, requesterPersonDocumentId, contractDigitalSignDocumentId, "880134"
        );
    }

    @ApiOperation(value = "880135")
    @PostMapping(value = "/880135")
    public void send880135(
        @ApiParam("ssoId")
        @RequestParam(value = "ssoId", required = false) String ssoId,
        @ApiParam("notifiablePersonDocumentId")
        @RequestParam(value = "notifiablePersonDocumentId", required = false) String notifiablePersonDocumentId,
        @ApiParam("requesterPersonDocumentId")
        @RequestParam(value = "requesterPersonDocumentId", required = false) String requesterPersonDocumentId,
        @ApiParam("contractDigitalSignDocumentId")
        @RequestParam(value = "contractDigitalSignDocumentId", required = false) String contractDigitalSignDocumentId
    ) {
        testElkUserNotificationService.sendContractDigitalSignRequestNotification(
            ssoId, notifiablePersonDocumentId, requesterPersonDocumentId, contractDigitalSignDocumentId, "880135"
        );
    }

    @ApiOperation(value = "880114")
    @PostMapping(value = "/880114")
    public void send880114(
        @ApiParam("ssoId")
        @RequestParam(value = "ssoId", required = false) String ssoId,
        @ApiParam("notifiablePersonDocumentId")
        @RequestParam(value = "notifiablePersonDocumentId", required = false) String notifiablePersonDocumentId,
        @ApiParam("requesterPersonDocumentId")
        @RequestParam(value = "requesterPersonDocumentId", required = false) String requesterPersonDocumentId,
        @ApiParam("contractDigitalSignDocumentId")
        @RequestParam(value = "contractDigitalSignDocumentId", required = false) String contractDigitalSignDocumentId
    ) {
        testElkUserNotificationService.sendContractDigitalSignRequestNotification(
            ssoId, notifiablePersonDocumentId, requesterPersonDocumentId, contractDigitalSignDocumentId, "880114"
        );
    }

    @ApiOperation(value = "880147")
    @PostMapping(value = "/880147")
    public void send880147(
        @ApiParam("ssoId")
        @RequestParam(value = "ssoId", required = false) String ssoId,
        @ApiParam("notifiablePersonDocumentId")
        @RequestParam(value = "notifiablePersonDocumentId", required = false) String notifiablePersonDocumentId,
        @ApiParam("requesterPersonDocumentId")
        @RequestParam(value = "requesterPersonDocumentId", required = false) String requesterPersonDocumentId,
        @ApiParam("contractDigitalSignDocumentId")
        @RequestParam(value = "contractDigitalSignDocumentId", required = false) String contractDigitalSignDocumentId
    ) {
        testElkUserNotificationService.sendContractDigitalSignRequestNotification(
            ssoId, notifiablePersonDocumentId, requesterPersonDocumentId, contractDigitalSignDocumentId, "880147"
        );
    }
}
