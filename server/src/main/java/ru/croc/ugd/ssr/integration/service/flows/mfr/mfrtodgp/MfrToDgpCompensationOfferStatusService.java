package ru.croc.ugd.ssr.integration.service.flows.mfr.mfrtodgp;

import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.integration.service.flows.mfr.dgptomfr.PersonInfoMfrFlowService;
import ru.croc.ugd.ssr.integration.service.flows.mfr.mappers.mfrtodgp.MfrToDgpCompensationOfferStatusMapper;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateTaskData;
import ru.croc.ugd.ssr.model.integration.mfr.MfrToDgpCompensationOfferStatus;
import ru.croc.ugd.ssr.model.trade.TradeAdditionDocument;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.document.TradeAdditionDocumentService;
import ru.croc.ugd.ssr.service.trade.TradeAdditionService;

import java.util.List;

/**
 *  6. Сведения о статусе предложения по компенсации
 */
@Service
public class MfrToDgpCompensationOfferStatusService extends MfrToDgpFlowService {

    private final MfrToDgpCompensationOfferStatusMapper mfrToDgpCompensationOfferStatusMapper;

    public MfrToDgpCompensationOfferStatusService(
        final TradeAdditionDocumentService tradeAdditionDocumentService,
        final TradeAdditionService tradeAdditionService,
        final PersonDocumentService personDocumentService,
        final PersonInfoMfrFlowService personInfoMfrFlowService,
        final MfrToDgpCompensationOfferStatusMapper mfrToDgpCompensationOfferStatusMapper
    ) {
        super(tradeAdditionDocumentService, tradeAdditionService, personDocumentService, personInfoMfrFlowService);
        this.mfrToDgpCompensationOfferStatusMapper = mfrToDgpCompensationOfferStatusMapper;
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
        final MfrToDgpCompensationOfferStatus mfrToDgpCompensationOfferStatus = retrieveAttributes(coordinateTaskData);
        final List<PersonDocument> personDocuments = retrievePersonDocuments(
            mfrToDgpCompensationOfferStatus.getAffairId()
        );
        return mfrToDgpCompensationOfferStatusMapper.toTradeAdditionDocument(
            tradeAdditionDocument,
            mfrToDgpCompensationOfferStatus,
            integrationFlowDocumentId,
            retrievePersonIds(personDocuments)
        );
    }

    private MfrToDgpCompensationOfferStatus retrieveAttributes(final CoordinateTaskData coordinateTaskData) {
        return retrieveCustomAttributes(coordinateTaskData)
            .filter(attributes -> attributes instanceof MfrToDgpCompensationOfferStatus)
            .map(MfrToDgpCompensationOfferStatus.class::cast)
            .orElse(null);
    }
}
