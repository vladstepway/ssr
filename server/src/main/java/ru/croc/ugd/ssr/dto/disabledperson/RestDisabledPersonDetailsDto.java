package ru.croc.ugd.ssr.dto.disabledperson;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class RestDisabledPersonDetailsDto {
    @JsonProperty("isUsingWheelchair")
    private final boolean usingWheelchair;
    @JsonProperty("isWheelchairUserDetected")
    private final boolean wheelchairUserDetected;
    @JsonProperty("isDisabledPerson")
    private final boolean disabledPerson;
}
