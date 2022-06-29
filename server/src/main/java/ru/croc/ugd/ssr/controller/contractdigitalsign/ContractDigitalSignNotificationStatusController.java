package ru.croc.ugd.ssr.controller.contractdigitalsign;

import static java.util.Optional.of;
import static java.util.Optional.ofNullable;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.contractdigitalsign.ContractDigitalSignData;
import ru.croc.ugd.ssr.contractdigitalsign.Owner;
import ru.croc.ugd.ssr.dto.ElkUserNotificationDto;
import ru.croc.ugd.ssr.dto.contractdigitalsign.ContractDigitalSignNotificationStatus;
import ru.croc.ugd.ssr.integration.service.notification.ContractDigitalSignElkNotificationService;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.contractDigitalSign.ContractDigitalSignDocument;
import ru.croc.ugd.ssr.model.contractappointment.ContractAppointmentDocument;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.contractdigitalsign.ContractDigitalSignService;
import ru.croc.ugd.ssr.service.document.ContractAppointmentDocumentService;
import ru.croc.ugd.ssr.service.document.ContractDigitalSignDocumentService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/contract-digital-sign-notification-status")
public class ContractDigitalSignNotificationStatusController {

    private final PersonDocumentService personDocumentService;
    private final ContractDigitalSignDocumentService contractDigitalSignDocumentService;
    private final ContractAppointmentDocumentService contractAppointmentDocumentService;
    private final ContractDigitalSignElkNotificationService contractDigitalSignElkNotificationService;
    private final ContractDigitalSignService contractDigitalSignService;

    @ApiOperation(value = "1080.1")
    @PostMapping(value = "/1080.1")
    public void send10801(
        @ApiParam("contractDigitalSignDocumentId")
        @RequestParam String contractDigitalSignDocumentId,
        @ApiParam("ownerPersonDocumentId")
        @RequestParam(value = "ownerPersonDocumentId", required = false) String ownerPersonDocumentId,
        @ApiParam("signerPersonDocumentId")
        @RequestParam(value = "signerPersonDocumentId", required = false) String signerPersonDocumentId
    ) {
        sendNotificationStatus(
            ContractDigitalSignNotificationStatus.SIGNED_INCORRECTLY,
            contractDigitalSignDocumentId,
            ownerPersonDocumentId,
            signerPersonDocumentId
        );
    }

    @ApiOperation(value = "1080.2")
    @PostMapping(value = "/1080.2")
    public void send10802(
        @ApiParam("contractDigitalSignDocumentId")
        @RequestParam String contractDigitalSignDocumentId,
        @ApiParam("ownerPersonDocumentId")
        @RequestParam(value = "ownerPersonDocumentId", required = false) String ownerPersonDocumentId,
        @ApiParam("signerPersonDocumentId")
        @RequestParam(value = "signerPersonDocumentId", required = false) String signerPersonDocumentId
    ) {
        sendNotificationStatus(
            ContractDigitalSignNotificationStatus.SIGNED_INCORRECTLY_BY_OWNER,
            contractDigitalSignDocumentId,
            ownerPersonDocumentId,
            signerPersonDocumentId
        );
    }

    @ApiOperation(value = "1080.3")
    @PostMapping(value = "/1080.3")
    public void send10803(
        @ApiParam("contractDigitalSignDocumentId")
        @RequestParam String contractDigitalSignDocumentId,
        @ApiParam("ownerPersonDocumentId")
        @RequestParam(value = "ownerPersonDocumentId", required = false) String ownerPersonDocumentId,
        @ApiParam("signerPersonDocumentId")
        @RequestParam(value = "signerPersonDocumentId", required = false) String signerPersonDocumentId
    ) {
        sendNotificationStatus(
            ContractDigitalSignNotificationStatus.SIGNING_PERIOD_EXPIRED,
            contractDigitalSignDocumentId,
            ownerPersonDocumentId,
            signerPersonDocumentId
        );
    }

    @ApiOperation(value = "8021.X")
    @PostMapping(value = "/8021.X")
    public void send8021X(
        @ApiParam("contractDigitalSignDocumentId")
        @RequestParam String contractDigitalSignDocumentId,
        @ApiParam("ownerPersonDocumentId")
        @RequestParam(value = "ownerPersonDocumentId", required = false) String ownerPersonDocumentId,
        @ApiParam("signerPersonDocumentId")
        @RequestParam(value = "signerPersonDocumentId", required = false) String signerPersonDocumentId
    ) {
        sendNotificationStatus(
            ContractDigitalSignNotificationStatus.SIGNED_BY_OWNER,
            contractDigitalSignDocumentId,
            ownerPersonDocumentId,
            signerPersonDocumentId
        );
    }

    @ApiOperation(value = "1077")
    @PostMapping(value = "/1077")
    public void send1077(
        @ApiParam("contractDigitalSignDocumentId")
        @RequestParam String contractDigitalSignDocumentId,
        @ApiParam("ownerPersonDocumentId")
        @RequestParam(value = "ownerPersonDocumentId", required = false) String ownerPersonDocumentId,
        @ApiParam("signerPersonDocumentId")
        @RequestParam(value = "signerPersonDocumentId", required = false) String signerPersonDocumentId
    ) {
        sendNotificationStatus(
            ContractDigitalSignNotificationStatus.ACCEPTED,
            contractDigitalSignDocumentId,
            ownerPersonDocumentId,
            signerPersonDocumentId
        );
    }

    @ApiOperation(value = "8011")
    @PostMapping(value = "/8011")
    public void receive8011(
        @ApiParam("eno")
        @RequestParam String eno,
        @ApiParam("personDocumentId")
        @RequestParam(value = "personDocumentId") String personDocumentId
    ) {
        log.info("Получен подписанный договор: eno = {}", eno);
        final ElkUserNotificationDto elkUserNotificationDto = ElkUserNotificationDto.builder()
            .personDocumentId(personDocumentId)
            .eno(eno)
            .creationDateTime(LocalDateTime.now())
            .statusId(ContractDigitalSignNotificationStatus.SENT.getId())
            .build();
        contractDigitalSignService.addNotificationStatusToContractDigitalSign(elkUserNotificationDto);
    }


    private void sendNotificationStatus(
        final ContractDigitalSignNotificationStatus status,
        final String contractDigitalSignDocumentId,
        final String ownerPersonDocumentId,
        final String signerPersonDocumentId
    ) {
        final ContractDigitalSignDocument contractDigitalSignDocument =
            contractDigitalSignDocumentService.fetchDocument(contractDigitalSignDocumentId);
        final ContractAppointmentDocument contractAppointmentDocument =
            contractAppointmentDocumentService.fetchByContractDigitalSign(contractDigitalSignDocument);

        final PersonDocument signerPersonDocument = ofNullable(signerPersonDocumentId)
            .flatMap(personDocumentService::fetchById)
            .orElse(null);
        final List<Owner> owners = of(contractDigitalSignDocument.getDocument().getContractDigitalSignData())
            .map(ContractDigitalSignData::getOwners)
            .map(ContractDigitalSignData.Owners::getOwner)
            .orElse(Collections.emptyList());

        final Integer reasonCode = contractDigitalSignElkNotificationService.retrieveReasonCode(status, owners);
        final String notifiablePersonDocumentId = ofNullable(ownerPersonDocumentId).orElse(signerPersonDocumentId);
        owners.stream()
            .filter(owner -> Objects.equals(owner.getPersonDocumentId(), notifiablePersonDocumentId))
            .findFirst()
            .ifPresent(owner -> contractDigitalSignElkNotificationService.sendNotificationStatus(
                status,
                contractAppointmentDocument,
                owner,
                signerPersonDocument,
                reasonCode
            ));
    }
}
