package ru.croc.ugd.ssr.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
public class SignContractDto {

    private final String personDocumentId;
    private final String contractFileId;
    private final String contractFileName;
    private final LocalDate contractSignDate;
    private final String actFileId;
    private final String actFileName;
    private final LocalDate actSignDate;
    private final String contractAppointmentId;
    @JsonProperty("isContractEntered")
    private final boolean isContractEntered;
    private final String refuseReason;
}
