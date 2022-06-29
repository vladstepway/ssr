package ru.croc.ugd.ssr.service.trade;

import static ru.croc.ugd.ssr.service.trade.model.TradeAdditionSheetConstants.AGREEMENT_TYPE_COLUMN_INDEX;
import static ru.croc.ugd.ssr.utils.DateTimeUtils.getDateFromString;

import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.service.excel.model.process.XssfCellProcessResult;
import ru.croc.ugd.ssr.service.excel.model.process.XssfRowProcessResult;
import ru.croc.ugd.ssr.service.trade.model.TradeAdditionSheetConstants;
import ru.croc.ugd.ssr.service.trade.utils.TradeTypeUtils;
import ru.croc.ugd.ssr.trade.AuctionResult;
import ru.croc.ugd.ssr.trade.ClaimStatus;
import ru.croc.ugd.ssr.trade.CommissionDecisionResult;
import ru.croc.ugd.ssr.trade.EstateInfoType;
import ru.croc.ugd.ssr.trade.PersonInfoType;
import ru.croc.ugd.ssr.trade.TradeAdditionType;
import ru.croc.ugd.ssr.trade.TradeResult;
import ru.croc.ugd.ssr.trade.TradeType;
import ru.croc.ugd.ssr.utils.StreamUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Populator for trade addition.
 */
@AllArgsConstructor
@Service
public class TradeAdditionValuePopulator {
    // TODO I know this code sucks. Improve if better times will come.
    // TODO Chain pattern probably suits here.
    /**
     * update input for each row.
     * @param xssfRowProcessResult xssfRowProcessResult
     * @param xssfCellProcessResult xssfCellProcessResult
     * @param tradeAdditionType tradeAdditionType
     */
    public void mapProcessRowResult(final XssfRowProcessResult xssfRowProcessResult,
                                    final XssfCellProcessResult xssfCellProcessResult,
                                    final TradeAdditionType tradeAdditionType) {
        final TradeAdditionSheetConstants tradeAdditionSheetConstants =
                TradeAdditionSheetConstants.findByIndex(xssfCellProcessResult.getCollIndex());
        if (tradeAdditionSheetConstants == null) {
            return;
        }
        if (tradeAdditionType.getOldEstate() == null) {
            tradeAdditionType.setOldEstate(new EstateInfoType());
        }
        final String cellValue = StringUtils.trimToNull(xssfCellProcessResult.getCellRawValue());
        switch (tradeAdditionSheetConstants) {
            case FILENAME_COLUMN_INDEX:
                tradeAdditionType.setAttachedFileName(cellValue);
                break;
            case UNOM_OLD_3_COLUMN_INDEX: {
                final XssfCellProcessResult agreementTypeResult = xssfRowProcessResult
                        .findByIndex(AGREEMENT_TYPE_COLUMN_INDEX.getColumnIndex());
                if (agreementTypeResult == null) {
                    break;
                }
                if (Objects.equals(agreementTypeResult.getCellRawValue(), TradeType.TRADE_IN_TWO_YEARS.value())) {
                    tradeAdditionType.getOldEstate().setUnom(cellValue);
                }
                break;
            }
            case UNOM_OLD_COLUMN_INDEX: {
                final XssfCellProcessResult agreementTypeResult = xssfRowProcessResult
                        .findByIndex(AGREEMENT_TYPE_COLUMN_INDEX.getColumnIndex());
                if (agreementTypeResult == null) {
                    break;
                }
                if (!Objects.equals(agreementTypeResult.getCellRawValue(), TradeType.TRADE_IN_TWO_YEARS.value())) {
                    tradeAdditionType.getOldEstate().setUnom(cellValue);
                }
                break;
            }
            case FLAT_OLD_NUM_COLUMN_INDEX:
                tradeAdditionType.getOldEstate().setFlatNumber(cellValue);
                break;
            case ROOM_OLD_NUMBER_COLUMN_INDEX:
                tradeAdditionType.getOldEstate()
                        .getRooms()
                        .addAll(getCommaSeparatedAsArray(cellValue));
                break;
            case UNOM_NEW_COLUMN_INDEX: {
                final List<EstateInfoType> newEstates = getCommaSeparatedAsArray(cellValue)
                        .stream()
                        .map(unom -> {
                            EstateInfoType estateInfoType = new EstateInfoType();
                            estateInfoType.setUnom(unom);
                            return estateInfoType;
                        })
                        .collect(Collectors.toList());
                tradeAdditionType.getNewEstates().addAll(newEstates);
                break;
            }
            case FLAT_NUM_NEW_COLUMN_INDEX: {
                final List<String> newFlatNums = getCommaSeparatedAsArray(cellValue);
                IntStream.range(0, newFlatNums.size())
                        .forEach(i -> {
                            if (StreamUtils.indexExists(tradeAdditionType.getNewEstates(), i)) {
                                tradeAdditionType.getNewEstates().get(i).setFlatNumber(newFlatNums.get(i));
                            } else {
                                EstateInfoType estateInfoType = new EstateInfoType();
                                estateInfoType.setFlatNumber(newFlatNums.get(i));
                                tradeAdditionType.getNewEstates().add(estateInfoType);
                            }
                        });
                break;
            }
            case PERSON_ID_COLUMN_INDEX:
                tradeAdditionType.getPersonsInfo()
                        .addAll(getCommaSeparatedAsArray(cellValue).stream().map(personId -> {
                            final PersonInfoType personInfoType = new PersonInfoType();
                            personInfoType.setPersonId(personId);
                            return personInfoType;
                        })
                        .collect(Collectors.toList()));
                break;
            case AFFAIR_ID_COLUMN_INDEX: {
                final List<String> affairIds = getCommaSeparatedAsArray(cellValue);
                tradeAdditionType.setAffairId(affairIds.size() > 0 ? affairIds.get(0) : null);
                break;
            }
            case AGREEMENT_TYPE_COLUMN_INDEX: {
                try {
                    tradeAdditionType.setTradeType(TradeType.fromValue(cellValue));
                } catch (IllegalArgumentException ex) {
                    // skip processing.
                }
                break;
            }
            case LETTER_DATE_COLUMN_INDEX:
                tradeAdditionType.setOfferLetterDate(getDateFromString(cellValue));
                break;
            case REQUEST_DATE_COLUMN_INDEX:
                if (TradeTypeUtils.is4or5TradeType(xssfRowProcessResult)) {
                    tradeAdditionType.setAgreementDate(getDateFromString(cellValue));
                } else {
                    tradeAdditionType.setApplicationDate(getDateFromString(cellValue));
                }
                break;
            case DEAL_4_5_COLUMN_INDEX: {
                if (TradeTypeUtils.is4or5TradeType(xssfRowProcessResult)) {
                    try {
                        tradeAdditionType.setTradeResult(TradeResult.fromValue(cellValue));
                    } catch (IllegalArgumentException ex) {
                        // skip.
                    }
                }
                break;
            }
            case COMMISSION_DATE_COLUMN_INDEX:
                tradeAdditionType.setCommissionDecisionDate(getDateFromString(cellValue));
                break;
            case COMMISSION_DECISION_COLUMN_INDEX: {
                try {
                    tradeAdditionType.setCommissionDecisionResult(CommissionDecisionResult.fromValue(cellValue));
                } catch (IllegalArgumentException ex) {
                    // skip.
                }
                break;
            }
            case AUCTION_DATE_COLUMN_INDEX:
                tradeAdditionType.setAuctionDate(getDateFromString(cellValue));
                break;
            case AUCTION_RESULT_COLUMN_INDEX: {
                try {
                    tradeAdditionType.setAuctionResult(AuctionResult.fromValue(cellValue));
                } catch (IllegalArgumentException ex) {
                    // skip.
                }
                break;
            }
            case CONTRACT_READINESS_DATE_COLUMN_INDEX:
                tradeAdditionType.setContractReadinessDate(getDateFromString(cellValue));
                break;
            case SIGNED_CONTRACT_DATE_COLUMN_INDEX:
                tradeAdditionType.setContractSignedDate(getDateFromString(cellValue));
                break;
            case CONTRACT_NUMBER_COLUMN_INDEX:
                tradeAdditionType.setContractNumber(cellValue);
                break;
            case KEY_ISSUE_DATE_COLUMN_INDEX:
                tradeAdditionType.setKeysIssueDate(getDateFromString(cellValue));
                break;
            case REQUEST_STATUS_COLUMN_INDEX: {
                try {
                    tradeAdditionType.setClaimStatus(ClaimStatus.fromValue(cellValue));
                } catch (IllegalArgumentException ex) {
                    // skip.
                }
                break;
            }
            case SELL_ID_COLUMN_INDEX:
                tradeAdditionType.setSellId(cellValue);
                break;
            case COMMENT_COLUMN_INDEX:
                break;
            default:
                break;
        }
    }

    private List<String> getCommaSeparatedAsArray(String line) {
        if (StringUtils.isEmpty(line)) {
            return Collections.EMPTY_LIST;
        }
        return Arrays.asList(StringUtils.split(line, ","))
                .stream()
                .map(String::trim)
                .collect(Collectors.toList());
    }
}
