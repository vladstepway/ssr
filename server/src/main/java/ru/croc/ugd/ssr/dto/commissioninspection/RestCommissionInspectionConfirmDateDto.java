package ru.croc.ugd.ssr.dto.commissioninspection;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class RestCommissionInspectionConfirmDateDto {

    private final LocalDateTime confirmedDateTime;
    private final String address;
    private final String unom;
    private final String flatNumber;
}
