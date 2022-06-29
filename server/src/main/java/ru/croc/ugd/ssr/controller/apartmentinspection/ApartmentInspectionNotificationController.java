package ru.croc.ugd.ssr.controller.apartmentinspection;

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
@RequestMapping("/apartment-inspection-notification")
public class ApartmentInspectionNotificationController {
    private final TestElkUserNotificationService testElkUserNotificationService;

    @ApiOperation(value = "send")
    @PostMapping(value = "/send")
    public void sendApartmentInspectionNotification(
        @ApiParam("ssoId")
        @RequestParam(value = "id", defaultValue = "168ba8a0-ff62-4e2e-9b40-018bbde456b1") String ssoId,
        @ApiParam("personDocumentId")
        @RequestParam(value = "personDocumentId", required = false) String personDocumentId,
        @ApiParam("apartmentInspectionId")
        @RequestParam(value = "apartmentInspectionId", required = false) String apartmentInspectionId,
        @ApiParam("cipId")
        @RequestParam(value = "cipId", defaultValue = "c3464b61-141a-44dd-8918-ff3dc16977d5") String cipId
    ) {
        testElkUserNotificationService
            .sendApartmentInspectionNotification(ssoId, personDocumentId, cipId, apartmentInspectionId);
    }
}
