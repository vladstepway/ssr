package ru.croc.ugd.ssr.mapper;

import static java.util.Optional.ofNullable;
import static org.mapstruct.ReportingPolicy.ERROR;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.PersonType.OfferLetters.OfferLetter;
import ru.croc.ugd.ssr.dto.RealEstateDataAndFlatInfoDto;
import ru.croc.ugd.ssr.dto.flatappointment.FlatAppointmentFlowStatus;
import ru.croc.ugd.ssr.dto.flatappointment.RestFlatAppointmentApplicantDto;
import ru.croc.ugd.ssr.dto.flatappointment.RestFlatAppointmentDto;
import ru.croc.ugd.ssr.flatappointment.Applicant;
import ru.croc.ugd.ssr.flatappointment.FlatAppointmentData;
import ru.croc.ugd.ssr.flatappointment.FlatAppointmentParticipant;
import ru.croc.ugd.ssr.generated.dto.flatappointment.ExternalRestFlatAppointmentApplicantDto;
import ru.croc.ugd.ssr.generated.dto.flatappointment.ExternalRestFlatAppointmentCipDto;
import ru.croc.ugd.ssr.generated.dto.flatappointment.ExternalRestFlatAppointmentDto;
import ru.croc.ugd.ssr.generated.dto.flatappointment.ExternalRestFlatAppointmentLetterDto;
import ru.croc.ugd.ssr.model.CipDocument;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.flatAppointment.FlatAppointmentDocument;
import ru.croc.ugd.ssr.utils.CipUtils;
import ru.croc.ugd.ssr.utils.PersonUtils;
import ru.croc.ugd.ssr.utils.RealEstateUtils;

import java.util.List;
import java.util.function.Function;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ERROR)
public interface FlatAppointmentMapper {

    @Mapping(
        target = "letter",
        expression = "java( toExternalRestFlatAppointmentLetterDto(letter, toChedFileLink) )"
    )
    @Mapping(target = "cip", source = "cip", qualifiedByName = "toCipDto")
    @Mapping(target = "bookingId", source = "flatAppointment.bookingId")
    @Mapping(target = "appointmentDateTime", source = "flatAppointment.appointmentDateTime")
    ExternalRestFlatAppointmentDto toExternalRestFlatAppointmentDto(
        final FlatAppointmentData flatAppointment,
        final CipDocument cip,
        final OfferLetter letter,
        final Function<OfferLetter, String> toChedFileLink
    );

    @Named("toCipDto")
    default ExternalRestFlatAppointmentCipDto toCipDto(final CipDocument cip) {
        return toExternalRestFlatAppointmentCipDto(cip);
    }

    @Mapping(target = "id", source = "letter.letterId")
    @Mapping(target = "fileLink", expression = "java( toChedFileLink.apply(letter) )")
    ExternalRestFlatAppointmentLetterDto toExternalRestFlatAppointmentLetterDto(
        final OfferLetter letter, final Function<OfferLetter, String> toChedFileLink
    );

    @Mapping(target = "ssoId", source = "ssoID")
    @Mapping(target = "lastName", source = "lastName", qualifiedByName = "getLastNameLetter")
    @Mapping(target = "dob", source = "birthDate")
    ExternalRestFlatAppointmentApplicantDto toExternalRestFlatAppointmentApplicantDto(final PersonType personType);

    List<ExternalRestFlatAppointmentApplicantDto> toExternalRestFlatAppointmentApplicantDtos(
        final List<PersonType> personType
    );

    @Mapping(target = "id", source = "cip.document.documentID")
    @Mapping(target = "address", source = "cip", qualifiedByName = "toCipAddress")
    @Mapping(target = "phone", source = "cip.document.cipData.phone")
    @Mapping(target = "startDate", source = "cip.document.cipData.cipDateStart")
    @Mapping(target = "slotsAllowed", source = "cip.document.cipData.slotNumber", defaultValue = "1")
    ExternalRestFlatAppointmentCipDto toExternalRestFlatAppointmentCipDto(final CipDocument cip);

    @Named("toAddressFrom")
    default String toAddressFrom(final RealEstateDataAndFlatInfoDto realEstateDataAndFlatInfoDto) {
        return RealEstateUtils.getFlatAddress(realEstateDataAndFlatInfoDto);
    }

    @Named("toCipAddress")
    default String toCipAddress(final CipDocument cip) {
        return CipUtils.getCipAddress(cip.getDocument().getCipData());
    }

    @Mapping(target = "id", source = "document.id")
    @Mapping(target = "eno", source = "document.document.flatAppointmentData.eno")
    @Mapping(target = "appointmentDateTime", source = "document.document.flatAppointmentData.appointmentDateTime")
    @Mapping(target = "statusId", source = "document.document.flatAppointmentData.statusId")
    @Mapping(
        target = "status",
        source = "document.document.flatAppointmentData.statusId",
        qualifiedByName = "toApplicationStatusValue"
    )
    @Mapping(target = "letter.id", source = "document.document.flatAppointmentData.offerLetterId")
    @Mapping(target = "letter.fileLink", source = "document.document.flatAppointmentData.offerLetterFileLink")
    @Mapping(target = "isPerformed", source = "document.document.flatAppointmentData.performed")
    @Mapping(target = "participants", source = "document.document.flatAppointmentData.participants.participants")
    @Mapping(target = "cancelDate", source = "document.document.flatAppointmentData.cancelDate")
    @Mapping(target = "cancelReason", source = "document.document.flatAppointmentData.cancelReason")
    @Mapping(target = "processInstanceId", source = "document.document.flatAppointmentData.processInstanceId")
    @Mapping(target = "cipId", source = "document.document.flatAppointmentData.cipId")
    @Mapping(target = "cipAddress", source = "cip", qualifiedByName = "toCipAddress")
    @Mapping(target = "slotNumber", source = "cip.document.cipData.slotNumber", defaultValue = "1")
    @Mapping(target = "applicant", source = "applicantDto")
    RestFlatAppointmentDto toRestFlatAppointmentDto(
        final FlatAppointmentDocument document,
        final CipDocument cip,
        final RestFlatAppointmentApplicantDto applicantDto
    );

    @Mapping(target = "personDocumentId", source = "personDocument.id")
    @Mapping(target = "lastName", source = "personDocument.document.personData.lastName")
    @Mapping(target = "firstName", source = "personDocument.document.personData.firstName")
    @Mapping(target = "middleName", source = "personDocument.document.personData.middleName")
    @Mapping(target = "email", source = "flatAppointment.applicant.email")
    @Mapping(target = "phone", source = "flatAppointment.applicant.phone")
    @Mapping(
        target = "additionalPhone",
        source = "flatAppointment.applicant.additionalPhone"
    )
    @Mapping(target = "addressFrom", source = "realEstateDataAndFlatInfoDto", qualifiedByName = "toAddressFrom")
    RestFlatAppointmentApplicantDto toRestFlatAppointmentApplicantDto(
        final FlatAppointmentData flatAppointment,
        final PersonDocument personDocument,
        final RealEstateDataAndFlatInfoDto realEstateDataAndFlatInfoDto
    );

    @Mapping(target = "lastName", ignore = true)
    @Mapping(target = "firstName", ignore = true)
    @Mapping(target = "middleName", ignore = true)
    @Mapping(target = "addressFrom", ignore = true)
    RestFlatAppointmentApplicantDto toRestFlatAppointmentApplicantDto(final Applicant applicant);

    @Named("getLastNameLetter")
    default String getLastNameLetter(final String lastName) {
        return ofNullable(lastName)
            .map(name -> String.valueOf(name.charAt(0)))
            .map(name -> name.concat("."))
            .orElse(null);
    }

    @Named("toApplicationStatusValue")
    default String toApplicationStatusValue(final String statusId) {
        return ofNullable(statusId)
            .map(FlatAppointmentFlowStatus::of)
            .map(FlatAppointmentFlowStatus::getSsrStatus)
            .orElse(null);
    }

    List<FlatAppointmentParticipant> toFlatAppointmentParticipantList(final List<PersonDocument> participants);

    @Mapping(target = "personDocumentId", source = "personDocument.id")
    @Mapping(
        target = "fullName",
        source = "personDocument.document.personData",
        qualifiedByName = "toFullName"
    )
    @Mapping(
        target = "birthDate",
        source = "personDocument.document.personData.birthDate"
    )
    FlatAppointmentParticipant toFlatAppointmentParticipant(
        final PersonDocument personDocument
    );

    @Named("toFullName")
    default String toFullName(final PersonType personType) {
        return PersonUtils.getFullName(personType);
    }

}
