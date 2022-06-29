package ru.croc.ugd.ssr.dto.apartmentinspection;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class PersonConsentDto {

    private final String acceptedDefectsActFileId;
    private final LocalDateTime acceptedDefectsDate;
    private final boolean hasConsent;
}
