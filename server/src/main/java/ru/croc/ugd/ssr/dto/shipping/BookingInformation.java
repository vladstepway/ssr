package ru.croc.ugd.ssr.dto.shipping;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * BookingInformation.
 */
@Data
@Builder
public class BookingInformation {

    private Boolean isPossible;

    private String reason;

    private String enoServiceNumber;

    private CheckResponseApplicantDto applicant;

    private List<ApartmentFromDto> fromApartment;

    private List<ApartmentToDto> toApartment;

}
