package ru.croc.ugd.ssr.dto.guardianship;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
public class GuardianshipHandleRequestDto {

    private final LocalDate decisionDate;
    private final Integer decisionType;
    private final String decisionFileId;
    private final Integer declineReasonType;
    private final String declineReason;
}
