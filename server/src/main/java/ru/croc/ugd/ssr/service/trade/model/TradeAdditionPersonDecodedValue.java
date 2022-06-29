package ru.croc.ugd.ssr.service.trade.model;

import lombok.Builder;
import lombok.Data;
import ru.croc.ugd.ssr.Person;

/**
 * TradeAdditionPersonDecodedValue.
 */
@Data
@Builder
public class TradeAdditionPersonDecodedValue {
    private String personDocumentId;
    private String personFio;
    private Person person;
}
