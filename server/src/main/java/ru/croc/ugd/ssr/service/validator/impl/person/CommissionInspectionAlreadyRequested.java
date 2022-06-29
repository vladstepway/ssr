package ru.croc.ugd.ssr.service.validator.impl.person;

import static java.util.Optional.ofNullable;

import lombok.AllArgsConstructor;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.service.document.CommissionInspectionDocumentService;
import ru.croc.ugd.ssr.service.validator.ValueBasedValidatorUnit;
import ru.croc.ugd.ssr.service.validator.model.ValidatablePersonData;
import ru.croc.ugd.ssr.service.validator.model.ValidatorUnitResult;
import ru.croc.ugd.ssr.trade.EstateInfoType;
import ru.croc.ugd.ssr.trade.TradeAdditionType;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Один из правообладателей квартиры уже подал заявку на комиссионный осмотр. Оформлять еще одну не нужно.
 *
 * @param <T> тип валидируемого объекта
 */
@AllArgsConstructor
public class CommissionInspectionAlreadyRequested<T extends ValidatablePersonData>
    implements ValueBasedValidatorUnit<T> {

    private final CommissionInspectionDocumentService commissionInspectionDocumentService;
    private final String errorMessage;

    @Override
    public ValidatorUnitResult validate(final T personType) {
        final Optional<EstateInfoType> estateInfoType = ofNullable(personType.getTradeAddition())
            .map(TradeAdditionType::getNewEstates)
            .map(List::stream)
            .orElse(Stream.empty())
            .findFirst();

        final String unom = estateInfoType
            .map(EstateInfoType::getUnom)
            .orElse(ofNullable(personType.getNewFlat())
                .map(PersonType.NewFlatInfo.NewFlat::getCcoUnom)
                .map(BigInteger::toString)
                .orElse(null)
            );

        final String flatNum = estateInfoType
            .map(EstateInfoType::getFlatNumber)
            .orElse(ofNullable(personType.getNewFlat())
                .map(PersonType.NewFlatInfo.NewFlat::getCcoFlatNum)
                .orElse(null)
            );

        final String letterId = ofNullable(personType.getOfferLetter())
            .map(PersonType.OfferLetters.OfferLetter::getLetterId)
            .orElse(null);

        boolean hasActiveInspections = false;

        if (unom != null && flatNum != null) {
            hasActiveInspections = commissionInspectionDocumentService.existsActiveByUnomAndFlatNumber(unom, flatNum);
        } else if (letterId != null) {
            hasActiveInspections = commissionInspectionDocumentService.existsActiveByLetterId(letterId);
        }

        return hasActiveInspections
            ? ValidatorUnitResult.fail(errorMessage)
            : ValidatorUnitResult.ok();
    }
}
