package ru.croc.ugd.ssr.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ReasonDto {

    @JsonProperty("ISPOSSBLE")
    private final boolean possible;
    @JsonProperty("REASON")
    private final String reason;
}
