package ru.croc.ugd.ssr.integration.service.flows.mfr.mfrtodgp;

import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.integration.service.flows.mfr.dgptomfr.PersonInfoMfrFlowService;
import ru.croc.ugd.ssr.integration.service.flows.mfr.mappers.mfrtodgp.MfrToDgpAuctionClaimResultInfoMapper;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateTaskData;
import ru.croc.ugd.ssr.model.integration.mfr.MfrToDgpAuctionClaimResultInfo;
import ru.croc.ugd.ssr.model.trade.TradeAdditionDocument;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.document.TradeAdditionDocumentService;
import ru.croc.ugd.ssr.service.trade.TradeAdditionService;

/**
 *  10. Сведения о результатах рассмотрения заявок на участие в аукционе
 */
@Service
public class MfrToDgpAuctionClaimResultInfoService extends MfrToDgpFlowService {

    private final MfrToDgpAuctionClaimResultInfoMapper mfrToDgpAuctionClaimResultInfoMapper;

    public MfrToDgpAuctionClaimResultInfoService(
        final TradeAdditionDocumentService tradeAdditionDocumentService,
        final TradeAdditionService tradeAdditionService,
        final PersonDocumentService personDocumentService,
        final PersonInfoMfrFlowService personInfoMfrFlowService,
        final MfrToDgpAuctionClaimResultInfoMapper mfrToDgpAuctionClaimResultInfoMapper
    ) {
        super(tradeAdditionDocumentService, tradeAdditionService, personDocumentService, personInfoMfrFlowService);
        this.mfrToDgpAuctionClaimResultInfoMapper = mfrToDgpAuctionClaimResultInfoMapper;
    }

    @Override
    protected String retrieveSellId(final CoordinateTaskData coordinateTaskData) {
        return retrieveAttributes(coordinateTaskData).getSellId();
    }

    @Override
    protected TradeAdditionDocument retrieveTradeAddition(
        final CoordinateTaskData coordinateTaskData,
        final String integrationFlowDocumentId,
        final TradeAdditionDocument tradeAdditionDocument
    ) {
        final MfrToDgpAuctionClaimResultInfo mfrToDgpAuctionClaimResultInfo = retrieveAttributes(coordinateTaskData);
        return mfrToDgpAuctionClaimResultInfoMapper.toTradeAdditionDocument(
            tradeAdditionDocument, mfrToDgpAuctionClaimResultInfo, integrationFlowDocumentId
        );
    }

    private MfrToDgpAuctionClaimResultInfo retrieveAttributes(final CoordinateTaskData coordinateTaskData) {
        return retrieveCustomAttributes(coordinateTaskData)
            .filter(attributes -> attributes instanceof MfrToDgpAuctionClaimResultInfo)
            .map(MfrToDgpAuctionClaimResultInfo.class::cast)
            .orElse(null);
    }
}
