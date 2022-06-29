package ru.croc.ugd.ssr.dto.shipping;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

/**
 * ShippingDayScheduleTotalsDto.
 */
@Builder(toBuilder = true)
@Value
public class ShippingBookingTotalDto {

    private final LocalDate shippingDate;
    private final int totalBookings;
    private final int totalSlots;
    private final boolean isRemoved;

    @JsonProperty("isRemoved")
    public boolean isRemoved() {
        return isRemoved;
    }
}
