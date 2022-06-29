package ru.croc.ugd.ssr.dto.compensation;

import lombok.Builder;
import lombok.Value;

import java.math.BigInteger;
import java.util.List;

/**
 * Данные по квартирам на компенсацию.
 */
@Value
@Builder
public class RestCompensationDto {

    /**
     * ИД карточки.
     */
    private final String id;
    /**
     * ID расселяемого дома.
     */
    private final String realEstateId;
    /**
     * ID Запроса на переселение домов/квартир.
     */
    private final String resettlementRequestId;
    /**
     * УНОМ расселяемого дома.
     */
    private final BigInteger unom;
    /**
     * Данные по квартирам на компенсацию.
     */
    private final List<RestCompensationFlatDto> flats;

}
