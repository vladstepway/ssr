package ru.croc.ugd.ssr.integration.service.flows.mfr.mfrtodgp;

import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.integration.service.flows.mfr.dgptomfr.PersonInfoMfrFlowService;
import ru.croc.ugd.ssr.integration.service.flows.mfr.mappers.mfrtodgp.MfrToDgpContractReadyInfoMapper;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateTaskData;
import ru.croc.ugd.ssr.model.integration.mfr.MfrToDgpContractReadyInfo;
import ru.croc.ugd.ssr.model.trade.TradeAdditionDocument;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.document.TradeAdditionDocumentService;
import ru.croc.ugd.ssr.service.trade.TradeAdditionService;

/**
 *  12. Сведения о готовности договора
 */
@Service
public class MfrToDgpContractReadyInfoService extends MfrToDgpFlowService {

    private final MfrToDgpContractReadyInfoMapper mfrToDgpContractReadyInfoMapper;

    public MfrToDgpContractReadyInfoService(
        final TradeAdditionDocumentService tradeAdditionDocumentService,
        final TradeAdditionService tradeAdditionService,
        final PersonDocumentService personDocumentService,
        final PersonInfoMfrFlowService personInfoMfrFlowService,
        final MfrToDgpContractReadyInfoMapper mfrToDgpContractReadyInfoMapper
    ) {
        super(tradeAdditionDocumentService, tradeAdditionService, personDocumentService, personInfoMfrFlowService);
        this.mfrToDgpContractReadyInfoMapper = mfrToDgpContractReadyInfoMapper;
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
        final MfrToDgpContractReadyInfo mfrToDgpContractReadyInfo = retrieveAttributes(coordinateTaskData);
        return mfrToDgpContractReadyInfoMapper.toTradeAdditionDocument(
            tradeAdditionDocument, mfrToDgpContractReadyInfo, integrationFlowDocumentId
        );
    }

    private MfrToDgpContractReadyInfo retrieveAttributes(final CoordinateTaskData coordinateTaskData) {
        return retrieveCustomAttributes(coordinateTaskData)
            .filter(attributes -> attributes instanceof MfrToDgpContractReadyInfo)
            .map(MfrToDgpContractReadyInfo.class::cast)
            .orElse(null);
    }
}
