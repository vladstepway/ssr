package ru.croc.ugd.ssr.dto.shipping;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

/**
 * CopingNotaryDaySchedulesRequestDto.
 */
@Value
@Builder
public class CopingNotaryDaySchedulesRequestDto {

    private LocalDate startDate;

    private String notary;

    private LocalDate endDate;

}
