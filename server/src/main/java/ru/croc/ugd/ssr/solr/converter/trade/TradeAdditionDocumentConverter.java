package ru.croc.ugd.ssr.solr.converter.trade;

import static java.util.Optional.of;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.ListUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.model.trade.TradeAdditionDocument;
import ru.croc.ugd.ssr.solr.UgdSsrTradeAddition;
import ru.croc.ugd.ssr.solr.converter.SolrDocumentConversionException;
import ru.croc.ugd.ssr.solr.SsrDocumentConverter;
import ru.croc.ugd.ssr.trade.PersonInfoType;
import ru.croc.ugd.ssr.trade.TradeAddition;
import ru.croc.ugd.ssr.trade.TradeAdditionType;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.reinform.cdp.document.model.DocumentType;

import java.util.stream.Collectors;

/**
 * TradeAdditionDocumentConverter.
 */
@Service
@RequiredArgsConstructor
public class TradeAdditionDocumentConverter extends SsrDocumentConverter<TradeAdditionDocument, UgdSsrTradeAddition> {

    private final SolrTradeAdditionMapper solrTradeAdditionMapper;

    @NotNull
    @Override
    public DocumentType<TradeAdditionDocument> getDocumentType() {
        return SsrDocumentTypes.TRADE_ADDITION;
    }

    @NotNull
    @Override
    public UgdSsrTradeAddition convertDocument(@NotNull final TradeAdditionDocument tradeAdditionDocument) {
        final UgdSsrTradeAddition solrTradeAddition = createDocument(getAnyAccessType(), tradeAdditionDocument.getId());

        final TradeAdditionType tradeAdditionType =
            of(tradeAdditionDocument.getDocument())
                .map(TradeAddition::getTradeAdditionTypeData)
                .orElseThrow(() -> new SolrDocumentConversionException(tradeAdditionDocument.getId()));

        final String personNames = ListUtils.emptyIfNull(tradeAdditionType
            .getPersonsInfo())
            .stream()
            .map(PersonInfoType::getPersonFio)
            .collect(Collectors.joining("\n"));
        return solrTradeAdditionMapper.toUgdSsrTradeAddition(solrTradeAddition, tradeAdditionType, personNames);
    }

}
