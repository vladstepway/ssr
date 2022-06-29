package ru.croc.ugd.ssr.integration.service.flows.mfr.mfrtodgp;

import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.dto.tradeaddition.EstateInfoDto;
import ru.croc.ugd.ssr.integration.service.flows.mfr.dgptomfr.PersonInfoMfrFlowService;
import ru.croc.ugd.ssr.integration.service.flows.mfr.mappers.mfrtodgp.MfrToDgpCompensationOfferInfoMapper;
import ru.croc.ugd.ssr.mapper.TradeApplicationFileMapper.TradeFileDto;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateTaskData;
import ru.croc.ugd.ssr.model.integration.mfr.MfrToDgpCompensationOfferInfo;
import ru.croc.ugd.ssr.model.trade.TradeAdditionDocument;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.document.TradeAdditionDocumentService;
import ru.croc.ugd.ssr.service.trade.TradeAdditionService;
import ru.croc.ugd.ssr.trade.TradeApplicationFileType;
import ru.reinform.types.StoreType;

import java.util.List;

/**
 *  5. Сведения о предложениях компенсации и вне района
 */
@Service
public class MfrToDgpCompensationOfferInfoService extends MfrToDgpFlowService {

    private final MfrToDgpCompensationOfferInfoMapper mfrToDgpCompensationOfferInfoMapper;

    public MfrToDgpCompensationOfferInfoService(
        final TradeAdditionDocumentService tradeAdditionDocumentService,
        final TradeAdditionService tradeAdditionService,
        final PersonDocumentService personDocumentService,
        final PersonInfoMfrFlowService personInfoMfrFlowService,
        final MfrToDgpCompensationOfferInfoMapper mfrToDgpCompensationOfferInfoMapper
    ) {
        super(tradeAdditionDocumentService, tradeAdditionService, personDocumentService, personInfoMfrFlowService);
        this.mfrToDgpCompensationOfferInfoMapper = mfrToDgpCompensationOfferInfoMapper;
    }

    @Override
    protected String retrieveSellId(final CoordinateTaskData coordinateTaskData) {
        return retrieveAttributes(coordinateTaskData).getSellId();
    }

    @Override
    protected TradeFileDto retrieveFileDto(final CoordinateTaskData coordinateTaskData) {
        return ofNullable(retrieveAttributes(coordinateTaskData))
            .map(MfrToDgpCompensationOfferInfo::getChedFileId)
            .map(chedFileId -> TradeFileDto.builder()
                .chedFileId(chedFileId)
                .storeType(StoreType.ISUR)
                .build())
            .orElse(null);
    }

    @Override
    protected void addFileStoreId(
        final TradeAdditionDocument tradeAdditionDocument, final TradeApplicationFileType tradeApplicationFileType
    ) {
        ofNullable(tradeApplicationFileType)
            .map(TradeApplicationFileType::getFileId)
            .ifPresent(fileStoreId -> tradeAdditionDocument.getDocument()
                .getTradeAdditionTypeData()
                .setApplicationFileId(fileStoreId));
        ofNullable(tradeApplicationFileType)
            .map(TradeApplicationFileType::getChedId)
            .ifPresent(chedFileId -> tradeAdditionDocument.getDocument()
                .getTradeAdditionTypeData()
                .setApplicationChedId(chedFileId));
    }

    @Override
    protected TradeAdditionDocument retrieveTradeAddition(
        final CoordinateTaskData coordinateTaskData,
        final String integrationFlowDocumentId,
        final TradeAdditionDocument tradeAdditionDocument
    ) {
        final MfrToDgpCompensationOfferInfo mfrToDgpCompensationOfferInfo = retrieveAttributes(coordinateTaskData);
        final List<PersonDocument> personDocuments = retrievePersonDocuments(
            mfrToDgpCompensationOfferInfo.getAffairId()
        );
        final EstateInfoDto oldEstate = nonNull(tradeAdditionDocument)
            ? retrieveOldEstate(personDocuments)
            : null;
        return mfrToDgpCompensationOfferInfoMapper.toTradeAdditionDocument(
            tradeAdditionDocument,
            mfrToDgpCompensationOfferInfo,
            integrationFlowDocumentId,
            oldEstate,
            retrievePersonIds(personDocuments)
        );
    }

    @Override
    protected void processAfterDeploy(final TradeAdditionDocument tradeAdditionDocument) {
        sendPersonInfoFlow(tradeAdditionDocument);
    }

    private MfrToDgpCompensationOfferInfo retrieveAttributes(final CoordinateTaskData coordinateTaskData) {
        return retrieveCustomAttributes(coordinateTaskData)
            .filter(attributes -> attributes instanceof MfrToDgpCompensationOfferInfo)
            .map(MfrToDgpCompensationOfferInfo.class::cast)
            .orElse(null);
    }
}
