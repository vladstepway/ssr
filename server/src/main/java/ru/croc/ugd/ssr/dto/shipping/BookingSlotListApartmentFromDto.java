package ru.croc.ugd.ssr.dto.shipping;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class BookingSlotListApartmentFromDto {

    private String unomOld;

    private LocalDate moveDate;

}
