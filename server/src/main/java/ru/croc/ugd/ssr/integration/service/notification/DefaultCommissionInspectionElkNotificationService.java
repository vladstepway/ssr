package ru.croc.ugd.ssr.integration.service.notification;

import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.ApartmentInspection;
import ru.croc.ugd.ssr.ApartmentInspectionType;
import ru.croc.ugd.ssr.Cip;
import ru.croc.ugd.ssr.CipType;
import ru.croc.ugd.ssr.DelayReasonData;
import ru.croc.ugd.ssr.commission.CommissionInspectionData;
import ru.croc.ugd.ssr.commission.CommissionInspectionHistoryEvent;
import ru.croc.ugd.ssr.commissionorganisation.CommissionInspectionOrganisation;
import ru.croc.ugd.ssr.commissionorganisation.CommissionInspectionOrganisationData;
import ru.croc.ugd.ssr.dto.commissioninspection.CommissionInspectionConfig;
import ru.croc.ugd.ssr.dto.commissioninspection.CommissionInspectionFlowStatus;
import ru.croc.ugd.ssr.enums.CommissionInspectionOrganisationType;
import ru.croc.ugd.ssr.integration.command.CommissionInspectionPublishReasonCommand;
import ru.croc.ugd.ssr.integration.service.CommissionInspectionEventPublisher;
import ru.croc.ugd.ssr.model.ApartmentInspectionDocument;
import ru.croc.ugd.ssr.model.CipDocument;
import ru.croc.ugd.ssr.model.commissioninspection.CommissionInspectionDocument;
import ru.croc.ugd.ssr.model.commissionorganisation.CommissionInspectionOrganisationDocument;
import ru.croc.ugd.ssr.service.ApartmentInspectionService;
import ru.croc.ugd.ssr.service.ChedFileService;
import ru.croc.ugd.ssr.service.cip.CipService;
import ru.croc.ugd.ssr.service.commissioninspection.CommissionInspectionUtils;
import ru.croc.ugd.ssr.service.document.CommissionInspectionOrganisationDocumentService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

/**
 * Сервис отправки сообщений в ЭЛК для КО.
 */
@Async
@Slf4j
@Component
@AllArgsConstructor
public class DefaultCommissionInspectionElkNotificationService implements CommissionInspectionElkNotificationService {

    private static final String OLD_REGISTERED_STATUS_TEXT =
        "Заявка на устранение дефектов в квартире по Программе реновации зарегистрирована. "
            + "В течение трех рабочих дней сотрудник префектуры свяжется с вами для согласования даты"
            + " и времени комиссионного осмотра. Пожалуйста, согласуйте время осмотра в течение двух недель"
            + " с даты регистрации заявки.<br/>"
            + "До проведения комиссионного осмотра вы можете отозвать заявку либо внести изменения в перечень "
            + "дефектов.";

    private static final String OLD_MOVE_DATE_SECOND_VISIT_REJECTED_STATUS_TEXT =
        "Вы не можете перенести время осмотра более двух раз либо срок согласования осмотра превысил две недели "
            + "с даты получения уведомления о выполнении работ по Акту комиссионного осмотра о выявленных нарушениях. "
            + "Пожалуйста, свяжитесь с %s по телефону %s.";

    private static final String OLD_REFUSE_FLAT_TEXT =
        "Заявка на устранение дефектов в квартире по Программе реновации отозвана по инициативе заявителя.";

    private static final String OLD_DEFECTS_FOUND_TEXT =
        "По результатам проведенного осмотра квартиры %s, выявлены дефекты жилого помещения. "
            + "Ожидайте уведомление об устранении дефектов и приглашение на повторный осмотр жилого помещения. <br/>"
            + "<a href=\"%s\">Ссылка на акт</a>";

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final CommissionInspectionEventPublisher commissionInspectionEventPublisher;
    private final ApartmentInspectionService apartmentInspectionService;
    private final ChedFileService chedFileService;
    private final CommissionInspectionOrganisationDocumentService organisationDocumentService;
    private final CipService cipService;
    private final CommissionInspectionConfig commissionInspectionConfig;

    @Override
    public void sendStatusAsync(final CommissionInspectionFlowStatus status, final String eno) {
        final CommissionInspectionPublishReasonCommand publishReasonCommand = createPublishReasonCommand(status, eno);

        commissionInspectionEventPublisher.publishStatus(publishReasonCommand);
    }

    @Override
    public void sendStatusAsync(
        final CommissionInspectionFlowStatus status, final CommissionInspectionDocument document
    ) {
        final CommissionInspectionData commissionInspection = document
            .getDocument()
            .getCommissionInspectionData();
        final CommissionInspectionPublishReasonCommand publishReasonCommand =
            createPublishReasonCommand(status, commissionInspection);
        commissionInspectionEventPublisher.publishStatus(publishReasonCommand);
    }

    @Override
    public CompletableFuture<Void> sendStatusAsync(
        final List<CommissionInspectionFlowStatus> statuses, final CommissionInspectionDocument document
    ) {
        statuses.forEach(status -> sendStatusAsync(status, document));

        return CompletableFuture.completedFuture(null);
    }

    @Override
    public void sendToBk(final String message) {
        commissionInspectionEventPublisher.publishToBk(message);
    }

    @Override
    public void sendStatusToBk(final String message) {
        commissionInspectionEventPublisher.publishStatusToBk(message);
    }

    private CommissionInspectionPublishReasonCommand createPublishReasonCommand(
        final CommissionInspectionFlowStatus status, final String eno
    ) {
        final CommissionInspectionData commissionInspectionData = new CommissionInspectionData();
        commissionInspectionData.setEno(eno);

        return createPublishReasonCommand(status, commissionInspectionData);
    }

    private CommissionInspectionPublishReasonCommand createPublishReasonCommand(
        final CommissionInspectionFlowStatus status,
        final CommissionInspectionData commissionInspectionData
    ) {
        return CommissionInspectionPublishReasonCommand.builder()
            .commissionInspectionData(commissionInspectionData)
            .responseReasonDate(LocalDateTime.now())
            .status(status)
            .elkStatusText(createStatusText(status, commissionInspectionData))
            .build();
    }

    private String createStatusText(
        final CommissionInspectionFlowStatus status, final CommissionInspectionData commissionInspection
    ) {
        final String address = commissionInspection.getAddress()
            + ((commissionInspection.getFlatNum() != null)
            ? ", кв. " + commissionInspection.getFlatNum() : "");

        final LocalDateTime confirmedInspectionDateTime = commissionInspection.getConfirmedInspectionDateTime();
        final String confirmedInspectionDate = ofNullable(confirmedInspectionDateTime)
            .map(inspectionDateTime -> inspectionDateTime.format(DATE_FORMATTER))
            .orElse("-");
        final String confirmedInspectionTime = ofNullable(confirmedInspectionDateTime)
            .map(inspectionDateTime -> inspectionDateTime.format(TIME_FORMATTER))
            .orElse("-");
        final String registrationDate = Optional
            .of(commissionInspection)
            .map(ciData -> CommissionInspectionUtils
                .retrieveHistoryEventsByStatus(ciData, CommissionInspectionFlowStatus.REGISTERED))
            .map(List::stream)
            .orElse(Stream.empty())
            .findFirst()
            .map(CommissionInspectionHistoryEvent::getCreatedAt)
            .map(createdAt -> createdAt.plusDays(14))
            .map(startDate -> startDate.format(DATE_FORMATTER))
            .orElse("-");

        final Optional<ApartmentInspectionType> optionalApartmentInspection =
            getApartmentInspection(commissionInspection);

        final String signedActFileLink = optionalApartmentInspection
            .map(ApartmentInspectionType::getSignedActChedFileId)
            .flatMap(chedFileService::getChedFileLink)
            .orElse(null);

        final String acceptedDefectsActFileLink = optionalApartmentInspection
            .map(ApartmentInspectionType::getAcceptedDefectsActChedFileId)
            .flatMap(chedFileService::getChedFileLink)
            .orElse(null);

        final Optional<DelayReasonData> lastDelayReason = optionalApartmentInspection
            .map(ApartmentInspectionType::getDelayReason)
            .map(Collection::stream)
            .orElse(Stream.empty())
            .reduce((first, second) -> second);

        final String delayReasonText = lastDelayReason
            .map(DelayReasonData::getDelayReasonText)
            .orElse("-");
        final String delayReasonDate = lastDelayReason
            .map(DelayReasonData::getDelayDate)
            .map(dt -> dt.format(DATE_FORMATTER))
            .orElse("-");
        final String delayReasonDays = lastDelayReason
            .map(DelayReasonData::getDelayDate)
            .map(DefaultCommissionInspectionElkNotificationService::daysBefore)
            .orElse("-");

        final CipType cip = ofNullable(commissionInspection.getCipId())
            .map(cipService::fetchDocument)
            .map(CipDocument::getDocument)
            .map(Cip::getCipData)
            .orElse(null);

        final CommissionInspectionOrganisationData commissionInspectionOrganisation =
            ofNullable(commissionInspection.getOrganisationId())
                .map(organisationDocumentService::fetchDocument)
                .map(CommissionInspectionOrganisationDocument::getDocument)
                .map(CommissionInspectionOrganisation::getCommissionInspectionOrganisationData)
                .orElse(null);

        final String organisationName = nonNull(cip)
            ? "Центром информирования по переселению"
            : ofNullable(commissionInspectionOrganisation)
            .map(CommissionInspectionOrganisationData::getType)
            .map(CommissionInspectionOrganisationType::ofTypeValue)
            .map(CommissionInspectionOrganisationType::getElkValue)
            .map(elkValue -> elkValue + ofNullable(commissionInspectionOrganisation.getArea())
                .map(area -> " " + area)
                .orElse("")
            )
            .orElse(null);

        final String organisationPhone = ofNullable(cip)
            .map(CipType::getPhone)
            .orElseGet(() -> ofNullable(commissionInspectionOrganisation)
                .map(CommissionInspectionOrganisationData::getPhone)
                .orElse(null)
            );

        final String statusTest = retrieveStatusText(status);

        switch (status) {
            case TIME_CONFIRMATION_REQUIRED_FIRST_VISIT:
            case TIME_CONFIRMATION_REQUIRED_SECOND_VISIT:
                return String.format(statusTest, organisationName, organisationPhone, registrationDate);
            case TIME_CONFIRMED_FIRST_VISIT:
            case TIME_CONFIRMED_SECOND_VISIT:
                return String.format(statusTest, address, confirmedInspectionDate, confirmedInspectionTime);
            case MOVE_DATE_FIRST_VISIT_REJECTED:
            case MOVE_DATE_SECOND_VISIT_REJECTED:
                return String.format(statusTest, organisationName, organisationPhone);
            case DEFECTS_FOUND:
                return String.format(
                    statusTest,
                    confirmedInspectionDate,
                    signedActFileLink
                );
            case DEFECTS_NOT_FIXED:
                return String.format(
                    statusTest,
                    confirmedInspectionDate,
                    acceptedDefectsActFileLink
                );
            case FINISHED_NO_DEFECTS:
                return String.format(
                    statusTest,
                    signedActFileLink
                );
            case FINISHED_DEFECTS_FIXED:
                return String.format(
                    statusTest,
                    acceptedDefectsActFileLink
                );
            case DEFECTS_ELIMINATION_DATE_CALCULATED:
                return String.format(
                    statusTest,
                    delayReasonDays
                );
            case PROLONGATION:
                return String.format(
                    statusTest,
                    delayReasonText, delayReasonDate
                );
            default:
                return statusTest;
        }
    }

    private String retrieveStatusText(final CommissionInspectionFlowStatus status) {
        if (!commissionInspectionConfig.isCommissionInspectionModernizationEnabled()) {
            switch (status) {
                case REGISTERED:
                    return OLD_REGISTERED_STATUS_TEXT;
                case MOVE_DATE_SECOND_VISIT_REJECTED:
                    return OLD_MOVE_DATE_SECOND_VISIT_REJECTED_STATUS_TEXT;
                case REFUSE_FLAT:
                    return OLD_REFUSE_FLAT_TEXT;
                case DEFECTS_FOUND:
                    return OLD_DEFECTS_FOUND_TEXT;
                case PROLONGATION:
                    return null;
                default:
                    break;
            }
        }
        return status.getStatusText();
    }

    private Optional<ApartmentInspectionType> getApartmentInspection(
        final CommissionInspectionData commissionInspection
    ) {
        return ofNullable(commissionInspection.getCurrentApartmentInspectionId())
            .map(apartmentInspectionService::fetchDocument)
            .map(ApartmentInspectionDocument::getDocument)
            .map(ApartmentInspection::getApartmentInspectionData);
    }

    private static String daysBefore(final LocalDateTime dt) {
        final LocalDate dateTo = dt.toLocalDate();
        long days = ChronoUnit.DAYS.between(LocalDate.now(), dateTo);
        if (days < 0) {
            days = 0;
        }

        final long lastDigit = days % 10;
        String suffix;
        if (days >= 11 && days <= 19 || lastDigit == 0 || lastDigit >= 5) {
            suffix = "дней";
        } else if (lastDigit == 1) {
            suffix = "день";
        } else {
            suffix = "дня";
        }
        return String.format("%s %s", days, suffix);
    }
}
