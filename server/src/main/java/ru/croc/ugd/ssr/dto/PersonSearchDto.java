package ru.croc.ugd.ssr.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
public class PersonSearchDto {

    private final String personId;
    private final String snils;
    private final String affairId;
    private final String firstname;
    private final String middlename;
    private final String lastname;
    private final String unom;
    private final String flatNum;
    private final LocalDate birthdate;
}
