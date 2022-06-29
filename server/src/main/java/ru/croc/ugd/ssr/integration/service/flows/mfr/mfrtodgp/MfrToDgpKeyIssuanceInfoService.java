package ru.croc.ugd.ssr.integration.service.flows.mfr.mfrtodgp;

import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.integration.service.flows.mfr.dgptomfr.PersonInfoMfrFlowService;
import ru.croc.ugd.ssr.integration.service.flows.mfr.mappers.mfrtodgp.MfrToDgpKeyIssuanceInfoMapper;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateTaskData;
import ru.croc.ugd.ssr.model.integration.mfr.MfrToDgpKeyIssuanceInfo;
import ru.croc.ugd.ssr.model.trade.TradeAdditionDocument;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.document.TradeAdditionDocumentService;
import ru.croc.ugd.ssr.service.trade.TradeAdditionService;

/**
 *  17. Сведения о выдаче ключей от новой квартиры
 */
@Service
public class MfrToDgpKeyIssuanceInfoService extends MfrToDgpFlowService {

    private final MfrToDgpKeyIssuanceInfoMapper mfrToDgpKeyIssuanceInfoMapper;

    public MfrToDgpKeyIssuanceInfoService(
        final TradeAdditionDocumentService tradeAdditionDocumentService,
        final TradeAdditionService tradeAdditionService,
        final PersonDocumentService personDocumentService,
        final PersonInfoMfrFlowService personInfoMfrFlowService,
        final MfrToDgpKeyIssuanceInfoMapper mfrToDgpKeyIssuanceInfoMapper
    ) {
        super(tradeAdditionDocumentService, tradeAdditionService, personDocumentService, personInfoMfrFlowService);
        this.mfrToDgpKeyIssuanceInfoMapper = mfrToDgpKeyIssuanceInfoMapper;
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
        final MfrToDgpKeyIssuanceInfo mfrToDgpKeyIssuanceInfo = retrieveAttributes(coordinateTaskData);
        return mfrToDgpKeyIssuanceInfoMapper.toTradeAdditionDocument(
            tradeAdditionDocument, mfrToDgpKeyIssuanceInfo, integrationFlowDocumentId
        );
    }

    private MfrToDgpKeyIssuanceInfo retrieveAttributes(final CoordinateTaskData coordinateTaskData) {
        return retrieveCustomAttributes(coordinateTaskData)
            .filter(attributes -> attributes instanceof MfrToDgpKeyIssuanceInfo)
            .map(MfrToDgpKeyIssuanceInfo.class::cast)
            .orElse(null);
    }
}
