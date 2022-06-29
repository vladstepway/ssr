package ru.croc.ugd.ssr.integration.service.flows.mfr.mfrtodgp;

import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.integration.service.flows.mfr.dgptomfr.PersonInfoMfrFlowService;
import ru.croc.ugd.ssr.integration.service.flows.mfr.mappers.mfrtodgp.MfrToDgpContractRegistrationInfoMapper;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateTaskData;
import ru.croc.ugd.ssr.model.integration.mfr.MfrToDgpContractRegistrationInfo;
import ru.croc.ugd.ssr.model.trade.TradeAdditionDocument;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.document.TradeAdditionDocumentService;
import ru.croc.ugd.ssr.service.trade.TradeAdditionService;

/**
 *  14. Сведения о регистрации договора
 */
@Service
public class MfrToDgpContractRegistrationInfoService extends MfrToDgpFlowService {

    private final MfrToDgpContractRegistrationInfoMapper mfrToDgpContractRegistrationInfoMapper;

    public MfrToDgpContractRegistrationInfoService(
        final TradeAdditionDocumentService tradeAdditionDocumentService,
        final TradeAdditionService tradeAdditionService,
        final PersonDocumentService personDocumentService,
        final PersonInfoMfrFlowService personInfoMfrFlowService,
        final MfrToDgpContractRegistrationInfoMapper mfrToDgpContractRegistrationInfoMapper
    ) {
        super(tradeAdditionDocumentService, tradeAdditionService, personDocumentService, personInfoMfrFlowService);
        this.mfrToDgpContractRegistrationInfoMapper = mfrToDgpContractRegistrationInfoMapper;
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
        final MfrToDgpContractRegistrationInfo mfrToDgpContractRegistrationInfo = retrieveAttributes(
            coordinateTaskData
        );
        return mfrToDgpContractRegistrationInfoMapper.toTradeAdditionDocument(
            tradeAdditionDocument, mfrToDgpContractRegistrationInfo, integrationFlowDocumentId
        );
    }

    private MfrToDgpContractRegistrationInfo retrieveAttributes(final CoordinateTaskData coordinateTaskData) {
        return retrieveCustomAttributes(coordinateTaskData)
            .filter(attributes -> attributes instanceof MfrToDgpContractRegistrationInfo)
            .map(MfrToDgpContractRegistrationInfo.class::cast)
            .orElse(null);
    }
}
