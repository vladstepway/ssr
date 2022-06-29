package ru.croc.ugd.ssr.dto.apartmentdefectconfirmation;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class DefectFlatDto {
    /**
     * Квартира
     */
    private final String flat;
    /**
     * Этаж
     */
    private final Integer floor;
    /**
     * Подъезд
     */
    private final String entrance;
}
