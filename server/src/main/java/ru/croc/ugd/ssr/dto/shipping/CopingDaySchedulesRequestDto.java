package ru.croc.ugd.ssr.dto.shipping;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

/**
 * CopingDaySchedulesRequestDto.
 */
@Data
@Builder
public class CopingDaySchedulesRequestDto {

    private LocalDate startDate;

    private LocalDate endDate;

}
