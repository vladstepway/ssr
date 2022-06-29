package ru.croc.ugd.ssr.dto.contractdigitalsign;

import lombok.Builder;
import lombok.Value;
import ru.croc.ugd.ssr.PersonType;

import java.time.LocalDate;

@Value
@Builder
public class WordFileLinkDto {

    private final LocalDate appointmentDate;
    private final String folderId;
    private final PersonType.Contracts.Contract.Files.File rtfFile;
}
