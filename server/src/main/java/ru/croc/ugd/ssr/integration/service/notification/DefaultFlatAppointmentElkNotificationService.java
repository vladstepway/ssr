package ru.croc.ugd.ssr.integration.service.notification;

import static java.util.Optional.ofNullable;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.Cip;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.dto.flatappointment.FlatAppointmentFlowStatus;
import ru.croc.ugd.ssr.flatappointment.FlatAppointmentData;
import ru.croc.ugd.ssr.integration.command.FlatAppointmentPublishReasonCommand;
import ru.croc.ugd.ssr.integration.service.FlatAppointmentEventPublisher;
import ru.croc.ugd.ssr.model.CipDocument;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.flatAppointment.FlatAppointmentDocument;
import ru.croc.ugd.ssr.service.ChedFileService;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.cip.CipService;
import ru.croc.ugd.ssr.utils.CipUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Сервис отправки сообщений в ЭЛК для записи на осмотр квартиры.
 */
@Service
@Slf4j
@AllArgsConstructor
public class DefaultFlatAppointmentElkNotificationService implements FlatAppointmentElkNotificationService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final FlatAppointmentEventPublisher flatAppointmentEventPublisher;
    private final CipService cipService;
    private final PersonDocumentService personDocumentService;
    private final ChedFileService chedFileService;
    private final ElkUserNotificationService elkUserNotificationService;

    @Override
    public void sendStatus(final FlatAppointmentFlowStatus status, final FlatAppointmentDocument document) {
        final FlatAppointmentPublishReasonCommand publishReasonCommand
            = createPublishReasonCommand(status, document.getDocument().getFlatAppointmentData());
        flatAppointmentEventPublisher.publishStatus(publishReasonCommand);
        if (status.getEventCode() != null) {
            sendNotification(status, document);
        }
    }

    @Override
    public void sendStatus(final FlatAppointmentFlowStatus status, final String eno) {
        final FlatAppointmentPublishReasonCommand publishReasonCommand =
            createPublishReasonCommand(status, eno);
        flatAppointmentEventPublisher.publishStatus(publishReasonCommand);
    }

    @Override
    public void sendToBk(final String message) {
        flatAppointmentEventPublisher.publishToBk(message);
    }

    @Override
    public void sendStatusToBk(final String message) {
        flatAppointmentEventPublisher.publishStatusToBk(message);
    }

    @Override
    public Map<String, String> getExtraTemplateParams(
        final FlatAppointmentData flatAppointment,
        final PersonType requesterPersonType,
        final PersonType ownerPersonType
    ) {
        final Map<String, String> extraParams = new HashMap<>();

        if (ownerPersonType != null) {
            extraParams.put("$ownerFio", ownerPersonType.getFIO());
        }
        if (requesterPersonType != null) {
            extraParams.put("$requesterFio", requesterPersonType.getFIO());
        }
        extraParams.put("$appointmentDate", getFormattedAppointmentDate(flatAppointment));
        extraParams.put("$appointmentTime", getFormattedAppointmentTime(flatAppointment));

        final String cipAddress = ofNullable(flatAppointment.getCipId())
            .flatMap(cipService::fetchById)
            .map(CipDocument::getDocument)
            .map(Cip::getCipData)
            .map(CipUtils::getCipAddress)
            .orElse(null);
        extraParams.put("$cipAddress", cipAddress);

        final String cancelReason = ofNullable(flatAppointment.getCancelReason()).orElse("");
        extraParams.put("$cancelReason", cancelReason);

        return extraParams;
    }

    private FlatAppointmentPublishReasonCommand createPublishReasonCommand(
        final FlatAppointmentFlowStatus status, final String eno
    ) {
        final FlatAppointmentData flatAppointment = new FlatAppointmentData();
        flatAppointment.setEno(eno);
        return createPublishReasonCommand(status, flatAppointment);
    }

    private FlatAppointmentPublishReasonCommand createPublishReasonCommand(
        final FlatAppointmentFlowStatus status, final FlatAppointmentData flatAppointment
    ) {
        return FlatAppointmentPublishReasonCommand.builder()
            .flatAppointment(flatAppointment)
            .responseReasonDate(LocalDateTime.now())
            .elkStatusText(createStatusText(status, flatAppointment))
            .status(status)
            .build();
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

    private void sendNotification(
        final FlatAppointmentFlowStatus status, final FlatAppointmentDocument document
    ) {
        final FlatAppointmentData flatAppointment = document.getDocument().getFlatAppointmentData();
        if (flatAppointment == null || flatAppointment.getApplicant() == null) {
            log.info("Skipping DefaultFlatAppointmentElkNotificationService.sendNotification"
                + " as flat appointment or applicant is empty");
            return;
        }
        final String applicantId = flatAppointment.getApplicant().getPersonDocumentId();
        final PersonDocument requesterPersonDocument = personDocumentService.fetchDocument(applicantId);
        sendNotificationForAllOwners(status, flatAppointment, requesterPersonDocument);
    }

    private void sendNotificationForAllOwners(
        final FlatAppointmentFlowStatus status,
        final FlatAppointmentData flatAppointment,
        final PersonDocument requesterPersonDocument
    ) {
        final PersonType requesterPersonType = requesterPersonDocument.getDocument().getPersonData();
        final List<PersonDocument> ownerPersonDocuments = personDocumentService
            .getOtherFlatOwners(requesterPersonDocument);

        ownerPersonDocuments.forEach(ownerPersonDocument ->
            elkUserNotificationService.sendFlatAppointmentNotification(
                ownerPersonDocument,
                status,
                getExtraTemplateParams(flatAppointment,
                    requesterPersonType,
                    ownerPersonDocument
                        .getDocument()
                        .getPersonData()))
        );
    }
}
