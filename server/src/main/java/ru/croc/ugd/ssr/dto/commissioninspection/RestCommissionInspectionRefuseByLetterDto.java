package ru.croc.ugd.ssr.dto.commissioninspection;

import lombok.Value;

@Value
public class RestCommissionInspectionRefuseByLetterDto {

    private final String reason;
    private final String letterId;
}
