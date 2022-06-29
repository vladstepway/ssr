package ru.croc.ugd.ssr.mapper;

import static org.mapstruct.ReportingPolicy.ERROR;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.croc.ugd.ssr.dto.shipping.ApartmentFromDto;
import ru.croc.ugd.ssr.dto.shipping.ApartmentToDto;
import ru.croc.ugd.ssr.dto.shipping.BookingSlotListApartmentFromDto;
import ru.croc.ugd.ssr.dto.shipping.BookingSlotListApartmentToDto;
import ru.croc.ugd.ssr.dto.shipping.PrebookingApartmentFromDto;
import ru.croc.ugd.ssr.generated.dto.RestApartmentFromDto;
import ru.croc.ugd.ssr.generated.dto.RestApartmentToDto;
import ru.croc.ugd.ssr.generated.dto.RestBookingSlotListApartmentFromDto;
import ru.croc.ugd.ssr.generated.dto.RestBookingSlotListApartmentToDto;
import ru.croc.ugd.ssr.generated.dto.RestPrebookingApartmentFromDto;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ERROR)
public interface ApartmentMapper {

    @Mapping(target = "uid", source = "UID")
    @Mapping(target = "unomOld", source = "UNOMOLD")
    @Mapping(target = "cadNumberOld", source = "CADNUMBEROLD")
    @Mapping(target = "flatNumberOld", source = "FLATNUMBEROLD")
    @Mapping(target = "entranceOld", source = "ENTRANCEOLD")
    @Mapping(target = "address", source = "ADDRESS")
    @Mapping(target = "floorOld", source = "FLOOROLD")
    @Mapping(target = "roomNumber", source = "ROOMNUMBER")
    @Mapping(target = "roomNumberOld", source = "ROOMNUMBEROLD")
    @Mapping(target = "moveDate", source = "MOVEDATE")
    ApartmentFromDto toApartmentFromDto(final RestApartmentFromDto restApartmentFromDto);

    @Mapping(target = "UID", source = "uid")
    @Mapping(target = "UNOMOLD", source = "unomOld")
    @Mapping(target = "CADNUMBEROLD", source = "cadNumberOld")
    @Mapping(target = "FLATNUMBEROLD", source = "flatNumberOld")
    @Mapping(target = "ENTRANCEOLD", source = "entranceOld")
    @Mapping(target = "ADDRESS", source = "address")
    @Mapping(target = "FLOOROLD", source = "floorOld")
    @Mapping(target = "ROOMNUMBER", source = "roomNumber")
    @Mapping(target = "ROOMNUMBEROLD", source = "roomNumberOld")
    @Mapping(target = "MOVEDATE", source = "moveDate")
    RestApartmentFromDto toRestApartmentFromDto(final ApartmentFromDto apartmentFromDto);

    @Mapping(target = "uid", source = "UID")
    @Mapping(target = "unomNew", source = "UNOMNEW")
    @Mapping(target = "cadNumberNew", source = "CADNUMBERNEW")
    @Mapping(target = "entranceNew", source = "ENTRANCENEW")
    @Mapping(target = "address", source = "ADDRESS")
    @Mapping(target = "floorNew", source = "FLOORNEW")
    @Mapping(target = "flatNumberNew", source = "FLATNUMBERNEW")
    @Mapping(target = "roomNumberNew", source = "ROOMNUMBERNEW")
    ApartmentToDto toApartmentToDto(final RestApartmentToDto restApartmentToDto);

    @Mapping(target = "UID", source = "uid")
    @Mapping(target = "UNOMNEW", source = "unomNew")
    @Mapping(target = "CADNUMBERNEW", source = "cadNumberNew")
    @Mapping(target = "ENTRANCENEW", source = "entranceNew")
    @Mapping(target = "ADDRESS", source = "address")
    @Mapping(target = "FLOORNEW", source = "floorNew")
    @Mapping(target = "FLATNUMBERNEW", source = "flatNumberNew")
    @Mapping(target = "ROOMNUMBERNEW", source = "roomNumberNew")
    RestApartmentToDto toRestApartmentToDto(final ApartmentToDto apartmentToDto);

    @Mapping(target = "unomOld", source = "UNOMOLD")
    PrebookingApartmentFromDto toPrebookingApartmentFromDto(
        final RestPrebookingApartmentFromDto restPrebookingApartmentFromDto
    );

    @Mapping(target = "unomOld", source = "UNOMOLD")
    @Mapping(target = "moveDate", source = "MOVEDATE")
    BookingSlotListApartmentFromDto toBookingSlotListApartmentFromDto(
        final RestBookingSlotListApartmentFromDto restBookingSlotListApartmentFromDto
    );

    @Mapping(target = "unomNew", source = "UNOMNEW")
    BookingSlotListApartmentToDto toBookingSlotListApartmentToDto(
        final RestBookingSlotListApartmentToDto restBookingSlotListApartmentToDto
    );

    List<ApartmentFromDto> toApartmentFromDtoList(final List<RestApartmentFromDto> restApartmentFromDtoList);

    List<RestApartmentFromDto> toRestApartmentFromDtoList(final List<ApartmentFromDto> apartmentFromDtoList);

    List<ApartmentToDto> toApartmentToDtoList(final List<RestApartmentToDto> restApartmentToDtoList);

    List<RestApartmentToDto> toRestApartmentToDtoList(final List<ApartmentToDto> apartmentToDtoList);
}
