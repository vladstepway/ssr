package ru.croc.ugd.ssr.solr.converter.egrn;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import ru.croc.ugd.ssr.egrn.ExtractAboutPropertyRoom;
import ru.croc.ugd.ssr.egrn.FlatRequestCriteria;
import ru.croc.ugd.ssr.egrn.LevelAll;
import ru.croc.ugd.ssr.solr.UgdSsrEgrnFlatRequest;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface SolrEgrnFlatRequestMapper {

    @Mapping(target = "sysType", ignore = true)
    @Mapping(target = "sysAccessTypes", ignore = true)
    @Mapping(target = "ugdSsrEgrnFlatRoomNumber", ignore = true)
    @Mapping(target = "sysDocumentId", source = "requestCriteria.cadastralNumber", qualifiedByName = "toSysDocumentId")
    @Mapping(
        target = "ugdSsrEgrnFlatAddress",
        source = "extractAboutPropertyRoom.roomRecord.addressRoom.address.address.readableAddress"
    )
    @Mapping(
        target = "ugdSsrEgrnFlatFloor",
        source = "extractAboutPropertyRoom.roomRecord.locationInBuild.level"
    )
    @Mapping(
        target = "ugdSsrEgrnFlatCadastralNumber",
        source = "requestCriteria.cadastralNumber"
    )
    @Mapping(
        target = "ugdSsrEgrnFlatCcoId",
        source = "requestCriteria.ccoDocumentId"
    )
    @Mapping(
        target = "ugdSsrEgrnFlatNumber",
        source = "requestCriteria.flatNumber"
    )
    @Mapping(
        target = "ugdSsrEgrnFlatRealEstateId",
        source = "requestCriteria.realEstateDocumentId"
    )
    @Mapping(
        target = "ugdSsrEgrnFlatUnom",
        source = "requestCriteria.unom"
    )
    @Mapping(
        target = "ugdSsrEgrnFlatResettled",
        source = "flatResettled"
    )
    UgdSsrEgrnFlatRequest toUgdSsrEgrnFlatRequest(
        @MappingTarget final UgdSsrEgrnFlatRequest ugdSsrEgrnFlatRequest,
        final ExtractAboutPropertyRoom extractAboutPropertyRoom,
        final FlatRequestCriteria requestCriteria,
        final Boolean flatResettled
    );

    default String getFloorValue(final List<LevelAll> levelAll) {
        return levelAll.stream()
            .map(LevelAll::getFloor)
            .findFirst()
            .orElse(null);
    }

    @Named("toSysDocumentId")
    default String toSysDocumentId(final String cadastralNumber) {
        return UUID.nameUUIDFromBytes(cadastralNumber.getBytes()).toString();
    }
}
