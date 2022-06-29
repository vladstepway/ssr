package ru.croc.ugd.ssr.dto.commissioninspection;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DefectDto {

    private final String id;
    /**
     * Элемент квартиры
     */
    private final String flatElement;
    /**
     * Описание дефекта
     */
    private final String description;
    /**
     * Устранен ли дефект
     */
    @JsonProperty("isEliminated")
    private final boolean eliminated;
}
