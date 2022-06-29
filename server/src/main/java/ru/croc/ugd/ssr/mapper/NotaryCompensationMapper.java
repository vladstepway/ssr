package ru.croc.ugd.ssr.mapper;

import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static org.mapstruct.ReportingPolicy.ERROR;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.dto.notarycompensation.NotaryCompensationFlowStatus;
import ru.croc.ugd.ssr.dto.notarycompensation.RestNotaryCompensationApplicantDto;
import ru.croc.ugd.ssr.dto.notarycompensation.RestNotaryCompensationBankDetailsDto;
import ru.croc.ugd.ssr.dto.notarycompensation.RestNotaryCompensationDto;
import ru.croc.ugd.ssr.generated.dto.notarycompensation.ExternalRestAffairNotaryCompensationDto;
import ru.croc.ugd.ssr.generated.dto.notarycompensation.ExternalRestApartmentFromNotaryCompensationDto;
import ru.croc.ugd.ssr.generated.dto.notarycompensation.ExternalRestInvitedApplicantNotaryCompensationDto;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.notarycompensation.BankDetails;
import ru.croc.ugd.ssr.notarycompensation.NotaryCompensationApplicant;
import ru.croc.ugd.ssr.notarycompensation.NotaryCompensationData;
import ru.croc.ugd.ssr.service.validator.model.ValidatablePersonData;

import java.math.BigInteger;
import java.util.List;
import java.util.function.Function;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ERROR)
public interface NotaryCompensationMapper {

    @Mapping(target = "isPossible", source = "isPossible")
    @Mapping(target = "reason", source = "reason")
    @Mapping(target = "affairId", source = "validatablePersonData.person.affairId")
    @Mapping(target = "personDocumentId", source = "validatablePersonData.personId")
    @Mapping(target = "invitedApplicants", source = "personDocuments")
    @Mapping(target = "apartmentFrom", source = "validatablePersonData.person")
    ExternalRestAffairNotaryCompensationDto toRestAffairDto(
        final ValidatablePersonData validatablePersonData,
        final List<PersonDocument> personDocuments,
        final boolean isPossible,
        final String reason,
        @Context final Function<BigInteger, String> retrieveAddress
    );

    @Mapping(target = "personDocumentId", source = "id")
    @Mapping(target = "dob", source = "document.personData.birthDate")
    @Mapping(target = "ssoId", source = "document.personData.ssoID")
    @Mapping(target = "firstName", source = "document.personData.firstName")
    @Mapping(target = "lastName", source = "document.personData.lastName", qualifiedByName = "getLastNameLetter")
    @Mapping(target = "middleName", source = "document.personData.middleName")
    ExternalRestInvitedApplicantNotaryCompensationDto toRestInvitedApplicantDto(final PersonDocument personDocument);

    @Named("getLastNameLetter")
    default String getLastNameLetter(final String lastName) {
        return ofNullable(lastName)
            .map(name -> String.valueOf(name.charAt(0)))
            .map(name -> name.concat("."))
            .orElse(null);
    }

    @Mapping(target = "address", source = "personType.UNOM", qualifiedByName = "toAddress")
    @Mapping(target = "flatNumber", source = "personType.flatNum")
    @Mapping(target = "unom", source = "personType.UNOM")
    @Mapping(target = "roomNumbers", source = "personType.roomNum", qualifiedByName = "toRoomNumbers")
    ExternalRestApartmentFromNotaryCompensationDto toRestApartmentFromDto(
        final PersonType personType, @Context final Function<BigInteger, String> retrieveAddress
    );

    @Named("toAddress")
    default String toAddress(final BigInteger unom, @Context final Function<BigInteger, String> retrieveAddress) {
        return retrieveAddress.apply(unom);
    }

    @Named("toRoomNumbers")
    default String toRoomNumbers(List<String> roomNum) {
        if (nonNull(roomNum) && !roomNum.isEmpty()) {
            return String.join(", ", roomNum);
        } else {
            return null;
        }
    }

    @Mapping(target = "eno", source = "notaryCompensationData.eno")
    @Mapping(target = "applicationDateTime", source = "notaryCompensationData.applicationDateTime")
    @Mapping(target = "statusId", source = "notaryCompensationData.statusId")
    @Mapping(
        target = "status",
        source = "notaryCompensationData.statusId",
        qualifiedByName = "toApplicationStatusValue"
    )
    @Mapping(target = "affairId", source = "notaryCompensationData.affairId")
    @Mapping(
        target = "applicant",
        source = "notaryCompensationData.applicant",
        qualifiedByName = "toRestNotaryCompensationApplicant"
    )
    @Mapping(
        target = "bankDetails",
        source = "notaryCompensationData.bankDetails",
        qualifiedByName = "toRestNotaryCompensationBankDetails"
    )
    RestNotaryCompensationDto toRestNotaryCompensation(final NotaryCompensationData notaryCompensationData);

    @Named("toRestNotaryCompensationApplicant")
    RestNotaryCompensationApplicantDto toRestNotaryCompensationApplicant(
        final NotaryCompensationApplicant applicant
    );

    @Named("toRestNotaryCompensationBankDetails")
    RestNotaryCompensationBankDetailsDto toRestNotaryCompensationBankDetails(
        final BankDetails bankDetails
    );

    @Named("toApplicationStatusValue")
    default String toApplicationStatusValue(final String statusId) {
        return ofNullable(statusId)
            .map(NotaryCompensationFlowStatus::of)
            .map(NotaryCompensationFlowStatus::getSsrStatus)
            .orElse(null);
    }
}
