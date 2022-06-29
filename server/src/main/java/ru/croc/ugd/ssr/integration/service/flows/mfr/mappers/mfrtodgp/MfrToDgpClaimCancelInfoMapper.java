package ru.croc.ugd.ssr.integration.service.flows.mfr.mappers.mfrtodgp;

import static org.mapstruct.ReportingPolicy.ERROR;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import ru.croc.ugd.ssr.model.integration.mfr.MfrToDgpClaimCancelInfo;
import ru.croc.ugd.ssr.model.trade.TradeAdditionDocument;
import ru.croc.ugd.ssr.trade.ClaimStatus;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ERROR)
public interface MfrToDgpClaimCancelInfoMapper {

    @Mapping(target = "document.tradeAdditionTypeData.mfrFlow", constant = "true")
    @Mapping(target = "document.tradeAdditionTypeData.confirmed", constant = "true")
    @Mapping(target = "document.tradeAdditionTypeData.indexed", constant = "false")
    @Mapping(target = "document.tradeAdditionTypeData.sellId", source = "info.sellId")
    @Mapping(
        target = "document.tradeAdditionTypeData.claimStatus", source = "info.reason", qualifiedByName = "toClaimStatus"
    )
    @Mapping(target = "document.tradeAdditionTypeData.claimCancelDate", source = "info.claimCancelDate")
    @Mapping(target = "document.tradeAdditionTypeData.integrationFlowDocumentId", source = "integrationFlowDocumentId")
    TradeAdditionDocument toTradeAdditionDocument(
        @MappingTarget final TradeAdditionDocument tradeAdditionDocument,
        final MfrToDgpClaimCancelInfo info,
        final String integrationFlowDocumentId
    );

    @Named("toClaimStatus")
    default ClaimStatus toClaimStatus(final String reason) {
        switch (reason) {
            case "1":
                return ClaimStatus.RECALLED;
            case "2":
                return ClaimStatus.REJECTED_BY_COMMISSION;
            case "3":
                return ClaimStatus.REJECTED_WINNER_AUCTION;
            case "4":
                return ClaimStatus.REJECTED_BY_PRIORITY;
            case "5":
                return ClaimStatus.AUCTION_LOST;
            case "6":
                return ClaimStatus.REJECTED;
            case "7":
                return ClaimStatus.REJECTED_COMPENSATION;
            default:
                return null;
        }
    }
}
