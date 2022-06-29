package ru.croc.ugd.ssr.service.validator.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.croc.ugd.ssr.CipType;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.contractdigitalsign.ContractDigitalSignData;
import ru.croc.ugd.ssr.trade.TradeAdditionType;

/**
 * Данные пользователя для валидации.
 */
@Data
@Builder
@AllArgsConstructor
public class ValidatablePersonData {

    private String personId;
    private PersonType person;
    private TradeAdditionType tradeAddition;
    private PersonType.NewFlatInfo.NewFlat newFlat;
    private PersonType.OfferLetters.OfferLetter offerLetter;
    private String contractDigitalSignId;
    private ContractDigitalSignData contractDigitalSignData;
    private CipType cipData;

    public ValidatablePersonData(final String personId, final PersonType person) {
        this.personId = personId;
        this.person = person;
    }
}
