package ru.croc.ugd.ssr.dto.commissioninspection;

import lombok.Value;

/**
 * RestCommissionInspectionRefuseDto.
 */
@Value
public class RestCommissionInspectionRefuseDto {

    private final String reason;
    private final String refusalStatus;
}
