package ru.croc.ugd.ssr.dto.person;

import lombok.Builder;
import lombok.Value;
import ru.croc.ugd.ssr.dto.apartmentinspection.RestPersonDto;

import java.time.LocalDate;
import java.util.List;

@Value
@Builder
public class RestOfferLetterDto {
    private final String letterId;
    private final String fileStoreId;
    private final String resettlementType;
    private final List<RestPersonDto> persons;
    private final boolean hasConsent;
    private final LocalDate consentDate;
    private final String contractNumber;
    private final LocalDate contractSingDate;
}
