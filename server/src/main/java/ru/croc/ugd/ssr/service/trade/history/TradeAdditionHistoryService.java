package ru.croc.ugd.ssr.service.trade.history;

import static java.util.Optional.ofNullable;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.math3.util.Pair;
import org.javers.core.Changes;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.Change;
import org.javers.core.diff.changetype.ValueChange;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.dto.tradeaddition.TradeAdditionHistoryDto;
import ru.croc.ugd.ssr.mapper.TradeAdditionHistoryMapper;
import ru.croc.ugd.ssr.model.trade.TradeAdditionDocument;
import ru.croc.ugd.ssr.model.trade.TradeAdditionHistoryDocument;
import ru.croc.ugd.ssr.service.document.TradeAdditionDocumentService;
import ru.croc.ugd.ssr.service.document.TradeAdditionHistoryDocumentService;
import ru.croc.ugd.ssr.trade.TradeAdditionHistoryType;
import ru.croc.ugd.ssr.trade.TradeAdditionType;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TradeAdditionHistoryService.
 */
@Slf4j
@Service
@AllArgsConstructor
public class TradeAdditionHistoryService {
    private static final List<String> UNREQUIRED_PROPERTIES;

    private final TradeAdditionHistoryDocumentService tradeAdditionHistoryDocumentService;
    private final TradeAdditionDocumentService tradeAdditionDocumentService;
    private final TradeAdditionHistoryMapper tradeAdditionHistoryMapper;

    /**
     * save history and calc diff.
     * @param oldTradeAdditionDocument oldTradeAdditionDocument
     * @param newTradeAdditionDocument newTradeAdditionDocument
     * @return formatted diff.
     */
    public Pair<String, Boolean> calculateModificationsForTradeAddition(
        final TradeAdditionDocument oldTradeAdditionDocument,
        final TradeAdditionDocument newTradeAdditionDocument) {
        final TradeAdditionType oldTradeAddition = oldTradeAdditionDocument.getDocument().getTradeAdditionTypeData();
        final TradeAdditionType newTradeAddition = newTradeAdditionDocument.getDocument().getTradeAdditionTypeData();
        final Javers javers = JaversBuilder.javers()
            .withNewObjectsSnapshot(true)
            .build();
        final Changes changes = javers.compare(oldTradeAddition, newTradeAddition)
            .getChanges();
        final List<Change> filteredChanges = filterUnrequiredChanges(changes);
        final String changeLog = javers.processChangeList(filteredChanges,
            new TradeAdditionHistoryChangeLogProcessor());
        return Pair.create(changeLog, hasAnyNotFromEmptyChange(filteredChanges));
    }

    /**
     * save new history.
     * @param tradeAdditionDocument tradeAdditionDocument
     */
    public void saveHistoryForNewTradeAddition(final TradeAdditionDocument tradeAdditionDocument) {
        final TradeAdditionHistoryType newTradeAdditionHistory =
            createNewTradeAdditionHistoryDocument(tradeAdditionDocument
                .getDocument()
                .getTradeAdditionTypeData(), tradeAdditionDocument.getId());
        tradeAdditionHistoryDocumentService.createNewTradeAdditionHistoryDocument(newTradeAdditionHistory);
    }

    public void saveHistoryForNewTradeAddition(
        final TradeAdditionDocument tradeAdditionDocument,
        final String changeLog
    ) {
        final TradeAdditionHistoryType newTradeAdditionHistory =
            createNewTradeAdditionHistoryDocument(tradeAdditionDocument
                .getDocument()
                .getTradeAdditionTypeData(), tradeAdditionDocument.getId());
        newTradeAdditionHistory.setChanges(changeLog);
        tradeAdditionHistoryDocumentService.createNewTradeAdditionHistoryDocument(newTradeAdditionHistory);
    }

    private boolean hasAnyNotFromEmptyChange(final List<Change> changes) {
        return ListUtils.emptyIfNull(changes)
            .stream()
            .filter(change -> change instanceof ValueChange)
            .map(ValueChange.class::cast)
            .anyMatch(valueChange -> valueChange.getLeft() != null
                || (valueChange.getLeft() instanceof String
                && StringUtils.isNotEmpty((String) valueChange.getLeft())));
    }

    private List<Change> filterUnrequiredChanges(final List<Change> changes) {
        final List<Change> requiredChanges = ListUtils.emptyIfNull(changes)
            .stream()
            .filter(change -> change instanceof ValueChange)
            .map(ValueChange.class::cast)
            .filter(change -> !change.isPropertyAdded())
            .filter(change -> !UNREQUIRED_PROPERTIES.contains(change.getPropertyName()))
            .map(Change.class::cast)
            .collect(Collectors.toList());
        requiredChanges.addAll(ListUtils.emptyIfNull(changes)
            .stream()
            .filter(change -> !(change instanceof ValueChange))
            .collect(Collectors.toList()));
        return requiredChanges;
    }

    private TradeAdditionHistoryType createNewTradeAdditionHistoryDocument(final TradeAdditionType tradeAdditionType,
                                                                           final String documentId) {
        final TradeAdditionHistoryType tradeAdditionHistory = new TradeAdditionHistoryType();
        tradeAdditionHistory.setUpdateDateTime(LocalDateTime.now());
        tradeAdditionHistory.setUploadedFileId(tradeAdditionType.getUploadedFileId());
        tradeAdditionHistory.setComment(tradeAdditionType.getComment());
        tradeAdditionHistory.setPageName(tradeAdditionType.getPageName());
        tradeAdditionHistory.setRecordNumber(tradeAdditionType.getRecordNumber());
        tradeAdditionHistory.setUniqueRecordKey(tradeAdditionType.getUniqueRecordKey());
        tradeAdditionHistory.setTradeAdditionDocumentId(documentId);
        return tradeAdditionHistory;
    }

    static {
        try {
            UNREQUIRED_PROPERTIES = Arrays.asList(
                TradeAdditionType.class.getDeclaredField("recordNumber").getName(),
                TradeAdditionType.class.getDeclaredField("batchDocumentId").getName(),
                TradeAdditionType.class.getDeclaredField("uploadedFileId").getName(),
                TradeAdditionType.class.getDeclaredField("uniqueRecordKey").getName(),
                TradeAdditionType.class.getDeclaredField("confirmed").getName(),
                TradeAdditionType.class.getDeclaredField("comment").getName(),
                TradeAdditionType.class.getDeclaredField("indexed").getName(),
                TradeAdditionType.class.getDeclaredField("buyInStatus").getName(),
                TradeAdditionType.class.getDeclaredField("compensationStatus").getName(),
                TradeAdditionType.class.getDeclaredField("sentNotifications").getName());
        } catch (NoSuchFieldException e) {
            log.error("Can't init unrequiredProperties for TradeAdditionHistoryService", e);
            throw new RuntimeException(e);
        }
    }

    public List<TradeAdditionHistoryDto> fetchByTradeAdditionId(final String tradeAdditionDocumentId) {
        final TradeAdditionDocument tradeAdditionDocument = tradeAdditionDocumentService.fetchDocument(
            tradeAdditionDocumentId
        );
        final TradeAdditionType tradeAdditionType = tradeAdditionDocument.getDocument().getTradeAdditionTypeData();
        final List<TradeAdditionHistoryDocument> tradeAdditionHistoryDocuments =
            ofNullable(tradeAdditionType.getUniqueRecordKey())
                .map(tradeAdditionHistoryDocumentService::findIndexedHistoryByUniqueKey)
                .orElseGet(() -> ofNullable(tradeAdditionType.getSellId())
                    .map(tradeAdditionHistoryDocumentService::findIndexedHistoryBySellId)
                    .orElse(null)
                );
        return tradeAdditionHistoryMapper.toTradeAdditionHistoryDtos(tradeAdditionHistoryDocuments);
    }
}
