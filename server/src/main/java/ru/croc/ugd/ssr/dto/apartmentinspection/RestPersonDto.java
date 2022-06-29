package ru.croc.ugd.ssr.dto.apartmentinspection;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
public class RestPersonDto {
    private final String personDocumentId;
    private final String fullName;
    private final LocalDate birthDate;
}
