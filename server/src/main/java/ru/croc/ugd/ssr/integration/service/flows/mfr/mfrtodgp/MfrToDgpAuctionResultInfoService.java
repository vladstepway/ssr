package ru.croc.ugd.ssr.integration.service.flows.mfr.mfrtodgp;

import static java.util.Optional.ofNullable;

import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.integration.service.flows.mfr.dgptomfr.PersonInfoMfrFlowService;
import ru.croc.ugd.ssr.integration.service.flows.mfr.mappers.mfrtodgp.MfrToDgpAuctionResultInfoMapper;
import ru.croc.ugd.ssr.mapper.TradeApplicationFileMapper.TradeFileDto;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateTaskData;
import ru.croc.ugd.ssr.model.integration.mfr.MfrToDgpAuctionResultInfo;
import ru.croc.ugd.ssr.model.trade.TradeAdditionDocument;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.document.TradeAdditionDocumentService;
import ru.croc.ugd.ssr.service.trade.TradeAdditionService;
import ru.croc.ugd.ssr.trade.TradeApplicationFileType;
import ru.reinform.types.StoreType;

/**
 *  11. Сведения о результатах проведения аукциона
 */
@Service
public class MfrToDgpAuctionResultInfoService extends MfrToDgpFlowService {

    private static final int PROTOCOL_FILE_TYPE = 1;

    private final MfrToDgpAuctionResultInfoMapper mfrToDgpAuctionResultInfoMapper;

    public MfrToDgpAuctionResultInfoService(
        final TradeAdditionDocumentService tradeAdditionDocumentService,
        final TradeAdditionService tradeAdditionService,
        final PersonDocumentService personDocumentService,
        final PersonInfoMfrFlowService personInfoMfrFlowService,
        final MfrToDgpAuctionResultInfoMapper mfrToDgpAuctionResultInfoMapper
    ) {
        super(tradeAdditionDocumentService, tradeAdditionService, personDocumentService, personInfoMfrFlowService);
        this.mfrToDgpAuctionResultInfoMapper = mfrToDgpAuctionResultInfoMapper;
    }

    @Override
    protected String retrieveSellId(final CoordinateTaskData coordinateTaskData) {
        return retrieveAttributes(coordinateTaskData).getSellId();
    }

    @Override
    protected TradeFileDto retrieveFileDto(final CoordinateTaskData coordinateTaskData) {
        return ofNullable(retrieveAttributes(coordinateTaskData))
            .map(MfrToDgpAuctionResultInfo::getChedFileId)
            .map(chedFileId -> TradeFileDto.builder()
                .chedFileId(chedFileId)
                .storeType(StoreType.ISUR)
                .fileType(PROTOCOL_FILE_TYPE)
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
                .setProtocolFileId(fileStoreId));
        ofNullable(tradeApplicationFileType)
            .map(TradeApplicationFileType::getChedId)
            .ifPresent(chedFileId -> tradeAdditionDocument.getDocument()
                .getTradeAdditionTypeData()
                .setProtocolChedId(chedFileId));
    }

    @Override
    protected TradeAdditionDocument retrieveTradeAddition(
        final CoordinateTaskData coordinateTaskData,
        final String integrationFlowDocumentId,
        final TradeAdditionDocument tradeAdditionDocument
    ) {
        final MfrToDgpAuctionResultInfo mfrToDgpAuctionResultInfo = retrieveAttributes(coordinateTaskData);
        return mfrToDgpAuctionResultInfoMapper.toTradeAdditionDocument(
            tradeAdditionDocument, mfrToDgpAuctionResultInfo, integrationFlowDocumentId
        );
    }

    private MfrToDgpAuctionResultInfo retrieveAttributes(final CoordinateTaskData coordinateTaskData) {
        return retrieveCustomAttributes(coordinateTaskData)
            .filter(attributes -> attributes instanceof MfrToDgpAuctionResultInfo)
            .map(MfrToDgpAuctionResultInfo.class::cast)
            .orElse(null);
    }
}
