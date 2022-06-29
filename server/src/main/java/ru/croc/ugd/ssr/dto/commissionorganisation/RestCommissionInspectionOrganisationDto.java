package ru.croc.ugd.ssr.dto.commissionorganisation;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class RestCommissionInspectionOrganisationDto {

    private final String id;
    private final String name;
    private final String phone;
    private final String address;
}
