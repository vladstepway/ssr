package ru.croc.ugd.ssr.solr.converter.trade;

import static org.mapstruct.ReportingPolicy.ERROR;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.croc.ugd.ssr.solr.UgdSsrTradeDataBatchStatus;
import ru.croc.ugd.ssr.trade.TradeDataBatchStatusType;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ERROR)
public interface SolrTradeAdditionBatchStatusMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(
        target = "ugdSsrTradeAdditionBatchStatusFileName",
        source = "uploadedFileName"
    )
    UgdSsrTradeDataBatchStatus toUgdSsrTradeAdditionBatchStatus(
        @MappingTarget final UgdSsrTradeDataBatchStatus ugdSsrTradeAdditionBatchStatus,
        final TradeDataBatchStatusType tradeDataBatchStatusType
    );


}
