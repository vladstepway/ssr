package ru.croc.ugd.ssr.mapper;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static org.mapstruct.ReportingPolicy.ERROR;

import org.apache.commons.lang3.tuple.Pair;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.db.projection.TenantProjection;
import ru.croc.ugd.ssr.dto.personaldocument.PdfFileDto;
import ru.croc.ugd.ssr.dto.personaldocument.RestDocumentDto;
import ru.croc.ugd.ssr.dto.personaldocument.RestFileDto;
import ru.croc.ugd.ssr.dto.personaldocument.RestParsedDocumentDto;
import ru.croc.ugd.ssr.dto.personaldocument.RestPersonDataDocumentsDto;
import ru.croc.ugd.ssr.dto.personaldocument.RestPersonalDocumentDto;
import ru.croc.ugd.ssr.dto.personaldocument.RestTenantDto;
import ru.croc.ugd.ssr.generated.dto.personaldocument.ExternalRestApartmentPersonalDocumentDto;
import ru.croc.ugd.ssr.generated.dto.personaldocument.ExternalRestDocumentPersonalDocumentDto;
import ru.croc.ugd.ssr.generated.dto.personaldocument.ExternalRestTenantPersonalDocumentDto;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.personaldocument.PersonalDocumentDocument;
import ru.croc.ugd.ssr.personaldocument.DocumentType;
import ru.croc.ugd.ssr.personaldocument.PersonalDocumentApplicationType;
import ru.croc.ugd.ssr.personaldocument.PersonalDocumentFile;
import ru.croc.ugd.ssr.personaldocument.PersonalDocumentRequest;
import ru.croc.ugd.ssr.personaldocument.PersonalDocumentRequestType;
import ru.croc.ugd.ssr.personaldocument.PersonalDocumentType;
import ru.croc.ugd.ssr.personaldocument.RequestedDocumentOwnerType;
import ru.croc.ugd.ssr.personaldocument.RequestedDocumentType;
import ru.croc.ugd.ssr.personaldocument.TenantType;
import ru.croc.ugd.ssr.service.personaldocument.type.SsrPersonalDocumentType;
import ru.croc.ugd.ssr.utils.PersonUtils;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ERROR)
public interface PersonalDocumentMapper {

    @Mapping(target = "address", expression = "java( retrieveAddress.apply(personType) )")
    @Mapping(target = "flatNumber", expression = "java( retrieveFlatNumber.apply(personType) )")
    @Mapping(target = "roomNumber", source = "personType.roomNum", qualifiedByName = "toRoomNumber")
    @Mapping(
        target = "tenants",
        expression = "java( toTenants("
            + "personType, personalDocumentRequest, existsApplication, retrieveLivers, retrieveSsrPersonalDocumentType"
            + ") )"
    )
    @Mapping(target = "requestDocumentId", source = "personalDocumentRequest.documentID")
    @Mapping(
        target = "documentUploadRequired",
        expression = "java( toDocumentUploadRequired(personalDocumentRequest, existsApplication) )"
    )
    @Mapping(
        target = "documents",
        expression = "java( toDocuments(personalDocumentRequest, retrieveSsrPersonalDocumentType) )"
    )
    @Mapping(target = "status", source = "personType.statusLiving", qualifiedByName = "toApartmentStatus")
    ExternalRestApartmentPersonalDocumentDto toExternalRestApartmentDto(
        final PersonType personType,
        final PersonalDocumentRequest personalDocumentRequest,
        final boolean existsApplication,
        final Function<PersonType, String> retrieveAddress,
        final Function<PersonType, String> retrieveFlatNumber,
        final Function<String, List<PersonDocument>> retrieveLivers,
        final Function<String, SsrPersonalDocumentType> retrieveSsrPersonalDocumentType
    );

    @Mapping(target = "personDocumentId", source = "personDocument.id")
    @Mapping(target = "ssoId", source = "personDocument.document.personData.ssoID")
    @Mapping(target = "lastName", source = "personDocument.document.personData.lastName")
    @Mapping(target = "firstName", source = "personDocument.document.personData.firstName")
    @Mapping(target = "middleName", source = "personDocument.document.personData.middleName")
    @Mapping(target = "dob", source = "personDocument.document.personData.birthDate")
    @Mapping(
        target = "status", source = "personDocument.document.personData.statusLiving", qualifiedByName = "toStatus"
    )
    @Mapping(
        target = "documentUploadRequired",
        expression = "java( toDocumentUploadRequired("
            + "personDocument.getId(), personalDocumentRequest, existsApplication"
            + ") )"
    )
    @Mapping(
        target = "documents",
        expression = "java( toTenantDocuments("
            + "personDocument.getId(), personalDocumentRequest, retrieveSsrPersonalDocumentType"
            + ") )"
    )
    ExternalRestTenantPersonalDocumentDto toExternalRestTenantDto(
        final PersonDocument personDocument,
        @Context final PersonalDocumentRequest personalDocumentRequest,
        @Context final boolean existsApplication,
        @Context final Function<String, SsrPersonalDocumentType> retrieveSsrPersonalDocumentType
    );

    List<ExternalRestTenantPersonalDocumentDto> toExternalRestTenantDtos(
        final List<PersonDocument> personDocuments,
        @Context final PersonalDocumentRequest personalDocumentRequest,
        @Context final boolean existsApplication,
        @Context final Function<String, SsrPersonalDocumentType> retrieveSsrPersonalDocumentType
    );

    @Mapping(target = "typeCode", source = "ssrPersonalDocumentType.kindCode")
    @Mapping(target = "typeName", source = "ssrPersonalDocumentType.name")
    @Mapping(target = "comment", source = "requestedDocumentType.comment")
    ExternalRestDocumentPersonalDocumentDto toExternalRestDocumentPersonalDocumentDto(
        final RequestedDocumentType requestedDocumentType,
        final SsrPersonalDocumentType ssrPersonalDocumentType
    );

    default List<ExternalRestDocumentPersonalDocumentDto> toExternalRestDocumentPersonalDocumentDtos(
        final List<RequestedDocumentType> requestedDocumentTypes,
        @Context final Function<String, SsrPersonalDocumentType> retrieveSsrPersonalDocumentType
    ) {
        if (isNull(requestedDocumentTypes)) {
            return null;
        }

        return requestedDocumentTypes.stream()
            .map(type -> toExternalRestDocumentPersonalDocumentDto(
                type, retrieveSsrPersonalDocumentType.apply(type.getTypeCode()))
            )
            .collect(Collectors.toList());
    }

    @Named("toApartmentStatus")
    default String toApartmentStatus(final String statusLiving) {
        if (nonNull(statusLiving)) {
            switch (statusLiving) {
                case "1":
                case "2":
                    return "Собственность";
                case "3":
                case "4":
                    return "Найм";
                default:
                    break;
            }
        }
        return null;
    }

    @Named("toStatus")
    default String toStatus(final String statusLiving) {
        if (nonNull(statusLiving)) {
            switch (statusLiving) {
                case "1":
                    return "Собственник";
                case "3":
                    return "Наниматель";
                case "2":
                case "4":
                    return "Зарегистрированный";
                default:
                    break;
            }
        }
        return null;
    }

    @Named("toRoomNumber")
    default String toRoomNumber(List<String> roomNum) {
        if (nonNull(roomNum) && !roomNum.isEmpty()) {
            return String.join(", ", roomNum);
        } else {
            return null;
        }
    }

    @Named("toTenants")
    default List<ExternalRestTenantPersonalDocumentDto> toTenants(
        final PersonType personType,
        final PersonalDocumentRequest personalDocumentRequest,
        final boolean existsApplication,
        final Function<String, List<PersonDocument>> retrieveLivers,
        final Function<String, SsrPersonalDocumentType> retrieveSsrPersonalDocumentType
    ) {
        if (nonNull(personType) && nonNull(personType.getAffairId())) {
            final List<PersonDocument> personTypes = retrieveLivers.apply(personType.getAffairId());
            return toExternalRestTenantDtos(
                personTypes,
                personalDocumentRequest,
                existsApplication,
                retrieveSsrPersonalDocumentType
            );
        } else {
            return null;
        }
    }

    @Named("toDocuments")
    default List<ExternalRestDocumentPersonalDocumentDto> toDocuments(
        final PersonalDocumentRequest personalDocumentRequest,
        final Function<String, SsrPersonalDocumentType> retrieveSsrPersonalDocumentType
    ) {
        final List<RequestedDocumentType> requestedDocumentTypes = ofNullable(personalDocumentRequest)
            .map(PersonalDocumentRequest::getPersonalDocumentRequestData)
            .map(PersonalDocumentRequestType::getDocuments)
            .map(PersonalDocumentRequestType.Documents::getDocument)
            .orElse(null);

        if (nonNull(requestedDocumentTypes) && !requestedDocumentTypes.isEmpty()) {
            return toExternalRestDocumentPersonalDocumentDtos(requestedDocumentTypes, retrieveSsrPersonalDocumentType);
        } else {
            return null;
        }
    }

    default List<ExternalRestDocumentPersonalDocumentDto> toTenantDocuments(
        final String personDocumentId,
        final PersonalDocumentRequest personalDocumentRequest,
        final Function<String, SsrPersonalDocumentType> retrieveSsrPersonalDocumentType
    ) {
        return ofNullable(personalDocumentRequest)
            .map(PersonalDocumentRequest::getPersonalDocumentRequestData)
            .map(PersonalDocumentRequestType::getOwners)
            .map(PersonalDocumentRequestType.Owners::getOwner)
            .orElse(Collections.emptyList())
            .stream()
            .filter(owner -> owner.getPersonDocumentId().equals(personDocumentId))
            .findFirst()
            .map(RequestedDocumentOwnerType::getDocuments)
            .map(RequestedDocumentOwnerType.Documents::getDocument)
            .map(a -> toExternalRestDocumentPersonalDocumentDtos(a, retrieveSsrPersonalDocumentType))
            .orElse(null);
    }

    default boolean toDocumentUploadRequired(
        final PersonalDocumentRequest personalDocumentRequest, final boolean existsApplication
    ) {
        return !existsApplication || !ofNullable(personalDocumentRequest)
            .map(PersonalDocumentRequest::getPersonalDocumentRequestData)
            .map(PersonalDocumentRequestType::getDocuments)
            .map(PersonalDocumentRequestType.Documents::getDocument)
            .orElse(Collections.emptyList())
            .isEmpty();
    }

    default boolean toDocumentUploadRequired(
        final String personDocumentId,
        final PersonalDocumentRequest personalDocumentRequest,
        final boolean existsApplication
    ) {
        return !existsApplication || !ofNullable(personalDocumentRequest)
            .map(PersonalDocumentRequest::getPersonalDocumentRequestData)
            .map(PersonalDocumentRequestType::getOwners)
            .map(PersonalDocumentRequestType.Owners::getOwner)
            .orElse(Collections.emptyList())
            .stream()
            .filter(owner -> owner.getPersonDocumentId().equals(personDocumentId))
            .findFirst()
            .map(RequestedDocumentOwnerType::getDocuments)
            .map(RequestedDocumentOwnerType.Documents::getDocument)
            .orElse(Collections.emptyList())
            .isEmpty();
    }

    default RestPersonDataDocumentsDto toRestPersonalDocumentsDto(
        final List<PersonalDocumentType> personalDocuments,
        final Function<String, PersonalDocumentApplicationType> retrievePersonalDocumentApplication
    ) {
        final Map<String, Pair<String, LocalDateTime>> applicationData = personalDocuments
            .stream()
            .map(PersonalDocumentType::getApplicationDocumentId)
            .filter(Objects::nonNull)
            .collect(Collectors.toMap(
                Function.identity(),
                applicationDocumentId -> {
                    final PersonalDocumentApplicationType personalDocumentApplication =
                        retrievePersonalDocumentApplication.apply(applicationDocumentId);
                    final String eno = ofNullable(personalDocumentApplication)
                        .map(PersonalDocumentApplicationType::getEno)
                        .orElse(null);
                    final LocalDateTime applicationDateTime = ofNullable(personalDocumentApplication)
                        .map(PersonalDocumentApplicationType::getApplicationDateTime)
                        .orElse(null);
                    return Pair.of(eno, applicationDateTime);
                },
                (a1, a2) -> a1
            ));

        final List<RestDocumentDto> restDocumentDtos = toRestDocumentDtos(
            personalDocuments, applicationData
        );

        final List<RestTenantDto> restTenantDtos = toRestTenantDtos(personalDocuments, applicationData);

        final List<RestTenantDto> restUnregisteredTenantDtos = restTenantDtos.stream()
            .filter(restTenantDto -> isNull(restTenantDto.getPersonDocumentId()))
            .collect(Collectors.toList());

        final Map<String, RestTenantDto> restRegisteredTenantDtos = restTenantDtos.stream()
            .filter(restTenantDto -> nonNull(restTenantDto.getPersonDocumentId()))
            .collect(Collectors.toMap(
                RestTenantDto::getPersonDocumentId,
                Function.identity(),
                this::mergeRegisteredRestTenantDto
            ));

        final List<RestTenantDto> restTenantDtosWithConcatenatedDocuments = Stream.concat(
                restRegisteredTenantDtos.values().stream(),
                restUnregisteredTenantDtos.stream()
            )
            .collect(Collectors.toList());

        sortDocuments(restDocumentDtos);
        restTenantDtosWithConcatenatedDocuments.stream()
            .map(RestTenantDto::getDocuments)
            .forEach(this::sortDocuments);

        return RestPersonDataDocumentsDto.builder()
            .documents(restDocumentDtos)
            .tenants(restTenantDtosWithConcatenatedDocuments)
            .build();
    }

    default void sortDocuments(final List<RestDocumentDto> documents) {
        documents.sort(
            Comparator.comparing(
                    RestDocumentDto::getUploadDateTime, Comparator.nullsFirst(LocalDateTime::compareTo)
                )
                .thenComparing(RestDocumentDto::getTypeCode, Comparator.nullsFirst(String::compareTo))
        );
    }

    default List<RestDocumentDto> toRestDocumentDtos(
        final List<PersonalDocumentType> personalDocuments,
        final Map<String, Pair<String, LocalDateTime>> applicationData
    ) {
        return personalDocuments.stream()
            .map(personalDocument -> toRestDocumentDto(personalDocument, applicationData))
            .filter(Objects::nonNull)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    }

    default List<RestDocumentDto> toRestDocumentDtos(
        final List<DocumentType> documents,
        final Pair<String, String> applicationData,
        final LocalDateTime uploadDateTime
    ) {
        return documents.stream()
            .map(document -> toRestDocumentDto(document, applicationData, uploadDateTime))
            .collect(Collectors.toList());
    }

    default List<RestDocumentDto> toRestDocumentDtos(
        final TenantType tenant, final Pair<String, String> applicationData, final LocalDateTime uploadDateTime
    ) {
        final List<DocumentType> documents = ofNullable(tenant)
            .map(TenantType::getDocuments)
            .map(TenantType.Documents::getDocument)
            .orElse(Collections.emptyList());
        return toRestDocumentDtos(documents, applicationData, uploadDateTime);
    }

    default List<RestTenantDto> toRestTenantDtos(
        final List<PersonalDocumentType> personalDocuments,
        final Map<String, Pair<String, LocalDateTime>> applicationData
    ) {
        return personalDocuments.stream()
            .map(personalDocument -> toRestTenantDto(personalDocument, applicationData))
            .filter(Objects::nonNull)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    }

    default List<RestTenantDto> toRestTenantDtos(
        final List<TenantType> tenants, final Pair<String, String> applicationData, final LocalDateTime uploadDateTime
    ) {
        return tenants.stream()
            .map(tenant -> toRestTenantDto(tenant, applicationData, uploadDateTime))
            .collect(Collectors.toList());
    }

    default List<RestDocumentDto> toRestDocumentDto(
        final PersonalDocumentType personalDocument, final Map<String, Pair<String, LocalDateTime>> applicationData
    ) {
        final List<DocumentType> documents = ofNullable(personalDocument)
            .map(PersonalDocumentType::getDocuments)
            .map(PersonalDocumentType.Documents::getDocument)
            .orElse(Collections.emptyList());

        final String applicationDocumentId = ofNullable(personalDocument)
            .map(PersonalDocumentType::getApplicationDocumentId)
            .orElse(null);

        final String eno = retrieveEno(applicationDocumentId, applicationData);

        final LocalDateTime uploadDateTime = retrieveUploadDateTime(
            personalDocument, applicationDocumentId, applicationData
        );

        return toRestDocumentDtos(documents, Pair.of(applicationDocumentId, eno), uploadDateTime);
    }

    @Mapping(target = "typeCode", source = "document.typeCode")
    @Mapping(target = "comment", source = "document.comment")
    @Mapping(target = "files", source = "document.files.file")
    @Mapping(target = "applicationDocumentId", source = "applicationData.left")
    @Mapping(target = "eno", source = "applicationData.right")
    @Mapping(target = "uploadDateTime", source = "uploadDateTime")
    RestDocumentDto toRestDocumentDto(
        final DocumentType document, final Pair<String, String> applicationData, final LocalDateTime uploadDateTime
    );

    default List<RestTenantDto> toRestTenantDto(
        final PersonalDocumentType personalDocument, final Map<String, Pair<String, LocalDateTime>> applicationData
    ) {
        final List<TenantType> tenants = ofNullable(personalDocument)
            .map(PersonalDocumentType::getTenants)
            .map(PersonalDocumentType.Tenants::getTenant)
            .orElse(Collections.emptyList());

        final String applicationDocumentId = ofNullable(personalDocument)
            .map(PersonalDocumentType::getApplicationDocumentId)
            .orElse(null);

        final String eno = retrieveEno(applicationDocumentId, applicationData);

        final LocalDateTime uploadDateTime = retrieveUploadDateTime(
            personalDocument, applicationDocumentId, applicationData
        );

        return toRestTenantDtos(tenants, Pair.of(applicationDocumentId, eno), uploadDateTime);
    }

    @Mapping(target = "personDocumentId", source = "tenant.personDocumentId")
    @Mapping(target = "fullName", source = "tenant", qualifiedByName = "toFullName")
    @Mapping(target = "birthDate", source = "tenant.birthDate")
    @Mapping(target = "status", source = "tenant.status")
    @Mapping(target = "suspicious", source = "tenant.isSuspicious")
    @Mapping(target = "suspicionReason", source = "tenant.suspicionReason")
    @Mapping(target = "documents", expression = "java( toRestDocumentDtos(tenant, applicationData, uploadDateTime) )")
    RestTenantDto toRestTenantDto(
        final TenantType tenant, final Pair<String, String> applicationData, final LocalDateTime uploadDateTime
    );

    @Mapping(target = "fileStoreId", source = "fileStoreId")
    @Mapping(target = "fileType", source = "fileType")
    RestFileDto toFile(final PersonalDocumentFile personalDocumentFiles);

    @Named("toFullName")
    default String toFullName(final TenantType tenantType) {
        return PersonUtils.getFullName(
            tenantType.getLastName(),
            tenantType.getFirstName(),
            tenantType.getMiddleName()
        );
    }

    @Mapping(target = "personDocumentId", source = "firstTenantDto.personDocumentId")
    @Mapping(target = "fullName", source = "firstTenantDto.fullName")
    @Mapping(target = "birthDate", source = "firstTenantDto.birthDate")
    @Mapping(target = "status", source = "firstTenantDto.status")
    @Mapping(target = "suspicious", source = "firstTenantDto.suspicious")
    @Mapping(target = "suspicionReason", source = "firstTenantDto.suspicionReason")
    @Mapping(target = "documents", expression = "java( toRegisteredTenantDocuments(firstTenantDto, secondTenantDto) )")
    RestTenantDto mergeRegisteredRestTenantDto(final RestTenantDto firstTenantDto, final RestTenantDto secondTenantDto);

    default List<RestDocumentDto> toRegisteredTenantDocuments(
        final RestTenantDto firstTenantDto, final RestTenantDto secondTenantDto
    ) {
        final List<RestDocumentDto> firstTenantDocuments = toRegisteredTenantDocuments(firstTenantDto);
        final List<RestDocumentDto> secondTenantDocuments = toRegisteredTenantDocuments(secondTenantDto);
        return Stream.concat(firstTenantDocuments.stream(), secondTenantDocuments.stream())
            .collect(Collectors.toList());
    }

    default List<RestDocumentDto> toRegisteredTenantDocuments(final RestTenantDto firstTenantDto) {
        return ofNullable(firstTenantDto)
            .map(RestTenantDto::getDocuments)
            .orElse(Collections.emptyList());
    }

    default String retrieveEno(
        final String applicationDocumentId, final Map<String, Pair<String, LocalDateTime>> applicationData
    ) {
        return ofNullable(applicationDocumentId)
            .map(applicationData::get)
            .map(Pair::getLeft)
            .orElse(null);
    }

    default LocalDateTime retrieveUploadDateTime(
        final PersonalDocumentType personalDocument,
        final String applicationDocumentId,
        final Map<String, Pair<String, LocalDateTime>> applicationData
    ) {
        return ofNullable(applicationDocumentId)
            .map(applicationData::get)
            .map(Pair::getRight)
            .orElseGet(() -> ofNullable(personalDocument)
                .map(PersonalDocumentType::getAcceptanceDateTime)
                .orElse(null)
            );
    }

    @Mapping(target = "document.personalDocumentData.affairId", source = "affairId")
    @Mapping(target = "document.personalDocumentData.unionFileStoreId", source = "unionFileStoreId")
    @Mapping(target = "document.personalDocumentData.addressFrom", source = "addressFrom")
    @Mapping(target = "document.personalDocumentData.letterId", source = "letterId")
    PersonalDocumentDocument toPersonalDocumentDocument(
        final String affairId, final String unionFileStoreId, final String addressFrom, final String letterId
    );

    @Mapping(target = "id", source = "id")
    @Mapping(target = "affairId", source = "document.personalDocumentData.affairId")
    @Mapping(target = "unionFileStoreId", source = "document.personalDocumentData.unionFileStoreId")
    @Mapping(target = "addressFrom", source = "document.personalDocumentData.addressFrom")
    @Mapping(target = "letterId", source = "document.personalDocumentData.letterId")
    @Mapping(
        target = "tenants",
        source = "document.personalDocumentData.tenants.tenant",
        qualifiedByName = "toRestTenantDtos"
    )
    @Mapping(
        target = "documents",
        source = "document.personalDocumentData.documents.document",
        qualifiedByName = "toRestDocumentDtos"
    )
    @Mapping(target = "processInstanceId", source = "document.personalDocumentData.processInstanceId")
    RestPersonalDocumentDto toRestPersonalDocumentDto(final PersonalDocumentDocument personalDocumentDocument);

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

    @Mapping(target = "typeCode", source = "typeCode")
    @Mapping(target = "files.file", source = "applicationFileStoreId", qualifiedByName = "toApplicationFiles")
    @Mapping(target = "comment", ignore = true)
    DocumentType toApplicationDocumentType(final String applicationFileStoreId, final String typeCode);

    @Named("toApplicationFiles")
    default List<PersonalDocumentFile> toApplicationFiles(final String applicationFileStoreId) {
        final PersonalDocumentFile personalDocumentFile = toPersonalDocumentFile(applicationFileStoreId);
        return Collections.singletonList(personalDocumentFile);
    }

    @Mapping(target = "typeCode", source = "restParsedDocumentDto.typeCode")
    @Mapping(target = "comment", source = "restParsedDocumentDto.comment")
    @Mapping(
        target = "files",
        expression = "java( toDocumentTypeFiles("
            + "restParsedDocumentDto, unionFileStoreId, folderId, personId, retrieveFileStoreId, retrieveFileName"
            + ") )"
    )
    DocumentType toDocumentType(
        final RestParsedDocumentDto restParsedDocumentDto,
        final String unionFileStoreId,
        final String folderId,
        final String personId,
        final Function<PdfFileDto, String> retrieveFileStoreId,
        final BiFunction<String, String, String> retrieveFileName
    );

    @Mapping(target = "personDocumentId", source = "tenantProjection.id")
    @Mapping(target = "lastName", source = "tenantProjection.lastName")
    @Mapping(target = "firstName", source = "tenantProjection.firstName")
    @Mapping(target = "middleName", source = "tenantProjection.middleName")
    @Mapping(target = "birthDate", source = "tenantProjection.birthDate")
    @Mapping(target = "status", source = "tenantProjection.statusLiving", qualifiedByName = "toStatus")
    @Mapping(
        target = "documents",
        expression = "java( toTenantTypeDocuments("
            + "restParsedDocumentDtos,"
            + "unionFileStoreId,"
            + "folderId,"
            + "tenantProjection.getPersonId(),"
            + "retrieveFileStoreId,"
            + "retrieveFileName"
            + ") )"
    )
    @Mapping(target = "isSuspicious", ignore = true)
    @Mapping(target = "suspicionReason", ignore = true)
    TenantType toTenantType(
        final TenantProjection tenantProjection,
        final List<RestParsedDocumentDto> restParsedDocumentDtos,
        final String unionFileStoreId,
        final String folderId,
        final Function<PdfFileDto, String> retrieveFileStoreId,
        final BiFunction<String, String, String> retrieveFileName
    );

    default TenantType.Documents toTenantTypeDocuments(
        final List<RestParsedDocumentDto> restParsedDocumentDtos,
        final String unionFileStoreId,
        final String folderId,
        final String personId,
        final Function<PdfFileDto, String> retrieveFileStoreId,
        final BiFunction<String, String, String> retrieveFileName
    ) {
        final TenantType.Documents documents = new TenantType.Documents();
        final List<DocumentType> documentTypes = toDocumentTypes(
            restParsedDocumentDtos, unionFileStoreId, folderId, personId, retrieveFileStoreId, retrieveFileName
        );
        if (nonNull(documentTypes)) {
            documents.getDocument().addAll(documentTypes);
        }
        return documents;
    }

    default List<DocumentType> toDocumentTypes(
        final List<RestParsedDocumentDto> restParsedDocumentDtos,
        final String unionFileStoreId,
        final String folderId,
        final String personId,
        final Function<PdfFileDto, String> retrieveFileStoreId,
        final BiFunction<String, String, String> retrieveFileName
    ) {
        return restParsedDocumentDtos.stream()
            .map(restParsedDocumentDto -> toDocumentType(
                restParsedDocumentDto, unionFileStoreId, folderId, personId, retrieveFileStoreId, retrieveFileName
            ))
            .collect(Collectors.toList());
    }

    @Mapping(
        target = "file",
        expression = "java( toPersonalDocumentFiles("
            + "restParsedDocumentDto, unionFileStoreId, folderId, personId, retrieveFileStoreId, retrieveFileName"
            + ") )"
    )
    DocumentType.Files toDocumentTypeFiles(
        final RestParsedDocumentDto restParsedDocumentDto,
        final String unionFileStoreId,
        final String folderId,
        final String personId,
        final Function<PdfFileDto, String> retrieveFileStoreId,
        final BiFunction<String, String, String> retrieveFileName
    );

    default List<PersonalDocumentFile> toPersonalDocumentFiles(
        final RestParsedDocumentDto restParsedDocumentDto,
        final String unionFileStoreId,
        final String folderId,
        final String personId,
        final Function<PdfFileDto, String> retrieveFileStoreId,
        final BiFunction<String, String, String> retrieveFileName
    ) {
        final List<Integer> pageNumber = ofNullable(restParsedDocumentDto)
            .map(RestParsedDocumentDto::getPageNumbers)
            .orElse(Collections.emptyList());

        final String typeCode = ofNullable(restParsedDocumentDto)
            .map(RestParsedDocumentDto::getTypeCode)
            .orElse(null);

        final String fileName = retrieveFileName.apply(typeCode, personId);

        final String fileStoreId = retrieveFileStoreId.apply(
            PdfFileDto.builder()
                .unionFileStoreId(unionFileStoreId)
                .folderId(folderId)
                .pageNumbers(pageNumber)
                .fileName(fileName)
                .build()
        );
        return Collections.singletonList(toPersonalDocumentFile(fileStoreId));
    }

    @Mapping(target = "fileStoreId", source = "fileStoreId")
    @Mapping(target = "fileType", constant = "pdf")
    @Mapping(target = "chedFileId", ignore = true)
    PersonalDocumentFile toPersonalDocumentFile(final String fileStoreId);
}
