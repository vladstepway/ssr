package ru.croc.ugd.ssr.dto.shipping;

import lombok.Data;

@Data
public class BookingSlotListRequestDto {

    private String ssoId;

    private String snils;

    private BookingSlotListApartmentFromDto fromApartment;

    private BookingSlotListApartmentToDto toApartment;

    private Integer slotDuration;

}
