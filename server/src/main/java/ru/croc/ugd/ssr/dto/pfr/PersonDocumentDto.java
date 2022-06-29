package ru.croc.ugd.ssr.dto.pfr;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class PersonDocumentDto {
    private final int docType;
    private final String series;
    private final String number;
    private final LocalDateTime issueDate;
    private final String issuer;
}
