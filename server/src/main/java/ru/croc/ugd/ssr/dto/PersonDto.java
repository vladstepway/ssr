package ru.croc.ugd.ssr.dto;

import lombok.Builder;
import lombok.Value;
import ru.croc.ugd.ssr.dto.commissioninspection.DefectDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Value
@Builder
public class PersonDto {

    private final String id;
    private final String fullName;
    private final LocalDate birthDate;
    private final String phone;
    private final String statusLiving;
    private final String birthLocation;
    private final String snils;
    private final String passport;
    private final String isDead;
    private final String isDisable;
    private final String isNonCapable;
    private final String isPreferential;

}
