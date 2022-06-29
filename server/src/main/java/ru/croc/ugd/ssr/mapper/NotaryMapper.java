package ru.croc.ugd.ssr.mapper;

import static java.util.Optional.ofNullable;
import static org.mapstruct.ReportingPolicy.ERROR;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.dto.RestEmployeeResponseDto;
import ru.croc.ugd.ssr.dto.notary.RestCreateUpdateNotaryRequestDto;
import ru.croc.ugd.ssr.dto.notary.RestNotaryInfoDto;
import ru.croc.ugd.ssr.dto.notary.RestNotaryResponseDto;
import ru.croc.ugd.ssr.generated.dto.notary.RestNotaryApartmentFromDto;
import ru.croc.ugd.ssr.generated.dto.notary.RestNotaryApartmentToDto;
import ru.croc.ugd.ssr.generated.dto.notary.RestNotaryApplicantDto;
import ru.croc.ugd.ssr.generated.dto.notary.RestNotaryDto;
import ru.croc.ugd.ssr.generated.dto.notary.RestNotaryListDto;
import ru.croc.ugd.ssr.model.notary.NotaryDocument;
import ru.croc.ugd.ssr.notary.Notary;
import ru.croc.ugd.ssr.notary.NotaryAddress;
import ru.reinform.cdp.ldap.model.UserBean;

import java.util.List;
import java.util.Objects;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ERROR)
public interface NotaryMapper {

    @Mapping(target = "id", source = "notary.documentID")
    @Mapping(target = "status", source = "notary.notaryData.status")
    @Mapping(target = "registrationNumber", source = "notary.notaryData.registrationNumber")
    @Mapping(target = "fullName", source = "notary.notaryData.fullName")
    @Mapping(target = "phones", source = "notary.notaryData.phones.phone")
    @Mapping(target = "address", source = "notary.notaryData.address")
    @Mapping(target = "employee", source = "employee")
    @Mapping(target = "additionalInformation", source = "notary.notaryData.additionalInformation")
    RestNotaryResponseDto toRestNotaryResponseDto(
        final Notary notary, final UserBean employee
    );

    @Mapping(target = "id", source = "notary.documentID")
    @Mapping(target = "registrationNumber", source = "notary.notaryData.registrationNumber")
    @Mapping(target = "fullName", source = "notary.notaryData.fullName")
    @Mapping(target = "phones", source = "notary.notaryData.phones.phone")
    @Mapping(target = "address", source = "notary.notaryData.address")
    RestNotaryInfoDto toRestNotaryInfoDto(final Notary notary);

    @Mapping(target = "document.notaryData.registrationNumber", source = "dto.registrationNumber")
    @Mapping(target = "document.notaryData.fullName", source = "dto.fullName")
    @Mapping(target = "document.notaryData.phones.phone", source = "dto.phones")
    @Mapping(target = "document.notaryData.address", source = "dto.address")
    @Mapping(target = "document.notaryData.status", constant = "ACTIVE")
    @Mapping(target = "document.notaryData.employeeLogin", source = "dto.employeeLogin")
    @Mapping(target = "document.notaryData.additionalInformation", source = "dto.additionalInformation")
    NotaryDocument toNotaryDocument(
        @MappingTarget final NotaryDocument notaryDocument,
        final RestCreateUpdateNotaryRequestDto dto
    );

    @Mapping(target = "login", source = "employee.accountName")
    @Mapping(target = "fullName", source = "employee.displayName")
    @Mapping(target = "position", source = "employee.post")
    @Mapping(target = "mail", source = "employee.mail")
    @Mapping(target = "organisation", source = "employee.departmentFullName")
    RestEmployeeResponseDto toRestNotaryEmployeeResponseDto(final UserBean employee);

    List<RestNotaryApplicantDto> toRestApplicantDtoList(final List<PersonType> personType);

    @Mapping(target = "dob", source = "birthDate")
    @Mapping(target = "ssoId", source = "ssoID")
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName", qualifiedByName = "getLastNameLetter")
    @Mapping(target = "middleName", source = "middleName")
    RestNotaryApplicantDto toRestNotaryApplicantDto(final PersonType personType);

    @Named("getLastNameLetter")
    default String getLastNameLetter(final String lastName) {
        return ofNullable(lastName)
            .map(name -> String.valueOf(name.charAt(0)))
            .map(name -> name.concat("."))
            .orElse(null);
    }

    @Mapping(target = "unom", source = "ccoUnom")
    @Mapping(target = "address", source = "ccoAddress")
    @Mapping(target = "flatNumber", source = "ccoFlatNum")
    RestNotaryApartmentToDto toRestNotaryApartmentToDto(final PersonType.NewFlatInfo.NewFlat newFlat);

    @Mapping(target = "address", source = "oldAddress")
    @Mapping(target = "flatNumber", source = "personType.flatNum")
    @Mapping(target = "unom", source = "personType.UNOM")
    @Mapping(target = "roomNumbers", source = "personType.roomNum", qualifiedByName = "toString")
    RestNotaryApartmentFromDto toRestNotaryApartmentFromDto(
        final String oldAddress,
        final PersonType personType
    );

    List<RestNotaryApartmentToDto> toRestNotaryApartmentToDtoList(
        final List<PersonType.NewFlatInfo.NewFlat> newFlats
    );

    default RestNotaryListDto toRestNotaryListDto(final List<NotaryDocument> notaryDocuments) {
        final RestNotaryListDto restFetchNotaryResponseDto = new RestNotaryListDto();
        restFetchNotaryResponseDto.notaries(toRestNotaryDtoList(notaryDocuments));

        return restFetchNotaryResponseDto;
    }

    List<RestNotaryDto> toRestNotaryDtoList(final List<NotaryDocument> notaryDocuments);

    @Mapping(target = "id", source = "notary.document.documentID")
    @Mapping(target = "fio", source = "notary.document.notaryData.fullName")
    @Mapping(target = "address", source = "notary.document.notaryData.address", qualifiedByName = "toAddress")
    @Mapping(target = "phone", source = "notary.document.notaryData.phones.phone", qualifiedByName = "toPhoneString")
    @Mapping(target = "additionalData", source = "notary.document.notaryData.additionalInformation")
    RestNotaryDto toRestNotaryDto(final NotaryDocument notary);

    @Named("toPhoneString")
    default String toPhoneString(final List<String> list) {
        return String.join(", ", list);
    }

    @Named("toAddress")
    default String toAddress(final NotaryAddress notaryAddress) {
        return ofNullable(notaryAddress)
            .map(NotaryAddress::getAddress)
            .map(address -> {
                if (Objects.nonNull(notaryAddress.getAdditionalInformation())) {
                    return address.concat(", примечание: " + notaryAddress.getAdditionalInformation());
                }
                return address;
            })
            .orElse(null);
    }

}
