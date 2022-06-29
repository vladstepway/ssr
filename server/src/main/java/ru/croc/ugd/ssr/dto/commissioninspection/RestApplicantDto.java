package ru.croc.ugd.ssr.dto.commissioninspection;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class RestApplicantDto {

    private final String id;
    private final String fullName;
    private final String email;
    private final String phone;
    private final String additionalPhone;
}
