package ru.croc.ugd.ssr.service.commissioninspection;

import static java.util.Optional.ofNullable;
import static ru.croc.ugd.ssr.dto.commissioninspection.CommissionInspectionFlowStatus.DEFECTS_FIXED;

import lombok.experimental.UtilityClass;
import ru.croc.ugd.ssr.commission.CommissionInspectionData;
import ru.croc.ugd.ssr.commission.CommissionInspectionHistoryEvent;
import ru.croc.ugd.ssr.commission.CommissionInspectionHistoryEvents;
import ru.croc.ugd.ssr.commission.InspectionType;
import ru.croc.ugd.ssr.dto.commissioninspection.CommissionInspectionFlowStatus;
import ru.croc.ugd.ssr.model.commissioninspection.CommissionInspectionDocument;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * CommissionInspectionUtils.
 */
@UtilityClass
public class CommissionInspectionUtils {

    public static Optional<CommissionInspectionHistoryEvent> retrieveLastHistoryEventByStatus(
        final CommissionInspectionData commissionInspection,
        final CommissionInspectionFlowStatus status
    ) {
        return ofNullable(commissionInspection.getHistory())
            .map(CommissionInspectionHistoryEvents::getEvents)
            .map(List::stream)
            .orElse(Stream.empty())
            .filter(event -> status.getId().equals(event.getEventId()))
            .max(Comparator.comparing(CommissionInspectionHistoryEvent::getCreatedAt));
    }

    public static List<CommissionInspectionHistoryEvent> retrieveHistoryEventsByStatus(
        final CommissionInspectionData commissionInspection,
        final CommissionInspectionFlowStatus status
    ) {
        return ofNullable(commissionInspection.getHistory())
            .map(CommissionInspectionHistoryEvents::getEvents)
            .map(List::stream)
            .orElse(Stream.empty())
            .filter(event -> status.getId().equals(event.getEventId()))
            .collect(Collectors.toList());
    }

    public static boolean isPrimaryInspection(final CommissionInspectionData commissionInspection) {
        return ofNullable(commissionInspection)
            .map(CommissionInspectionData::getHistory)
            .map(CommissionInspectionHistoryEvents::getEvents)
            .map(List::stream)
            .orElse(Stream.empty())
            .map(CommissionInspectionHistoryEvent::getEventId)
            .noneMatch(DEFECTS_FIXED.getId()::equals);
    }

    public static void addCommissionInspectionHistoryEvent(
        final CommissionInspectionDocument commissionInspectionDocument,
        final CommissionInspectionFlowStatus status
    ) {
        final CommissionInspectionData commissionInspection = commissionInspectionDocument
            .getDocument()
            .getCommissionInspectionData();
        final boolean isPrimary = isPrimaryInspection(commissionInspection);

        final CommissionInspectionHistoryEvent historyEvent = new CommissionInspectionHistoryEvent();
        historyEvent.setInspectionId(commissionInspection.getCurrentApartmentInspectionId());
        historyEvent.setInspectionType(isPrimary ? InspectionType.PRIMARY : InspectionType.SECONDARY);
        historyEvent.setCreatedAt(LocalDateTime.now());
        historyEvent.setEventId(status.getId());

        addCommissionInspectionHistoryEvent(commissionInspection, historyEvent);
    }

    public void addCommissionInspectionHistoryEvent(
        final CommissionInspectionData commissionInspection,
        final CommissionInspectionHistoryEvent historyEvent
    ) {
        final CommissionInspectionHistoryEvents history = ofNullable(commissionInspection.getHistory())
            .orElseGet(CommissionInspectionHistoryEvents::new);
        history.getEvents().add(historyEvent);

        commissionInspection.setHistory(history);
    }
}

