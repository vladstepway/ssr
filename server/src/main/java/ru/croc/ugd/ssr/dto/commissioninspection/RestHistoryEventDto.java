package ru.croc.ugd.ssr.dto.commissioninspection;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class RestHistoryEventDto {

    private final String inspectionId;
    private final String inspectionType;
    private final String eventId;
    private final LocalDateTime createdAt;
}
