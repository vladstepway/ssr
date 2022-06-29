package ru.croc.ugd.ssr.mapper;

import static org.mapstruct.ReportingPolicy.ERROR;

import lombok.Builder;
import lombok.Value;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.croc.ugd.ssr.model.trade.TradeApplicationFileDocument;
import ru.reinform.types.StoreType;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ERROR)
public interface TradeApplicationFileMapper {

    @Mapping(target = "document.tradeApplicationFileTypeData.chedId", source = "chedFileId")
    @Mapping(target = "document.tradeApplicationFileTypeData.fileId", source = "fileStoreId")
    @Mapping(target = "document.tradeApplicationFileTypeData.fileType", source = "fileType")
    @Mapping(
        target = "document.tradeApplicationFileTypeData.mfrFlowTradeAdditionDocumentId",
        source = "mfrFlowTradeAdditionDocumentId"
    )
    TradeApplicationFileDocument toTradeApplicationFileDocument(
        final Integer fileType,
        final String fileStoreId,
        final String chedFileId,
        final String mfrFlowTradeAdditionDocumentId
    );

    @Value
    @Builder
    class TradeFileDto {
        private final String chedFileId;
        private final StoreType storeType;
        private final Integer fileType;
    }
}
