package ru.croc.ugd.ssr.dto;

import lombok.Builder;
import lombok.Value;

import java.math.BigInteger;

@Value
@Builder
public class NewFlatDto {

    /**
     * ИД письма с предложением
     */
    private final String letterId;
    /**
     * Адрес дома
     */
    private final String ccoAddress;
    /**
     * Кадастровый номер квартиры
     */
    private final String ccoCadNum;
    /**
     * УНОМ дома
     */
    private final BigInteger ccoUnom;
    /**
     * Номер квартиры
     */
    private final String ccoFlatNum;
    /**
     * ИД документа разбора письма с предложением
     */
    private final String offerLetterParsingDocumentId;
}