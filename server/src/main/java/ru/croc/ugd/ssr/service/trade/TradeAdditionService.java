package ru.croc.ugd.ssr.service.trade;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.Person;
import ru.croc.ugd.ssr.exception.SsrException;
import ru.croc.ugd.ssr.integration.service.flows.TradeAdditionFlowsService;
import ru.croc.ugd.ssr.integrationflow.IntegrationFlowData;
import ru.croc.ugd.ssr.mapper.TradeAdditionHistoryMapper;
import ru.croc.ugd.ssr.mapper.TradeApplicationFileMapper;
import ru.croc.ugd.ssr.mapper.TradeApplicationFileMapper.TradeFileDto;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPTradeAdditionType;
import ru.croc.ugd.ssr.model.integrationflow.IntegrationFlowDocument;
import ru.croc.ugd.ssr.model.trade.TradeAdditionDocument;
import ru.croc.ugd.ssr.model.trade.TradeAdditionHistoryDocument;
import ru.croc.ugd.ssr.model.trade.TradeApplicationFileDocument;
import ru.croc.ugd.ssr.service.ChedFileService;
import ru.croc.ugd.ssr.service.document.TradeAdditionDocumentService;
import ru.croc.ugd.ssr.service.document.TradeAdditionHistoryDocumentService;
import ru.croc.ugd.ssr.service.document.TradeApplicationFileDocumentService;
import ru.croc.ugd.ssr.service.guardianship.GuardianshipService;
import ru.croc.ugd.ssr.service.trade.model.TradeAdditionDecodedValues;
import ru.croc.ugd.ssr.service.trade.model.TradeAdditionPersonDecodedValue;
import ru.croc.ugd.ssr.service.trade.notification.TradeAdditionNotificationService;
import ru.croc.ugd.ssr.service.trade.status.StatusCalculateCommand;
import ru.croc.ugd.ssr.service.trade.status.TradeAdditionStatusCalculator;
import ru.croc.ugd.ssr.service.validator.impl.trade.TradeAdditionObjectValidator;
import ru.croc.ugd.ssr.service.validator.model.ValidationResult;
import ru.croc.ugd.ssr.trade.BuyInStatus;
import ru.croc.ugd.ssr.trade.CompensationStatus;
import ru.croc.ugd.ssr.trade.TradeAddition;
import ru.croc.ugd.ssr.trade.TradeAdditionType;
import ru.croc.ugd.ssr.trade.TradeApplicationFileType;
import ru.croc.ugd.ssr.trade.TradeType;
import ru.reinform.types.StoreType;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TradeAdditionService {

    //TODO update ched document configs
    @Value("${integration.ched.tradeAdditionLetterFilesCode:12027}")
    private String tradeAdditionLetterFilesCode;
    @Value("${integration.ched.tradeAdditionLetterFilesDocType:AgreementEquivalentApartment}")
    private String tradeAdditionLetterFilesDocType;

    private final TradeAdditionDocumentService tradeAdditionDocumentService;
    private final TradeAdditionStatusCalculator tradeAdditionStatusCalculator;
    private final TradeAdditionNotificationService tradeAdditionNotificationService;
    private final TradeAdditionHistoryDocumentService tradeAdditionHistoryDocumentService;
    private final TradeAdditionObjectValidator<TradeAdditionType> tradeAdditionObjectValidator;
    private final TradeAdditionValueDecoder tradeAdditionValueDecoder;
    private final TradeAdditionFlowsService tradeAdditionFlowsService;
    private final GuardianshipService guardianshipService;
    private final TradeApplicationFileMapper tradeApplicationFileMapper;
    private final TradeApplicationFileDocumentService tradeApplicationFileDocumentService;
    private final TradeAdditionHistoryMapper tradeAdditionHistoryMapper;
    private final ChedFileService chedFileService;

    public void populateCalculatedStatuses(
        final TradeAdditionType newTradeAddition, final TradeAdditionDecodedValues tradeAdditionDecodedValues
    ) {
        final StatusCalculateCommand statusCalculateCommand = StatusCalculateCommand
            .builder()
            .personTypes(tradeAdditionDecodedValues.getPersonIdDataMapping()
                .values()
                .stream()
                .map(TradeAdditionPersonDecodedValue::getPerson)
                .map(Person::getPersonData)
                .collect(Collectors.toList()))
            .tradeAdditionType(newTradeAddition)
            .build();
        final BuyInStatus buyInStatus = tradeAdditionStatusCalculator
            .calculateBuyInStatus(statusCalculateCommand);
        final CompensationStatus compensationStatus = tradeAdditionStatusCalculator
            .calculateClaimStatus(statusCalculateCommand);

        newTradeAddition.setBuyInStatus(buyInStatus);
        newTradeAddition.setCompensationStatus(compensationStatus);
    }

    public void deployDocument(
        final TradeAdditionDocument tradeAdditionDocument,
        final TradeApplicationFileDocument tradeApplicationFileDocument
    ) {
        addApplicationFile(tradeAdditionDocument, tradeApplicationFileDocument);
        tradeAdditionNotificationService.processNotification(
            tradeAdditionDocument.getDocument(), tradeApplicationFileDocument
        );
        hideExistingIndexedTradeAddition(tradeAdditionDocument);
        tradeAdditionDocumentService.indexDocument(tradeAdditionDocument);
        startHandleGuardianshipRequestTaskIfNeeded(tradeAdditionDocument);
        tradeAdditionHistoryDocumentService.findHistoryByTradeAdditionId(tradeAdditionDocument.getId())
            .forEach(this::indexHistoryDocument);
    }

    public TradeAdditionDocument createTradeAdditionByMfrFlow(
        final TradeAdditionDocument newTradeAdditionDocument, final boolean existsSavedTradeAddition
    ) {
        final TradeAdditionType newTradeAddition = newTradeAdditionDocument.getDocument().getTradeAdditionTypeData();
        final ValidationResult tradeAdditionValidationResult = tradeAdditionObjectValidator.validate(newTradeAddition);
        if (!tradeAdditionValidationResult.isValid()) {
            throw new SsrException("Trade addition is invalid.");
        }
        if (!tradeAdditionValueDecoder.existsMappings()) {
            tradeAdditionValueDecoder.loadMappings();
        }
        final TradeAdditionDecodedValues tradeAdditionDecodedValues =
            tradeAdditionValueDecoder.decodeTradeAdditionCodes(newTradeAddition);
        if (tradeAdditionDecodedValues.isAnyNotFound()) {
            throw new SsrException("Failed to decrypt all codes.");
        }
        tradeAdditionValueDecoder.populateDecodedValues(tradeAdditionDecodedValues, newTradeAddition);
        populateCalculatedStatuses(newTradeAddition, tradeAdditionDecodedValues);

        return tradeAdditionDocumentService.createDocument(
            newTradeAdditionDocument,
            false,
            existsSavedTradeAddition ? "updateByMfrFlow" : "createByMfrFlow"
        );
    }

    public void saveTradeAdditionHistoryByMfrFlow(
        final TradeAdditionDocument tradeAdditionDocument, final IntegrationFlowDocument integrationFlowDocument
    ) {
        final TradeAddition tradeAddition = tradeAdditionDocument.getDocument();
        final IntegrationFlowData integrationFlowData = integrationFlowDocument.getDocument().getIntegrationFlowData();
        final TradeAdditionHistoryDocument tradeAdditionHistoryDocument =
            tradeAdditionHistoryMapper.toTradeAdditionHistoryDocument(tradeAddition, integrationFlowData);
        tradeAdditionHistoryDocumentService.createDocument(
            tradeAdditionHistoryDocument, true, "saveTradeAdditionHistoryByMfrFlow"
        );
    }

    private void indexHistoryDocument(final TradeAdditionHistoryDocument tradeAdditionHistoryDocument) {
        tradeAdditionHistoryDocument.getDocument().getTradeAdditionHistoryData().setIsIndexed(true);
        tradeAdditionHistoryDocumentService.updateDocument(tradeAdditionHistoryDocument.getId(),
            tradeAdditionHistoryDocument, true, true, null);
    }

    private void hideExistingIndexedTradeAddition(final TradeAdditionDocument tradeAdditionDocument) {
        if (tradeAdditionDocument.getDocument().getTradeAdditionTypeData().isMfrFlow()) {
            final String sellId = tradeAdditionDocument.getDocument()
                .getTradeAdditionTypeData()
                .getSellId();
            tradeAdditionDocumentService.fetchIndexedBySellId(sellId)
                .ifPresent(tradeAdditionDocumentService::unindexDocument);
        } else {
            final String uniqueRecordKey = tradeAdditionDocument.getDocument()
                .getTradeAdditionTypeData()
                .getUniqueRecordKey();
            tradeAdditionDocumentService.fetchIndexedByUniqueKey(uniqueRecordKey)
                .ifPresent(tradeAdditionDocumentService::unindexDocument);
        }
    }

    private void startHandleGuardianshipRequestTaskIfNeeded(final TradeAdditionDocument tradeAdditionDocument) {
        final TradeAdditionType tradeAddition =
            tradeAdditionDocument.getDocument().getTradeAdditionTypeData();
        if (tradeAddition.isConfirmed()
            && tradeAddition.isIndexed()
            && (tradeAddition.getTradeType() == TradeType.COMPENSATION
            || tradeAddition.getTradeType() == TradeType.OUT_OF_DISTRICT)
        ) {
            final String affairId = tradeAddition.getAffairId();
            guardianshipService.checkGuardianshipRequestAndStartHandle(affairId);
        }
    }

    public void deployTradeAddition(
        final TradeAdditionDocument tradeAdditionDocument,
        final TradeApplicationFileDocument tradeApplicationFileDocument
    ) {
        deployDocument(tradeAdditionDocument, tradeApplicationFileDocument);
        sendTradeAdditionDgiFlow(
            tradeAdditionDocument.getDocument().getTradeAdditionTypeData()
        );
    }

    private void sendTradeAdditionDgiFlow(final TradeAdditionType tradeAdditionTypeData) {
        final SuperServiceDGPTradeAdditionType.TradeAdditionInfo tradeAdditionInfo =
            new SuperServiceDGPTradeAdditionType.TradeAdditionInfo();
        tradeAdditionInfo.setTradeAdditionTypeData(tradeAdditionTypeData);

        final SuperServiceDGPTradeAdditionType superServiceDgpTradeAdditionType =
            new SuperServiceDGPTradeAdditionType();
        superServiceDgpTradeAdditionType.getTradeAdditionInfo().add(tradeAdditionInfo);
        tradeAdditionFlowsService.sendTradeAddition(superServiceDgpTradeAdditionType);
    }

    public TradeApplicationFileDocument createTradeApplicationFile(
        final TradeAdditionDocument tradeAdditionDocument,
        final IntegrationFlowDocument integrationFlowDocument,
        final TradeFileDto tradeFileDto
    ) {
        if (isNull(tradeFileDto)) {
            return null;
        }

        final String fileStoreId = ofNullable(tradeFileDto.getChedFileId())
            .map(chedFileId -> chedFileService.extractFileFromChedAndGetFileLink(
                chedFileId, integrationFlowDocument.getFolderId(), tradeFileDto.getStoreType()
            ))
            .orElse(null);

        final String chedFileId = StoreType.GU_DOCS.equals(tradeFileDto.getStoreType())
            ? tradeFileDto.getChedFileId()
            : ofNullable(fileStoreId).map(this::uploadFileToChed).orElse(null);
        final TradeApplicationFileDocument tradeApplicationFileDocument =
            tradeApplicationFileMapper.toTradeApplicationFileDocument(
                tradeFileDto.getFileType(), fileStoreId, chedFileId, tradeAdditionDocument.getId()
            );
        return tradeApplicationFileDocumentService.createDocument(
            tradeApplicationFileDocument, true, "createTradeApplicationFile"
        );
    }

    private String uploadFileToChed(final String fileStoreId) {
        return chedFileService.uploadFileToChed(
            fileStoreId, tradeAdditionLetterFilesCode, tradeAdditionLetterFilesDocType
        );
    }

    public void sendAttachmentsToChedIfNeeded(final TradeApplicationFileDocument tradeApplicationFileDocument) {
        if (nonNull(tradeApplicationFileDocument)) {
            final TradeApplicationFileType tradeApplicationFileType = tradeApplicationFileDocument
                .getDocument()
                .getTradeApplicationFileTypeData();
            if (StringUtils.isEmpty(tradeApplicationFileType.getChedId())) {
                final String chedId = uploadFileToChed(tradeApplicationFileType.getFileId());
                tradeApplicationFileType.setChedId(chedId);
                tradeApplicationFileDocumentService.updateDocument(
                    tradeApplicationFileDocument.getId(),
                    tradeApplicationFileDocument,
                    true,
                    true,
                    "Add chedId"
                );
            }
        }
    }

    public void addApplicationFile(
        final TradeAdditionDocument tradeAdditionDocument,
        final TradeApplicationFileDocument tradeApplicationFileDocument
    ) {
        if (nonNull(tradeApplicationFileDocument)) {
            final TradeAdditionType tradeAdditionType = tradeAdditionDocument.getDocument().getTradeAdditionTypeData();
            final TradeApplicationFileType tradeApplicationFileType = tradeApplicationFileDocument.getDocument()
                .getTradeApplicationFileTypeData();
            ofNullable(tradeApplicationFileType.getFileId())
                .ifPresent(tradeAdditionType::setApplicationFileId);
            ofNullable(tradeApplicationFileType.getChedId())
                .ifPresent(tradeAdditionType::setApplicationChedId);
        }
    }
}
