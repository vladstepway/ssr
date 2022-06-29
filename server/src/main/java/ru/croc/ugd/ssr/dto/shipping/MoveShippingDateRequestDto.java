package ru.croc.ugd.ssr.dto.shipping;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MoveShippingDateRequestDto {

    private String bookingId;
    private LocalDateTime shippingDateStart;
    private LocalDateTime shippingDateEnd;
}
