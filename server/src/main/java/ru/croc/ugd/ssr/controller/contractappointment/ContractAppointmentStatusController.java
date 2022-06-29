package ru.croc.ugd.ssr.controller.contractappointment;

import static java.util.Optional.ofNullable;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.Cip;
import ru.croc.ugd.ssr.contractappointment.ContractAppointmentData;
import ru.croc.ugd.ssr.dto.contractappointment.ContractAppointmentFlowStatus;
import ru.croc.ugd.ssr.integration.command.ContractAppointmentPublishReasonCommand;
import ru.croc.ugd.ssr.integration.service.ContractAppointmentEventPublisher;
import ru.croc.ugd.ssr.integration.service.notification.ContractAppointmentElkNotificationService;
import ru.croc.ugd.ssr.model.CipDocument;
import ru.croc.ugd.ssr.model.contractappointment.ContractAppointmentDocument;
import ru.croc.ugd.ssr.service.ChedFileService;
import ru.croc.ugd.ssr.service.cip.CipService;
import ru.croc.ugd.ssr.service.contractappointment.ContractAppointmentService;
import ru.croc.ugd.ssr.utils.CipUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@AllArgsConstructor
@RequestMapping("/contract-appointment-status")
public class ContractAppointmentStatusController {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final ContractAppointmentService contractAppointmentService;
    private final ContractAppointmentEventPublisher contractAppointmentEventPublisher;
    private final CipService cipService;
    private final ChedFileService chedFileService;
    private final ContractAppointmentElkNotificationService contractAppointmentElkNotificationService;

    @ApiOperation(value = "1069")
    @PostMapping(value = "/1069")
    public void send1069(
        @RequestParam String eno
    ) {
        publishMessage(ContractAppointmentFlowStatus.CANCEL_BY_APPLICANT_REQUEST, eno, "", null);
    }

    @ApiOperation(value = "1075")
    @PostMapping(value = "/1075")
    public void send1075(
        @RequestParam String eno
    ) {
        publishMessage(ContractAppointmentFlowStatus.CONTRACT_SIGNED, eno, null, null);
    }

    @ApiOperation(value = "1080")
    @PostMapping(value = "/1080")
    public void send1080(
        @RequestParam String eno
    ) {
        publishMessage(ContractAppointmentFlowStatus.REFUSE_TO_PROVIDE_SERVICE, eno, null, null);
    }

    @ApiOperation(value = "1090")
    @PostMapping(value = "/1090")
    public void send1090(
        @RequestParam String eno
    ) {
        publishMessage(ContractAppointmentFlowStatus.CANCELED_BY_APPLICANT, eno, "", null);
    }

    @ApiOperation(value = "1095")
    @PostMapping(value = "/1095")
    public void send1095(
        @RequestParam String eno,
        @ApiParam("cancelReason")
        @RequestParam(value = "cancelReason", required = false) String cancelReason
    ) {
        publishMessage(ContractAppointmentFlowStatus.CANCELED_BY_OPERATOR, eno, cancelReason, null);
    }

    @ApiOperation(value = "10190")
    @PostMapping(value = "/10190")
    public void send10190(
        @RequestParam String eno
    ) {
        publishMessage(ContractAppointmentFlowStatus.UNABLE_TO_CANCEL, eno, null, null);
    }

    @ApiOperation(value = "8021.1")
    @PostMapping(value = "/8021.1")
    public void send80211(
        @RequestParam String eno,
        @ApiParam("appointmentDateTime")
        @RequestParam(value = "appointmentDateTime", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime appointmentDateTime
    ) {
        publishMessage(ContractAppointmentFlowStatus.MOVED_BY_OPERATOR, eno, null, appointmentDateTime);
    }

    @ApiOperation(value = "103099")
    @PostMapping(value = "/103099")
    public void send103099(
        @RequestParam String eno
    ) {
        publishMessage(ContractAppointmentFlowStatus.TECHNICAL_CRASH_REGISTRATION, eno, null, null);
    }

    @ApiOperation(value = "116999")
    @PostMapping(value = "/116999")
    public void send116999(
        @RequestParam String eno
    ) {
        publishMessage(ContractAppointmentFlowStatus.TECHNICAL_CRASH_CANCELLATION_REJECTED, eno, null, null);
    }

    private ContractAppointmentData getContractAppointmentDataByEno(final String eno) {
        final ContractAppointmentDocument contractAppointmentDocument = contractAppointmentService
            .fetchByEno(eno);
        return contractAppointmentDocument.getDocument().getContractAppointmentData();
    }

    private void publishMessage(
        final ContractAppointmentFlowStatus status,
        final String eno,
        final String cancelReason,
        final LocalDateTime appointmentDateTime
    ) {
        final ContractAppointmentData contractAppointment = getContractAppointmentDataByEno(eno);

        if (cancelReason != null) {
            if (cancelReason.length() != 0) {
                contractAppointment.setCancelReason(cancelReason);
            }
            contractAppointment.setCancelDate(LocalDate.now());
        }

        if (appointmentDateTime != null) {
            contractAppointment.setAppointmentDateTime(appointmentDateTime);
        }

        publishMessage(status, contractAppointment);
    }

    private void publishMessage(
        final ContractAppointmentFlowStatus status,
        final ContractAppointmentData contractAppointment
    ) {
        String statusText = createStatusText(status, contractAppointment);

        final ContractAppointmentPublishReasonCommand publishReasonCommand =
            ContractAppointmentPublishReasonCommand.builder()
                .contractAppointment(contractAppointment)
                .status(status)
                .elkStatusText(statusText)
                .responseReasonDate(LocalDateTime.now())
                .build();
        contractAppointmentEventPublisher.publishStatus(publishReasonCommand);
    }

    private String createStatusText(
        final ContractAppointmentFlowStatus status, final ContractAppointmentData contractAppointment
    ) {
        switch (status) {
            case REGISTERED:
            case MOVED_BY_OPERATOR:
            case CANCELED_BY_APPLICANT:
            case CANCELED_BY_OPERATOR:
            case CONTRACT_SIGNED:
                return createComplexStatusText(status, contractAppointment);
            default:
                return status.getStatusText();
        }
    }

    private String createComplexStatusText(
        final ContractAppointmentFlowStatus status, final ContractAppointmentData contractAppointment
    ) {
        final String statusTemplate = status.getStatusText();

        final String addressTo = ofNullable(contractAppointment.getAddressTo()).orElse("-");
        final String eno = ofNullable(contractAppointment.getEno()).orElse("-");

        final String appointmentDate = getFormattedAppointmentDate(contractAppointment);
        final String appointmentTime = getFormattedAppointmentTime(contractAppointment);

        final String cancellationReason = ofNullable(contractAppointment.getCancelReason()).orElse("-");

        final String icsChedFileLink = ofNullable(contractAppointment.getIcsChedFileId())
            .flatMap(chedFileService::getChedFileLink)
            .orElse(null);

        final String cipAddress = ofNullable(contractAppointment.getCipId())
            .flatMap(cipService::fetchById)
            .map(CipDocument::getDocument)
            .map(Cip::getCipData)
            .map(CipUtils::getCipAddress)
            .orElse(null);

        switch (status) {
            case REGISTERED:
                return String.format(
                    statusTemplate,
                    addressTo,
                    appointmentDate,
                    appointmentTime,
                    cipAddress,
                    icsChedFileLink
                );
            case MOVED_BY_OPERATOR:
                return String.format(
                    statusTemplate,
                    addressTo,
                    appointmentDate,
                    appointmentTime,
                    cipAddress,
                    eno,
                    icsChedFileLink
                );
            case CANCELED_BY_APPLICANT:
                return String.format(statusTemplate, eno, addressTo);
            case CANCELED_BY_OPERATOR:
                return String.format(
                    statusTemplate,
                    eno,
                    appointmentDate,
                    appointmentTime,
                    cancellationReason
                );
            case CONTRACT_SIGNED:
                return contractAppointmentElkNotificationService.retrieveContractSignedStatusText(
                    statusTemplate, contractAppointment
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

    private String getFormattedAppointmentTime(final ContractAppointmentData contractAppointment) {
        return ofNullable(contractAppointment.getAppointmentDateTime())
            .map(e -> e.format(TIME_FORMATTER))
            .orElse("-");
    }
}
