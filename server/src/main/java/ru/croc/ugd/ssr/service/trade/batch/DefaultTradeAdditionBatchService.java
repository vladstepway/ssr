package ru.croc.ugd.ssr.service.trade.batch;

import static java.util.Objects.nonNull;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import ru.computel.common.filenet.client.FilenetFileBean;
import ru.croc.ugd.ssr.db.dao.TradeAdditionDao;
import ru.croc.ugd.ssr.dto.tradeaddition.TradeAdditionConfirmTradesDto;
import ru.croc.ugd.ssr.exception.SsrException;
import ru.croc.ugd.ssr.exception.trade.BatchAlreadyProcessed;
import ru.croc.ugd.ssr.exception.trade.BatchProcessingCrash;
import ru.croc.ugd.ssr.integration.service.flows.TradeAdditionFlowsService;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPTradeAdditionType;
import ru.croc.ugd.ssr.model.trade.TradeAdditionDocument;
import ru.croc.ugd.ssr.model.trade.TradeApplicationFileDocument;
import ru.croc.ugd.ssr.model.trade.TradeDataBatchStatusDocument;
import ru.croc.ugd.ssr.service.SsrFilestoreService;
import ru.croc.ugd.ssr.service.document.IExtendedDocumentService;
import ru.croc.ugd.ssr.service.document.TradeAdditionDocumentService;
import ru.croc.ugd.ssr.service.document.TradeApplicationFileDocumentService;
import ru.croc.ugd.ssr.service.document.TradeDataBatchStatusDocumentService;
import ru.croc.ugd.ssr.service.excel.model.process.ProcessParameters;
import ru.croc.ugd.ssr.service.trade.TradeAdditionService;
import ru.croc.ugd.ssr.service.trade.model.TradeAdditionGeneralConstants;
import ru.croc.ugd.ssr.trade.BatchProcessingStatus;
import ru.croc.ugd.ssr.trade.TradeAddition;
import ru.croc.ugd.ssr.trade.TradeAdditionType;
import ru.croc.ugd.ssr.trade.TradeApplicationFile;
import ru.croc.ugd.ssr.trade.TradeApplicationFileType;
import ru.croc.ugd.ssr.trade.TradeDataBatchStatusType;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.croc.ugd.ssr.utils.StreamUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * DefaultTradeAdditionBatchService.
 */
@Slf4j
@Component
@AllArgsConstructor
public class DefaultTradeAdditionBatchService implements TradeAdditionBatchService {

    private final SsrFilestoreService ssrFilestoreService;
    private final TradeAdditionFlowsService tradeAdditionFlowsService;
    private final TradeDataBatchStatusDocumentService tradeDataBatchStatusDocumentService;
    private final TradeAdditionBatchProcessor tradeAdditionBatchProcessor;
    private final TradeAdditionDocumentService tradeAdditionDocumentService;
    private final TradeApplicationFileDocumentService tradeApplicationFileDocumentService;
    private final TradeAdditionDao tradeAdditionDao;
    private final IExtendedDocumentService extendedDocumentService;
    private final ObjectMapper objectMapper;
    private final TradeAdditionService tradeAdditionService;

    @Override
    public void processTradeAdditionSheetFile(final String fileId, final String documentId) {
        try {
            final TradeDataBatchStatusDocument tradeDataBatchStatusDocument = fetchDocument(documentId);
            if (tradeDataBatchStatusDocument.getDocument().getTradeDataBatchStatusTypeData().getStatus() != null) {
                throw new BatchAlreadyProcessed();
            }
            final byte[] fileToProcess = ssrFilestoreService.getFile(fileId);
            final FilenetFileBean fileInfo = ssrFilestoreService.getFileInfo(fileId);
            populateFileInfoToBatchStatus(fileInfo, tradeDataBatchStatusDocument);
            final ProcessParameters processParameters = getProcessParameters(fileId, tradeDataBatchStatusDocument
                .getId());
            updateBatchWithInProgress(tradeDataBatchStatusDocument, fileId);
            tradeAdditionBatchProcessor
                .processBatchAsync(tradeDataBatchStatusDocument, fileToProcess, processParameters);
        } catch (Exception ex) {
            log.error("Failed to start batch processing. BatchId: " + documentId + " Uploaded fileId: " + fileId, ex);
            if (ex instanceof SsrException) {
                throw ex;
            }
            throw new BatchProcessingCrash();
        }
    }

    @Async
    @Override
    public void deployBatch(final String batchDocumentId) {
        Assert.notNull(batchDocumentId, "batchDocumentId is null");
        final List<TradeAdditionDocument> tradeAdditionDocumentList = tradeAdditionDocumentService
            .getAllDocumentsByBatchId(batchDocumentId);
        final TradeDataBatchStatusDocument tradeDataBatchStatusDocument = fetchDocument(batchDocumentId);
        final List<TradeApplicationFileDocument> tradeApplicationFileDocuments = tradeApplicationFileDocumentService
            .getAllDocumentsByBatchId(batchDocumentId);
        final AtomicInteger totalAmountOfDocumentsForDeployment = new AtomicInteger(0);
        final AtomicInteger currentAmountOfDeployed = new AtomicInteger(0);
        final List<SuperServiceDGPTradeAdditionType.TradeAdditionInfo> tradeAdditionInfos = new ArrayList<>();
        tradeAdditionDocumentList.stream()
            .filter(this::tradeAdditionDocumentIsConfirmed)
            .filter(this::tradeAdditionDocumentIsNotIndexed)
            .map(u -> {
                totalAmountOfDocumentsForDeployment.getAndIncrement();
                return u;
            })
            .forEach(tradeAdditionDocument -> {
                final TradeApplicationFileDocument tradeApplicationFileDocument = tradeApplicationFileDocuments
                    .stream()
                    .filter(document -> StringUtils.equals(
                        document.getDocument().getTradeApplicationFileTypeData().getFileName(),
                        tradeAdditionDocument.getDocument().getTradeAdditionTypeData().getAttachedFileName()))
                    .findFirst()
                    .orElse(null);
                if (nonNull(tradeApplicationFileDocument)) {
                    tradeAdditionService.sendAttachmentsToChedIfNeeded(tradeApplicationFileDocument);
                    ofNullable(tradeApplicationFileDocument.getDocument().getTradeApplicationFileTypeData())
                        .map(TradeApplicationFileType::getChedId)
                        .ifPresent(chedFileId -> tradeAdditionDocument.getDocument()
                            .getTradeAdditionTypeData()
                            .setApplicationChedId(chedFileId));
                }
                tradeAdditionService.deployDocument(tradeAdditionDocument, tradeApplicationFileDocument);
                final SuperServiceDGPTradeAdditionType.TradeAdditionInfo tradeAdditionInfo =
                    new SuperServiceDGPTradeAdditionType.TradeAdditionInfo();
                tradeAdditionInfo.setTradeAdditionTypeData(
                    tradeAdditionDocument.getDocument().getTradeAdditionTypeData()
                );
                tradeAdditionInfos.add(tradeAdditionInfo);
                incrementDeployedAmountStatus(tradeDataBatchStatusDocument,
                    (currentAmountOfDeployed.incrementAndGet() * 100) / totalAmountOfDocumentsForDeployment.get());
            });
        if (totalAmountOfDocumentsForDeployment.get() == 0) {
            incrementDeployedAmountStatus(tradeDataBatchStatusDocument, 100);
        }
        setAndSaveBatchDeployed(tradeDataBatchStatusDocument);
        final SuperServiceDGPTradeAdditionType superServiceDGPTradeAdditionType =
            new SuperServiceDGPTradeAdditionType();
        superServiceDGPTradeAdditionType.getTradeAdditionInfo().addAll(tradeAdditionInfos);
        tradeAdditionFlowsService.sendTradeAddition(superServiceDGPTradeAdditionType);
    }

    private void incrementDeployedAmountStatus(final TradeDataBatchStatusDocument tradeDataBatchStatusDocument,
                                               int percentageCompleted) {
        tradeDataBatchStatusDocument
            .getDocument()
            .getTradeDataBatchStatusTypeData()
            .setPercentageDeployed(percentageCompleted);
        updateBatchStatus(tradeDataBatchStatusDocument);
    }

    private boolean tradeAdditionDocumentIsConfirmed(final TradeAdditionDocument document) {
        return of(document.getDocument())
            .map(TradeAddition::getTradeAdditionTypeData)
            .filter(TradeAdditionType::isConfirmed)
            .isPresent();
    }

    private boolean tradeAdditionDocumentIsNotIndexed(final TradeAdditionDocument document) {
        return of(document.getDocument())
            .map(TradeAddition::getTradeAdditionTypeData)
            .filter(StreamUtils.not(TradeAdditionType::isIndexed))
            .isPresent();
    }

    @Override
    public boolean areAllBatchesDeployed() {
        final Boolean areAllDeployed = tradeAdditionDao.areAllUploadedBatchesDeployed();
        return areAllDeployed != null && areAllDeployed;
    }

    private void populateFileInfoToBatchStatus(final FilenetFileBean filenetFileBean,
                                               final TradeDataBatchStatusDocument tradeDataBatchStatusDocument) {
        if (filenetFileBean != null) {
            tradeDataBatchStatusDocument.getDocument()
                .getTradeDataBatchStatusTypeData()
                .setUploadedFileName(filenetFileBean.getFileName());
        }
    }

    private ProcessParameters getProcessParameters(final String fileId, final String documentId) {
        return ProcessParameters.builder()
            .processingParam(TradeAdditionGeneralConstants.UPLOADED_FILE_PROCESSING_KEY,
                fileId)
            .processingParam(TradeAdditionGeneralConstants.BATCH_DOCUMENT_ID_PROCESSING_KEY,
                documentId)
            .build();
    }

    private TradeDataBatchStatusDocument fetchDocument(final String documentId) {
        final TradeDataBatchStatusDocument tradeDataBatchStatusDocument =
            tradeDataBatchStatusDocumentService.fetchDocument(documentId);
        if (tradeDataBatchStatusDocument.getDocument().getTradeDataBatchStatusTypeData() == null) {
            tradeDataBatchStatusDocument.getDocument()
                .setTradeDataBatchStatusTypeData(new TradeDataBatchStatusType());
        }
        return tradeDataBatchStatusDocument;
    }


    private void updateBatchWithInProgress(final TradeDataBatchStatusDocument tradeDataBatchStatusDocument,
                                           final String fileId) {
        final TradeDataBatchStatusType tradeDataBatchStatusType = tradeDataBatchStatusDocument.getDocument()
            .getTradeDataBatchStatusTypeData();
        tradeDataBatchStatusType.setUploadedFileId(fileId);
        tradeDataBatchStatusType.setStatus(BatchProcessingStatus.INPROGRESS);
        tradeDataBatchStatusType.setPercentageReady(0);
        tradeDataBatchStatusDocumentService.updateDocument(tradeDataBatchStatusDocument.getId(),
            tradeDataBatchStatusDocument,
            true, true, null);
    }

    private void setAndSaveBatchDeployed(final TradeDataBatchStatusDocument tradeDataBatchStatusDocument) {
        tradeDataBatchStatusDocument.getDocument()
            .getTradeDataBatchStatusTypeData()
            .setDeployed(true);
        updateBatchStatus(tradeDataBatchStatusDocument);
    }

    private void updateBatchStatus(final TradeDataBatchStatusDocument tradeDataBatchStatusDocument) {
        tradeDataBatchStatusDocumentService.updateDocument(tradeDataBatchStatusDocument.getId(),
            tradeDataBatchStatusDocument,
            true, false, null);
    }

    @Override
    public void confirmTrades(final String batchDocumentId, final TradeAdditionConfirmTradesDto confirmTradesDto) {
        tradeAdditionDocumentService.getAllDocumentsByBatchId(batchDocumentId)
            .stream()
            .map(tradeAdditionDocument ->
                confirmTradeAddition(tradeAdditionDocument, confirmTradesDto.isConfirmAction())
            )
            .forEach(tradeAdditionDocument -> tradeAdditionDocumentService
                .updateDocument(tradeAdditionDocument.getId(), tradeAdditionDocument, true, true, null)
            );
    }

    private TradeAdditionDocument confirmTradeAddition(
        final TradeAdditionDocument tradeAdditionDocument, final boolean isConfirmAction
    ) {
        final TradeAdditionType tradeAdditionType = tradeAdditionDocument
            .getDocument()
            .getTradeAdditionTypeData();

        tradeAdditionType.setConfirmed(isConfirmAction);

        return tradeAdditionDocument;
    }

    @Override
    public List<TradeAdditionDocument> fetchTradeAdditions(
        final String batchDocumentId, final int pageNum, final int pageSize
    ) {
        try {
            final List<TradeAdditionDocument> tradeAdditionDocuments = extendedDocumentService
                .fetchDocumentsPageByQueryNode(
                    SsrDocumentTypes.TRADE_ADDITION,
                    objectMapper.readValue(getBatchSearchBody(batchDocumentId), ObjectNode.class),
                    Arrays.asList("tradeAddition.tradeAdditionTypeData.comment", "id"),
                    Sort.Direction.ASC.name(),
                    pageNum,
                    pageSize
                );

            return tradeAdditionDocuments.stream()
                .map(this::addApplicationFileId)
                .collect(Collectors.toList());
        } catch (IOException e) {
            throw new SsrException("Can't fetch trade addition list", e);
        }
    }

    private TradeAdditionDocument addApplicationFileId(final TradeAdditionDocument tradeAdditionDocument) {
        final TradeAdditionType tradeAddition = tradeAdditionDocument
            .getDocument()
            .getTradeAdditionTypeData();

        tradeApplicationFileDocumentService
            .findByBatchDocumentIdAndFileName(tradeAddition.getBatchDocumentId(), tradeAddition.getAttachedFileName())
            .map(TradeApplicationFileDocument::getDocument)
            .map(TradeApplicationFile::getTradeApplicationFileTypeData)
            .map(TradeApplicationFileType::getFileId)
            .ifPresent(tradeAddition::setApplicationFileId);

        return tradeAdditionDocument;
    }

    @Override
    public long countTradeAdditions(final String batchDocumentId) {
        try {
            return extendedDocumentService.countDocuments(
                SsrDocumentTypes.TRADE_ADDITION.getCode(),
                objectMapper.readValue(getBatchSearchBody(batchDocumentId), ObjectNode.class)
            );
        } catch (IOException e) {
            throw new SsrException("Can't count trade additions", e);
        }
    }

    private String getBatchSearchBody(final String batchDocumentId) {
        return "{"
            + "      \"tradeAddition\": {"
            + "        \"tradeAdditionTypeData\": {"
            + "          \"batchDocumentId\": \"" + batchDocumentId + "\""
            + "        }"
            + "      }"
            + "    }";
    }
}
