package ru.croc.ugd.ssr.service.trade;

import static ru.croc.ugd.ssr.service.trade.model.TradeAdditionSheetConstants.COMMENT_COLUMN_INDEX;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.math3.util.Pair;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import ru.croc.ugd.ssr.model.trade.TradeAdditionDocument;
import ru.croc.ugd.ssr.service.document.TradeAdditionDocumentService;
import ru.croc.ugd.ssr.service.document.TradeDataBatchStatusDocumentService;
import ru.croc.ugd.ssr.service.excel.model.process.XssfCellProcessResult;
import ru.croc.ugd.ssr.service.excel.model.process.XssfRowProcessResult;
import ru.croc.ugd.ssr.service.trade.history.TradeAdditionHistoryService;
import ru.croc.ugd.ssr.service.trade.model.TradeAdditionDecodedValues;
import ru.croc.ugd.ssr.service.trade.model.TradeAdditionGeneralConstants;
import ru.croc.ugd.ssr.service.trade.utils.TradeTypeUtils;
import ru.croc.ugd.ssr.service.validator.impl.trade.TradeAdditionObjectValidator;
import ru.croc.ugd.ssr.service.validator.model.ValidationResult;
import ru.croc.ugd.ssr.trade.TradeAdditionType;
import ru.croc.ugd.ssr.trade.TradeType;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
public class TradeAdditionRowSaverProcessor extends TradeSheetRowProcessor {
    private final TradeAdditionDocumentService tradeAdditionDocumentService;
    private final TradeAdditionHistoryService tradeAdditionHistoryService;
    private final TradeAdditionValuePopulator tradeAdditionValuePopulator;
    private final TradeAdditionObjectValidator tradeAdditionObjectValidator;
    private final TradeAdditionService tradeAdditionService;

    private TradeAdditionDecodedValues tradeAdditionDecodedValues;

    public TradeAdditionRowSaverProcessor(TradeSheetCellProcessor tradeSheetCellProcessor,
                                          TradeAdditionDocumentService tradeAdditionDocumentService,
                                          TradeAdditionHistoryService tradeAdditionHistoryService,
                                          TradeAdditionValuePopulator tradeAdditionValuePopulator,
                                          TradeAdditionValueDecoder tradeAdditionValueDecoder,
                                          TradeAdditionObjectValidator tradeAdditionObjectValidator,
                                          TradeDataBatchStatusDocumentService tradeDataBatchStatusDocumentService,
                                          TradeAdditionService tradeAdditionService) {
        super(tradeSheetCellProcessor, tradeDataBatchStatusDocumentService, tradeAdditionValueDecoder);
        this.tradeAdditionDocumentService = tradeAdditionDocumentService;
        this.tradeAdditionHistoryService = tradeAdditionHistoryService;
        this.tradeAdditionValuePopulator = tradeAdditionValuePopulator;
        this.tradeAdditionObjectValidator = tradeAdditionObjectValidator;
        this.tradeAdditionService = tradeAdditionService;
    }

    @Override
    protected XssfRowProcessResult saveRowAsTradeAddition(XSSFRow rowToProcess,
                                                          XssfRowProcessResult xssfRowProcessResult) {
        try {
            final TradeAdditionType tradeAdditionType = parseTradeAddition(xssfRowProcessResult);
            final ValidationResult tradeAdditionValidationResult = tradeAdditionObjectValidator
                .validate(tradeAdditionType);
            if (!tradeAdditionValidationResult.isValid()) {
                updateTradeBatchReportInfo(false);
                handleInvalidTradeAddition(rowToProcess, tradeAdditionValidationResult);
                return xssfRowProcessResult;
            }
            final boolean areDecoded = decodeTradeAddition(rowToProcess, tradeAdditionType);
            if (!areDecoded) {
                updateTradeBatchReportInfo(false);
                handleNotDecodedTradeAddition(rowToProcess);
                return xssfRowProcessResult;
            }
            handleNewTradeAddition(rowToProcess, tradeAdditionType);
        } catch (Exception ex) {
            updateTradeBatchReportInfo(false);
            log.error("Failed to store new trade addition, row number:"
                + rowToProcess.getRowNum()
                + "Uploaded file: " + getUploadedFileId(), ex);
            addAndSaveRowCommentSection(rowToProcess, "Ошибка сохранения строки!");
        }
        updateTradeBatchReportInfo(true);
        return xssfRowProcessResult;
    }

    private void handleInvalidTradeAddition(final XSSFRow rowToProcess,
                                            final ValidationResult tradeAdditionValidationResult) {
        addAndSaveRowCommentSection(rowToProcess, tradeAdditionValidationResult.getJoinedValidationMessage());
        saveIsRowRecorded(rowToProcess, false);
    }

    private void handleNotDecodedTradeAddition(final XSSFRow rowToProcess) {
        addAndSaveRowCommentSection(rowToProcess, "Не удалось расшифровать все коды.");
        saveIsRowRecorded(rowToProcess, false);
    }

    private TradeAdditionType parseTradeAddition(final XssfRowProcessResult xssfRowProcessResult) {
        final TradeAdditionType tradeAdditionType = new TradeAdditionType();
        final List<XssfCellProcessResult> xssfCellProcessResults = xssfRowProcessResult
            .getXssfCellProcessResults();
        if (!CollectionUtils.isEmpty(xssfCellProcessResults)) {
            xssfCellProcessResults
                .forEach(xssfCellProcessResult -> tradeAdditionValuePopulator
                    .mapProcessRowResult(xssfRowProcessResult,
                        xssfCellProcessResult,
                        tradeAdditionType));
        }
        return tradeAdditionType;
    }

    private boolean decodeTradeAddition(final XSSFRow rowToProcess,
                                        final TradeAdditionType tradeAdditionType) {
        this.tradeAdditionDecodedValues = tradeAdditionValueDecoder
            .decodeTradeAdditionCodes(tradeAdditionType);
        tradeAdditionValueDecoder.populateDecodedValues(tradeAdditionDecodedValues, tradeAdditionType);
        saveRowDecodeFioSection(rowToProcess, tradeAdditionDecodedValues.getDecodedPersonData());
        saveRowDecodeAddressOldSection(rowToProcess, tradeAdditionDecodedValues.getOldUnomDecodedData());
        saveRowDecodeAddressNewSection(rowToProcess, tradeAdditionDecodedValues.getNewUnomDecodedData());
        return !tradeAdditionDecodedValues.isAnyNotFound();
    }


    private String getUploadedFileId() {
        return processParameters
            .getProcessingParams()
            .get(TradeAdditionGeneralConstants.UPLOADED_FILE_PROCESSING_KEY);
    }

    private void addAndSaveRowCommentSection(XSSFRow rowToUpdate, String addNewValue) {
        saveCell(rowToUpdate, COMMENT_COLUMN_INDEX.getColumnIndex(), getCommentRowValue(rowToUpdate)
            + "\n" + addNewValue);
    }

    private void handleNewTradeAddition(final XSSFRow rowToProcess, final TradeAdditionType newTradeAddition) {
        populateGeneralValueToTradeAddition(newTradeAddition, rowToProcess);
        tradeAdditionService.populateCalculatedStatuses(newTradeAddition, tradeAdditionDecodedValues);
        final TradeAdditionDocument existingTradeAdditionDocument = getExistingIndexedTradeAdditionDocument(
            newTradeAddition.getUniqueRecordKey());
        final TradeAdditionDocument newTradeAdditionDocument = tradeAdditionDocumentService
            .getTradeAdditionDocumentFromType(newTradeAddition);
        newTradeAddition.setPageName(getPageNameFromRow(rowToProcess));

        if (existingTradeAdditionDocument != null && existingTradeAdditionDocument.getDocument() != null) {
            handleDocumentOverride(existingTradeAdditionDocument, newTradeAdditionDocument, rowToProcess);
        } else {
            final TradeAdditionDocument savedNewTradeAdditionDocument = tradeAdditionDocumentService
                .createDocument(newTradeAdditionDocument, false, null);
            tradeAdditionHistoryService.saveHistoryForNewTradeAddition(savedNewTradeAdditionDocument);
            saveIsRowRecorded(rowToProcess, true);
        }
    }

    private void handleDocumentOverride(
        final TradeAdditionDocument existingTradeAdditionDocument,
        final TradeAdditionDocument newTradeAdditionDocument,
        final XSSFRow rowToProcess
    ) {
        final TradeAdditionType newTradeAddition = newTradeAdditionDocument.getDocument()
            .getTradeAdditionTypeData();
        final TradeAdditionType existingTradeAddition = existingTradeAdditionDocument.getDocument()
            .getTradeAdditionTypeData();
        moveSentNotifications(existingTradeAddition, newTradeAddition);
        copyFileLinks(existingTradeAddition, newTradeAddition);
        final Pair<String, Boolean> changeLog = tradeAdditionHistoryService
            .calculateModificationsForTradeAddition(existingTradeAdditionDocument, newTradeAdditionDocument);
        if (!StringUtils.isBlank(changeLog.getFirst())) {
            if (changeLog.getSecond() != null && changeLog.getSecond()) {
                newTradeAdditionDocument.getDocument().getTradeAdditionTypeData().setConfirmed(false);
            }
            joinChangeLogToComment(rowToProcess, newTradeAddition, changeLog.getFirst());
            final TradeAdditionDocument savedNewTradeAdditionDocument = tradeAdditionDocumentService
                .createDocument(newTradeAdditionDocument, false, null);
            tradeAdditionHistoryService
                .saveHistoryForNewTradeAddition(savedNewTradeAdditionDocument, changeLog.getFirst());
            saveIsRowRecorded(rowToProcess, true);
        } else {
            joinChangeLogToComment(rowToProcess, newTradeAddition, "Не изменились");
            saveIsRowRecorded(rowToProcess, false);
        }
    }

    private void copyFileLinks(final TradeAdditionType oldTradeAddition, final TradeAdditionType newTradeAddition) {
        newTradeAddition.setApplicationFileId(
            oldTradeAddition.getApplicationFileId()
        );
        newTradeAddition.setApplicationChedId(
            oldTradeAddition.getApplicationChedId()
        );
    }

    private void moveSentNotifications(
        final TradeAdditionType oldTradeAddition, final TradeAdditionType newTradeAddition
    ) {
        newTradeAddition.getSentNotifications().addAll(
            oldTradeAddition.getSentNotifications()
        );
    }

    private void joinChangeLogToComment(final XSSFRow rowToProcess, final TradeAdditionType newTradeAddition,
                                        final String changeLog) {
        final String existingComment = newTradeAddition.getComment();
        final String updatedComment = existingComment == null ? changeLog :
            existingComment + "\n" + changeLog;
        newTradeAddition.setComment(updatedComment);
        saveRowCommentSection(rowToProcess, updatedComment);
    }

    private void populateGeneralValueToTradeAddition(final TradeAdditionType newTradeAddition,
                                                     final XSSFRow rowToProcess) {
        final String uniqueRecordKey = constructUniqueRecordKey(newTradeAddition);
        newTradeAddition.setUniqueRecordKey(uniqueRecordKey);
        newTradeAddition.setBatchDocumentId(processParameters
            .getProcessingParams()
            .get(TradeAdditionGeneralConstants.BATCH_DOCUMENT_ID_PROCESSING_KEY));
        newTradeAddition.setUploadedFileId(getUploadedFileId());
        newTradeAddition.setComment(getCommentRowValue(rowToProcess));
        newTradeAddition.setIndexed(false);
        newTradeAddition.setRecordNumber(String.valueOf(rowToProcess.getRowNum() + 1));
        if (StringUtils.isBlank(newTradeAddition.getComment())) {
            newTradeAddition.setConfirmed(true);
        }
    }

    private TradeAdditionDocument getExistingIndexedTradeAdditionDocument(final String uniqueRecordKey) {
        return StringUtils.isNotEmpty(uniqueRecordKey)
            ? tradeAdditionDocumentService.fetchIndexedByUniqueKey(uniqueRecordKey).orElse(null)
            : null;
    }

    private String constructUniqueRecordKey(final TradeAdditionType tradeAdditionType) {
        final TradeType tradeType = tradeAdditionType.getTradeType();
        return String.format("%s_%s",
            TradeTypeUtils.is4or5TradeType(tradeType)
                ? toStringDate(tradeAdditionType.getOfferLetterDate())
                : toStringDate(tradeAdditionType.getApplicationDate()),
            tradeAdditionType.getAffairId());
    }

    private String toStringDate(final LocalDate localDate) {
        if (localDate == null) {
            return Strings.EMPTY;
        }
        return localDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }
}
