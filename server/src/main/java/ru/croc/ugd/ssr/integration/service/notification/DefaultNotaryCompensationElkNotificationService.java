package ru.croc.ugd.ssr.integration.service.notification;

import static java.util.Optional.ofNullable;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.dto.notarycompensation.NotaryCompensationFlowStatus;
import ru.croc.ugd.ssr.integration.command.NotaryCompensationPublishReasonCommand;
import ru.croc.ugd.ssr.integration.service.NotaryCompensationEventPublisher;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.notaryCompensation.NotaryCompensationDocument;
import ru.croc.ugd.ssr.notarycompensation.NotaryCompensationData;
import ru.croc.ugd.ssr.service.PersonDocumentService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class DefaultNotaryCompensationElkNotificationService implements NotaryCompensationElkNotificationService {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private final NotaryCompensationEventPublisher notaryCompensationEventPublisher;
    private final ElkUserNotificationService elkUserNotificationService;
    private final PersonDocumentService personDocumentService;

    @Override
    public void sendStatus(final NotaryCompensationFlowStatus status, final NotaryCompensationDocument document) {
        final NotaryCompensationData notaryCompensationData = document.getDocument().getNotaryCompensationData();
        try {
            final NotaryCompensationPublishReasonCommand publishReasonCommand
                = createPublishReasonCommand(status, notaryCompensationData);
            notaryCompensationEventPublisher.publishStatus(publishReasonCommand);
            if (status.getEventCode() != null) {
                sendNotification(status, document);
            }
        } catch (Exception e) {
            log.warn(
                "Статус {} для заявки с eno {} не отправлен", status.getCode(), notaryCompensationData.getEno(), e
            );
        }
    }

    @Override
    public void sendStatus(final NotaryCompensationFlowStatus status, String eno) {
        try {
            final NotaryCompensationPublishReasonCommand publishReasonCommand =
                createPublishReasonCommand(status, eno);
            notaryCompensationEventPublisher.publishStatus(publishReasonCommand);
        } catch (Exception e) {
            log.warn("Статус {} для заявки с eno {} не отправлен", status.getCode(), eno, e);
        }
    }

    private NotaryCompensationPublishReasonCommand createPublishReasonCommand(
        final NotaryCompensationFlowStatus status, final String eno
    ) {
        final NotaryCompensationData notaryCompensationData = new NotaryCompensationData();
        notaryCompensationData.setEno(eno);
        return createPublishReasonCommand(status, notaryCompensationData);
    }

    private NotaryCompensationPublishReasonCommand createPublishReasonCommand(
        final NotaryCompensationFlowStatus status, final NotaryCompensationData compensationData
    ) {
        return NotaryCompensationPublishReasonCommand.builder()
            .notaryCompensation(compensationData)
            .responseReasonDate(LocalDateTime.now())
            .elkStatusText(createStatusText(status, compensationData))
            .status(status)
            .build();
    }

    private void sendNotification(
        final NotaryCompensationFlowStatus status,
        final NotaryCompensationDocument document
    ) {
        final NotaryCompensationData notaryCompensationData = document.getDocument().getNotaryCompensationData();
        if (notaryCompensationData == null || notaryCompensationData.getApplicant() == null) {
            log.info("Skipping DefaultNotaryCompensationElkNotificationService.sendNotification"
                + " as notary compensation or applicant is empty");
            return;
        }
        final String applicantId = notaryCompensationData.getApplicant().getPersonDocumentId();
        final PersonDocument requesterPersonDocument = personDocumentService.fetchDocument(applicantId);
        sendNotificationForAllOwners(status, notaryCompensationData, requesterPersonDocument);
    }

    private void sendNotificationForAllOwners(
        final NotaryCompensationFlowStatus status,
        final NotaryCompensationData notaryCompensationData,
        final PersonDocument requesterPersonDocument
    ) {
        final PersonType requesterPersonType = requesterPersonDocument.getDocument().getPersonData();
        final List<PersonDocument> ownerPersonDocuments = personDocumentService
            .getOtherFlatOwners(requesterPersonDocument);

        ownerPersonDocuments.forEach(ownerPersonDocument ->
            elkUserNotificationService.sendNotaryCompensationNotification(
                ownerPersonDocument,
                status,
                getExtraTemplateParams(
                    notaryCompensationData, requesterPersonType, ownerPersonDocument.getDocument().getPersonData()
                )
            )
        );
    }

    private Map<String, String> getExtraTemplateParams(
        final NotaryCompensationData notaryCompensation,
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

        extraParams.put("$eno", notaryCompensation.getEno());

        extraParams.put("$appointmentDate", getFormattedAppointmentDate(notaryCompensation));
        extraParams.put("$30DaysAfterAppointmentDate", getFormatted30DaysAfter(notaryCompensation));

        return extraParams;
    }

    private String getFormattedAppointmentDate(final NotaryCompensationData notaryCompensation) {
        return ofNullable(notaryCompensation.getApplicationDateTime())
            .map(e -> e.format(DATE_FORMATTER))
            .orElse("-");
    }

    private String getFormatted30DaysAfter(final NotaryCompensationData notaryCompensation) {
        return ofNullable(notaryCompensation.getApplicationDateTime())
            .map(e -> e.plusDays(30))
            .map(e -> e.format(DATE_FORMATTER))
            .orElse("-");
    }

    private String createStatusText(
        final NotaryCompensationFlowStatus status,
        final NotaryCompensationData compensationData
    ) {
        switch (status) {
            case MONEY_NOT_PAYED:
                return String.format(
                    status.getStatusText(),
                    compensationData.getMoneyIsNotPayedReason()
                );
            case REJECTED:
                return String.format(
                    status.getStatusText(),
                    compensationData.getServiceRejectReason()
                );
            default:
                return status.getStatusText();
        }
    }
}
