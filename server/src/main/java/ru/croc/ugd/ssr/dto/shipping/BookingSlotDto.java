package ru.croc.ugd.ssr.dto.shipping;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingSlotDto {

    private String uid;

    private String preBookingId;

    private LocalDateTime dateFrom;

    private LocalDateTime dateTo;
}
