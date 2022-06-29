package ru.croc.ugd.ssr.dto.shipping;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

/**
 * DeletingDaySchedulesRequestDto.
 */
@Data
@Builder
public class DeletingDaySchedulesRequestDto {

    private LocalDate startDate;

    private LocalDate endDate;

    private String district;

}
