package ru.croc.ugd.ssr.controller.shipping;

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
@RequestMapping("/shipping-notification")
public class ShippingNotificationController {

    private final TestElkUserNotificationService testElkUserNotificationService;

    @ApiOperation(value = "880069")
    @PostMapping(value = "/880069")
    public void send880069(
        @ApiParam("shippingDocumentId")
        @RequestParam(value = "shippingDocumentId", required = false) String shippingDocumentId,
        @ApiParam("ssoId")
        @RequestParam(value = "ssoId", required = false) String ssoId,
        @ApiParam("personDocumentId")
        @RequestParam(value = "personDocumentId", required = false) String personDocumentId,
        @ApiParam("moveDateTime")
        @RequestParam(value = "moveDateTime", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime moveDateTime,
        @ApiParam("oldAddress")
        @RequestParam(value = "oldAddress", required = false) String oldAddress,
        @ApiParam("newAddress")
        @RequestParam(value = "newAddress", required = false) String newAddress
    ) {
        testElkUserNotificationService.sendShippingNotification(
            shippingDocumentId,
            "880069",
            ssoId,
            personDocumentId,
            moveDateTime,
            oldAddress,
            newAddress
        );
    }

    @ApiOperation(value = "880070")
    @PostMapping(value = "/880070")
    public void send880070(
        @ApiParam("shippingDocumentId")
        @RequestParam(value = "shippingDocumentId", required = false) String shippingDocumentId,
        @ApiParam("ssoId")
        @RequestParam(value = "ssoId", required = false) String ssoId,
        @ApiParam("personDocumentId")
        @RequestParam(value = "personDocumentId", required = false) String personDocumentId,
        @RequestParam(value = "moveDateTime", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime moveDateTime,
        @ApiParam("oldAddress")
        @RequestParam(value = "oldAddress", required = false) String oldAddress,
        @ApiParam("newAddress")
        @RequestParam(value = "newAddress", required = false) String newAddress
    ) {
        testElkUserNotificationService.sendShippingNotification(
            shippingDocumentId,
            "880070",
            ssoId,
            personDocumentId,
            moveDateTime,
            oldAddress,
            newAddress
        );
    }

    @ApiOperation(value = "880072")
    @PostMapping(value = "/880072")
    public void send880072(
        @ApiParam("shippingDocumentId")
        @RequestParam(value = "shippingDocumentId", required = false) String shippingDocumentId,
        @ApiParam("ssoId")
        @RequestParam(value = "ssoId", required = false) String ssoId,
        @ApiParam("personDocumentId")
        @RequestParam(value = "personDocumentId", required = false) String personDocumentId,
        @RequestParam(value = "moveDateTime", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime moveDateTime,
        @ApiParam("oldAddress")
        @RequestParam(value = "oldAddress", required = false) String oldAddress,
        @ApiParam("newAddress")
        @RequestParam(value = "newAddress", required = false) String newAddress
    ) {
        testElkUserNotificationService.sendShippingNotification(
            shippingDocumentId,
            "880072",
            ssoId,
            personDocumentId,
            moveDateTime,
            oldAddress,
            newAddress
        );
    }

    @ApiOperation(value = "880071")
    @PostMapping(value = "/880071")
    public void send880071(
        @ApiParam("shippingDocumentId")
        @RequestParam(value = "shippingDocumentId", required = false) String shippingDocumentId,
        @ApiParam("ssoId")
        @RequestParam(value = "ssoId", required = false) String ssoId,
        @ApiParam("personDocumentId")
        @RequestParam(value = "personDocumentId", required = false) String personDocumentId,
        @RequestParam(value = "moveDateTime", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime moveDateTime,
        @ApiParam("oldAddress")
        @RequestParam(value = "oldAddress", required = false) String oldAddress,
        @ApiParam("newAddress")
        @RequestParam(value = "newAddress", required = false) String newAddress
    ) {
        testElkUserNotificationService.sendShippingNotification(
            shippingDocumentId,
            "880071",
            ssoId,
            personDocumentId,
            moveDateTime,
            oldAddress,
            newAddress
        );
    }
}
