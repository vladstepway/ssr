package ru.croc.ugd.ssr.dto.notarycompensation;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class RestNotaryCompensationRejectDto {

    private final String reason;
}
