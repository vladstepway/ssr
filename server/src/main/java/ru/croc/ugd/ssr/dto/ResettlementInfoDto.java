package ru.croc.ugd.ssr.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
public class ResettlementInfoDto {

    private final LocalDate creationDate;
    private final String letterId;
    private final String letterFileId;
    private final LocalDate acceptedDate;
    private final String acceptedFileId;
    private final String administrativeDocumentFileId;
    private final LocalDate administrativeDocumentFileDate;

}
