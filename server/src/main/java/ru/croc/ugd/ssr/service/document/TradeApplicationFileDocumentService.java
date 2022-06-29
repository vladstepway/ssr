package ru.croc.ugd.ssr.service.document;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.db.dao.TradeAdditionDao;
import ru.croc.ugd.ssr.model.trade.TradeApplicationFileDocument;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.reinform.cdp.db.model.DocumentData;
import ru.reinform.cdp.document.model.DocumentType;
import ru.reinform.cdp.document.service.AbstractDocumentService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

/**
 * TradeApplicationFileDocumentService.
 */
@AllArgsConstructor
@Service
@Slf4j
public class TradeApplicationFileDocumentService extends AbstractDocumentService<TradeApplicationFileDocument> {

    private final TradeAdditionDao tradeAdditionDao;

    @Nonnull
    @Override
    public DocumentType<TradeApplicationFileDocument> getDocumentType() {
        return SsrDocumentTypes.TRADE_APPLICATION_FILE;
    }

    public List<TradeApplicationFileDocument> getAllDocumentsByBatchId(final String batchId) {
        return tradeAdditionDao.getAllBatchApplicationFiles(batchId)
            .stream()
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
    }

    public Optional<TradeApplicationFileDocument> findByBatchDocumentIdAndFileName(
        final String batchDocumentId, final String fileName
    ) {
        final List<DocumentData> applicationFileDocuments = tradeAdditionDao
            .findByBatchDocumentIdAndFileName(batchDocumentId, fileName);

        if (applicationFileDocuments.size() > 1) {
            log.warn(
                "More than one application file found by batchDocumentId: {} and fileName: {}",
                batchDocumentId,
                fileName
            );
        }

        return applicationFileDocuments
            .stream()
            .map(this::parseDocumentData)
            .findFirst();
    }

    public List<String> getNotLinkedTradeApplicationFilesForBatch(final String batchDocumentId) {
        return tradeAdditionDao.getNotLinkedTradeApplicationFilesForBatch(batchDocumentId);
    }

    private TradeApplicationFileDocument parseDocumentData(@Nonnull DocumentData documentData) {
        return parseDocumentJson(documentData.getJsonData());
    }

    private TradeApplicationFileDocument parseDocumentJson(@Nonnull String json) {
        return jsonMapper.readObject(json, TradeApplicationFileDocument.class);
    }
}
