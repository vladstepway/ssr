package ru.croc.ugd.ssr.dto.offerletterparsing;

import lombok.Builder;
import lombok.Value;

/**
 * Автоматически распознанные данные о квартире.
 */
@Value
@Builder
public class RestAutomaticallyParsedFlatDataDto {

    /**
     * Автоматически распознанный адрес заселяемой квартиры.
     */
    private final String addressTo;
    /**
     * Автоматически распознанный кадастровый номер.
     */
    private final String cadNumber;
}
