package ru.croc.ugd.ssr.controller.flatappointment;

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
import ru.croc.ugd.ssr.dto.flatappointment.FlatAppointmentFlowStatus;
import ru.croc.ugd.ssr.flatappointment.FlatAppointmentData;
import ru.croc.ugd.ssr.integration.command.FlatAppointmentPublishReasonCommand;
import ru.croc.ugd.ssr.integration.service.FlatAppointmentEventPublisher;
import ru.croc.ugd.ssr.model.CipDocument;
import ru.croc.ugd.ssr.model.flatAppointment.FlatAppointmentDocument;
import ru.croc.ugd.ssr.service.ChedFileService;
import ru.croc.ugd.ssr.service.cip.CipService;
import ru.croc.ugd.ssr.service.flatappointment.FlatAppointmentService;
import ru.croc.ugd.ssr.utils.CipUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@AllArgsConstructor
@RequestMapping("/flat-appointment-status")
public class FlatAppointmentStatusController {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final FlatAppointmentService flatAppointmentService;
    private final FlatAppointmentEventPublisher flatAppointmentEventPublisher;
    private final CipService cipService;
    private final ChedFileService chedFileService;

    @ApiOperation(value = "1069")
    @PostMapping(value = "/1069")
    public void send1069(
        @RequestParam String eno
    ) {
        publishMessage(FlatAppointmentFlowStatus.CANCEL_BY_APPLICANT_REQUEST, eno, "", null);
    }

    @ApiOperation(value = "10190")
    @PostMapping(value = "/10190")
    public void send10190(
        @RequestParam String eno
    ) {
        publishMessage(FlatAppointmentFlowStatus.UNABLE_TO_CANCEL, eno, null, null);
    }

    @ApiOperation(value = "1090")
    @PostMapping(value = "/1090")
    public void send1090(
        @RequestParam String eno
    ) {
        publishMessage(FlatAppointmentFlowStatus.CANCELED_BY_APPLICANT, eno, "", null);
    }

    @ApiOperation(value = "1095")
    @PostMapping(value = "/1095")
    public void send1095(
        @RequestParam String eno,
        @ApiParam("cancelReason")
        @RequestParam(value = "cancelReason", required = false) String cancelReason
    ) {
        publishMessage(FlatAppointmentFlowStatus.CANCELED_BY_OPERATOR, eno, cancelReason, null);
    }

    @ApiOperation(value = "8021.1")
    @PostMapping(value = "/8021.1")
    public void send80211(
        @RequestParam String eno,
        @ApiParam("appointmentDateTime")
        @RequestParam(value = "appointmentDateTime", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime appointmentDateTime
    ) {
        publishMessage(FlatAppointmentFlowStatus.MOVED_BY_OPERATOR, eno, null, appointmentDateTime);
    }

    @ApiOperation(value = "1075")
    @PostMapping(value = "/1075")
    public void send1075(
        @RequestParam String eno
    ) {
        publishMessage(FlatAppointmentFlowStatus.INSPECTION_PERFORMED, eno, null, null);
    }

    @ApiOperation(value = "1080")
    @PostMapping(value = "/1080")
    public void send1080(
        @RequestParam String eno
    ) {
        publishMessage(FlatAppointmentFlowStatus.INSPECTION_NOT_PERFORMED, eno, null, null);
    }

    @ApiOperation(value = "103099")
    @PostMapping(value = "/103099")
    public void send103099(
        @RequestParam String eno
    ) {
        publishMessage(FlatAppointmentFlowStatus.TECHNICAL_CRASH_REGISTRATION, eno, null, null);
    }

    @ApiOperation(value = "116999")
    @PostMapping(value = "/116999")
    public void send116999(
        @RequestParam String eno
    ) {
        publishMessage(FlatAppointmentFlowStatus.TECHNICAL_CRASH_CANCELLATION_REJECTED, eno, null, null);
    }

    private FlatAppointmentData getFlatAppointmentDataByEno(final String eno) {
        final FlatAppointmentDocument flatAppointmentDocument = flatAppointmentService
            .fetchByEno(eno);
        return flatAppointmentDocument.getDocument().getFlatAppointmentData();
    }

    private void publishMessage(
        final FlatAppointmentFlowStatus status,
        final String eno,
        final String cancelReason,
        final LocalDateTime appointmentDateTime
    ) {
        final FlatAppointmentData flatAppointment = getFlatAppointmentDataByEno(eno);

        if (cancelReason != null) {
            if (cancelReason.length() != 0) {
                flatAppointment.setCancelReason(cancelReason);
            }
            flatAppointment.setCancelDate(LocalDate.now());
        }

        if (appointmentDateTime != null) {
            flatAppointment.setAppointmentDateTime(appointmentDateTime);
        }

        publishMessage(status, flatAppointment);
    }

    private void publishMessage(
        final FlatAppointmentFlowStatus status,
        final FlatAppointmentData flatAppointment
    ) {
        String statusText = createStatusText(status, flatAppointment);

        final FlatAppointmentPublishReasonCommand publishReasonCommand =
            FlatAppointmentPublishReasonCommand.builder()
                .flatAppointment(flatAppointment)
                .status(status)
                .elkStatusText(statusText)
                .responseReasonDate(LocalDateTime.now())
                .build();
        flatAppointmentEventPublisher.publishStatus(publishReasonCommand);
    }

    private String createStatusText(final FlatAppointmentFlowStatus status, final FlatAppointmentData flatAppointment) {
        switch (status) {
            case REGISTERED:
            case MOVED_BY_OPERATOR:
            case CANCELED_BY_APPLICANT:
            case CANCELED_BY_OPERATOR:
                return createComplexStatusText(status, flatAppointment);
            default:
                return status.getStatusText();
        }
    }

    private String createComplexStatusText(
        final FlatAppointmentFlowStatus status, final FlatAppointmentData flatAppointment
    ) {
        final String statusTemplate = status.getStatusText();

        final String eno = ofNullable(flatAppointment.getEno()).orElse("-");

        final String appointmentDate = getFormattedAppointmentDate(flatAppointment);
        final String appointmentTime = getFormattedAppointmentTime(flatAppointment);

        final String icsChedFileLink = ofNullable(flatAppointment.getIcsChedFileId())
            .flatMap(chedFileService::getChedFileLink)
            .orElse(null);

        final String cipAddress = ofNullable(flatAppointment.getCipId())
            .flatMap(cipService::fetchById)
            .map(CipDocument::getDocument)
            .map(Cip::getCipData)
            .map(CipUtils::getCipAddress)
            .orElse(null);

        switch (status) {
            case REGISTERED:
                return String.format(
                    statusTemplate,
                    appointmentDate,
                    appointmentTime,
                    cipAddress,
                    icsChedFileLink
                );
            case MOVED_BY_OPERATOR:
                return String.format(
                    statusTemplate,
                    appointmentDate,
                    appointmentTime,
                    cipAddress,
                    eno,
                    icsChedFileLink
                );
            case CANCELED_BY_APPLICANT:
            case CANCELED_BY_OPERATOR:
                return String.format(statusTemplate, eno);
            default:
                return statusTemplate;
        }
    }

    private String getFormattedAppointmentDate(final FlatAppointmentData flatAppointment) {
        return ofNullable(flatAppointment.getAppointmentDateTime())
            .map(e -> e.format(DATE_FORMATTER))
            .orElse("-");
    }

    private String getFormattedAppointmentTime(final FlatAppointmentData flatAppointment) {
        return ofNullable(flatAppointment.getAppointmentDateTime())
            .map(e -> e.format(TIME_FORMATTER))
            .orElse("-");
    }
}
