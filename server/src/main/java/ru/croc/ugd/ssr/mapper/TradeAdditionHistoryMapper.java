package ru.croc.ugd.ssr.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.croc.ugd.ssr.dto.tradeaddition.TradeAdditionHistoryDto;
import ru.croc.ugd.ssr.integrationflow.IntegrationFlowData;
import ru.croc.ugd.ssr.model.trade.TradeAdditionHistoryDocument;
import ru.croc.ugd.ssr.trade.TradeAddition;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface TradeAdditionHistoryMapper {

    @Mapping(target = "document.tradeAdditionHistoryData.updateDateTime", source = "integrationFlowData.createdAt")
    @Mapping(
        target = "document.tradeAdditionHistoryData.mfrFlowFileStoreId", source = "integrationFlowData.fileStoreId"
    )
    @Mapping(
        target = "document.tradeAdditionHistoryData.uniqueRecordKey",
        source = "tradeAddition.tradeAdditionTypeData.uniqueRecordKey"
    )
    @Mapping(target = "document.tradeAdditionHistoryData.sellId", source = "tradeAddition.tradeAdditionTypeData.sellId")
    @Mapping(target = "document.tradeAdditionHistoryData.tradeAdditionDocumentId", source = "tradeAddition.documentID")
    @Mapping(target = "document.tradeAdditionHistoryData.isIndexed", constant = "false")
    TradeAdditionHistoryDocument toTradeAdditionHistoryDocument(
        final TradeAddition tradeAddition, final IntegrationFlowData integrationFlowData
    );

    List<TradeAdditionHistoryDto> toTradeAdditionHistoryDtos(
        final List<TradeAdditionHistoryDocument> tradeAdditionHistoryDocuments
    );

    @Mapping(target = "uploadedFileId", source = "document.tradeAdditionHistoryData.uploadedFileId")
    @Mapping(target = "uniqueRecordKey", source = "document.tradeAdditionHistoryData.uniqueRecordKey")
    @Mapping( target = "tradeAdditionDocumentId",source = "document.tradeAdditionHistoryData.tradeAdditionDocumentId")
    @Mapping(target = "sellId", source = "document.tradeAdditionHistoryData.sellId")
    @Mapping(target = "updateDateTime", source = "document.tradeAdditionHistoryData.updateDateTime")
    @Mapping(target = "pageName", source = "document.tradeAdditionHistoryData.pageName")
    @Mapping(target = "recordNumber", source = "document.tradeAdditionHistoryData.recordNumber")
    @Mapping(target = "comment", source = "document.tradeAdditionHistoryData.comment")
    @Mapping(target = "changes", source = "document.tradeAdditionHistoryData.changes")
    @Mapping(target = "mfrFlowFileStoreId", source = "document.tradeAdditionHistoryData.mfrFlowFileStoreId")
    TradeAdditionHistoryDto toTradeAdditionHistoryDto(final TradeAdditionHistoryDocument tradeAdditionHistoryDocument);
}
