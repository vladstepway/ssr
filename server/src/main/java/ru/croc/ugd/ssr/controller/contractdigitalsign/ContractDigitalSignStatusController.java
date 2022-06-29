package ru.croc.ugd.ssr.controller.contractdigitalsign;

import static java.util.Optional.ofNullable;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.contractappointment.ContractAppointmentData;
import ru.croc.ugd.ssr.contractappointment.ContractAppointmentFile;
import ru.croc.ugd.ssr.dto.contractdigitalsign.ContractDigitalSignFlowStatus;
import ru.croc.ugd.ssr.integration.command.ContractDigitalSignPublishReasonCommand;
import ru.croc.ugd.ssr.integration.service.ContractDigitalSignEventPublisher;
import ru.croc.ugd.ssr.model.contractappointment.ContractAppointmentDocument;
import ru.croc.ugd.ssr.service.ChedFileService;
import ru.croc.ugd.ssr.service.contractappointment.ContractAppointmentService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/contract-digital-sign-status")
public class ContractDigitalSignStatusController {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private final ContractAppointmentService contractAppointmentService;
    private final ContractDigitalSignEventPublisher contractDigitalSignEventPublisher;
    private final ChedFileService chedFileService;

    @ApiOperation(value = "1050")
    @PostMapping(value = "/1050")
    public void send1050(
        @RequestParam String eno,
        @ApiParam("appointmentDate")
        @RequestParam(value = "appointmentDate", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate appointmentDate
    ) {
        publishMessage(ContractDigitalSignFlowStatus.REGISTERED, eno, appointmentDate);
    }

    @ApiOperation(value = "1075")
    @PostMapping(value = "/1075")
    public void send1075(
        @RequestParam String eno,
        @ApiParam("appointmentDate")
        @RequestParam(value = "appointmentDate", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate appointmentDate
    ) {
        log.info("Договор c подписями сохранен в ЦХЭД");
        publishMessage(ContractDigitalSignFlowStatus.CONTRACT_SIGNED, eno, appointmentDate);
    }

    @ApiOperation(value = "1080.1")
    @PostMapping(value = "/1080.1")
    public void send10801(
        @RequestParam String eno,
        @ApiParam("appointmentDate")
        @RequestParam(value = "appointmentDate", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate appointmentDate
    ) {
        publishMessage(ContractDigitalSignFlowStatus.REFUSE_TO_PROVIDE_SERVICE_BY_APPLICANT, eno, appointmentDate);
    }

    @ApiOperation(value = "1080.2")
    @PostMapping(value = "/1080.2")
    public void send10802(
        @RequestParam String eno,
        @ApiParam("appointmentDate")
        @RequestParam(value = "appointmentDate", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate appointmentDate,
        @RequestParam String ownerFio
    ) {
        publishMessage(
            ContractDigitalSignFlowStatus.REFUSE_TO_PROVIDE_SERVICE_BY_OWNER, eno, appointmentDate, ownerFio
        );
    }

    @ApiOperation(value = "1080.3")
    @PostMapping(value = "/1080.3")
    public void send10803(
        @RequestParam String eno,
        @ApiParam("appointmentDate")
        @RequestParam(value = "appointmentDate", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate appointmentDate,
        @RequestParam String ownerFio
    ) {
        publishMessage(
            ContractDigitalSignFlowStatus.REFUSE_TO_PROVIDE_SERVICE_DUE_TO_OWNER, eno, appointmentDate, ownerFio
        );
    }

    @ApiOperation(value = "1080.4")
    @PostMapping(value = "/1080.4")
    public void send10804(
        @RequestParam String eno,
        @ApiParam("appointmentDate")
        @RequestParam(value = "appointmentDate", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate appointmentDate
    ) {
        publishMessage(ContractDigitalSignFlowStatus.REFUSE_TO_PROVIDE_SERVICE_DUE_TO_EMPLOYEE, eno, appointmentDate);
    }

    @ApiOperation(value = "1090")
    @PostMapping(value = "/1090")
    public void send1090(
        @RequestParam String eno,
        @ApiParam("appointmentDate")
        @RequestParam(value = "appointmentDate", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate appointmentDate
    ) {
        publishMessage(ContractDigitalSignFlowStatus.CANCELED_BY_APPLICANT, eno, appointmentDate);
    }

    @ApiOperation(value = "1095")
    @PostMapping(value = "/1095")
    public void send1095(
        @RequestParam String eno,
        @ApiParam("appointmentDate")
        @RequestParam(value = "appointmentDate", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate appointmentDate
    ) {
        publishMessage(ContractDigitalSignFlowStatus.CANCELED_BY_OPERATOR, eno, appointmentDate);
    }

    @ApiOperation(value = "8021.1")
    @PostMapping(value = "/8021.1")
    public void send80211(
        @RequestParam String eno,
        @ApiParam("appointmentDate")
        @RequestParam(value = "appointmentDate", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate appointmentDate
    ) {
        publishMessage(ContractDigitalSignFlowStatus.MOVED_BY_OPERATOR, eno, appointmentDate);
    }

    private void publishMessage(
        final ContractDigitalSignFlowStatus status,
        final String eno,
        final LocalDate appointmentDate
    ) {
        publishMessage(status, eno, appointmentDate, "");
    }

    private void publishMessage(
        final ContractDigitalSignFlowStatus status,
        final String eno,
        final LocalDate appointmentDate,
        final String ownerFio
    ) {
        final ContractAppointmentData contractAppointment = getContractAppointmentDataByEno(eno);

        if (appointmentDate != null) {
            contractAppointment.setAppointmentDateTime(appointmentDate.atStartOfDay());
        }

        publishMessage(status, contractAppointment, ownerFio);
    }

    private void publishMessage(
        final ContractDigitalSignFlowStatus status,
        final ContractAppointmentData contractAppointment,
        final String ownerFio
    ) {
        final String statusText = createStatusText(status, contractAppointment, ownerFio);

        final ContractDigitalSignPublishReasonCommand publishReasonCommand =
            ContractDigitalSignPublishReasonCommand.builder()
                .contractAppointment(contractAppointment)
                .status(status)
                .elkStatusText(statusText)
                .responseReasonDate(LocalDateTime.now())
                .build();
        contractDigitalSignEventPublisher.publishStatus(publishReasonCommand);
    }

    private ContractAppointmentData getContractAppointmentDataByEno(final String eno) {
        final ContractAppointmentDocument contractAppointmentDocument = contractAppointmentService.fetchByEno(eno);
        return contractAppointmentDocument.getDocument().getContractAppointmentData();
    }

    private String createStatusText(
        final ContractDigitalSignFlowStatus status,
        final ContractAppointmentData contractAppointment,
        final String ownerFio
    ) {
        switch (status) {
            case REGISTERED:
            case CONTRACT_SIGNED:
            case CANCELED_BY_APPLICANT:
            case CANCELED_BY_OPERATOR:
            case MOVED_BY_OPERATOR:
            case REFUSE_TO_PROVIDE_SERVICE_BY_OWNER:
            case REFUSE_TO_PROVIDE_SERVICE_DUE_TO_OWNER:
                return createComplexStatusText(status, contractAppointment, ownerFio);
            default:
                return status.getStatusText();
        }
    }

    private String createComplexStatusText(
        final ContractDigitalSignFlowStatus status,
        final ContractAppointmentData contractAppointment,
        final String ownerFio
    ) {
        final String statusTemplate = status.getStatusText();

        final String addressTo = ofNullable(contractAppointment.getAddressTo()).orElse("-");
        final String eno = ofNullable(contractAppointment.getEno()).orElse("-");

        final String appointmentDate = getFormattedAppointmentDate(contractAppointment);

        final String icsChedFileLink = ofNullable(contractAppointment.getIcsChedFileId())
            .flatMap(chedFileService::getChedFileLink)
            .orElse(null);

        final String cancellationReason = ofNullable(contractAppointment.getCancelReason()).orElse("-");

        final String contractLink = ofNullable(contractAppointment.getContractFile())
            .map(ContractAppointmentFile::getChedFileId)
            .flatMap(chedFileService::getChedFileLink)
            .orElse(null);

        switch (status) {
            case REGISTERED:
            case MOVED_BY_OPERATOR:
                return String.format(
                    statusTemplate,
                    addressTo,
                    appointmentDate,
                    icsChedFileLink
                );
            case CONTRACT_SIGNED:
                return String.format(statusTemplate, appointmentDate, contractLink);
            case CANCELED_BY_APPLICANT:
                return String.format(statusTemplate, eno, addressTo);
            case CANCELED_BY_OPERATOR:
                return String.format(
                    statusTemplate,
                    eno,
                    appointmentDate,
                    cancellationReason
                );
            case REFUSE_TO_PROVIDE_SERVICE_BY_OWNER:
                return String.format(
                    statusTemplate,
                    ownerFio
                );
            default:
                return statusTemplate;
        }
    }

    private String getFormattedAppointmentDate(final ContractAppointmentData contractAppointment) {
        return ofNullable(contractAppointment.getAppointmentDateTime())
            .map(e -> e.format(DATE_FORMATTER))
            .orElse("-");
    }
}
