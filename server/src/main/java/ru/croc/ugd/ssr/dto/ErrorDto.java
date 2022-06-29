package ru.croc.ugd.ssr.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

//TODO define proper error message format
@Value
@Builder
public class ErrorDto {

    @JsonProperty("ISPOSSBLE")
    private final boolean legacyPossible;
    @JsonProperty("REASON")
    private final String legacyReason;

    @JsonProperty("isPossible")
    private final boolean possible;

    private final String reason;
    private final String code;
    private final String message;
    private final String stackTrace;
}
