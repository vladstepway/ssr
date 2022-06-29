package ru.croc.ugd.ssr.dto.person;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
public class RestPersonBirthDateDto {
    private final String personDocumentId;
    private final LocalDate birthDate;
}
