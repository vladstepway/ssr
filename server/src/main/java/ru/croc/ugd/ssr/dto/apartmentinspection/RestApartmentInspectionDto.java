package ru.croc.ugd.ssr.dto.apartmentinspection;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
public class RestApartmentInspectionDto {
    private final String actNum;
    private final String actFileStoreId;
    private final RestPersonDto person;
    private final LocalDate plannedFixDate;
    private final LocalDate actCreationDate;
    private final boolean hasConsent;
    private final boolean hasDefects;
    private final LocalDate defectsEliminatedNotificationDate;
    private final LocalDate acceptedDefectsDate;
}
