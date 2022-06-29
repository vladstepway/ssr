package ru.croc.ugd.ssr.dto.realestate;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class RestHousePreservationDto {

    /**
     * Признак сохранения дома.
     */
    @JsonProperty("isPreserved")
    private final boolean preserved;
}
