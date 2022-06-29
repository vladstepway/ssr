package ru.croc.ugd.ssr.mapper;

import static java.util.Objects.isNull;
import static org.mapstruct.ReportingPolicy.ERROR;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.croc.ugd.ssr.Person;
import ru.croc.ugd.ssr.dto.personaldocument.RestCreatePersonalDocumentRequestDto;
import ru.croc.ugd.ssr.dto.personaldocument.RestPersonalDocumentApplicationDto;
import ru.croc.ugd.ssr.dto.personaldocument.RestPersonalDocumentRequestDto;
import ru.croc.ugd.ssr.dto.personaldocument.RestRequestedDocumentDto;
import ru.croc.ugd.ssr.dto.personaldocument.RestRequestedDocumentOwnerDto;
import ru.croc.ugd.ssr.model.personaldocument.PersonalDocumentApplicationDocument;
import ru.croc.ugd.ssr.model.personaldocument.PersonalDocumentRequestDocument;
import ru.croc.ugd.ssr.personaldocument.RequestedDocumentOwnerType;
import ru.croc.ugd.ssr.personaldocument.RequestedDocumentType;
import ru.croc.ugd.ssr.utils.PersonUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ERROR)
public interface PersonalDocumentRequestMapper {

    @Mapping(target = "document.personalDocumentRequestData.personDocumentId", source = "dto.personDocumentId")
    @Mapping(
        target = "document.personalDocumentRequestData.requestDateTime",
        expression = "java(java.time.LocalDateTime.now())"
    )
    @Mapping(target = "document.personalDocumentRequestData.addressFrom", source = "dto.addressFrom")
    @Mapping(target = "document.personalDocumentRequestData.documents.document", source = "dto.titleDocuments")
    @Mapping(
        target = "document.personalDocumentRequestData.owners.owner",
        source = "dto.tenantDocuments",
        qualifiedByName = "toOwners"
    )
    @Mapping(target = "document.personalDocumentRequestData.initiatorLogin", source = "initiatorLogin")
    PersonalDocumentRequestDocument toPersonalDocumentRequestDocument(
        final RestCreatePersonalDocumentRequestDto dto,
        @Context final Function<String, Person> retrievePerson,
        final String initiatorLogin
    );

    @Mapping(target = "personDocumentId", source = "person.documentID")
    @Mapping(target = "lastName", source = "person.personData.lastName")
    @Mapping(target = "firstName", source = "person.personData.firstName")
    @Mapping(target = "middleName", source = "person.personData.middleName")
    @Mapping(target = "birthDate", source = "person.personData.birthDate")
    @Mapping(target = "documents.document", source = "documents")
    RequestedDocumentOwnerType toRequestedDocumentOwner(
        final Person person, final List<RestRequestedDocumentDto> documents
    );

    @Named("toOwners")
    default List<RequestedDocumentOwnerType> toOwners(
        final List<RestRequestedDocumentDto> restRequestedDocumentDtos,
        @Context final Function<String, Person> retrievePerson
    ) {
        if (isNull(restRequestedDocumentDtos)) {
            return null;
        }

        final Map<String, List<RestRequestedDocumentDto>> requestedDocuments = restRequestedDocumentDtos.stream()
            .collect(Collectors.toMap(
                RestRequestedDocumentDto::getPersonDocumentId,
                Arrays::asList,
                (l1, l2) -> Stream.concat(l1.stream(), l2.stream()).collect(Collectors.toList())
            ));

        return requestedDocuments.entrySet()
            .stream()
            .map(entry -> toRequestedDocumentOwner(retrievePerson.apply(entry.getKey()), entry.getValue()))
            .collect(Collectors.toList());
    }

    @Mapping(target = "id", source = "document.documentID")
    @Mapping(target = "personDocumentId", source = "document.personalDocumentRequestData.personDocumentId")
    @Mapping(target = "requestDateTime", source = "document.personalDocumentRequestData.requestDateTime")
    @Mapping(target = "addressFrom", source = "document.personalDocumentRequestData.addressFrom")
    @Mapping(
        target = "owners",
        source = "document.personalDocumentRequestData.owners.owner",
        qualifiedByName = "toRestRequestedDocumentOwnerDtos"
    )
    @Mapping(target = "documents", source = "document.personalDocumentRequestData.documents.document")
    @Mapping(target = "application", ignore = true)
    RestPersonalDocumentRequestDto toRestPersonalDocumentRequestDto(
        final PersonalDocumentRequestDocument personalDocumentRequestDocument
    );

    @Mapping(target = "id", source = "personalDocumentRequestDocument.id")
    @Mapping(
        target = "personDocumentId",
        source = "personalDocumentRequestDocument.document.personalDocumentRequestData.personDocumentId"
    )
    @Mapping(
        target = "requestDateTime",
        source = "personalDocumentRequestDocument.document.personalDocumentRequestData.requestDateTime"
    )
    @Mapping(
        target = "addressFrom",
        source = "personalDocumentRequestDocument.document.personalDocumentRequestData.addressFrom"
    )
    @Mapping(
        target = "owners",
        source = "personalDocumentRequestDocument.document.personalDocumentRequestData.owners.owner"
    )
    @Mapping(
        target = "documents",
        source = "personalDocumentRequestDocument.document.personalDocumentRequestData.documents.document"
    )
    @Mapping(
        target = "application",
        expression = "java( toRestPersonalDocumentApplicationDto.apply(personalDocumentApplicationDocument) )"
    )
    RestPersonalDocumentRequestDto toRestPersonalDocumentRequestDto(
        final PersonalDocumentRequestDocument personalDocumentRequestDocument,
        final PersonalDocumentApplicationDocument personalDocumentApplicationDocument,
        final Function<PersonalDocumentApplicationDocument, RestPersonalDocumentApplicationDto>
            toRestPersonalDocumentApplicationDto
    );

    default List<RestPersonalDocumentRequestDto> toRestPersonalDocumentRequestDtos(
        final List<PersonalDocumentRequestDocument> personalDocumentRequestDocuments,
        final Function<PersonalDocumentRequestDocument, PersonalDocumentApplicationDocument>
            retrievePersonalDocumentApplication,
        final Function<PersonalDocumentApplicationDocument, RestPersonalDocumentApplicationDto>
            toRestPersonalDocumentApplicationDto
    ) {
        return personalDocumentRequestDocuments.stream()
            .map(personalDocumentRequestDocument -> {
                final PersonalDocumentApplicationDocument personalDocumentApplicationDocument =
                    retrievePersonalDocumentApplication.apply(personalDocumentRequestDocument);
                return toRestPersonalDocumentRequestDto(
                    personalDocumentRequestDocument,
                    personalDocumentApplicationDocument,
                    toRestPersonalDocumentApplicationDto
                );
            })
            .collect(Collectors.toList());
    }

    @Mapping(target = "typeCode", source = "document.typeCode")
    @Mapping(target = "comment", source = "document.comment")
    @Mapping(target = "personDocumentId", ignore = true)
    RestRequestedDocumentDto toRestRequestedDocumentDto(final RequestedDocumentType document);

    @Mapping(target = "personDocumentId", source = "personDocumentId")
    @Mapping(target = "fullName", source = "requestedDocumentOwner", qualifiedByName = "toFullName")
    @Mapping(target = "birthDate", source = "birthDate")
    @Mapping(target = "documents", source = "documents.document")
    RestRequestedDocumentOwnerDto toRestRequestedDocumentOwnerDto(
        final RequestedDocumentOwnerType requestedDocumentOwner
    );

    @Named("toFullName")
    default String toFullName(final RequestedDocumentOwnerType requestedDocumentOwner) {
        return PersonUtils.getFullName(
            requestedDocumentOwner.getLastName(),
            requestedDocumentOwner.getFirstName(),
            requestedDocumentOwner.getMiddleName()
        );
    }
}
