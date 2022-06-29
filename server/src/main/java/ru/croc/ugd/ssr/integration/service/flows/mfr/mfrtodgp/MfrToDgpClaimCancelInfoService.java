package ru.croc.ugd.ssr.integration.service.flows.mfr.mfrtodgp;

import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.integration.service.flows.mfr.dgptomfr.PersonInfoMfrFlowService;
import ru.croc.ugd.ssr.integration.service.flows.mfr.mappers.mfrtodgp.MfrToDgpClaimCancelInfoMapper;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateTaskData;
import ru.croc.ugd.ssr.model.integration.mfr.MfrToDgpClaimCancelInfo;
import ru.croc.ugd.ssr.model.trade.TradeAdditionDocument;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.document.TradeAdditionDocumentService;
import ru.croc.ugd.ssr.service.trade.TradeAdditionService;

/**
 *  16. Сведения об аннулировании заявления
 */
@Service
public class MfrToDgpClaimCancelInfoService extends MfrToDgpFlowService {

    private final MfrToDgpClaimCancelInfoMapper mfrToDgpClaimCancelInfoMapper;

    public MfrToDgpClaimCancelInfoService(
        final TradeAdditionDocumentService tradeAdditionDocumentService,
        final TradeAdditionService tradeAdditionService,
        final PersonDocumentService personDocumentService,
        final PersonInfoMfrFlowService personInfoMfrFlowService,
        final MfrToDgpClaimCancelInfoMapper mfrToDgpClaimCancelInfoMapper
    ) {
        super(tradeAdditionDocumentService, tradeAdditionService, personDocumentService, personInfoMfrFlowService);
        this.mfrToDgpClaimCancelInfoMapper = mfrToDgpClaimCancelInfoMapper;
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
        final MfrToDgpClaimCancelInfo mfrToDgpClaimCancelInfo = retrieveAttributes(coordinateTaskData);
        return mfrToDgpClaimCancelInfoMapper.toTradeAdditionDocument(
            tradeAdditionDocument, mfrToDgpClaimCancelInfo, integrationFlowDocumentId
        );
    }

    private MfrToDgpClaimCancelInfo retrieveAttributes(final CoordinateTaskData coordinateTaskData) {
        return retrieveCustomAttributes(coordinateTaskData)
            .filter(attributes -> attributes instanceof MfrToDgpClaimCancelInfo)
            .map(MfrToDgpClaimCancelInfo.class::cast)
            .orElse(null);
    }
}
