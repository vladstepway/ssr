package ru.croc.ugd.ssr.service.validator.impl.person;

import static java.util.Optional.ofNullable;

import lombok.AllArgsConstructor;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.validator.ValueBasedValidatorUnit;
import ru.croc.ugd.ssr.service.validator.model.ValidatablePersonData;
import ru.croc.ugd.ssr.service.validator.model.ValidatorUnitResult;
import ru.croc.ugd.ssr.trade.TradeAdditionType;

import java.time.LocalDate;

/**
 * Подать заявку можно только в период действия гарантийного срока.
 * @param <T> тип валидируемого объекта
 */
@AllArgsConstructor
public class WarrantyPeriodExpired<T extends ValidatablePersonData> implements ValueBasedValidatorUnit<T> {

    public static final int WARRANTY_YEARS = 5;

    private final PersonDocumentService personDocumentService;

    private final String errorMessage;

    @Override
    public ValidatorUnitResult validate(final T personToValidate) {
        final LocalDate tradeAdditionContractSignDate = ofNullable(personToValidate.getTradeAddition())
            .map(TradeAdditionType::getContractSignedDate)
            .orElse(null);

        if (tradeAdditionContractSignDate == null) {
            // если нет то все ок
            return personDocumentService.getContractSignDate(personToValidate.getPerson())
                .filter(WarrantyPeriodExpired::isExpired)
                .map(date -> ValidatorUnitResult.fail(errorMessage))
                .orElseGet(ValidatorUnitResult::ok);
        } else if (isExpired(tradeAdditionContractSignDate)) {
            return ValidatorUnitResult.fail(errorMessage);
        } else {
            return ValidatorUnitResult.ok();
        }
    }

    /**
     * Проверка гарантийного периода.
     * @param contractSignDate дата подписания договора
     * @return false - гарантийный период оконцен
     */
    public static boolean isExpired(final LocalDate contractSignDate) {
        if (contractSignDate == null) {
            return false;
        }
        return contractSignDate.plusYears(WARRANTY_YEARS).isBefore(LocalDate.now());
    }

}
