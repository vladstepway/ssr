package ru.croc.ugd.ssr.dto.guardianship;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
public class GuardianshipRequestDto {

    private final String id;
    private final LocalDate requestDate;
    private final String requestFileId;
    private final String requesterPersonId;
    private final String affairId;
    private final LocalDate decisionDate;
    private final String decisionFileId;
    private final Integer decisionType;
    private final Integer declineReasonType;
    private final String declineReason;
    private final String processInstanceId;
}
