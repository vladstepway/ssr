package ru.croc.ugd.ssr.integration.service.flows.mfr.mfrtodgp;

import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.integration.service.flows.mfr.dgptomfr.PersonInfoMfrFlowService;
import ru.croc.ugd.ssr.integration.service.flows.mfr.mappers.mfrtodgp.MfrToDgpBuyInDecisionResultInfoMapper;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateTaskData;
import ru.croc.ugd.ssr.model.integration.mfr.MfrToDgpBuyInDecisionResultInfo;
import ru.croc.ugd.ssr.model.trade.TradeAdditionDocument;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.document.TradeAdditionDocumentService;
import ru.croc.ugd.ssr.service.trade.TradeAdditionService;

/**
 *  9. Сведения о вынесенном решении Комиссией Фонда реновации по докупке
 */
@Service
public class MfrToDgpBuyInDecisionResultInfoService extends MfrToDgpFlowService {

    private final MfrToDgpBuyInDecisionResultInfoMapper mfrToDgpBuyInDecisionResultInfoMapper;

    public MfrToDgpBuyInDecisionResultInfoService(
        final TradeAdditionDocumentService tradeAdditionDocumentService,
        final TradeAdditionService tradeAdditionService,
        final PersonDocumentService personDocumentService,
        final PersonInfoMfrFlowService personInfoMfrFlowService,
        final MfrToDgpBuyInDecisionResultInfoMapper mfrToDgpBuyInDecisionResultInfoMapper
    ) {
        super(tradeAdditionDocumentService, tradeAdditionService, personDocumentService, personInfoMfrFlowService);
        this.mfrToDgpBuyInDecisionResultInfoMapper = mfrToDgpBuyInDecisionResultInfoMapper;
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
        final MfrToDgpBuyInDecisionResultInfo mfrToDgpBuyInDecisionResultInfo = retrieveAttributes(coordinateTaskData);
        return mfrToDgpBuyInDecisionResultInfoMapper.toTradeAdditionDocument(
            tradeAdditionDocument, mfrToDgpBuyInDecisionResultInfo, integrationFlowDocumentId
        );
    }

    private MfrToDgpBuyInDecisionResultInfo retrieveAttributes(final CoordinateTaskData coordinateTaskData) {
        return retrieveCustomAttributes(coordinateTaskData)
            .filter(attributes -> attributes instanceof MfrToDgpBuyInDecisionResultInfo)
            .map(MfrToDgpBuyInDecisionResultInfo.class::cast)
            .orElse(null);
    }
}
