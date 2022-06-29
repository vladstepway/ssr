package ru.croc.ugd.ssr.dto.notary;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

/**
 * Правообладатель.
 */
@Builder
@Value
public class RestNotaryApplicationOwnerResponseDto {

    /**
     * ID жителя в Person.
     */
    private final String personUid;
    /**
     * Телефон.
     */
    private final String phone;
    /**
     * Доп. телефон.
     */
    private final  String additionalPhone;
    /**
     * Фамилия, имя, отчество.
     */
    private final String fullName;
    /**
     * Дата рождения.
     */
    private final LocalDate birthDate;

}
