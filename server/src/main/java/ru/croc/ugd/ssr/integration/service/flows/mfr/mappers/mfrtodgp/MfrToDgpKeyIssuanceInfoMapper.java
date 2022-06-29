package ru.croc.ugd.ssr.integration.service.flows.mfr.mappers.mfrtodgp;

import static java.util.Optional.ofNullable;
import static org.mapstruct.ReportingPolicy.ERROR;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.util.CollectionUtils;
import ru.croc.ugd.ssr.model.integration.mfr.MfrToDgpKeyIssuanceInfo;
import ru.croc.ugd.ssr.model.trade.TradeAdditionDocument;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ERROR)
public interface MfrToDgpKeyIssuanceInfoMapper extends MfrToDgpInfoMapper {

    @Mapping(target = "document.tradeAdditionTypeData.mfrFlow", constant = "true")
    @Mapping(target = "document.tradeAdditionTypeData.confirmed", constant = "true")
    @Mapping(target = "document.tradeAdditionTypeData.indexed", constant = "false")
    @Mapping(target = "document.tradeAdditionTypeData.sellId", source = "info.sellId")
    @Mapping(target = "document.tradeAdditionTypeData.affairId", source = "info.affairId")
    @Mapping(target = "document.tradeAdditionTypeData.personsInfo", source = "personIds")
    @Mapping(target = "document.tradeAdditionTypeData.keysIssueDate", source = "info.keyIssuanceDate")
    @Mapping(target = "document.tradeAdditionTypeData.integrationFlowDocumentId", source = "integrationFlowDocumentId")
    TradeAdditionDocument toTradeAdditionDocument(
        @MappingTarget final TradeAdditionDocument tradeAdditionDocument,
        final MfrToDgpKeyIssuanceInfo info,
        final String integrationFlowDocumentId,
        final List<String> personIds
    );

    default TradeAdditionDocument toTradeAdditionDocument(
        final TradeAdditionDocument tradeAdditionDocument,
        final MfrToDgpKeyIssuanceInfo info,
        final String integrationFlowDocumentId
    ) {
        final List<String> personIds = ofNullable(info)
            .map(MfrToDgpKeyIssuanceInfo::getPersonIds)
            .filter(collection -> !CollectionUtils.isEmpty(collection))
            .orElse(null);
        return toTradeAdditionDocument(tradeAdditionDocument, info, integrationFlowDocumentId, personIds);
    }
}
