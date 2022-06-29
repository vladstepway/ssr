package ru.croc.ugd.ssr.dto.shipping;

import lombok.Builder;
import lombok.Data;
import ru.croc.ugd.ssr.generated.dto.RestCheckResponseApplicantDto.RIGHTTYPEEnum;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Data
@Builder
public class CheckResponseApplicantDto {

    private String ssoId;

    private String snils;

    private String lastName;

    private String firstName;

    private String middleName;

    private LocalDateTime dob;

    private RIGHTTYPEEnum rightType;

    private Boolean isDisable;
}
