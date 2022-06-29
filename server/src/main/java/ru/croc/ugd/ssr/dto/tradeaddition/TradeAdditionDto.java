package ru.croc.ugd.ssr.dto.tradeaddition;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.util.List;

@Value
@Builder
public class TradeAdditionDto {
    private final String id;
    private final String affairId;
    private final LocalDate applicationDate;
    private final String attachedFileName;
    private final String batchDocumentId;
    private final String buyInStatus;
    private final String claimStatus;
    private final String compensationStatus;
    private final LocalDate commissionDecisionDate;
    private final String commissionDecisionResult;
    private final boolean confirmed;
    private final boolean indexed;
    private final List<EstateInfoDto> newEstates;
    private final EstateInfoDto oldEstate;
    private final LocalDate offerLetterDate;
    private final String pageName;
    private final List<PersonInfoDto> personsInfo;
    private final String recordNumber;
    private final String tradeType;
    private final String uniqueRecordKey;
    private final String uploadedFileId;
    private final String renovationFondStatus;
    private final String tradeResult;
    private final LocalDate agreementDate;
    private final LocalDate auctionDate;
    private final String auctionResult;
    private final LocalDate contractReadinessDate;
    private final LocalDate contractSignedDate;
    private final String contractNumber;
    private final LocalDate keysIssueDate;
    private final String comment;
    private final String applicationFileId;
    private final List<NotificationInfoDto> sentNotifications;
    private final String protocolFileId;
    private final String commissionDeclineReason;
    private final LocalDate plannedAuctionDate;
    private final String auctionOutput;
    private final String buyInFunds;
    private final LocalDate claimCancelDate;
    private final LocalDate registrationDate;
    private final LocalDate contractTerminationDate;
    private final LocalDate fondReceiveDate;
    private final boolean terminated;
}
