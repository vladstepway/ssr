package ru.croc.ugd.ssr.dto.commissioninspection;

import lombok.Builder;
import lombok.Value;
import ru.croc.ugd.ssr.dto.RestLetterDto;
import ru.croc.ugd.ssr.generated.dto.RestDefectDto;

import java.time.LocalDateTime;
import java.util.List;

@Value
@Builder
public class RestCommissionInspectionDto {

    private final String id;
    private final String eno;
    private final String address;
    private final String flatNumber;
    private final String ccoUnom;
    private final String applicationStatusId;
    private final String applicationStatus;
    private final String processInstanceId;
    private final String currentApartmentInspectionId;
    private final String completionReasonCode;
    private final String completionReason;
    private final LocalDateTime completionDateTime;
    private final LocalDateTime applicationDateTime;
    private final LocalDateTime confirmedDateTime;
    private final RestApplicantDto applicant;
    private final RestLetterDto letter;
    private final List<RestDefectDto> defects;
    private final List<RestHistoryEventDto> history;
}
