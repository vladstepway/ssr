package ru.croc.ugd.ssr.mapper;

import static java.util.Objects.nonNull;
import static org.mapstruct.ReportingPolicy.ERROR;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.croc.ugd.ssr.FlatType;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.dto.PersonDataAndFlatInfoDto;
import ru.croc.ugd.ssr.dto.RealEstateDataAndFlatInfoDto;
import ru.croc.ugd.ssr.dto.TradeAdditionFlatInfoDto;
import ru.croc.ugd.ssr.dto.shipping.ApartmentFromDto;
import ru.croc.ugd.ssr.dto.shipping.ApartmentToDto;
import ru.croc.ugd.ssr.dto.shipping.BookingInformation;
import ru.croc.ugd.ssr.dto.shipping.CheckResponseApplicantDto;
import ru.croc.ugd.ssr.generated.dto.RestCheckResponseApplicantDto;
import ru.croc.ugd.ssr.trade.EstateInfoType;
import ru.croc.ugd.ssr.utils.RealEstateUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ERROR, uses = ApartmentMapper.class)
public interface CheckBookingInformationMapper {

    @Mapping(target = "isPossible", constant = "true")
    @Mapping(target = "reason", constant = "")
    @Mapping(target = "enoServiceNumber", constant = "")
    @Mapping(target = "fromApartment", source = "dto", qualifiedByName = "extractFromApartmentFromPersonDataAndFlat")
    @Mapping(target = "applicant", source = "dto")
    @Mapping(target = "toApartment", source = "dto", qualifiedByName = "extractToApartmentFromPersonData")
    BookingInformation toBookingInformationFromPersonDataAndFlat(PersonDataAndFlatInfoDto dto);

    @Mapping(target = "ssoId", source = "dto.personData.ssoID")
    @Mapping(target = "snils", source = "dto.personData.SNILS")
    @Mapping(target = "lastName", source = "dto.personData.lastName")
    @Mapping(target = "firstName", source = "dto.personData.firstName")
    @Mapping(target = "middleName", source = "dto.personData.middleName")
    @Mapping(target = "dob", source = "dto.personData.birthDate",
        qualifiedByName = "extractDobFromBirthDate")
    @Mapping(target = "rightType", source = "dto.personData.owner",
        qualifiedByName = "extractRightTypeFromPersonData")
    @Mapping(target = "isDisable", source = "dto.personData.addInfo.isDisable",
        qualifiedByName = "extractIsDisable")
    CheckResponseApplicantDto toCheckResponseApplicantDto(PersonDataAndFlatInfoDto dto);

    @Mapping(target = "uid", source = "dto.flat.flatID")
    @Mapping(target = "unomOld", source = "dto.personData.UNOM")
    @Mapping(target = "cadNumberOld", source = "dto.personData.cadNum")
    @Mapping(target = "flatNumberOld", source = "dto.flat", qualifiedByName = "getFlatNumberOld")
    @Mapping(target = "entranceOld", source = "dto.flat.entrance")
    @Mapping(target = "floorOld", source = "dto.flat.floor")
    @Mapping(target = "roomNumber", source = "dto.personData.roomNum")
    @Mapping(target = "roomNumberOld", source = "dto.flat.roomsCount")
    @Mapping(target = "moveDate", source = "dto.moveDate")
    @Mapping(target = "address", source = "dto", qualifiedByName = "toFullAddress")
    ApartmentFromDto getApartmentFromByPersonData(final PersonDataAndFlatInfoDto dto);

    @Mapping(target = "uid", source = "dto.flat.flatID")
    @Mapping(target = "unomOld", source = "dto.estateInfoType.unom")
    @Mapping(target = "cadNumberOld", source = "dto.estateInfoType.cadNumber")
    @Mapping(target = "flatNumberOld", source = "dto.flat", qualifiedByName = "getFlatNumberOld")
    @Mapping(target = "entranceOld", source = "dto.flat.entrance")
    @Mapping(target = "floorOld", source = "dto.flat.floor")
    @Mapping(target = "roomNumber", source = "dto.estateInfoType.rooms")
    @Mapping(target = "roomNumberOld", source = "dto.flat.roomsCount")
    @Mapping(target = "moveDate", source = "dto.moveDate")
    @Mapping(target = "address", source = "dto", qualifiedByName = "toFullAddressTradeAddition")
    ApartmentFromDto getApartmentFromByTradeAdditionData(final TradeAdditionFlatInfoDto dto);

    @Mapping(target = "entranceNew", source = "newFlat.entrance")
    @Mapping(target = "floorNew", source = "newFlat.floor")
    @Mapping(target = "roomNumberNew", source = "newFlat.roomNum")
    @Mapping(target = "uid", source = "newFlat", qualifiedByName = "newFlatId")
    @Mapping(target = "address", source = "newFlat.ccoAddress")
    @Mapping(target = "unomNew", source = "newFlat.ccoUnom")
    @Mapping(target = "cadNumberNew", source = "newFlat.ccoCadNum")
    @Mapping(target = "flatNumberNew", source = "newFlat.ccoFlatNum")
    ApartmentToDto getApartmentToByPersonData(final PersonType.NewFlatInfo.NewFlat newFlat);

    @Mapping(target = "uid", source = "estateInfoType", qualifiedByName = "newFlatIdFromTradeAddition")
    @Mapping(target = "unomNew", source = "unom")
    @Mapping(target = "address", source = "address")
    @Mapping(target = "cadNumberNew", source = "cadNumber")
    @Mapping(target = "flatNumberNew", source = "flatNumber")
    @Mapping(target = "entranceNew", ignore = true)
    @Mapping(target = "floorNew", ignore = true)
    @Mapping(target = "roomNumberNew", ignore = true)
    ApartmentToDto getApartmentToByEstateInfoType(final EstateInfoType estateInfoType);

    @Named("getFlatNumberOld")
    default String extractFlatNumberOld(final FlatType flatType) {
        if (flatType == null) {
            return null;
        }
        if (flatType.getApartmentL4VALUE() != null) {
            return flatType.getApartmentL4VALUE();
        }
        return flatType.getFlatNumber();
    }

    @Named("toFullAddress")
    default String toFullAddress(PersonDataAndFlatInfoDto dto) {
        final RealEstateDataAndFlatInfoDto realEstateDataAndFlatInfoDto = RealEstateDataAndFlatInfoDto
            .builder()
            .realEstateData(dto.getRealEstateData())
            .flat(dto.getFlat())
            .build();
        return RealEstateUtils.getFlatAddress(realEstateDataAndFlatInfoDto);
    }

    @Named("toFullAddressTradeAddition")
    default String toFullAddressTradeAddition(TradeAdditionFlatInfoDto dto) {
        return RealEstateUtils.getFlatAddress(dto.getRealEstateData(), dto.getFlat());
    }

    @Named("extractDobFromBirthDate")
    default LocalDateTime extractDobFromBirthDate(final LocalDate birthDate) {
        if (birthDate != null) {
            return LocalDateTime.of(birthDate, LocalTime.NOON);
        } else {
            return null;
        }
    }

    @Named("extractIsDisable")
    default boolean extractIsDisable(final String isDisable) {
        return "true".equalsIgnoreCase(isDisable);
    }

    @Named("newFlatId")
    default String generateIdForNewFlat(final PersonType.NewFlatInfo.NewFlat newFlat) {
        return String.format("%s_%s", newFlat.getCcoUnom(), newFlat.getCcoFlatNum());
    }

    @Named("newFlatIdFromTradeAddition")
    default String generateIdForNewFlat(final EstateInfoType estateInfoType) {
        return String.format("%s_%s", estateInfoType.getUnom(), estateInfoType.getFlatNumber());
    }

    @Named("extractRightTypeFromPersonData")
    default RestCheckResponseApplicantDto.RIGHTTYPEEnum extractRightTypeFromPersonData(final boolean owner) {

        return owner ? RestCheckResponseApplicantDto.RIGHTTYPEEnum.OWNER :
            RestCheckResponseApplicantDto.RIGHTTYPEEnum.EMPLOYER;
    }

    @Named("extractFromApartmentFromPersonDataAndFlat")
    default List<ApartmentFromDto> extractFromApartmentFromPersonDataAndFlat(final PersonDataAndFlatInfoDto dto) {
        final List<ApartmentFromDto> apartmentFromDtos = new ArrayList<>();
        if (dto != null && dto.getPersonData() != null && dto.getFlat() != null) {
            apartmentFromDtos.add(getApartmentFromByPersonData(dto));

        }
        return apartmentFromDtos;
    }

    @Named("extractToApartmentFromPersonDataAndFlat")
    default List<ApartmentToDto> extractToApartmentFromPersonData(final PersonDataAndFlatInfoDto dto) {
        final Set<String> signedContractOrderIds = Optional.ofNullable(dto)
            .map(PersonDataAndFlatInfoDto::getPersonData)
            .map(PersonType::getContracts)
            .map(PersonType.Contracts::getContract)
            .map(List::stream)
            .orElse(Stream.empty())
            .filter(contract -> nonNull(contract.getContractSignDate()))
            .map(PersonType.Contracts.Contract::getOrderId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

        return Optional.ofNullable(dto)
            .map(PersonDataAndFlatInfoDto::getPersonData)
            .map(PersonType::getNewFlatInfo)
            .map(PersonType.NewFlatInfo::getNewFlat)
            .map(List::stream)
            .orElse(Stream.empty())
            .filter(newFlat -> nonNull(newFlat.getCcoUnom()))
            .filter(newFlat -> nonNull(newFlat.getAgrDate())
                || signedContractOrderIds.contains(newFlat.getOrderId()))
            .map(this::getApartmentToByPersonData)
            .collect(Collectors.toList());
    }
}
