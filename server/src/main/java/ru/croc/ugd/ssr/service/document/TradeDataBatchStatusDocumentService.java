package ru.croc.ugd.ssr.service.document;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.model.trade.TradeDataBatchStatusDocument;
import ru.croc.ugd.ssr.service.DocumentWithFolder;
import ru.croc.ugd.ssr.trade.BatchProcessingStatus;
import ru.croc.ugd.ssr.trade.TradeDataBatchStatus;
import ru.croc.ugd.ssr.trade.TradeDataBatchStatusType;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.reinform.cdp.document.model.DocumentType;

import javax.annotation.Nonnull;

/**
 * TradeDataBatchStatusDocumentService.
 */
@AllArgsConstructor
@Service
@Slf4j
public class TradeDataBatchStatusDocumentService extends DocumentWithFolder<TradeDataBatchStatusDocument> {
    @Nonnull
    @Override
    public DocumentType<TradeDataBatchStatusDocument> getDocumentType() {
        return SsrDocumentTypes.TRADE_DATA_BATCH_STATUS;
    }

    /**
     * Create new document with uploadedFile id.
     * @param uploadedFileId uploadedFileId
     * @return documentID.
     */
    @Nonnull
    public String createNewBatchStatus(final String uploadedFileId) {
        final TradeDataBatchStatusDocument document = createProcessingStatusDocument();
        document.getDocument().getTradeDataBatchStatusTypeData().setUploadedFileId(uploadedFileId);
        document.getDocument().getTradeDataBatchStatusTypeData().setStatus(BatchProcessingStatus.INPROGRESS);
        return super.createDocument(document, false, null).getId();
    }

    private TradeDataBatchStatusDocument createProcessingStatusDocument() {
        final TradeDataBatchStatusDocument tradeDataBatchStatusDocument = new TradeDataBatchStatusDocument();
        final TradeDataBatchStatus tradeDataBatchStatus = new TradeDataBatchStatus();
        final TradeDataBatchStatusType tradeDataBatchStatusType = new TradeDataBatchStatusType();
        tradeDataBatchStatus.setTradeDataBatchStatusTypeData(tradeDataBatchStatusType);
        tradeDataBatchStatusDocument.setDocument(tradeDataBatchStatus);
        return tradeDataBatchStatusDocument;
    }
}
