package ru.croc.ugd.ssr.integration.service.flows.mfr.mfrtodgp;

import static java.util.Objects.nonNull;

import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.dto.tradeaddition.EstateInfoDto;
import ru.croc.ugd.ssr.integration.service.flows.mfr.dgptomfr.PersonInfoMfrFlowService;
import ru.croc.ugd.ssr.integration.service.flows.mfr.mappers.mfrtodgp.MfrToDgpBuyInClaimInfoMapper;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateTaskData;
import ru.croc.ugd.ssr.model.integration.mfr.MfrToDgpBuyInClaimInfo;
import ru.croc.ugd.ssr.model.trade.TradeAdditionDocument;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.document.TradeAdditionDocumentService;
import ru.croc.ugd.ssr.service.trade.TradeAdditionService;

import java.util.List;

/**
 *  7. Сведения о заявлении по докупке
 */
@Service
public class MfrToDgpBuyInClaimInfoService extends MfrToDgpFlowService {

    private final MfrToDgpBuyInClaimInfoMapper mfrToDgpBuyInClaimInfoMapper;

    public MfrToDgpBuyInClaimInfoService(
        final TradeAdditionDocumentService tradeAdditionDocumentService,
        final TradeAdditionService tradeAdditionService,
        final PersonDocumentService personDocumentService,
        final PersonInfoMfrFlowService personInfoMfrFlowService,
        final MfrToDgpBuyInClaimInfoMapper mfrToDgpBuyInClaimInfoMapper
    ) {
        super(tradeAdditionDocumentService, tradeAdditionService, personDocumentService, personInfoMfrFlowService);
        this.mfrToDgpBuyInClaimInfoMapper = mfrToDgpBuyInClaimInfoMapper;
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
        final MfrToDgpBuyInClaimInfo mfrToDgpBuyInClaimInfo = retrieveAttributes(coordinateTaskData);
        final List<PersonDocument> personDocuments = retrievePersonDocuments(mfrToDgpBuyInClaimInfo.getAffairId());
        final EstateInfoDto oldEstate = nonNull(tradeAdditionDocument)
            ? retrieveOldEstate(personDocuments)
            : null;
        return mfrToDgpBuyInClaimInfoMapper.toTradeAdditionDocument(
            tradeAdditionDocument,
            mfrToDgpBuyInClaimInfo,
            integrationFlowDocumentId,
            oldEstate,
            retrievePersonIds(personDocuments)
        );
    }

    @Override
    protected void processAfterDeploy(final TradeAdditionDocument tradeAdditionDocument) {
        sendPersonInfoFlow(tradeAdditionDocument);
    }

    private MfrToDgpBuyInClaimInfo retrieveAttributes(final CoordinateTaskData coordinateTaskData) {
        return retrieveCustomAttributes(coordinateTaskData)
            .filter(attributes -> attributes instanceof MfrToDgpBuyInClaimInfo)
            .map(MfrToDgpBuyInClaimInfo.class::cast)
            .orElse(null);
    }
}
