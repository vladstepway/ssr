package ru.croc.ugd.ssr.dto.shipping;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * BookingSlotListDto.
 */
@Data
@Builder
public class BookingSlotListDto {

    private List<BookingSlotDto> bookingSlots;
}
