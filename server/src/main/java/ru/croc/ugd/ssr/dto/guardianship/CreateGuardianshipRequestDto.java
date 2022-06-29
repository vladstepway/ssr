package ru.croc.ugd.ssr.dto.guardianship;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
public class CreateGuardianshipRequestDto {

    private final LocalDate requestDate;
    private final String requestFileId;
    private final String requesterPersonId;
    private final String affairId;
}
