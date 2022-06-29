package ru.croc.ugd.ssr.solr.converter.trade;

import static java.util.Optional.of;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.model.trade.TradeDataBatchStatusDocument;
import ru.croc.ugd.ssr.solr.UgdSsrTradeDataBatchStatus;
import ru.croc.ugd.ssr.solr.converter.SolrDocumentConversionException;
import ru.croc.ugd.ssr.solr.SsrDocumentConverter;
import ru.croc.ugd.ssr.trade.TradeDataBatchStatus;
import ru.croc.ugd.ssr.trade.TradeDataBatchStatusType;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.reinform.cdp.document.model.DocumentType;


/**
 * TradeAdditionBatchStatusDocumentConverter.
 */
@Service
@RequiredArgsConstructor
public class TradeAdditionBatchStatusDocumentConverter
    extends SsrDocumentConverter<TradeDataBatchStatusDocument, UgdSsrTradeDataBatchStatus> {

    private final SolrTradeAdditionBatchStatusMapper solrTradeAdditionBatchStatusMapper;

    @NotNull
    @Override
    public DocumentType<TradeDataBatchStatusDocument> getDocumentType() {
        return SsrDocumentTypes.TRADE_DATA_BATCH_STATUS;
    }

    @NotNull
    @Override
    public UgdSsrTradeDataBatchStatus convertDocument(
        @NotNull final TradeDataBatchStatusDocument tradeDataBatchStatusDocument) {
        final UgdSsrTradeDataBatchStatus ugdSsrTradeAdditionBatchStatus
            = createDocument(getAnyAccessType(), tradeDataBatchStatusDocument.getId());

        final TradeDataBatchStatusType tradeDataBatchStatusType =
            of(tradeDataBatchStatusDocument.getDocument())
                .map(TradeDataBatchStatus::getTradeDataBatchStatusTypeData)
                .orElseThrow(() -> new SolrDocumentConversionException(tradeDataBatchStatusDocument.getId()));

        return solrTradeAdditionBatchStatusMapper
            .toUgdSsrTradeAdditionBatchStatus(ugdSsrTradeAdditionBatchStatus, tradeDataBatchStatusType);
    }

}
