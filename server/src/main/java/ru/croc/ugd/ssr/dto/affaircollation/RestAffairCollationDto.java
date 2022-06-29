package ru.croc.ugd.ssr.dto.affaircollation;

import lombok.Builder;
import lombok.Value;

/**
 * Даннык о запросе на сверку жителей.
 */
@Value
@Builder
public class RestAffairCollationDto {

    /**
     * УНОМ
     */
    private final String unom;
    /**
     * Номер квартиры
     */
    private final String flatNumber;
    /**
     * ИД семьи
     */
    private final String affairId;
}
