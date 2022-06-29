package ru.croc.ugd.ssr.dto.shipping;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingRequestDto {

    private String snils;

    private String ssoId;

    private String timetableUid;

    private LocalDateTime dateFrom;

    private LocalDateTime dateTo;

    private String bookingUid;
}
