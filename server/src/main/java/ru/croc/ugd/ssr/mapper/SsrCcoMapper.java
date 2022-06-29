package ru.croc.ugd.ssr.mapper;

import static org.mapstruct.ReportingPolicy.ERROR;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.croc.ugd.ssr.OrganizationInformation;
import ru.croc.ugd.ssr.dto.ssrcco.SsrCcoDto;
import ru.croc.ugd.ssr.dto.ssrcco.SsrCcoEmployeeDto;
import ru.croc.ugd.ssr.dto.ssrcco.SsrCcoOrganizationDto;
import ru.croc.ugd.ssr.enums.SsrCcoOrganizationType;
import ru.croc.ugd.ssr.model.ssrcco.SsrCcoDocument;
import ru.croc.ugd.ssr.ssrcco.SsrCcoEmployee;
import ru.croc.ugd.ssr.ssrcco.SsrCcoOrganization;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ERROR)
public interface SsrCcoMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "psDocumentId", source = "document.ssrCcoData.psDocumentId")
    @Mapping(target = "unom", source = "document.ssrCcoData.unom")
    @Mapping(target = "address", source = "document.ssrCcoData.address")
    @Mapping(target = "area", source = "document.ssrCcoData.area")
    @Mapping(target = "district", source = "document.ssrCcoData.district")
    @Mapping(target = "organizations", source = "document.ssrCcoData.organizations")
    @Mapping(target = "employees", source = "document.ssrCcoData.employees")
    SsrCcoDto toSsrCcoDto(final SsrCcoDocument ssrCcoDocument);

    SsrCcoOrganizationDto toSsrCcoOrganizationDto(final SsrCcoOrganization ssrCcoOrganization);

    SsrCcoEmployeeDto toSsrCcoEmployeeDto(final SsrCcoEmployee ssrCcoEmployee);

    default SsrCcoOrganizationType toSsrCcoOrganizationType(final int type) {
        return SsrCcoOrganizationType.ofTypeCode(type);
    }

    @Mapping(target = "document.ssrCcoData.psDocumentId", source = "psDocumentId")
    @Mapping(target = "document.ssrCcoData.unom", source = "unom")
    @Mapping(target = "document.ssrCcoData.address", source = "address")
    @Mapping(target = "document.ssrCcoData.area", source = "area")
    @Mapping(target = "document.ssrCcoData.district", source = "district")
    @Mapping(target = "document.ssrCcoData.organizations", ignore = true)
    @Mapping(target = "document.ssrCcoData.employees", ignore = true)
    SsrCcoDocument toSsrCcoDocument(final SsrCcoDto ssrCcoDto);

    @Mapping(target = "id", expression = "java( java.util.UUID.randomUUID().toString() )")
    @Mapping(target = "type", source = "type.typeCode")
    SsrCcoEmployee toSsrCcoEmployee(final SsrCcoEmployeeDto employeeDto);

    @Mapping(target = "externalId", source = "organizationInformation.externalId")
    @Mapping(target = "fullName", source = "organizationInformation.orgFullName")
    @Mapping(target = "type", source = "organizationType")
    SsrCcoOrganization toSsrCcoOrganization(
        final OrganizationInformation organizationInformation,
        final int organizationType
    );
}
