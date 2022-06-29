package ru.croc.ugd.ssr.mapper;

import static org.mapstruct.ReportingPolicy.ERROR;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.croc.ugd.ssr.dto.personaldocument.RestDocumentDto;
import ru.croc.ugd.ssr.dto.personaldocument.RestFileDto;
import ru.croc.ugd.ssr.dto.personaldocument.RestPersonalDocumentApplicationDto;
import ru.croc.ugd.ssr.dto.personaldocument.RestTenantDto;
import ru.croc.ugd.ssr.model.personaldocument.PersonalDocumentApplicationDocument;
import ru.croc.ugd.ssr.model.personaldocument.PersonalDocumentDocument;
import ru.croc.ugd.ssr.personaldocument.DocumentType;
import ru.croc.ugd.ssr.personaldocument.PersonalDocumentApplicationType;
import ru.croc.ugd.ssr.personaldocument.PersonalDocumentFile;
import ru.croc.ugd.ssr.personaldocument.TenantType;
import ru.croc.ugd.ssr.utils.PersonUtils;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ERROR)
public interface PersonalDocumentApplicationMapper {

    @Mapping(target = "id", source = "personalDocumentApplicationDocument.id")
    @Mapping(
        target = "eno", source = "personalDocumentApplicationDocument.document.personalDocumentApplicationData.eno"
    )
    @Mapping(
        target = "applicationDateTime",
        source = "personalDocumentApplicationDocument.document.personalDocumentApplicationData.applicationDateTime"
    )
    @Mapping(
        target = "personDocumentId",
        source = "personalDocumentApplicationDocument.document.personalDocumentApplicationData.personDocumentId"
    )
    @Mapping(
        target = "fullName",
        source = "personalDocumentApplicationDocument.document.personalDocumentApplicationData",
        qualifiedByName = "toFullName"
    )
    @Mapping(
        target = "addressFrom",
        source = "personalDocumentApplicationDocument.document.personalDocumentApplicationData.addressFrom"
    )
    @Mapping(
        target = "unionFileStoreId", source = "personalDocumentDocument.document.personalDocumentData.unionFileStoreId"
    )
    @Mapping(
        target = "tenants",
        source = "personalDocumentDocument.document.personalDocumentData.tenants.tenant",
        qualifiedByName = "toTenantDtos"
    )
    @Mapping(
        target = "documents",
        source = "personalDocumentDocument.document.personalDocumentData.documents.document",
        qualifiedByName = "toDocumentDtos"
    )
    RestPersonalDocumentApplicationDto toRestPersonalDocumentApplicationDto(
        final PersonalDocumentApplicationDocument personalDocumentApplicationDocument,
        final PersonalDocumentDocument personalDocumentDocument
    );

    default List<RestPersonalDocumentApplicationDto> toRestPersonalDocumentApplicationDtos(
        final List<PersonalDocumentApplicationDocument> personalDocumentApplicationDocuments,
        final Function<PersonalDocumentApplicationDocument, PersonalDocumentDocument> retrievePersonalDocument
    ) {
        return personalDocumentApplicationDocuments.stream()
            .map(personalDocumentApplicationDocument -> {
                final PersonalDocumentDocument personalDocumentDocument = retrievePersonalDocument.apply(
                    personalDocumentApplicationDocument
                );
                return toRestPersonalDocumentApplicationDto(
                    personalDocumentApplicationDocument, personalDocumentDocument
                );
            })
            .collect(Collectors.toList());
    }

    @Named("toTenantDtos")
    List<RestTenantDto> toTenantDtos(final List<TenantType> tenants);

    @Mapping(target = "personDocumentId", source = "personDocumentId")
    @Mapping(target = "fullName", source = "tenant", qualifiedByName = "toFullName")
    @Mapping(target = "birthDate", source = "birthDate")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "suspicious", source = "isSuspicious")
    @Mapping(target = "suspicionReason", source = "suspicionReason")
    @Mapping(target = "documents", source = "documents.document", qualifiedByName = "toDocumentDtos")
    RestTenantDto toTenantDto(final TenantType tenant);

    @Named("toDocumentDtos")
    List<RestDocumentDto> toDocumentDtos(final List<DocumentType> documents);

    @Mapping(target = "typeCode", source = "typeCode")
    @Mapping(target = "comment", source = "comment")
    @Mapping(target = "files", source = "document.files.file")
    @Mapping(target = "applicationDocumentId", ignore = true)
    @Mapping(target = "eno", ignore = true)
    @Mapping(target = "uploadDateTime", ignore = true)
    RestDocumentDto toDocumentDto(final DocumentType document);

    @Mapping(target = "fileStoreId", source = "fileStoreId")
    @Mapping(target = "fileType", source = "fileType")
    RestFileDto toFile(final PersonalDocumentFile personalDocumentFiles);

    @Named("toFullName")
    default String toFullName(final PersonalDocumentApplicationType personalDocumentApplication) {
        return PersonUtils.getFullName(
            personalDocumentApplication.getLastName(),
            personalDocumentApplication.getFirstName(),
            personalDocumentApplication.getMiddleName()
        );
    }

    @Named("toFullName")
    default String toFullName(final TenantType tenantType) {
        return PersonUtils.getFullName(tenantType.getLastName(), tenantType.getFirstName(), tenantType.getMiddleName());
    }
}
