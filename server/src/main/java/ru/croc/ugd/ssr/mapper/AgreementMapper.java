package ru.croc.ugd.ssr.mapper;

import static java.util.Optional.ofNullable;
import static org.mapstruct.ReportingPolicy.ERROR;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.db.projection.TenantProjection;
import ru.croc.ugd.ssr.dto.agreement.RestAgreementDto;
import ru.croc.ugd.ssr.dto.agreement.RestAgreementParticipantDto;
import ru.croc.ugd.ssr.utils.PersonUtils;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ERROR)
public interface AgreementMapper {

    @Mapping(target = "letterId", source = "letterId")
    @Mapping(target = "date", source = "date")
    @Mapping(target = "event", source = "event")
    @Mapping(target = "fullDocs", source = "fullDocs")
    @Mapping(target = "waitingDocsCodes", source = "waitingDocsCodes")
    @Mapping(target = "waitingDocs", source = "waitingDocs")
    @Mapping(target = "refuseReason", source = "refuseReason")
    @Mapping(target = "refuseReasonText", source = "refuseReasonText")
    @Mapping(target = "fileLink", source = "fileLink")
    @Mapping(target = "participants", source = "participants.personID", qualifiedByName = "toParticipants")
    RestAgreementDto toRestAgreementDto(
        final PersonType.Agreements.Agreement agreement,
        @Context final Function<String, TenantProjection> retrieveTenant
    );

    List<RestAgreementDto> toRestAgreementDtos(
        final List<PersonType.Agreements.Agreement> agreements,
        @Context final Function<String, TenantProjection> retrieveTenant
    );

    @Mapping(target = "personDocumentId", source = "tenantProjection.id")
    @Mapping(target = "fullName", source = "tenantProjection", qualifiedByName = "toFullName")
    @Mapping(target = "birthDate", source = "tenantProjection.birthDate")
    RestAgreementParticipantDto toRestAgreementParticipantDto(final TenantProjection tenantProjection);

    @Named("toParticipants")
    default List<RestAgreementParticipantDto> toParticipants(
        final List<String> personDocumentIds, @Context final Function<String, TenantProjection> retrieveTenant
    ) {
        return ofNullable(personDocumentIds)
            .map(List::stream)
            .orElse(Stream.empty())
            .map(retrieveTenant)
            .map(this::toRestAgreementParticipantDto)
            .collect(Collectors.toList());
    }

    @Named("toFullName")
    default String toFullName(final TenantProjection tenantProjection) {
        return PersonUtils.getFullName(tenantProjection);
    }
}
