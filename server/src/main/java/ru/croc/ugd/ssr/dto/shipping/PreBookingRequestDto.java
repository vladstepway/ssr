package ru.croc.ugd.ssr.dto.shipping;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PreBookingRequestDto {

    private String ssoId;

    private String snils;

    private PrebookingApartmentFromDto fromApartment;

    private String timetableUid;

    private LocalDateTime dateFrom;

    private LocalDateTime dateTo;

    private Integer slotDuration;

}
