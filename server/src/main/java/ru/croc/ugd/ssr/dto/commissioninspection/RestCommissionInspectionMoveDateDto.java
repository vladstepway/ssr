package ru.croc.ugd.ssr.dto.commissioninspection;

import lombok.Value;

import java.time.LocalDateTime;

/**
 * RestCommissionInspectionMoveDateDto.
 */
@Value
public class RestCommissionInspectionMoveDateDto {

    private final LocalDateTime moveDateTime;
}
