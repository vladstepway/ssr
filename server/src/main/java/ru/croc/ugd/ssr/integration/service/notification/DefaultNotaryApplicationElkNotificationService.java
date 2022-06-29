package ru.croc.ugd.ssr.integration.service.notification;

import static java.util.Optional.ofNullable;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.dto.notary.NotaryApplicationFlowStatus;
import ru.croc.ugd.ssr.enums.NotaryApplicationSource;
import ru.croc.ugd.ssr.integration.command.NotaryApplicationPublishReasonCommand;
import ru.croc.ugd.ssr.integration.service.NotaryApplicationEventPublisher;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.notary.NotaryApplicationDocument;
import ru.croc.ugd.ssr.model.notary.NotaryDocument;
import ru.croc.ugd.ssr.notary.Apartment;
import ru.croc.ugd.ssr.notary.Apartments;
import ru.croc.ugd.ssr.notary.Notary;
import ru.croc.ugd.ssr.notary.NotaryAddress;
import ru.croc.ugd.ssr.notary.NotaryApplicationHistoryEvent;
import ru.croc.ugd.ssr.notary.NotaryApplicationHistoryEvents;
import ru.croc.ugd.ssr.notary.NotaryApplicationType;
import ru.croc.ugd.ssr.notary.NotaryPhone;
import ru.croc.ugd.ssr.notary.NotaryType;
import ru.croc.ugd.ssr.service.ChedFileService;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.document.NotaryDocumentService;
import ru.croc.ugd.ssr.utils.StreamUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Сервис отправки сообщений в ЭЛК для записи на приём к натариусу.
 */
@Service
@Slf4j
@AllArgsConstructor
public class DefaultNotaryApplicationElkNotificationService implements NotaryApplicationElkNotificationService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final NotaryApplicationEventPublisher notaryApplicationEventPublisher;
    private final NotaryDocumentService notaryDocumentService;
    private final ElkUserNotificationService elkUserNotificationService;
    private final PersonDocumentService personDocumentService;
    private final ChedFileService chedFileService;

    @Override
    public void sendStatus(final NotaryApplicationFlowStatus status, final NotaryApplicationDocument document) {
        final NotaryApplicationType notaryApplicationData = document.getDocument().getNotaryApplicationData();
        final NotaryApplicationPublishReasonCommand publishReasonCommand
            = createPublishReasonCommand(status, notaryApplicationData);
        final boolean isMpgu =
            NotaryApplicationSource.MPGU.value().equals(notaryApplicationData.getSource());
        if (isMpgu) {
            notaryApplicationEventPublisher.publishStatus(publishReasonCommand);
        }
        if (status.getEventCode() != null) {
            sendNotification(status, document);
        }
    }

    @Override
    public void sendStatus(final NotaryApplicationFlowStatus status, final String eno) {
        final NotaryApplicationPublishReasonCommand publishReasonCommand =
            createPublishReasonCommand(status, eno);
        notaryApplicationEventPublisher.publishStatus(publishReasonCommand);
    }

    @Override
    public void sendToBk(final String message) {
        notaryApplicationEventPublisher.publishToBk(message);
    }

    @Override
    public void sendStatusToBk(final String message) {
        notaryApplicationEventPublisher.publishStatusToBk(message);
    }

    private String createStatusText(
        final NotaryApplicationFlowStatus status,
        NotaryApplicationType notaryApplicationType
    ) {
        final String statusTemplate = status.getStatusText();

        switch (status) {
            case REFUSE_TO_PROVIDE_SERVICE:
            case WAITING_DOCUMENTS:
            case DECLINED:
            case CANCELED_BY_NOTARY:
            case REGISTERED:
            case DRAFT_CONTRACT_READY:
            case RETURN_VISIT_REQUIRED:
            case APPOINTMENT_OPEN:
            case BOOKING_CLOSED:
            case DOCUMENTS_RECEIVED:
            case RECOLLECT_DOCUMENTS:
            case REFUSE_NO_BOOKING:
            case APPOINTMENT_REMINDER:
            case BOOKED:
            case CANCELED_BY_APPLICANT:
                return createComplexStatusText(status, notaryApplicationType);
            default:
                return statusTemplate;
        }
    }

    private String createComplexStatusText(
        final NotaryApplicationFlowStatus status,
        final NotaryApplicationType notaryApplicationType
    ) {
        final String statusTemplate = status.getStatusText();

        final String lastComment = retrieveLastComment(notaryApplicationType).orElse("");

        final NotaryType notary = notaryDocumentService
            .fetchById(notaryApplicationType.getNotaryId())
            .map(NotaryDocument::getDocument)
            .map(Notary::getNotaryData)
            .orElse(null);

        final String notaryPhone = getPhone(notary);
        final String notaryFullName = getFullName(notary);
        final String notaryAddress = getAddress(notary);

        final String appointmentDate = getFormattedAppointmentDate(notaryApplicationType);
        final String appointmentTime = getFormattedAppointmentTime(notaryApplicationType);
        final String dayBeforeAppointmentDate = getFormattedDayBefore(notaryApplicationType);

        final String notaryApplicationEno = notaryApplicationType.getEno();

        switch (status) {
            case REFUSE_TO_PROVIDE_SERVICE:
            case WAITING_DOCUMENTS:
            case DECLINED:
                return String.format(statusTemplate, lastComment);
            case CANCELED_BY_NOTARY:
                return String.format(
                    statusTemplate,
                    notaryApplicationEno,
                    lastComment
                );
            case REGISTERED:
                return String.format(
                    statusTemplate,
                    notaryFullName,
                    lastComment
                );
            case DRAFT_CONTRACT_READY:
                return String.format(
                    statusTemplate,
                    notaryFullName,
                    notaryPhone,
                    lastComment
                );
            case RETURN_VISIT_REQUIRED:
                return String.format(
                    statusTemplate,
                    notaryFullName,
                    notaryPhone
                );
            case APPOINTMENT_OPEN:
            case BOOKING_CLOSED:
                return String.format(
                    statusTemplate,
                    notaryFullName,
                    notaryFullName,
                    notaryPhone
                );
            case DOCUMENTS_RECEIVED:
            case RECOLLECT_DOCUMENTS:
            case REFUSE_NO_BOOKING:
                return String.format(
                    statusTemplate,
                    notaryFullName
                );
            case APPOINTMENT_REMINDER:
                return String.format(
                    statusTemplate,
                    notaryFullName,
                    notaryAddress,
                    appointmentDate,
                    appointmentTime,
                    dayBeforeAppointmentDate
                );
            case BOOKED:
                final String icsChedFileLink = ofNullable(notaryApplicationType.getIcsChedFileId())
                    .flatMap(chedFileService::getChedFileLink)
                    .orElse(null);

                return String.format(
                    statusTemplate,
                    notaryFullName,
                    appointmentDate,
                    appointmentTime,
                    notaryAddress,
                    appointmentTime,
                    dayBeforeAppointmentDate,
                    icsChedFileLink
                );
            case CANCELED_BY_APPLICANT:
                final Optional<LocalDateTime> maxStatusDateTime = retrieveMaxStatusDate(
                    notaryApplicationType,
                    Arrays.asList(
                        NotaryApplicationFlowStatus.DRAFT_CONTRACT_READY.getId(),
                        NotaryApplicationFlowStatus.APPOINTMENT_OPEN.getId()
                    )
                );
                final String formattedMaxStatusDate = maxStatusDateTime
                    .map(e -> e.plusDays(7))
                    .map(e -> e.format(DATE_FORMATTER))
                    .orElse("-");

                return String.format(
                    statusTemplate,
                    notaryApplicationEno,
                    formattedMaxStatusDate,
                    notaryFullName,
                    notaryPhone
                );
            default:
                return statusTemplate;
        }
    }

    private String getFormattedAppointmentDate(final NotaryApplicationType notaryApplicationType) {
        return extractAppointmentDate(notaryApplicationType)
            .map(e -> e.format(DATE_FORMATTER))
            .orElse("-");
    }

    private String getFormattedAppointmentTime(final NotaryApplicationType notaryApplicationType) {
        return extractAppointmentDate(notaryApplicationType)
            .map(e -> e.format(TIME_FORMATTER))
            .orElse("-");
    }

    private String getFormattedDayBefore(final NotaryApplicationType notaryApplicationType) {
        return extractAppointmentDate(notaryApplicationType)
            .map(e -> e.minusDays(1))
            .map(e -> e.format(DATE_FORMATTER))
            .orElse("-");
    }

    private String getFormatted30DaysAfter(final NotaryApplicationType notaryApplicationType) {
        return extractAppointmentDate(notaryApplicationType)
            .map(e -> e.plusDays(30))
            .map(e -> e.format(DATE_FORMATTER))
            .orElse("-");
    }

    private Optional<LocalDateTime> extractAppointmentDate(final NotaryApplicationType notaryApplicationType) {
        return StreamUtils.or(
            ofNullable(notaryApplicationType.getAppointmentDateTime()),
            () -> retrieveAppointmentDateFromHistory(notaryApplicationType)
        );
    }

    private Optional<LocalDateTime> retrieveMaxStatusDate(
        final NotaryApplicationType notaryApplicationType, final List<String> statusIdList
    ) {
        return getHistoryStream(notaryApplicationType)
            .filter(e -> statusIdList.contains(e.getEventId()))
            .map(NotaryApplicationHistoryEvent::getEventDate)
            .max(LocalDateTime::compareTo);
    }

    private String getPhone(final NotaryType notary) {
        return ofNullable(notary)
            .map(NotaryType::getPhones)
            .map(NotaryPhone::getPhone)
            .map(List::stream)
            .orElse(Stream.empty())
            .filter(StringUtils::hasText)
            .findAny()
            .orElse("-");
    }

    private String getFullName(final NotaryType notary) {
        return ofNullable(notary)
            .map(NotaryType::getFullName)
            .orElse("-");
    }

    private String getAddress(final NotaryType notary) {
        return ofNullable(notary)
            .map(NotaryType::getAddress)
            .map(NotaryAddress::getAddress)
            .orElse("-");
    }

    private Optional<String> retrieveLastComment(final NotaryApplicationType notaryApplicationType) {
        return getHistoryStream(notaryApplicationType)
            .max(Comparator.comparing(NotaryApplicationHistoryEvent::getEventDate))
            .map(NotaryApplicationHistoryEvent::getComment);
    }

    private Optional<LocalDateTime> retrieveAppointmentDateFromHistory(
        final NotaryApplicationType notaryApplicationType
    ) {
        return getHistoryStream(notaryApplicationType)
            .filter(notaryApplicationHistoryEvent ->
                Objects.equals(NotaryApplicationFlowStatus.BOOKED.getId(),
                    notaryApplicationHistoryEvent.getEventId()))
            .max(Comparator.comparing(NotaryApplicationHistoryEvent::getEventDate))
            .map(NotaryApplicationHistoryEvent::getAppointmentDateTime);
    }

    private Stream<NotaryApplicationHistoryEvent> getHistoryStream(final NotaryApplicationType notaryApplicationType) {
        return ofNullable(notaryApplicationType)
            .map(NotaryApplicationType::getHistory)
            .map(NotaryApplicationHistoryEvents::getHistory)
            .map(List::stream)
            .orElse(Stream.empty());
    }

    private void sendNotification(
        final NotaryApplicationFlowStatus status,
        final NotaryApplicationDocument document
    ) {
        final NotaryApplicationType notaryApplicationData = document.getDocument().getNotaryApplicationData();
        if (notaryApplicationData == null || notaryApplicationData.getApplicant() == null) {
            log.info("Skipping DefaultNotaryApplicationElkNotificationService.sendNotification"
                + " as notary application or applicant is empty");
            return;
        }
        final String applicantId = notaryApplicationData.getApplicant().getPersonId();
        final PersonDocument requesterPersonDocument = personDocumentService.fetchDocument(applicantId);
        sendNotificationForAllOwners(status, notaryApplicationData, requesterPersonDocument);
    }

    private void sendNotificationForAllOwners(
        final NotaryApplicationFlowStatus status,
        final NotaryApplicationType notaryApplicationData,
        final PersonDocument requesterPersonDocument
    ) {
        final boolean isMpgu = NotaryApplicationSource.MPGU.value().equals(notaryApplicationData.getSource());
        final PersonType requesterPersonType = requesterPersonDocument.getDocument().getPersonData();
        final NotaryApplicationFlowStatus notaryApplicationStatus = isMpgu
            ? getPortalStatusByRequesterStatus(status)
            : getOwnerStatusByRequesterStatus(status);
        final List<PersonDocument> ownerPersonDocuments = personDocumentService
            .getOtherFlatOwners(requesterPersonDocument);

        if (!isMpgu) {
            elkUserNotificationService.sendNotaryNotification(
                requesterPersonDocument,
                status,
                getExtraTemplateParams(notaryApplicationData,
                    requesterPersonType,
                    null, null));
        }
        if (notaryApplicationStatus != null) {
            ownerPersonDocuments.forEach(ownerPersonDocument ->
                elkUserNotificationService.sendNotaryNotification(
                    ownerPersonDocument,
                    notaryApplicationStatus,
                    getExtraTemplateParams(notaryApplicationData,
                        requesterPersonType,
                        ownerPersonDocument.getDocument().getPersonData(), null))
            );
        }
    }

    private NotaryApplicationFlowStatus getPortalStatusByRequesterStatus(
        final NotaryApplicationFlowStatus requesterStatus
    ) {
        return getStatusByPostfix(requesterStatus, "_PORTAL");
    }

    private NotaryApplicationFlowStatus getOwnerStatusByRequesterStatus(
        final NotaryApplicationFlowStatus requesterStatus
    ) {
        return getStatusByPostfix(requesterStatus, "_OWNER");
    }

    private NotaryApplicationFlowStatus getStatusByPostfix(
        final NotaryApplicationFlowStatus requesterStatus,
        final String postfix
    ) {
        try {
            return NotaryApplicationFlowStatus.valueOf(requesterStatus.name() + postfix);
        } catch (IllegalArgumentException ex) {
            log.warn("No available notification status for portal");
        }
        return null;
    }

    @Override
    public Map<String, String> getExtraTemplateParams(
        final NotaryApplicationType notaryApplicationData,
        final PersonType requesterPersonType,
        final PersonType ownerPersonType,
        final NotaryType notaryType
    ) {
        final Map<String, String> extraParams = new HashMap<>();
        NotaryType notary = notaryType;
        if (notaryType == null) {
            final NotaryDocument notaryDocument = notaryDocumentService
                .fetchDocument(notaryApplicationData.getNotaryId());
            notary = notaryDocument.getDocument().getNotaryData();
        }
        extraParams.put("$requestNumber", notaryApplicationData.getEno());
        extraParams.put("$notaryFio", notary.getFullName());
        if (notary.getPhones() != null) {
            extraParams.put("$notaryPhones", String.join(", ", notary.getPhones().getPhone()));
        }

        if (notary.getAddress() != null) {
            final NotaryAddress notaryAddress = notary.getAddress();
            String address = notaryAddress.getAddress();
            if (StringUtils.hasText(notaryAddress.getAdditionalInformation())) {
                address += ", " + notaryAddress.getAdditionalInformation();
            }
            extraParams.put("$address", address);
        }

        final Apartments apartmentsTo = notaryApplicationData.getApartmentTo();
        if (apartmentsTo != null) {
            final String newAddresses = apartmentsTo.getApartment()
                .stream()
                .filter(Objects::nonNull)
                .map(Apartment::getAddress)
                .collect(Collectors.joining("; "));
            extraParams.put("$addressTo", newAddresses);
        }
        populateHistoryDependantParams(notaryApplicationData, extraParams);

        if (ownerPersonType != null) {
            extraParams.put("$ownerFio", ownerPersonType.getFIO());
        }
        if (requesterPersonType != null) {
            extraParams.put("$requesterFio", requesterPersonType.getFIO());
        }
        extraParams.put("$appointmentDate", getFormattedAppointmentDate(notaryApplicationData));
        extraParams.put("$appointmentTime", getFormattedAppointmentTime(notaryApplicationData));
        extraParams.put("$dayBeforeAppointmentDate", getFormattedDayBefore(notaryApplicationData));
        extraParams.put("$30DaysAfterAppointmentDate", getFormatted30DaysAfter(notaryApplicationData));
        extraParams.put("$eno", notaryApplicationData.getEno());
        return extraParams;
    }

    private void populateHistoryDependantParams(
        final NotaryApplicationType notaryApplicationData,
        final Map<String, String> extraParams
    ) {
        retrieveLastComment(notaryApplicationData)
            .ifPresent(lastComment -> extraParams.put("$declineReason", lastComment));

        retrieveMaxStatusDate(
            notaryApplicationData,
            Arrays.asList(
                NotaryApplicationFlowStatus.APPOINTMENT_OPEN.getId(),
                NotaryApplicationFlowStatus.DRAFT_CONTRACT_READY.getId()
            )).map(e -> e.plusDays(7))
            .map(e -> e.format(DATE_FORMATTER))
            .ifPresent(maxStatusDate -> extraParams.put("$possibleAttemptDate", maxStatusDate));
    }

    private NotaryApplicationPublishReasonCommand createPublishReasonCommand(
        final NotaryApplicationFlowStatus status, final String eno
    ) {
        final NotaryApplicationType notaryApplicationType = new NotaryApplicationType();
        notaryApplicationType.setEno(eno);
        return createPublishReasonCommand(status, notaryApplicationType);
    }

    private NotaryApplicationPublishReasonCommand createPublishReasonCommand(
        final NotaryApplicationFlowStatus status, final NotaryApplicationType notaryApplicationType
    ) {
        return NotaryApplicationPublishReasonCommand.builder()
            .notaryApplication(notaryApplicationType)
            .responseReasonDate(LocalDateTime.now())
            .elkStatusText(createStatusText(status, notaryApplicationType))
            .status(status)
            .build();
    }
}
