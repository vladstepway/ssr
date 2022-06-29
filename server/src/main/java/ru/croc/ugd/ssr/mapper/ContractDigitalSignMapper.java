package ru.croc.ugd.ssr.mapper;

import static java.util.Optional.ofNullable;
import static org.mapstruct.ReportingPolicy.ERROR;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.computel.common.filenet.client.FilenetFileBean;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.contractappointment.ContractAppointment;
import ru.croc.ugd.ssr.contractappointment.ContractAppointmentData;
import ru.croc.ugd.ssr.contractdigitalsign.ContractDigitalSignFile;
import ru.croc.ugd.ssr.contractdigitalsign.ElkUserNotification;
import ru.croc.ugd.ssr.contractdigitalsign.Employee;
import ru.croc.ugd.ssr.contractdigitalsign.Owner;
import ru.croc.ugd.ssr.contractdigitalsign.SignData;
import ru.croc.ugd.ssr.dto.ElkUserNotificationDto;
import ru.croc.ugd.ssr.dto.contractappointment.RestEmployeeSignatureDto;
import ru.croc.ugd.ssr.dto.contractappointment.RestFileSignatureDto;
import ru.croc.ugd.ssr.dto.contractappointment.RestOwnerSignatureDto;
import ru.croc.ugd.ssr.dto.contractdigitalsign.ContractDigitalSignFlowStatus;
import ru.croc.ugd.ssr.dto.contractdigitalsign.RestContractAppointmentDto;
import ru.croc.ugd.ssr.dto.contractdigitalsign.RestEmployeeContractDigitalSignDto;
import ru.croc.ugd.ssr.dto.contractdigitalsign.RestFileDataDto;
import ru.croc.ugd.ssr.dto.contractdigitalsign.RestOwnerDto;
import ru.croc.ugd.ssr.generated.dto.contractdigitalsign.ExternalRestApartmentContractDigitalSignDto;
import ru.croc.ugd.ssr.generated.dto.contractdigitalsign.ExternalRestCipContractDigitalSignDto;
import ru.croc.ugd.ssr.model.CipDocument;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.api.chess.CcoSolrResponse;
import ru.croc.ugd.ssr.model.contractDigitalSign.ContractDigitalSignDocument;
import ru.croc.ugd.ssr.utils.CipUtils;
import ru.croc.ugd.ssr.utils.PersonUtils;
import ru.reinform.cdp.filestore.model.signature.SignatureInfo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ERROR)
public interface ContractDigitalSignMapper {

    @Mapping(target = "isPossible", source = "isPossible")
    @Mapping(target = "reason", source = "reason")
    @Mapping(target = "address", source = "address")
    @Mapping(target = "cip", source = "cipDocument")
    @Mapping(target = "contractDigitalSignDocumentId", source = "contractDigitalSignId")
    @Mapping(target = "contractFileId", source = "owner.contractFile.chedFileId")
    @Mapping(target = "actFileId", source = "owner.actFile.chedFileId")
    ExternalRestApartmentContractDigitalSignDto toExternalRestApartmentDto(
        final String contractDigitalSignId,
        final Owner owner,
        final CipDocument cipDocument,
        final String address,
        final boolean isPossible,
        final String reason
    );

    @Mapping(target = "id", source = "id")
    @Mapping(target = "phone", source = "document.cipData.phone")
    @Mapping(target = "address", source = "cipDocument", qualifiedByName = "toCipAddress")
    ExternalRestCipContractDigitalSignDto toExternalRestCipDto(final CipDocument cipDocument);

    @Named("toCipAddress")
    default String toCipAddress(final CipDocument cipDocument) {
        return CipUtils.getCipAddress(cipDocument.getDocument().getCipData());
    }

    @Mapping(
        target = "document.contractDigitalSignData.contractAppointmentId",
        source = "contractAppointmentId"
    )
    @Mapping(
        target = "document.contractDigitalSignData.appointmentDate",
        source = "contractAppointment.appointmentDateTime",
        qualifiedByName = "toLocalDate"
    )
    @Mapping(target = "document.contractDigitalSignData.owners.owner", source = "personDocuments")
    ContractDigitalSignDocument toContractDigitalSignDocument(
        final ContractAppointmentData contractAppointment,
        final String contractAppointmentId,
        final List<PersonDocument> personDocuments
    );

    @Mapping(target = "personDocumentId", source = "id")
    @Mapping(target = "snils", source = "document.personData.SNILS")
    @Mapping(target = "ssoId", source = "document.personData.ssoID")
    @Mapping(target = "fullName", source = "document.personData", qualifiedByName = "toFullName")
    @Mapping(target = "birthDate", source = "document.personData.birthDate")
    @Mapping(target = "contractFile", ignore = true)
    @Mapping(target = "actFile", ignore = true)
    @Mapping(target = "elkUserNotifications", ignore = true)
    Owner toOwner(final PersonDocument personDocument);

    @Named("toFullName")
    default String toFullName(final PersonType personType) {
        return PersonUtils.getFullName(personType);
    }

    @Named("toLocalDate")
    default LocalDate toLocalDate(final LocalDateTime dateTime) {
        return dateTime.toLocalDate();
    }

    @Mapping(target = "personDocumentId", source = "personDocumentId")
    @Mapping(target = "actSignature", source = "actFile")
    @Mapping(target = "contractSignature", source = "contractFile")
    RestOwnerSignatureDto toRestOwnerSignatureDto(final Owner owner);

    @Mapping(target = "number", source = "signData.number")
    @Mapping(target = "fullName", source = "signData.fullName")
    @Mapping(target = "dateTimeFrom", source = "signData.dateTimeFrom")
    @Mapping(target = "dateTimeTo", source = "signData.dateTimeTo")
    @Mapping(target = "signDateTime", source = "signData.signDateTime")
    @Mapping(target = "verified", source = "signData.isVerified")
    RestFileSignatureDto toRestFileSignatureDto(final ContractDigitalSignFile contractDigitalSignFile);

    List<RestOwnerSignatureDto> toRestOwnerSignatureDtos(final List<Owner> owners);

    default List<RestEmployeeContractDigitalSignDto> toRestEmployeeContractDigitalSignDtos(
        final List<ContractDigitalSignDocument> contractDigitalSignDocuments,
        final Function<ContractDigitalSignDocument, ContractAppointment> retrieveContractAppointment,
        final Function<ContractAppointment, CcoSolrResponse> retrieveCapitalConstructionObjectData,
        final Function<String, String> retrieveAddressFrom,
        final Function<String, FilenetFileBean> retrieveFileInfo
    ) {
        return contractDigitalSignDocuments.stream()
            .map(contractDigitalSignDocument -> {
                final ContractAppointment contractAppointment =
                    retrieveContractAppointment.apply(contractDigitalSignDocument);
                return toRestEmployeeContractDigitalSignDto(
                    contractDigitalSignDocument,
                    contractAppointment,
                    retrieveCapitalConstructionObjectData.apply(contractAppointment),
                    retrieveAddressFrom,
                    retrieveFileInfo
                );
            })
            .collect(Collectors.toList());
    }

    @Mapping(target = "contractDigitalSignId", source = "contractDigitalSignDocument.id")
    @Mapping(target = "area", source = "ccoSolrResponse.area")
    @Mapping(target = "district", source = "ccoSolrResponse.district")
    @Mapping(
        target = "addressFrom",
        source = "contractAppointment.contractAppointmentData.applicant.personDocumentId",
        qualifiedByName = "toAddressFrom"
    )
    @Mapping(
        target = "addressTo",
        source = "contractAppointment.contractAppointmentData.addressTo"
    )
    @Mapping(
        target = "owners",
        source = "contractDigitalSignDocument.document.contractDigitalSignData.owners.owner"
    )
    @Mapping(
        target = "contractFileData",
        source = "contractDigitalSignDocument.document.contractDigitalSignData.employee.contractFile",
        qualifiedByName = "toRestFileDataDto"
    )
    @Mapping(
        target = "actFileData",
        source = "contractDigitalSignDocument.document.contractDigitalSignData.employee.actFile",
        qualifiedByName = "toRestFileDataDto"
    )
    @Mapping(target = "contractAppointment", source = "contractAppointment")
    RestEmployeeContractDigitalSignDto toRestEmployeeContractDigitalSignDto(
        final ContractDigitalSignDocument contractDigitalSignDocument,
        final ContractAppointment contractAppointment,
        final CcoSolrResponse ccoSolrResponse,
        @Context final Function<String, String> retrieveAddressFrom,
        @Context final Function<String, FilenetFileBean> retrieveFileInfo
    );

    @Mapping(target = "personDocumentId", source = "personDocumentId")
    @Mapping(target = "fullName", source = "fullName")
    @Mapping(target = "birthDate", source = "birthDate")
    RestOwnerDto toRestOwnerDto(final Owner owner);

    @Named("toAddressFrom")
    default String toAddressFrom(
        final String personDocumentId, @Context final Function<String, String> retrieveAddressFrom
    ) {
        return retrieveAddressFrom.apply(personDocumentId);
    }

    @Named("toRestFileDataDto")
    default RestFileDataDto toRestFileDataDto(
        final ContractDigitalSignFile contractDigitalSignFile,
        @Context final Function<String, FilenetFileBean> retrieveFileInfo
    ) {
        return ofNullable(contractDigitalSignFile)
            .map(ContractDigitalSignFile::getFileStoreId)
            .map(retrieveFileInfo)
            .map(fileInfo -> toRestFileDataDto(contractDigitalSignFile, fileInfo))
            .orElse(null);
    }

    @Mapping(target = "fileStoreId", source = "contractDigitalSignFile.fileStoreId")
    @Mapping(target = "fileName", source = "fileInfo.fileName")
    @Mapping(target = "fileType", source = "fileInfo.fileType")
    @Mapping(target = "mimeType", source = "fileInfo.mimeType")
    @Mapping(target = "fileSize", source = "fileInfo.fileSize")
    @Mapping(target = "creationDateTime", source = "fileInfo.dateCreated")
    @Mapping(target = "lastModifiedDateTime", source = "fileInfo.dateLastModified")
    @Mapping(target = "isVerified", source = "contractDigitalSignFile.signData.isVerified")
    @Mapping(target = "signDateTime", source = "contractDigitalSignFile.signData.signDateTime")
    RestFileDataDto toRestFileDataDto(
        final ContractDigitalSignFile contractDigitalSignFile, final FilenetFileBean fileInfo
    );

    @Mapping(target = "id", source = "documentID")
    @Mapping(target = "eno", source = "contractAppointmentData.eno")
    RestContractAppointmentDto toRestContractAppointmentDto(final ContractAppointment contractAppointment);

    @Mapping(target = "signId", source = "signatureInfo.signId")
    @Mapping(target = "isVerified", source = "isSuccess")
    @Mapping(target = "number", source = "signatureInfo.serialNumber")
    @Mapping(target = "fullName", source = "signatureInfo.subject.fio")
    @Mapping(target = "dateTimeFrom", source = "signatureInfo.subject.validFrom", qualifiedByName = "toLocalDateTime")
    @Mapping(target = "dateTimeTo", source = "signatureInfo.subject.validTo", qualifiedByName = "toLocalDateTime")
    @Mapping(target = "signDateTime", source = "signatureInfo.signTime", qualifiedByName = "toLocalDateTime")
    SignData toSignData(final SignatureInfo signatureInfo, final boolean isSuccess);

    @Named("toLocalDateTime")
    default LocalDateTime toLocalDateTime(final Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    @Mapping(target = "employeeFullName", source = "employeeFullName")
    @Mapping(target = "actSignature", source = "employee.actFile")
    @Mapping(target = "contractSignature", source = "employee.contractFile")
    RestEmployeeSignatureDto toRestEmployeeSignatureDto(final Employee employee, final String employeeFullName);

    @Mapping(target = "eno", source = "elkUserNotificationDto.eno")
    @Mapping(target = "creationDateTime", source = "elkUserNotificationDto.creationDateTime")
    @Mapping(target = "eventCode", source = "contractDigitalSignFlowStatus.eventCode")
    ElkUserNotification toElkUserNotification(
        final ElkUserNotificationDto elkUserNotificationDto,
        final ContractDigitalSignFlowStatus contractDigitalSignFlowStatus
    );
}
