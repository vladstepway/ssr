package ru.croc.ugd.ssr.service.validator.impl.trade;

import static ru.croc.ugd.ssr.service.trade.model.TradeAdditionSheetConstants.AFFAIR_ID_COLUMN_INDEX;
import static ru.croc.ugd.ssr.service.trade.model.TradeAdditionSheetConstants.AGREEMENT_TYPE_COLUMN_INDEX;
import static ru.croc.ugd.ssr.service.trade.model.TradeAdditionSheetConstants.AUCTION_DATE_COLUMN_INDEX;
import static ru.croc.ugd.ssr.service.trade.model.TradeAdditionSheetConstants.AUCTION_RESULT_COLUMN_INDEX;
import static ru.croc.ugd.ssr.service.trade.model.TradeAdditionSheetConstants.COMMISSION_DATE_COLUMN_INDEX;
import static ru.croc.ugd.ssr.service.trade.model.TradeAdditionSheetConstants.COMMISSION_DECISION_COLUMN_INDEX;
import static ru.croc.ugd.ssr.service.trade.model.TradeAdditionSheetConstants.CONTRACT_READINESS_DATE_COLUMN_INDEX;
import static ru.croc.ugd.ssr.service.trade.model.TradeAdditionSheetConstants.DEAL_4_5_COLUMN_INDEX;
import static ru.croc.ugd.ssr.service.trade.model.TradeAdditionSheetConstants.FLAT_NUM_NEW_COLUMN_INDEX;
import static ru.croc.ugd.ssr.service.trade.model.TradeAdditionSheetConstants.FLAT_OLD_NUM_COLUMN_INDEX;
import static ru.croc.ugd.ssr.service.trade.model.TradeAdditionSheetConstants.KEY_ISSUE_DATE_COLUMN_INDEX;
import static ru.croc.ugd.ssr.service.trade.model.TradeAdditionSheetConstants.LETTER_DATE_COLUMN_INDEX;
import static ru.croc.ugd.ssr.service.trade.model.TradeAdditionSheetConstants.PERSON_ID_COLUMN_INDEX;
import static ru.croc.ugd.ssr.service.trade.model.TradeAdditionSheetConstants.REQUEST_DATE_COLUMN_INDEX;
import static ru.croc.ugd.ssr.service.trade.model.TradeAdditionSheetConstants.REQUEST_STATUS_COLUMN_INDEX;
import static ru.croc.ugd.ssr.service.trade.model.TradeAdditionSheetConstants.SIGNED_CONTRACT_DATE_COLUMN_INDEX;
import static ru.croc.ugd.ssr.service.trade.model.TradeAdditionSheetConstants.UNOM_OLD_3_COLUMN_INDEX;
import static ru.croc.ugd.ssr.service.trade.model.TradeAdditionSheetConstants.UNOM_OLD_COLUMN_INDEX;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.service.excel.model.ExtractedCellValue;
import ru.croc.ugd.ssr.service.validator.MappedValueValidator;
import ru.croc.ugd.ssr.service.validator.ValueBasedNotEmptyValidatorUnit;
import ru.croc.ugd.ssr.service.validator.impl.excel.DateValidator;
import ru.croc.ugd.ssr.service.validator.impl.excel.MandatoryValidator;
import ru.croc.ugd.ssr.service.validator.model.ValidatorUnitResult;

import java.util.Arrays;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;

@Service
@Slf4j
public class TradeAdditionSheetDataValidator<T extends ExtractedCellValue> extends MappedValueValidator<T> {
    private static final String NUMERIC_VALUE_MESSAGE = "Поле %s не является числом";
    private static final String AFFAIR_ID_VALUE_MESSAGE = "AffairId может иметь только одно значение";
    private static final String NUMBER_RANGE_VALUE_MESSAGE = "Поле %s должно находиться между значениями %s и %s";

    @PostConstruct
    public void init() {
        this.registerValidatorInMap(FLAT_OLD_NUM_COLUMN_INDEX.getColumnIndex(),
            new MandatoryValidator());
        this.registerValidatorInMap(UNOM_OLD_3_COLUMN_INDEX.getColumnIndex(),
            new NumericValidator());
        this.registerValidatorInMap(UNOM_OLD_COLUMN_INDEX.getColumnIndex(),
            new NumericValidator());
        this.registerValidatorInMap(FLAT_NUM_NEW_COLUMN_INDEX.getColumnIndex(),
            new MandatoryValidator());
        this.registerValidatorInMap(PERSON_ID_COLUMN_INDEX.getColumnIndex(),
            new MandatoryValidator());
        this.registerValidatorInMap(AFFAIR_ID_COLUMN_INDEX.getColumnIndex(),
            new MandatoryValidator(),
            new AffairIdValidator());
        this.registerValidatorInMap(AGREEMENT_TYPE_COLUMN_INDEX.getColumnIndex(),
            new MandatoryValidator(), new NumericValidator(), new NumberRangeValidator(1, 5));
        this.registerValidatorInMap(REQUEST_STATUS_COLUMN_INDEX.getColumnIndex(),
            new MandatoryValidator(), new NumberRangeValidator(1, 8));
        /**
         * Mandatory based on trade type. See TradeAdditionObjectValidator.
         */
        this.registerValidatorInMap(LETTER_DATE_COLUMN_INDEX.getColumnIndex(),
            new DateValidator());
        /**
         * Mandatory based on trade type. See TradeAdditionObjectValidator.
         */
        this.registerValidatorInMap(REQUEST_DATE_COLUMN_INDEX.getColumnIndex(),
            new DateValidator());
        this.registerValidatorInMap(DEAL_4_5_COLUMN_INDEX.getColumnIndex(),
            new NumericValidator(), new NumberRangeValidator(1, 3));
        this.registerValidatorInMap(COMMISSION_DATE_COLUMN_INDEX.getColumnIndex(),
            new DateValidator());
        this.registerValidatorInMap(COMMISSION_DECISION_COLUMN_INDEX.getColumnIndex(),
            new NumericValidator(), new NumberRangeValidator(1, 3));
        this.registerValidatorInMap(AUCTION_DATE_COLUMN_INDEX.getColumnIndex(),
            new DateValidator());
        this.registerValidatorInMap(CONTRACT_READINESS_DATE_COLUMN_INDEX.getColumnIndex(),
            new DateValidator());
        this.registerValidatorInMap(AUCTION_RESULT_COLUMN_INDEX.getColumnIndex(),
            new NumericValidator(), new NumberRangeValidator(1, 2));
        this.registerValidatorInMap(SIGNED_CONTRACT_DATE_COLUMN_INDEX.getColumnIndex(),
            new DateValidator());
        this.registerValidatorInMap(KEY_ISSUE_DATE_COLUMN_INDEX.getColumnIndex(),
            new DateValidator());
    }

    @Override
    public Object getKeyForValidator(final ExtractedCellValue extractedCellValue) {
        return extractedCellValue.getColIndex();
    }

    class NumericValidator implements ValueBasedNotEmptyValidatorUnit<T> {
        @Override
        public ValidatorUnitResult validate(T extractedCellValue) {
            if (StringUtils.isEmpty(extractedCellValue.getRawValue())) {
                return ValidatorUnitResult.ok();
            }
            if (!StringUtils.isNumeric(extractedCellValue.getRawValue())) {
                return ValidatorUnitResult.fail(String.format(NUMERIC_VALUE_MESSAGE,
                    extractedCellValue.getColHeaderName()));
            }
            return ValidatorUnitResult.ok();
        }
    }

    class AffairIdValidator implements ValueBasedNotEmptyValidatorUnit<T> {
        @Override
        public ValidatorUnitResult validate(T extractedCellValue) {
            final String valueToValidate = extractedCellValue.getRawValue();
            if (StringUtils.isEmpty(valueToValidate)) {
                return ValidatorUnitResult.ok();
            }
            boolean isUniqueSingleValue = Arrays.asList(valueToValidate.split(","))
                .stream()
                .map(String::trim)
                .collect(Collectors.toSet())
                .size() == 1;
            return isUniqueSingleValue ? ValidatorUnitResult.ok() :
                ValidatorUnitResult.fail(AFFAIR_ID_VALUE_MESSAGE);
        }
    }

    class NumberRangeValidator implements ValueBasedNotEmptyValidatorUnit<T> {
        private int from;
        private int to;

        public NumberRangeValidator(int from, int to) {
            this.from = from;
            this.to = to;
        }

        @Override
        public ValidatorUnitResult validate(T extractedCellValue) {
            final String valueToValidate = extractedCellValue.getRawValue();
            if (StringUtils.isEmpty(valueToValidate)) {
                return ValidatorUnitResult.ok();
            }
            if (NumberUtils.isParsable(valueToValidate)) {
                final int parsedValue = Integer.valueOf(valueToValidate);
                return parsedValue <= to && parsedValue >= from
                    ? ValidatorUnitResult.ok()
                    : ValidatorUnitResult.fail(String.format(
                    NUMBER_RANGE_VALUE_MESSAGE,
                    extractedCellValue.getColHeaderName(),
                    from,
                    to
                ));
            }
            return ValidatorUnitResult.ok();
        }
    }
}
