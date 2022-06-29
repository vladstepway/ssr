package ru.croc.ugd.ssr.scheduler.commissioninspection;

import static java.util.Optional.of;
import static ru.croc.ugd.ssr.dto.commissioninspection.CommissionInspectionFlowStatus.DEFECTS_FIXED;
import static ru.croc.ugd.ssr.dto.commissioninspection.CommissionInspectionFlowStatus.DEFECTS_FOUND;
import static ru.croc.ugd.ssr.dto.commissioninspection.CommissionInspectionFlowStatus.FINISHED_NO_CALL;
import static ru.croc.ugd.ssr.dto.commissioninspection.CommissionInspectionFlowStatus.MOVE_DATE_FIRST_VISIT_REQUEST;
import static ru.croc.ugd.ssr.dto.commissioninspection.CommissionInspectionFlowStatus.MOVE_DATE_SECOND_VISIT_REQUEST;
import static ru.croc.ugd.ssr.dto.commissioninspection.CommissionInspectionFlowStatus.PROLONGATION;
import static ru.croc.ugd.ssr.dto.commissioninspection.CommissionInspectionFlowStatus.REFUSE_NO_CALL;
import static ru.croc.ugd.ssr.dto.commissioninspection.CommissionInspectionFlowStatus.REGISTERED;
import static ru.croc.ugd.ssr.dto.commissioninspection.CommissionInspectionFlowStatus.TIME_CONFIRMATION_REQUIRED_FIRST_VISIT;
import static ru.croc.ugd.ssr.dto.commissioninspection.CommissionInspectionFlowStatus.TIME_CONFIRMATION_REQUIRED_SECOND_VISIT;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.commission.CommissionInspection;
import ru.croc.ugd.ssr.commission.CommissionInspectionData;
import ru.croc.ugd.ssr.commission.CommissionInspectionHistoryEvent;
import ru.croc.ugd.ssr.commission.CommissionInspectionHistoryEvents;
import ru.croc.ugd.ssr.dto.commissioninspection.CommissionInspectionConfig;
import ru.croc.ugd.ssr.dto.commissioninspection.CommissionInspectionFlowStatus;
import ru.croc.ugd.ssr.model.commissioninspection.CommissionInspectionDocument;
import ru.croc.ugd.ssr.service.commissioninspection.CommissionInspectionService;
import ru.croc.ugd.ssr.service.document.CommissionInspectionDocumentService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * CommissionInspectionScheduler.
 */
@Slf4j
@Component
@AllArgsConstructor
public class CommissionInspectionScheduler {

    private static final List<CommissionInspectionFlowStatus> PROLONGATION_SCHEDULER_TRIGGER = Collections
        .singletonList(DEFECTS_FOUND);

    private static final List<CommissionInspectionFlowStatus> FIRST_VISIT_SCHEDULER_TRIGGER = Arrays.asList(
        REGISTERED,
        MOVE_DATE_FIRST_VISIT_REQUEST
    );

    private static final List<CommissionInspectionFlowStatus> SECOND_VISIT_SCHEDULER_TRIGGER = Arrays.asList(
        DEFECTS_FIXED,
        MOVE_DATE_SECOND_VISIT_REQUEST
    );

    private final CommissionInspectionDocumentService commissionInspectionDocumentService;
    private final CommissionInspectionService commissionInspectionService;
    private final CommissionInspectionSchedulerConfig schedulerConfig;
    private final CommissionInspectionConfig commissionInspectionConfig;

    /**
     * Send prolongation status.
     */
    @Scheduled(cron = "${schedulers.commission-inspection.prolongation-cron:0 0 2 * * ?}")
    public void sendProlongation() {
        if (!commissionInspectionConfig.isCommissionInspectionModernizationEnabled()) {
            log.debug("Start prolongation job");

            sendProlongation(schedulerConfig.getDaysBeforeProlongation());

            log.debug("Finish prolongation job");
        }
    }

    /**
     * Send prolongation status.
     * @param daysBeforeProlongation daysBeforeProlongation
     */
    public void sendProlongation(final int daysBeforeProlongation) {
        sendStatusFilterByDaysSinceLastEvent(
            PROLONGATION_SCHEDULER_TRIGGER,
            event -> true,
            days -> days == daysBeforeProlongation,
            document -> this.sendFlowStatusWithHistoryUpdate(document, PROLONGATION)
        );
    }

    /**
     * Send first visit confirmation required.
     */
    @Scheduled(cron = "${schedulers.commission-inspection.first-visit-confirmation-cron:0 0 3 * * ?}")
    public void sendFirstVisitConfirmationRequired() {
        log.debug("Start first visit confirmation required job");

        sendFirstVisitConfirmationRequired(schedulerConfig.getDaysToFirstVisitConfirmation());

        log.debug("Finish first visit confirmation required job");
    }

    /**
     * Send first visit confirmation required.
     * @param daysToFirstVisitConfirmation daysToFirstVisitConfirmation
     */
    public void sendFirstVisitConfirmationRequired(final int daysToFirstVisitConfirmation) {
        sendStatusFilterByDaysSinceLastEvent(
            FIRST_VISIT_SCHEDULER_TRIGGER,
            event -> !TIME_CONFIRMATION_REQUIRED_FIRST_VISIT.getId().equals(event.getEventId()),
            days -> days == daysToFirstVisitConfirmation,
            document -> this.sendFlowStatusWithHistoryUpdate(document, TIME_CONFIRMATION_REQUIRED_FIRST_VISIT)
        );
    }

    /**
     * Send second visit confirmation required.
     */
    @Scheduled(cron = "${schedulers.commission-inspection.second-visit-confirmation-cron:0 0 4 * * ?}")
    public void sendSecondVisitConfirmationRequired() {
        log.debug("Start second visit confirmation required job");

        sendSecondVisitConfirmationRequired(schedulerConfig.getDaysToSecondVisitConfirmation());

        log.debug("Finish second visit confirmation required job");
    }

    /**
     * Send second visit confirmation required.
     * @param daysToSecondVisitConfirmation daysToSecondVisitConfirmation
     */
    public void sendSecondVisitConfirmationRequired(final int daysToSecondVisitConfirmation) {
        sendStatusFilterByDaysSinceLastEvent(
            SECOND_VISIT_SCHEDULER_TRIGGER,
            event -> !TIME_CONFIRMATION_REQUIRED_SECOND_VISIT.getId().equals(event.getEventId()),
            days -> days == daysToSecondVisitConfirmation,
            document -> this.sendFlowStatusWithHistoryUpdate(document, TIME_CONFIRMATION_REQUIRED_SECOND_VISIT)
        );
    }

    /**
     * Send refuse no call.
     */
    @Scheduled(cron = "${schedulers.commission-inspection.refuse-no-call-cron:0 0 5 * * ?}")
    public void sendRefuseNoCall() {
        log.debug("Start refuse no call job");

        sendRefuseNoCall(schedulerConfig.getDaysToRefuseNoCall());

        log.debug("Finish refuse no call job");
    }

    /**
     * Send refuse no call.
     * @param daysToRefuseNoCall daysToRefuseNoCall
     */
    public void sendRefuseNoCall(final int daysToRefuseNoCall) {
        sendStatusFilterByDaysSinceLastEvent(
            FIRST_VISIT_SCHEDULER_TRIGGER,
            event -> TIME_CONFIRMATION_REQUIRED_FIRST_VISIT.getId().equals(event.getEventId()),
            days -> days == daysToRefuseNoCall,
            document -> this.processNoCall(document, REFUSE_NO_CALL)
        );
    }

    /**
     * Send finish no call.
     */
    @Scheduled(cron = "${schedulers.commission-inspection.finish-no-call-cron:0 0 6 * * ?}")
    public void sendFinishNoCall() {
        log.debug("Start finish no call job");

        sendFinishNoCall(schedulerConfig.getDaysToFinishNoCall());

        log.debug("Finish finish no call job");
    }

    /**
     * Send finish no call.
     * @param daysToFinishNoCall daysToFinishNoCall
     */
    public void sendFinishNoCall(final int daysToFinishNoCall) {
        sendStatusFilterByDaysSinceLastEvent(
            SECOND_VISIT_SCHEDULER_TRIGGER,
            event -> TIME_CONFIRMATION_REQUIRED_SECOND_VISIT.getId().equals(event.getEventId()),
            days -> days == daysToFinishNoCall,
            document -> this.processNoCall(document, FINISHED_NO_CALL)
        );
    }

    private void processNoCall(
        final CommissionInspectionDocument document, final CommissionInspectionFlowStatus noCallStatus
    ) {
        try {
            log.info("Send {}: commissionInspectionId: {}", noCallStatus, document.getId());
            commissionInspectionService.processNoCall(document, noCallStatus);
        } catch (Exception e) {
            log.error("Fail to send {}: commissionInspectionId: {}", noCallStatus, document.getId(), e);
        }
    }

    private void sendFlowStatusWithHistoryUpdate(
        final CommissionInspectionDocument document, final CommissionInspectionFlowStatus status
    ) {
        try {
            log.info("Send {}: commissionInspectionId: {}", status, document.getId());
            commissionInspectionService.sendFlowStatusWithHistoryUpdate(document, status);
        } catch (Exception e) {
            log.error("Fail to send {}: commissionInspectionId: {}", status, document.getId(), e);
        }
    }

    private void sendStatusFilterByDaysSinceLastEvent(
        final List<CommissionInspectionFlowStatus> requiredStatusesToCheck,
        final Predicate<CommissionInspectionHistoryEvent> eventPredicate,
        final Predicate<Long> filterByDays,
        final Consumer<CommissionInspectionDocument> documentProcessor
    ) {
        commissionInspectionDocumentService
            .findByStatusIdIn(requiredStatusesToCheck)
            .stream()
            .filter(document -> filterByDaysSinceLastEvent(document, filterByDays, eventPredicate))
            .forEach(documentProcessor);
    }

    private boolean filterByDaysSinceLastEvent(
        final CommissionInspectionDocument commissionInspectionDocument,
        final Predicate<Long> filterByDays,
        final Predicate<CommissionInspectionHistoryEvent> eventPredicate
    ) {
        return toLatestEventDate(commissionInspectionDocument, eventPredicate)
            .map(maxEventDateTime -> Duration.between(maxEventDateTime, LocalDateTime.now()))
            .map(Duration::toDays)
            .filter(filterByDays)
            .isPresent();
    }

    private Optional<LocalDateTime> toLatestEventDate(
        final CommissionInspectionDocument commissionInspectionDocument,
        final Predicate<CommissionInspectionHistoryEvent> eventPredicate
    ) {
        return getCommissionInspectionHistoryEventStream(commissionInspectionDocument)
            .filter(eventPredicate)
            .map(CommissionInspectionHistoryEvent::getCreatedAt)
            .max(LocalDateTime::compareTo);
    }

    private Stream<CommissionInspectionHistoryEvent> getCommissionInspectionHistoryEventStream(
        final CommissionInspectionDocument commissionInspectionDocument
    ) {
        return of(commissionInspectionDocument.getDocument())
            .map(CommissionInspection::getCommissionInspectionData)
            .map(CommissionInspectionData::getHistory)
            .map(CommissionInspectionHistoryEvents::getEvents)
            .map(List::stream)
            .orElse(Stream.empty());
    }
}
