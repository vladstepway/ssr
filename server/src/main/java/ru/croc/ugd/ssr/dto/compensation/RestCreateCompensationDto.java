package ru.croc.ugd.ssr.dto.compensation;

import lombok.Value;

/**
 * Запрос на создание данных по компенсации.
 */
@Value
public class RestCreateCompensationDto {

    /**
     * ID Запроса на переселение домов/квартир.
     */
    private final String resettlementRequestId;

}
