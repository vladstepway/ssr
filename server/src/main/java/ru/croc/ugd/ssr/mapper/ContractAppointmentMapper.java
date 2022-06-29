package ru.croc.ugd.ssr.mapper;

import static java.util.Optional.ofNullable;
import static org.mapstruct.ReportingPolicy.ERROR;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.PersonType.Contracts.Contract.Files.File;
import ru.croc.ugd.ssr.contractappointment.Applicant;
import ru.croc.ugd.ssr.contractappointment.ContractAppointmentData;
import ru.croc.ugd.ssr.contractappointment.ContractAppointmentFile;
import ru.croc.ugd.ssr.dto.RealEstateDataAndFlatInfoDto;
import ru.croc.ugd.ssr.dto.contractappointment.ContractAppointmentFlowStatus;
import ru.croc.ugd.ssr.dto.contractappointment.RestContractAppointmentApplicantDto;
import ru.croc.ugd.ssr.dto.contractappointment.RestContractAppointmentDto;
import ru.croc.ugd.ssr.generated.dto.contractappointment.ExternalRestContractAppointmentApplicantDto;
import ru.croc.ugd.ssr.generated.dto.contractappointment.ExternalRestContractAppointmentCipDto;
import ru.croc.ugd.ssr.generated.dto.contractappointment.ExternalRestContractAppointmentDto;
import ru.croc.ugd.ssr.model.CipDocument;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.contractappointment.ContractAppointmentDocument;
import ru.croc.ugd.ssr.utils.CipUtils;
import ru.croc.ugd.ssr.utils.RealEstateUtils;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ERROR)
public interface ContractAppointmentMapper {

    @Mapping(target = "address", source = "cip", qualifiedByName = "toCipAddress")
    @Mapping(target = "phone", source = "cip.document.cipData.phone")
    @Mapping(target = "id", source = "cip.document.documentID")
    ExternalRestContractAppointmentCipDto toRestContractAppointmentCipDto(final CipDocument cip);

    List<ExternalRestContractAppointmentApplicantDto> toRestContractAppointmentApplicantDtoList(
        final List<PersonType> personType
    );

    @Mapping(target = "dob", source = "birthDate")
    @Mapping(target = "ssoId", source = "ssoID")
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName", qualifiedByName = "getLastNameLetter")
    @Mapping(target = "middleName", source = "middleName")
    ExternalRestContractAppointmentApplicantDto toRestContractAppointmentApplicantDto(final PersonType personType);

    @Mapping(target = "personDocumentId", source = "personDocument.id")
    @Mapping(target = "lastName", source = "personDocument.document.personData.lastName")
    @Mapping(target = "firstName", source = "personDocument.document.personData.firstName")
    @Mapping(target = "middleName", source = "personDocument.document.personData.middleName")
    @Mapping(target = "email", source = "contractAppointment.applicant.email")
    @Mapping(target = "phone", source = "contractAppointment.applicant.phone")
    @Mapping(
        target = "additionalPhone",
        source = "contractAppointment.applicant.additionalPhone"
    )
    @Mapping(target = "addressFrom", source = "realEstateDataAndFlatInfoDto", qualifiedByName = "toAddressFrom")
    RestContractAppointmentApplicantDto toRestContractAppointmentApplicantDto(
        final ContractAppointmentData contractAppointment,
        final PersonDocument personDocument,
        final RealEstateDataAndFlatInfoDto realEstateDataAndFlatInfoDto
    );

    @Mapping(target = "lastName", ignore = true)
    @Mapping(target = "firstName", ignore = true)
    @Mapping(target = "middleName", ignore = true)
    @Mapping(target = "addressFrom", ignore = true)
    RestContractAppointmentApplicantDto toRestContractAppointmentApplicantDto(final Applicant applicant);

    @Named("getLastNameLetter")
    default String getLastNameLetter(final String lastName) {
        return ofNullable(lastName)
            .map(name -> String.valueOf(name.charAt(0)))
            .map(name -> name.concat("."))
            .orElse(null);
    }

    @Mapping(target = "id", source = "document.id")
    @Mapping(target = "eno", source = "document.document.contractAppointmentData.eno")
    @Mapping(target = "statusId", source = "document.document.contractAppointmentData.statusId")
    @Mapping(
        target = "status",
        source = "document.document.contractAppointmentData.statusId",
        qualifiedByName = "toApplicationStatusValue"
    )
    @Mapping(target = "appointmentDateTime", source = "document.document.contractAppointmentData.appointmentDateTime")
    @Mapping(target = "contractOrderId", source = "document.document.contractAppointmentData.contractOrderId")
    @Mapping(target = "addressTo", source = "document.document.contractAppointmentData.addressTo")
    @Mapping(target = "cipId", source = "document.document.contractAppointmentData.cipId")
    @Mapping(target = "cipAddress", source = "cipDocument", qualifiedByName = "toCipAddress")
    @Mapping(
        target = "isContractEntered",
        source = "document.document.contractAppointmentData.isContractEntered",
        qualifiedByName = "toIsContractEntered"
    )
    @Mapping(target = "cancelDate", source = "document.document.contractAppointmentData.cancelDate")
    @Mapping(target = "cancelReason", source = "document.document.contractAppointmentData.cancelReason")
    @Mapping(target = "refuseReason", source = "document.document.contractAppointmentData.refuseReason")
    @Mapping(target = "contractSignDate", source = "document.document.contractAppointmentData.contractSignDate")
    @Mapping(target = "actSignDate", source = "document.document.contractAppointmentData.actSignDate")
    @Mapping(target = "processInstanceId", source = "document.document.contractAppointmentData.processInstanceId")
    @Mapping(target = "applicant", source = "applicantDto")
    @Mapping(target = "signType", source = "document.document.contractAppointmentData.signType")
    RestContractAppointmentDto toRestContractAppointmentDto(
        final ContractAppointmentDocument document,
        final CipDocument cipDocument,
        final RestContractAppointmentApplicantDto applicantDto
    );

    @Named("toAddressFrom")
    default String toAddressFrom(final RealEstateDataAndFlatInfoDto realEstateDataAndFlatInfoDto) {
        return RealEstateUtils.getFlatAddress(realEstateDataAndFlatInfoDto);
    }

    @Named("toCipAddress")
    default String toCipAddress(final CipDocument cipDocument) {
        return CipUtils.getCipAddress(cipDocument.getDocument().getCipData());
    }

    @Mapping(target = "cip", source = "cip")
    @Mapping(target = "address", source = "contractAppointment.addressTo")
    @Mapping(target = "bookingId", source = "contractAppointment.bookingId")
    @Mapping(target = "appointmentDateTime", source = "contractAppointment.appointmentDateTime")
    ExternalRestContractAppointmentDto toExternalRestContractAppointmentDto(
        final ContractAppointmentData contractAppointment,
        final CipDocument cip
    );

    @Named("toApplicationStatusValue")
    default String toApplicationStatusValue(final String statusId) {
        return ofNullable(statusId)
            .map(ContractAppointmentFlowStatus::of)
            .map(ContractAppointmentFlowStatus::getSsrStatus)
            .orElse(null);
    }

    @Named("toIsContractEntered")
    default boolean toIsContractEntered(final Boolean isContractEntered) {
        return Boolean.TRUE.equals(isContractEntered);
    }

    @Mapping(target = "fileName")
    @Mapping(target = "fileStoreId", source = "fileLink")
    @Mapping(target = "chedFileId", source = "chedFileId")
    ContractAppointmentFile toContractAppointmentFile(final File file);

}
