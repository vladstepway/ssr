package ru.croc.ugd.ssr.service.document;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.db.dao.TradeAdditionDao;
import ru.croc.ugd.ssr.model.trade.TradeAdditionHistoryDocument;
import ru.croc.ugd.ssr.trade.TradeAdditionHistory;
import ru.croc.ugd.ssr.trade.TradeAdditionHistoryType;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.reinform.cdp.db.model.DocumentData;
import ru.reinform.cdp.document.model.DocumentType;
import ru.reinform.cdp.document.service.AbstractDocumentService;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

/**
 * TradeAdditionDocumentService.
 */
@AllArgsConstructor
@Service
@Slf4j
public class TradeAdditionHistoryDocumentService extends AbstractDocumentService<TradeAdditionHistoryDocument> {
    private TradeAdditionDao tradeAdditionDao;

    @Nonnull
    @Override
    public DocumentType<TradeAdditionHistoryDocument> getDocumentType() {
        return SsrDocumentTypes.TRADE_ADDITION_HISTORY;
    }

    /**
     * Create new TradeAdditionDocument.
     * @param tradeAdditionHistoryType tradeAdditionType
     * @return documentID.
     */
    @Nonnull
    public TradeAdditionHistoryDocument createNewTradeAdditionHistoryDocument(final TradeAdditionHistoryType
                                                                            tradeAdditionHistoryType) {
        final TradeAdditionHistoryDocument document = createTradeAdditionDocument(tradeAdditionHistoryType);
        return super.createDocument(document, true, null);
    }

    public List<TradeAdditionHistoryDocument> findIndexedHistoryByUniqueKey(final String uniqueKey) {
        return tradeAdditionDao.findActiveTradeAdditionHistoryByUniqueKey(uniqueKey)
            .stream()
            .map(this::parseDocumentData)
            .filter(Objects::nonNull)
            .sorted(Comparator.comparing(tradeAdditionHistoryDocument -> tradeAdditionHistoryDocument
                .getDocument()
                .getTradeAdditionHistoryData()
                .getUpdateDateTime()))
            .collect(Collectors.toList());
    }

    public List<TradeAdditionHistoryDocument> findIndexedHistoryBySellId(final String sellId) {
        return tradeAdditionDao.findActiveTradeAdditionHistoryBySellId(sellId)
            .stream()
            .map(this::parseDocumentData)
            .filter(Objects::nonNull)
            .sorted(Comparator.comparing(tradeAdditionHistoryDocument -> tradeAdditionHistoryDocument
                .getDocument()
                .getTradeAdditionHistoryData()
                .getUpdateDateTime()))
            .collect(Collectors.toList());
    }

    public List<TradeAdditionHistoryDocument> findHistoryByTradeAdditionId(final String tradeAdditionId) {
        return tradeAdditionDao.findHistoryByTradeAdditionId(tradeAdditionId)
            .stream()
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
    }

    private TradeAdditionHistoryDocument parseDocumentData(@Nonnull DocumentData documentData) {
        return parseDocumentJson(documentData.getJsonData());
    }

    private TradeAdditionHistoryDocument parseDocumentJson(@Nonnull String json) {
        return jsonMapper.readObject(json, TradeAdditionHistoryDocument.class);
    }

    private TradeAdditionHistoryDocument createTradeAdditionDocument(final TradeAdditionHistoryType
                                                                             tradeAdditionHistoryType) {
        final TradeAdditionHistoryDocument tradeAdditionHistoryDocument = new TradeAdditionHistoryDocument();
        final TradeAdditionHistory tradeAdditionHistory = new TradeAdditionHistory();
        tradeAdditionHistory.setTradeAdditionHistoryData(tradeAdditionHistoryType);
        tradeAdditionHistoryDocument.setDocument(tradeAdditionHistory);
        return tradeAdditionHistoryDocument;
    }
}
