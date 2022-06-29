package ru.croc.ugd.ssr.dto.shipping;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ValidateShippingDateRequestDto {
    private LocalDate startDate;
    private LocalDate endDate;
    private String district;
}
