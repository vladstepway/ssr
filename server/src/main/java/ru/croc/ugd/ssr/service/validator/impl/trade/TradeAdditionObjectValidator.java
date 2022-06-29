package ru.croc.ugd.ssr.service.validator.impl.trade;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.service.trade.utils.TradeTypeUtils;
import ru.croc.ugd.ssr.service.validator.ChainedValueValidator;
import ru.croc.ugd.ssr.service.validator.ValueBasedNotEmptyValidatorUnit;
import ru.croc.ugd.ssr.service.validator.model.ValidatorUnitResult;
import ru.croc.ugd.ssr.trade.TradeAdditionType;
import ru.croc.ugd.ssr.trade.TradeType;

import javax.annotation.PostConstruct;

/**
 * Валидатор TradeAdditionType.
 */
@Service
@Slf4j
public class TradeAdditionObjectValidator<T extends TradeAdditionType> extends ChainedValueValidator<T> {
    private static final String OLD_UNOM_MISSING_MESSAGE = "Не заполнен UNOM расселяемого дома";
    private static final String OFFER_LETTER_DATE_MISSING_MESSAGE = "Не заполнена дата письма с предложением";
    private static final String APPLICATION_DATE_MISSING_MESSAGE = "Не заполнена дата подачи заявления";
    private static final String NEW_UNOM_FLAT_MISMATCH_MESSAGE =
            "Не совпадает количество квартир и UNOM для заселяемых домов";

    @PostConstruct
    public void init() {
        this.registerValidatorInChain(new OldUnomMandatory());
        this.registerValidatorInChain(new NewUnomAndFlatAmountMatchValidator());
        this.registerValidatorInChain(new MandatoryFieldBasedOnTradeType());
    }

    class OldUnomMandatory implements ValueBasedNotEmptyValidatorUnit<T> {
        @Override
        public ValidatorUnitResult validate(TradeAdditionType tradeAdditionType) {
            if (tradeAdditionType.getOldEstate() == null
                    || tradeAdditionType.getOldEstate().getUnom() == null) {
                return ValidatorUnitResult.fail(OLD_UNOM_MISSING_MESSAGE);
            }
            return ValidatorUnitResult.ok();
        }
    }

    class NewUnomAndFlatAmountMatchValidator implements ValueBasedNotEmptyValidatorUnit<T> {
        @Override
        public ValidatorUnitResult validate(TradeAdditionType tradeAdditionType) {
            if (tradeAdditionType.getNewEstates() == null) {
                return ValidatorUnitResult.ok();
            }
            final boolean isAnyMissing = tradeAdditionType
                    .getNewEstates()
                    .stream()
                    .anyMatch(estateInfoType -> StringUtils.isEmpty(estateInfoType.getFlatNumber())
                            || StringUtils.isEmpty(estateInfoType.getUnom()));
            return isAnyMissing
                    ? ValidatorUnitResult.fail(NEW_UNOM_FLAT_MISMATCH_MESSAGE)
                    : ValidatorUnitResult.ok();
        }
    }

    class MandatoryFieldBasedOnTradeType implements ValueBasedNotEmptyValidatorUnit<T> {
        @Override
        public ValidatorUnitResult validate(TradeAdditionType tradeAdditionType) {
            final TradeType tradeType = tradeAdditionType.getTradeType();
            if (TradeTypeUtils.is4or5TradeType(tradeType)) {
                return tradeAdditionType.getOfferLetterDate() == null
                        ? ValidatorUnitResult.fail(OFFER_LETTER_DATE_MISSING_MESSAGE)
                        : ValidatorUnitResult.ok();
            } else {
                return tradeAdditionType.getApplicationDate() == null
                        ? ValidatorUnitResult.fail(APPLICATION_DATE_MISSING_MESSAGE)
                        : ValidatorUnitResult.ok();
            }
        }
    }
}
