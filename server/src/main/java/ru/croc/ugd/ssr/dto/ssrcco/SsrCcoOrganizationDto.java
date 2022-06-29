package ru.croc.ugd.ssr.dto.ssrcco;

import lombok.Builder;
import lombok.Value;
import ru.croc.ugd.ssr.enums.SsrCcoOrganizationType;

@Value
@Builder
public class SsrCcoOrganizationDto {

    private final String externalId;
    private final String fullName;
    private final SsrCcoOrganizationType type;
}
