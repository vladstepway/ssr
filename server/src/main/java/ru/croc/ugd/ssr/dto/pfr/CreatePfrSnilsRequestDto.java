package ru.croc.ugd.ssr.dto.pfr;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class CreatePfrSnilsRequestDto {
    private final String personDocumentId;
    private final String lastName;
    private final String firstName;
    private final String middleName;
    private final LocalDateTime birthDate;
    private final int genderCode;
    private final int docType;
    private final String series;
    private final String number;
    private final LocalDateTime issueDate;
    private final String issuer;
}
