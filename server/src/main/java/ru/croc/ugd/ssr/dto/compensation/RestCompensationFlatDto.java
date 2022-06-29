package ru.croc.ugd.ssr.dto.compensation;

import lombok.Builder;
import lombok.Value;

/**
 * Данные по квартире на компенсацию.
 */
@Value
@Builder
public class RestCompensationFlatDto {

    /**
     * ID квартиры.
     */
    private final String flatId;
    /**
     * Номер квартиры.
     */
    private final String flatNum;
    /**
     * Идентификатор семьи.
     */
    private final String affairId;
    /**
     * Номера комнат через запятую.
     */
    private final String roomNum;
}
