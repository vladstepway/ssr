package ru.croc.ugd.ssr.dto.realestate;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

/**
 * Запрос на изменение информации по нежилым помещениям.
 */
@Builder
@Value
public class RestUpdateNonResidentialSpaceInfoDto {

    /**
     * Наличие нежилых помещений (true - есть нежилые помещения, false - нет).
     */
    private final Boolean hasNonResidentialSpaces;
    /**
     * Нежилые помещения изъяты (true - нежилые помещения изъяты, false - нет).
     */
    private final Boolean withdrawnNonResidentialSpaces;
}
