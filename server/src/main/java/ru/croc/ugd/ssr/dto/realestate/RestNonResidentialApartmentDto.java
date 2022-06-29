package ru.croc.ugd.ssr.dto.realestate;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

/**
 * Данные о нежилых помещениях.
 */
@Builder
@Value
public class RestNonResidentialApartmentDto {

    /**
     * Кадастровый номер.
     */
    private final String cadNumber;
    /**
     * Кадастровая стоимость.
     */
    private final BigDecimal cost;
    /**
     * Площадь, кв.м.
     */
    private final BigDecimal area;
    /**
     * Вид объекта недвижимости.
     */
    private final String name;
    /**
     * Назначение помещения.
     */
    private final String purpose;
    /**
     * Помещение.
     */
    private final String apartment;
    /**
     * Правообладатель.
     */
    private final String rightHolder;
    /**
     * Обременение.
     */
    private final String restrict;
}
