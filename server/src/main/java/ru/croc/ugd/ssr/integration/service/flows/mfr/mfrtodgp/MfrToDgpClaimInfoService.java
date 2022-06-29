package ru.croc.ugd.ssr.integration.service.flows.mfr.mfrtodgp;

import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.integration.service.flows.mfr.dgptomfr.PersonInfoMfrFlowService;
import ru.croc.ugd.ssr.integration.service.flows.mfr.mappers.mfrtodgp.MfrToDgpClaimInfoMapper;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateTaskData;
import ru.croc.ugd.ssr.model.integration.mfr.MfrToDgpClaimInfo;
import ru.croc.ugd.ssr.model.trade.TradeAdditionDocument;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.document.TradeAdditionDocumentService;
import ru.croc.ugd.ssr.service.trade.TradeAdditionService;

/**
 *  8. Данные по заявлению
 */
@Service
public class MfrToDgpClaimInfoService extends MfrToDgpFlowService {

    private final MfrToDgpClaimInfoMapper mfrToDgpClaimInfoMapper;

    public MfrToDgpClaimInfoService(
        final TradeAdditionDocumentService tradeAdditionDocumentService,
        final TradeAdditionService tradeAdditionService,
        final PersonDocumentService personDocumentService,
        final PersonInfoMfrFlowService personInfoMfrFlowService,
        final MfrToDgpClaimInfoMapper mfrToDgpClaimInfoMapper
    ) {
        super(tradeAdditionDocumentService, tradeAdditionService, personDocumentService, personInfoMfrFlowService);
        this.mfrToDgpClaimInfoMapper = mfrToDgpClaimInfoMapper;
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
        final MfrToDgpClaimInfo mfrToDgpClaimInfo = retrieveAttributes(coordinateTaskData);
        return mfrToDgpClaimInfoMapper.toTradeAdditionDocument(
            tradeAdditionDocument, mfrToDgpClaimInfo, integrationFlowDocumentId
        );
    }

    private MfrToDgpClaimInfo retrieveAttributes(final CoordinateTaskData coordinateTaskData) {
        return retrieveCustomAttributes(coordinateTaskData)
            .filter(attributes -> attributes instanceof MfrToDgpClaimInfo)
            .map(MfrToDgpClaimInfo.class::cast)
            .orElse(null);
    }
}
