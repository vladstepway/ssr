package ru.croc.ugd.ssr.service.trade.notification;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.integration.service.notification.ElkUserNotificationService;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.trade.TradeAdditionDocument;
import ru.croc.ugd.ssr.model.trade.TradeApplicationFileDocument;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.document.TradeAdditionDocumentService;
import ru.croc.ugd.ssr.service.trade.TradeAdditionService;
import ru.croc.ugd.ssr.service.trade.model.TradeAdditionNotificationData;
import ru.croc.ugd.ssr.trade.AuctionResult;
import ru.croc.ugd.ssr.trade.BuyInStatus;
import ru.croc.ugd.ssr.trade.ClaimStatus;
import ru.croc.ugd.ssr.trade.CompensationStatus;
import ru.croc.ugd.ssr.trade.NotificationInfo;
import ru.croc.ugd.ssr.trade.PersonInfoType;
import ru.croc.ugd.ssr.trade.TradeAddition;
import ru.croc.ugd.ssr.trade.TradeAdditionType;
import ru.croc.ugd.ssr.trade.TradeApplicationFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class TradeAdditionNotificationService {

    private final ElkUserNotificationService elkUserNotificationService;
    private final PersonDocumentService personDocumentService;
    private final TradeAdditionDocumentService tradeAdditionDocumentService;
    private final TradeAdditionService tradeAdditionService;

    public TradeAdditionNotificationService(
        final ElkUserNotificationService elkUserNotificationService,
        final PersonDocumentService personDocumentService,
        final TradeAdditionDocumentService tradeAdditionDocumentService,
        @Lazy final TradeAdditionService tradeAdditionService
    ) {
        this.elkUserNotificationService = elkUserNotificationService;
        this.personDocumentService = personDocumentService;
        this.tradeAdditionDocumentService = tradeAdditionDocumentService;
        this.tradeAdditionService = tradeAdditionService;
    }

    @Async
    public void processNotification(
        final TradeAddition tradeAddition,
        final TradeApplicationFileDocument tradeApplicationFileDocument
    ) {
        if (shouldSendNotification(tradeAddition.getTradeAdditionTypeData())) {
            findRequiredNotification(tradeAddition)
                .ifPresent(tradeAdditionNotificationData ->
                    sendNotificationForTradeAddition(
                        tradeAddition,
                        tradeApplicationFileDocument,
                        tradeAdditionNotificationData
                    ));
        }
    }

    private boolean shouldSendNotification(final TradeAdditionType tradeAdditionType) {
        final LocalDate maxActionDate = getMaxActionDate(tradeAdditionType);
        return maxActionDate != null && maxActionDate.plusDays(5).isAfter(LocalDate.now());
    }

    private LocalDate getMaxActionDate(final TradeAdditionType tradeAdditionType) {
        return Stream.of(
                tradeAdditionType.getOfferLetterDate(),
                tradeAdditionType.getApplicationDate(),
                tradeAdditionType.getAgreementDate(),
                tradeAdditionType.getCommissionDecisionDate(),
                tradeAdditionType.getAuctionDate(),
                tradeAdditionType.getContractReadinessDate(),
                tradeAdditionType.getContractSignedDate(),
                tradeAdditionType.getKeysIssueDate())
            .filter(Objects::nonNull)
            .max(LocalDate::compareTo)
            .orElse(null);
    }

    private Optional<TradeAdditionNotificationData> findRequiredNotification(final TradeAddition tradeAddition) {
        final TradeAdditionType tradeAdditionType = tradeAddition.getTradeAdditionTypeData();
        final NotificationInfo lastSentNotification = ListUtils.emptyIfNull(tradeAdditionType.getSentNotifications())
            .stream()
            .sorted(Comparator.comparing(NotificationInfo::getSentDate).reversed())
            .filter(NotificationInfo::isOk)
            .findFirst()
            .orElse(null);
        final BuyInStatus buyInStatus = tradeAdditionType.getBuyInStatus();
        final CompensationStatus compensationStatus = tradeAdditionType.getCompensationStatus();
        final Enum actualStatus = buyInStatus == null ? compensationStatus : buyInStatus;
        final ClaimStatus claimStatus = tradeAdditionType.getClaimStatus();
        return Arrays.stream(TradeAdditionNotificationData.values())
            .filter(tradeAdditionNotificationData -> isNotificationDataMatch(
                tradeAdditionNotificationData,
                actualStatus,
                lastSentNotification,
                claimStatus
            ))
            .findFirst()
            .filter(data -> isRequiredNotification(tradeAdditionType, data.getRequiredStatus()));
    }

    private boolean isRequiredNotification(final TradeAdditionType tradeAdditionType, final Enum requiredStatus) {
        final AuctionResult auctionResult = tradeAdditionType.getAuctionResult();

        return !isAuctionWon(requiredStatus)
            || isAuctionWonAndHeld(requiredStatus, auctionResult);
    }

    private boolean isAuctionWon(final Enum requiredStatus) {
        return BuyInStatus.AUCTION_WON.equals(requiredStatus);
    }

    private boolean isAuctionWonAndHeld(final Enum requiredStatus, final AuctionResult auctionResult) {
        return BuyInStatus.AUCTION_WON.equals(requiredStatus) && AuctionResult.HELD.equals(auctionResult);
    }

    private boolean isNotificationDataMatch(
        final TradeAdditionNotificationData notificationDataToVerify,
        final Enum actualStatus,
        final NotificationInfo lastSentNotificationInfo,
        final ClaimStatus claimStatus
    ) {
        final boolean isStatusMatch = notificationDataToVerify.getRequiredStatus().equals(actualStatus);
        final boolean isClaimStatusMatch = isNull(notificationDataToVerify.getClaimStatus())
            || Objects.equals(claimStatus, notificationDataToVerify.getClaimStatus());

        if (lastSentNotificationInfo == null || !isStatusMatch) {
            return isStatusMatch && isClaimStatusMatch;
        }
        final String lastSentEventCode = lastSentNotificationInfo.getEventCode();
        final int lastSentPriority = TradeAdditionNotificationData.getPriorityByEventCode(lastSentEventCode);

        final boolean isSentPriorityLessOrEquals = lastSentPriority <= notificationDataToVerify.getPriority();
        final boolean isEventCodeSentSame = Objects.equals(lastSentEventCode, notificationDataToVerify.getEventCode());

        return isSentPriorityLessOrEquals && !isEventCodeSentSame && isClaimStatusMatch;
    }

    private void sendNotificationForTradeAddition(
        final TradeAddition tradeAddition,
        final TradeApplicationFileDocument tradeApplicationFileDocument,
        final TradeAdditionNotificationData tradeAdditionNotificationData
    ) {
        log.debug("Starting sending notification: claimStatus = {}, requiredStatus = {}, eventCode = {}, title = {}",
            nonNull(tradeAdditionNotificationData.getClaimStatus())
                ? tradeAdditionNotificationData.getClaimStatus().value()
                : null,
            tradeAdditionNotificationData.getRequiredStatus(),
            tradeAdditionNotificationData.getEventCode(),
            tradeAdditionNotificationData.getNotificationTitle()
        );
        final AtomicBoolean isAnyNotOk = new AtomicBoolean(false);
        final List<String> personDocumentIds = tradeAddition
            .getTradeAdditionTypeData()
            .getPersonsInfo()
            .stream()
            .map(PersonInfoType::getPersonDocumentId)
            .collect(Collectors.toList());
        personDocumentService.fetchDocuments(personDocumentIds)
            .forEach(personDocument -> {
                boolean result = sendNotificationForPerson(
                    personDocument,
                    tradeApplicationFileDocument,
                    tradeAdditionNotificationData
                );
                if (!result) {
                    isAnyNotOk.set(true);
                }
            });
        setNewNotificationInfo(tradeAddition, tradeAdditionNotificationData, isAnyNotOk.get());
    }

    private void setNewNotificationInfo(final TradeAddition tradeAddition,
                                        final TradeAdditionNotificationData tradeAdditionNotificationData,
                                        final boolean anyFailed) {
        final TradeAdditionType tradeAdditionType = tradeAddition.getTradeAdditionTypeData();

        final List<NotificationInfo> notificationInfos = tradeAdditionType.getSentNotifications();
        final NotificationInfo newNotificationInfo = new NotificationInfo();
        newNotificationInfo.setEventCode(tradeAdditionNotificationData.getEventCode());
        newNotificationInfo.setOk(!anyFailed);
        newNotificationInfo.setSentDate(LocalDateTime.now());

        notificationInfos.add(newNotificationInfo);
        final TradeAdditionDocument tradeAdditionDocument = tradeAdditionDocumentService
            .getTradeAdditionDocumentFromType(tradeAdditionType);
        tradeAdditionDocument.getDocument().setDocumentID(tradeAddition.getDocumentID());
        tradeAdditionDocumentService.updateDocument(
            tradeAddition.getDocumentID(),
            tradeAdditionDocument,
            true,
            tradeAdditionType.isIndexed() != null && tradeAdditionType.isIndexed(),
            null
        );
    }

    private boolean sendNotificationForPerson(
        final PersonDocument personDocument,
        final TradeApplicationFileDocument tradeApplicationFileDocument,
        final TradeAdditionNotificationData tradeAdditionNotificationData
    ) {
        if (CompensationStatus.LETTER_ACCEPTED.equals(tradeAdditionNotificationData.getRequiredStatus())
            || CompensationStatus.INSPECTION_COMPETED.equals(tradeAdditionNotificationData.getRequiredStatus())
        ) {
            tradeAdditionService.sendAttachmentsToChedIfNeeded(tradeApplicationFileDocument);
        }
        try {
            elkUserNotificationService.sendTradeAdditionNotification(
                tradeAdditionNotificationData,
                ofNullable(tradeApplicationFileDocument)
                    .map(TradeApplicationFileDocument::getDocument)
                    .map(TradeApplicationFile::getTradeApplicationFileTypeData)
                    .orElse(null),
                personDocument
            );
        } catch (Exception ex) {
            log.error("Could not send notification to person: {}", personDocument.getId(), ex);
            return false;
        }
        return true;
    }
}
