package ru.croc.ugd.ssr.dto.apartmentdefectconfirmation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class RestDefectDto {
    /**
     * ИД дефекта
     */
    private final String id;
    /**
     * ИД акта осмотра квартиры
     */
    private final String apartmentInspectionId;
    /**
     * Элемент квартиры
     */
    private final String flatElement;
    /**
     * Описание дефекта
     */
    private final String description;
    /**
     * Данные о квартире
     */
    private final DefectFlatDto flatData;
    /**
     * Устранен ли дефект
     */
    @JsonProperty("isEliminated")
    private final boolean eliminated;
    /**
     * Данные об устранении дефекта
     */
    private final RestEliminationDto eliminationData;
    /**
     * Дефект заблокирован
     */
    @JsonProperty("isBlocked")
    private final Boolean blocked;
    /**
     * Жители
     */
    private final List<RestPersonDto> persons;
}
