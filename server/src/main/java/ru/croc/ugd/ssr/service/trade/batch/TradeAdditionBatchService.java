package ru.croc.ugd.ssr.service.trade.batch;

import ru.croc.ugd.ssr.dto.tradeaddition.TradeAdditionConfirmTradesDto;
import ru.croc.ugd.ssr.model.trade.TradeAdditionDocument;

import java.util.List;

/**
 * TradeAdditionBatchService.
 */
public interface TradeAdditionBatchService {

    /**
     * processTradeAdditionSheetFile.
     * @param fileId fileId.
     * @param documentId documentId.
     */
    void processTradeAdditionSheetFile(final String fileId, final String documentId);

    /**
     * reindexes TradeAdditions in batch and set batch to deployed.
     * @param documentId documentId.
     */
    void deployBatch(final String documentId);

    /**
     * areAllBatchesDeployed.
     *
     * @return areAllBatchesDeployed.
     */
    boolean areAllBatchesDeployed();

    /**
     * Confirms trades by batch id.
     * @param batchDocumentId batchDocumentId
     * @param confirmTradesDto confirmTradesDto
     */
    void confirmTrades(final String batchDocumentId, final TradeAdditionConfirmTradesDto confirmTradesDto);

    /**
     * Fetches trade additions by batchDocumentId.
     *
     * @param batchDocumentId batchDocumentId
     * @param pageNum pageNum
     * @param pageSize pageSize
     * @return trade addition list
     */
    List<TradeAdditionDocument> fetchTradeAdditions(
        final String batchDocumentId, final int pageNum, final int pageSize
    );

    /**
     * Count trade additions.
     *
     * @param batchDocumentId batchDocumentId
     * @return trade additions count
     */
    long countTradeAdditions(final String batchDocumentId);
}
