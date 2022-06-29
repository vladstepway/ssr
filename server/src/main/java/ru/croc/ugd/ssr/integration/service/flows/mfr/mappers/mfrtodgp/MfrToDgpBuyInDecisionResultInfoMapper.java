package ru.croc.ugd.ssr.integration.service.flows.mfr.mappers.mfrtodgp;

import static java.util.Optional.ofNullable;
import static org.mapstruct.ReportingPolicy.ERROR;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.util.CollectionUtils;
import ru.croc.ugd.ssr.model.integration.mfr.MfrToDgpBuyInDecisionResultInfo;
import ru.croc.ugd.ssr.model.trade.TradeAdditionDocument;
import ru.croc.ugd.ssr.trade.CommissionDecisionResult;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ERROR)
public interface MfrToDgpBuyInDecisionResultInfoMapper extends MfrToDgpInfoMapper {

    @Mapping(target = "document.tradeAdditionTypeData.mfrFlow", constant = "true")
    @Mapping(target = "document.tradeAdditionTypeData.confirmed", constant = "true")
    @Mapping(target = "document.tradeAdditionTypeData.indexed", constant = "false")
    @Mapping(target = "document.tradeAdditionTypeData.sellId", source = "info.sellId")
    @Mapping(target = "document.tradeAdditionTypeData.affairId", source = "info.affairId")
    @Mapping(target = "document.tradeAdditionTypeData.personsInfo", source = "personIds")
    @Mapping(target = "document.tradeAdditionTypeData.commissionDecisionDate", source = "info.statusDate")
    @Mapping(
        target = "document.tradeAdditionTypeData.commissionDecisionResult",
        source = "info.decisionResult",
        qualifiedByName = "toCommissionDecisionResult"
    )
    @Mapping(target = "document.tradeAdditionTypeData.commissionDeclineReason", source = "info.declineReason")
    @Mapping(target = "document.tradeAdditionTypeData.integrationFlowDocumentId", source = "integrationFlowDocumentId")
    TradeAdditionDocument toTradeAdditionDocument(
        @MappingTarget final TradeAdditionDocument tradeAdditionDocument,
        final MfrToDgpBuyInDecisionResultInfo info,
        final String integrationFlowDocumentId,
        final List<String> personIds
    );

    default TradeAdditionDocument toTradeAdditionDocument(
        final TradeAdditionDocument tradeAdditionDocument,
        final MfrToDgpBuyInDecisionResultInfo info,
        final String integrationFlowDocumentId
    ) {
        final List<String> personIds = ofNullable(info)
            .map(MfrToDgpBuyInDecisionResultInfo::getPersonIds)
            .filter(collection -> !CollectionUtils.isEmpty(collection))
            .orElse(null);
        return toTradeAdditionDocument(tradeAdditionDocument, info, integrationFlowDocumentId, personIds);
    }

    @Named("toCommissionDecisionResult")
    default CommissionDecisionResult toCommissionDecisionResult(final String decisionResult) {
        return CommissionDecisionResult.fromValue(decisionResult);
    }
}
