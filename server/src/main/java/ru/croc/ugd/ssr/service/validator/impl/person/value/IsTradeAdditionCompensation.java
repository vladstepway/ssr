package ru.croc.ugd.ssr.service.validator.impl.person.value;

import static java.util.Optional.ofNullable;

import lombok.AllArgsConstructor;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.model.trade.TradeAdditionDocument;
import ru.croc.ugd.ssr.service.document.TradeAdditionDocumentService;
import ru.croc.ugd.ssr.service.validator.ValueBasedValidatorUnit;
import ru.croc.ugd.ssr.service.validator.model.ValidatablePersonData;
import ru.croc.ugd.ssr.service.validator.model.ValidatorUnitResult;
import ru.croc.ugd.ssr.trade.TradeAddition;
import ru.croc.ugd.ssr.trade.TradeAdditionType;
import ru.croc.ugd.ssr.trade.TradeType;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Услуга не может быть предоставлена заявителю, если он получает равноценное возмещение
 * в денежной форме взамен предложенной квартиры.
 * @param <T> тип валидируемого объекта
 */
@AllArgsConstructor
public class IsTradeAdditionCompensation<T extends ValidatablePersonData>
    implements ValueBasedValidatorUnit<T> {

    private final TradeAdditionDocumentService tradeAdditionDocumentService;
    private final Function<T, String> retrieveErrorMessage;

    @Override
    public ValidatorUnitResult validate(final T validatablePersonData) {
        if (isCompensation(validatablePersonData.getPersonId(), validatablePersonData.getNewFlat())) {
            return ValidatorUnitResult.fail(retrieveErrorMessage.apply(validatablePersonData));
        }
        return ValidatorUnitResult.ok();
    }

    private boolean isCompensation(final String personDocumentId, final PersonType.NewFlatInfo.NewFlat newFlat) {
        final List<TradeAdditionDocument> tradeAdditionDocuments = tradeAdditionDocumentService
            .fetchIndexedByPersonId(personDocumentId);
        return tradeAdditionDocuments
            .stream()
            .map(TradeAdditionDocument::getDocument)
            .map(TradeAddition::getTradeAdditionTypeData)
            .filter(tradeAdditionType ->
                TradeType.COMPENSATION.equals(tradeAdditionType.getTradeType())
                    || TradeType.TRADE_WITH_COMPENSATION.equals(tradeAdditionType.getTradeType()))
            .anyMatch(tradeAddition -> isTradeAdditionForNewFlat(tradeAddition, newFlat));
    }

    private boolean isTradeAdditionForNewFlat(
        final TradeAdditionType tradeAddition, final PersonType.NewFlatInfo.NewFlat newFlat
    ) {
        final String ccoFlatNum = ofNullable(newFlat)
            .map(PersonType.NewFlatInfo.NewFlat::getCcoFlatNum)
            .orElse(null);
        final String ccoUnom = ofNullable(newFlat)
            .map(PersonType.NewFlatInfo.NewFlat::getCcoUnom)
            .map(BigInteger::toString)
            .orElse(null);
        return ofNullable(tradeAddition)
            .map(TradeAdditionType::getNewEstates)
            .orElse(Collections.emptyList())
            .stream()
            .anyMatch(newEstate -> Objects.equals(newEstate.getUnom(), ccoUnom)
                && Objects.equals(newEstate.getFlatNumber(), ccoFlatNum));
    }
}
