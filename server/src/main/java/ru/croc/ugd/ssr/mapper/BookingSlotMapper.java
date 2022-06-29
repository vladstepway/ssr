package ru.croc.ugd.ssr.mapper;

import static org.mapstruct.ReportingPolicy.ERROR;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.croc.ugd.ssr.dto.shipping.BookingRequestDto;
import ru.croc.ugd.ssr.dto.shipping.BookingResultDto;
import ru.croc.ugd.ssr.dto.shipping.BookingSlotDto;
import ru.croc.ugd.ssr.dto.shipping.BookingSlotListDto;
import ru.croc.ugd.ssr.dto.shipping.BookingSlotListRequestDto;
import ru.croc.ugd.ssr.dto.shipping.CopingDaySchedulesRequestDto;
import ru.croc.ugd.ssr.dto.shipping.DeletingDaySchedulesRequestDto;
import ru.croc.ugd.ssr.dto.shipping.PreBookingRequestDto;
import ru.croc.ugd.ssr.dto.shipping.PreBookingResultDto;
import ru.croc.ugd.ssr.generated.dto.RestBookingRequestDto;
import ru.croc.ugd.ssr.generated.dto.RestBookingResponseDto;
import ru.croc.ugd.ssr.generated.dto.RestBookingSlotDto;
import ru.croc.ugd.ssr.generated.dto.RestBookingSlotListRequestDto;
import ru.croc.ugd.ssr.generated.dto.RestBookingSlotListResponseDto;
import ru.croc.ugd.ssr.generated.dto.RestCopingDaySchedulesRequestDto;
import ru.croc.ugd.ssr.generated.dto.RestPrebookingRequestDto;
import ru.croc.ugd.ssr.generated.dto.RestPrebookingResponseDto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ERROR, uses = {ApartmentMapper.class})
public interface BookingSlotMapper {
    @Mapping(target = "ssoId", source = "SSOID")
    @Mapping(target = "fromApartment", source = "from")
    @Mapping(target = "toApartment", source = "to")
    BookingSlotListRequestDto toBookingSlotListRequestDto(
        final RestBookingSlotListRequestDto restBookingSlotListRequestDto
    );

    @Mapping(target = "to", source = "bookingSlots")
    RestBookingSlotListResponseDto toRestBookingSlotListResponseDto(
        final BookingSlotListDto bookingSlotListDto
    );

    @Mapping(target = "UID", source = "uid")
    RestBookingSlotDto toRestBookingSlotDto(final BookingSlotDto bookingSlotDto);

    @Mapping(target = "ssoId", source = "SSOID")
    BookingRequestDto toBookingRequestDto(final RestBookingRequestDto restBookingRequestDto);

    @Mapping(target = "bookingUid", source = "bookingUid")
    RestBookingResponseDto toRestBookingResponseDto(final BookingResultDto bookingResultDto);

    @Mapping(target = "ssoId", source = "SSOID")
    @Mapping(target = "fromApartment", source = "from")
    PreBookingRequestDto toPreBookingRequestDto(final RestPrebookingRequestDto restPrebookingRequestDto);

    RestPrebookingResponseDto toRestPrebookingResponseDto(final PreBookingResultDto preBookingResultDto);

    CopingDaySchedulesRequestDto toCopingDaySchedulesRequestDto(
        final RestCopingDaySchedulesRequestDto restCopingDaySchedulesRequestDto);

    DeletingDaySchedulesRequestDto toDeletingDaySchedulesRequestDto(
        final RestCopingDaySchedulesRequestDto restCopingDaySchedulesRequestDto);

}
