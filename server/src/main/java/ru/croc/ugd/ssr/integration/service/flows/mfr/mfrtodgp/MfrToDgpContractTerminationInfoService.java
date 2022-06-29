package ru.croc.ugd.ssr.integration.service.flows.mfr.mfrtodgp;

import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.integration.service.flows.mfr.dgptomfr.PersonInfoMfrFlowService;
import ru.croc.ugd.ssr.integration.service.flows.mfr.mappers.mfrtodgp.MfrToDgpContractTerminationInfoMapper;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateTaskData;
import ru.croc.ugd.ssr.model.integration.mfr.MfrToDgpContractTerminationInfo;
import ru.croc.ugd.ssr.model.trade.TradeAdditionDocument;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.document.TradeAdditionDocumentService;
import ru.croc.ugd.ssr.service.trade.TradeAdditionService;

/**
 *  15. Сведения о расторжении договора
 */
@Service
public class MfrToDgpContractTerminationInfoService extends MfrToDgpFlowService {

    private final MfrToDgpContractTerminationInfoMapper mfrToDgpContractTerminationInfoMapper;

    public MfrToDgpContractTerminationInfoService(
        final TradeAdditionDocumentService tradeAdditionDocumentService,
        final TradeAdditionService tradeAdditionService,
        final PersonDocumentService personDocumentService,
        final PersonInfoMfrFlowService personInfoMfrFlowService,
        final MfrToDgpContractTerminationInfoMapper mfrToDgpContractTerminationInfoMapper
    ) {
        super(tradeAdditionDocumentService, tradeAdditionService, personDocumentService, personInfoMfrFlowService);
        this.mfrToDgpContractTerminationInfoMapper = mfrToDgpContractTerminationInfoMapper;
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
        final MfrToDgpContractTerminationInfo mfrToDgpContractTerminationInfo = retrieveAttributes(coordinateTaskData);
        return mfrToDgpContractTerminationInfoMapper.toTradeAdditionDocument(
            tradeAdditionDocument, mfrToDgpContractTerminationInfo, integrationFlowDocumentId
        );
    }

    private MfrToDgpContractTerminationInfo retrieveAttributes(final CoordinateTaskData coordinateTaskData) {
        return retrieveCustomAttributes(coordinateTaskData)
            .filter(attributes -> attributes instanceof MfrToDgpContractTerminationInfo)
            .map(MfrToDgpContractTerminationInfo.class::cast)
            .orElse(null);
    }
}
