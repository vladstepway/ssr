package ru.croc.ugd.ssr.mapper;

import static java.util.Optional.ofNullable;
import static org.mapstruct.ReportingPolicy.ERROR;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.croc.ugd.ssr.dto.tradeaddition.EstateInfoDto;
import ru.croc.ugd.ssr.dto.tradeaddition.NotificationInfoDto;
import ru.croc.ugd.ssr.dto.tradeaddition.PersonInfoDto;
import ru.croc.ugd.ssr.dto.tradeaddition.TradeAdditionDto;
import ru.croc.ugd.ssr.model.trade.TradeAdditionDocument;
import ru.croc.ugd.ssr.trade.AuctionOutput;
import ru.croc.ugd.ssr.trade.AuctionResult;
import ru.croc.ugd.ssr.trade.BuyInFunds;
import ru.croc.ugd.ssr.trade.BuyInStatus;
import ru.croc.ugd.ssr.trade.ClaimStatus;
import ru.croc.ugd.ssr.trade.CommissionDecisionResult;
import ru.croc.ugd.ssr.trade.CompensationStatus;
import ru.croc.ugd.ssr.trade.EstateInfoType;
import ru.croc.ugd.ssr.trade.NotificationInfo;
import ru.croc.ugd.ssr.trade.PersonInfoType;
import ru.croc.ugd.ssr.trade.TradeResult;
import ru.croc.ugd.ssr.trade.TradeType;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ERROR)
public interface TradeAdditionMapper {
    @Mapping(target = "id", source = "id")
    @Mapping(target = "affairId", source = "document.tradeAdditionTypeData.affairId")
    @Mapping(target = "applicationDate", source = "document.tradeAdditionTypeData.applicationDate")
    @Mapping(target = "attachedFileName", source = "document.tradeAdditionTypeData.attachedFileName")
    @Mapping(target = "batchDocumentId", source = "document.tradeAdditionTypeData.batchDocumentId")
    @Mapping(
        target = "buyInStatus",
        source = "document.tradeAdditionTypeData.buyInStatus",
        qualifiedByName = "toBuyInStatus"
    )
    @Mapping(
        target = "claimStatus",
        source = "document.tradeAdditionTypeData.claimStatus",
        qualifiedByName = "toClaimStatus"
    )
    @Mapping(
        target = "compensationStatus",
        source = "document.tradeAdditionTypeData.compensationStatus",
        qualifiedByName = "toCompensationStatus"
    )
    @Mapping(target = "commissionDecisionDate", source = "document.tradeAdditionTypeData.commissionDecisionDate")
    @Mapping(
        target = "commissionDecisionResult",
        source = "document.tradeAdditionTypeData.commissionDecisionResult",
        qualifiedByName = "toCommissionDecisionStatus"
    )
    @Mapping(target = "confirmed", source = "document.tradeAdditionTypeData.confirmed")
    @Mapping(target = "indexed", source = "document.tradeAdditionTypeData.indexed")
    @Mapping(target = "newEstates", source = "document.tradeAdditionTypeData.newEstates")
    @Mapping(target = "oldEstate", source = "document.tradeAdditionTypeData.oldEstate")
    @Mapping(target = "offerLetterDate", source = "document.tradeAdditionTypeData.offerLetterDate")
    @Mapping(target = "pageName", source = "document.tradeAdditionTypeData.pageName")
    @Mapping(target = "personsInfo", source = "document.tradeAdditionTypeData.personsInfo")
    @Mapping(target = "recordNumber", source = "document.tradeAdditionTypeData.recordNumber")
    @Mapping(
        target = "tradeType",
        source = "document.tradeAdditionTypeData.tradeType",
        qualifiedByName = "toTradeType"
    )
    @Mapping(target = "uniqueRecordKey", source = "document.tradeAdditionTypeData.uniqueRecordKey")
    @Mapping(target = "uploadedFileId", source = "document.tradeAdditionTypeData.uploadedFileId")
    @Mapping(
        target = "renovationFondStatus",
        source = "document.tradeAdditionTypeData.claimStatus",
        qualifiedByName = "toRenovationFondStatus"
    )
    @Mapping(target = "applicationFileId", source = "document.tradeAdditionTypeData.applicationFileId")
    @Mapping(target = "contractReadinessDate", source = "document.tradeAdditionTypeData.contractReadinessDate")
    @Mapping(target = "contractSignedDate", source = "document.tradeAdditionTypeData.contractSignedDate")
    @Mapping(target = "keysIssueDate", source = "document.tradeAdditionTypeData.keysIssueDate")
    @Mapping(target = "auctionDate", source = "document.tradeAdditionTypeData.auctionDate")
    @Mapping(target = "agreementDate", source = "document.tradeAdditionTypeData.agreementDate")
    @Mapping(target = "contractNumber", source = "document.tradeAdditionTypeData.contractNumber")
    @Mapping(target = "comment", source = "document.tradeAdditionTypeData.comment")
    @Mapping(
        target = "tradeResult",
        source = "document.tradeAdditionTypeData.tradeResult",
        qualifiedByName = "toTradeResult"
    )
    @Mapping(
        target = "auctionResult",
        source = "document.tradeAdditionTypeData.auctionResult",
        qualifiedByName = "toAuctionResult"
    )
    @Mapping(
        target = "sentNotifications",
        source = "document.tradeAdditionTypeData.sentNotifications",
        qualifiedByName = "toSentNotifications"
    )
    @Mapping(target = "protocolFileId", source = "document.tradeAdditionTypeData.protocolFileId")
    @Mapping(target = "commissionDeclineReason", source = "document.tradeAdditionTypeData.commissionDeclineReason")
    @Mapping(target = "plannedAuctionDate", source = "document.tradeAdditionTypeData.plannedAuctionDate")
    @Mapping(
        target = "auctionOutput",
        source = "document.tradeAdditionTypeData.auctionOutput",
        qualifiedByName = "toAuctionOutput"
    )
    @Mapping(
        target = "buyInFunds", source = "document.tradeAdditionTypeData.buyInFunds", qualifiedByName = "toBuyInFunds"
    )
    @Mapping(target = "claimCancelDate", source = "document.tradeAdditionTypeData.claimCancelDate")
    @Mapping(target = "registrationDate", source = "document.tradeAdditionTypeData.registrationDate")
    @Mapping(target = "contractTerminationDate", source = "document.tradeAdditionTypeData.contractTerminationDate")
    @Mapping(target = "fondReceiveDate", source = "document.tradeAdditionTypeData.fondReceiveDate")
    @Mapping(target = "terminated", source = "document.tradeAdditionTypeData.terminated")
    TradeAdditionDto toTradeAdditionDto(final TradeAdditionDocument tradeAdditionDocument);

    @Mapping(target = "unom", source = "estateInfoType.unom")
    @Mapping(target = "address", source = "estateInfoType.address")
    @Mapping(target = "cadNumber", source = "estateInfoType.cadNumber")
    @Mapping(target = "flatNumber", source = "estateInfoType.flatNumber")
    @Mapping(target = "rooms", source = "estateInfoType.rooms")
    EstateInfoDto toEstate(final EstateInfoType estateInfoType);

    List<EstateInfoDto> toEstates(final List<EstateInfoType> newEstates);

    @Mapping(target = "personId", source = "personInfoType.personId")
    @Mapping(target = "personFio", source = "personInfoType.personFio")
    @Mapping(target = "personDocumentId", source = "personInfoType.personDocumentId")
    PersonInfoDto toPersonInfoDto(final PersonInfoType personInfoType);

    List<PersonInfoDto> toPersonInfo(final List<PersonInfoType> personInfoTypes);

    @Named("toBuyInStatus")
    default String toBuyInStatus(final BuyInStatus buyInStatus) {
        return ofNullable(buyInStatus)
            .map(BuyInStatus::value)
            .orElse(null);
    }

    @Named("toClaimStatus")
    default String toClaimStatus(final ClaimStatus claimStatus) {
        return ofNullable(claimStatus)
            .map(ClaimStatus::value)
            .orElse(null);
    }

    @Named("toCompensationStatus")
    default String toCompensationStatus(final CompensationStatus compensationStatus) {
        return ofNullable(compensationStatus)
            .map(CompensationStatus::value)
            .orElse(null);
    }

    @Named("toCommissionDecisionStatus")
    default String toCommissionDecisionStatus(final CommissionDecisionResult commissionDecisionResult) {
        return ofNullable(commissionDecisionResult)
            .map(CommissionDecisionResult::value)
            .orElse(null);
    }

    @Named("toTradeType")
    default String toTradeType(final TradeType tradeType) {
        return ofNullable(tradeType)
            .map(TradeType::value)
            .orElse(null);
    }

    @Named("toRenovationFondStatus")
    default String toRenovationFondStatus(final ClaimStatus claimStatus) {
        return ofNullable(claimStatus)
            .map(ClaimStatus::value)
            .orElse(null);
    }

    @Named("toTradeResult")
    default String toTradeResult(final TradeResult tradeResult) {
        return ofNullable(tradeResult)
            .map(TradeResult::value)
            .orElse(null);
    }

    @Named("toAuctionResult")
    default String toAuctionResult(final AuctionResult auctionResult) {
        return ofNullable(auctionResult)
            .map(AuctionResult::value)
            .orElse(null);
    }

    @Mapping(target = "eventCode", source = "sentNotification.eventCode")
    @Mapping(target = "sentDate", source = "sentNotification.sentDate")
    @Mapping(target = "ok", source = "sentNotification.ok")
    @Mapping(target = "errorDescription", source = "sentNotification.errorDescription")
    NotificationInfoDto toSentNotification(final NotificationInfo sentNotification);

    List<NotificationInfoDto> toSentNotifications(final List<NotificationInfo> sentNotifications);

    @Named("toAuctionOutput")
    default String toAuctionOutput(final AuctionOutput auctionOutput) {
        return ofNullable(auctionOutput)
            .map(AuctionOutput::value)
            .orElse(null);
    }

    @Named("toBuyInFunds")
    default String toBuyInFunds(final BuyInFunds buyInFunds) {
        return ofNullable(buyInFunds)
            .map(BuyInFunds::value)
            .orElse(null);
    }
}
