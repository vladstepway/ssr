package ru.croc.ugd.ssr.dto.ssrcco;

import lombok.Builder;
import lombok.Value;
import ru.croc.ugd.ssr.enums.SsrCcoOrganizationType;

import java.time.LocalDate;

@Value
@Builder
public class SsrCcoEmployeeDto {

    private final String id;
    private final String fullName;
    private final String login;
    private final String organisationName;
    private final String post;
    private final String email;
    private final LocalDate periodFrom;
    private final LocalDate periodTo;
    private final SsrCcoOrganizationType type;
}
