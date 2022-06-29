package ru.croc.ugd.ssr.mapper;

import static org.mapstruct.ReportingPolicy.ERROR;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.croc.ugd.ssr.dto.egrn.CreateEgrnFlatRequestDto;
import ru.croc.ugd.ssr.dto.realestate.RestNonResidentialApartmentDto;
import ru.croc.ugd.ssr.egrn.ExtractAboutPropertyRoom;
import ru.croc.ugd.ssr.egrn.FlatRequestCriteria;
import ru.croc.ugd.ssr.model.egrn.EgrnFlatRequestDocument;
import ru.croc.ugd.ssr.utils.EgrnUtils;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ERROR)
public interface EgrnFlatRequestMapper {

    @Mapping(
        target = "document.egrnFlatRequestData.requestCriteria",
        source = "requestDto"
    )
    @Mapping(
        target = "document.egrnFlatRequestData.serviceNumber",
        source = "serviceNumber"
    )
    @Mapping(
        target = "document.egrnFlatRequestData.creationDateTime",
        expression = "java(java.time.LocalDateTime.now())"
    )
    EgrnFlatRequestDocument toEgrnFlatRequestDocument(
        final CreateEgrnFlatRequestDto requestDto,
        final String serviceNumber
    );

    FlatRequestCriteria toRequestCriteria(final CreateEgrnFlatRequestDto requestDto);

    @Mapping(
        target = "cadNumber",
        source = "roomRecord.object.commonData.cadNumber"
    )
    @Mapping(
        target = "cost",
        source = "roomRecord.cost.value"
    )
    @Mapping(
        target = "area",
        source = "roomRecord.params.area"
    )
    @Mapping(
        target = "name",
        source = "roomRecord.params.name"
    )
    @Mapping(
        target = "purpose",
        source = "roomRecord.params.purpose.value"
    )
    @Mapping(
        target = "apartment",
        source = "roomRecord.addressRoom.address.address.addressFias.detailedLevel.apartment.nameApartment"
    )
    @Mapping(
        target = "rightHolder",
        source = "extractAboutPropertyRoom",
        qualifiedByName = "toRightHolder"
    )
    @Mapping(
        target = "restrict",
        source = "extractAboutPropertyRoom",
        qualifiedByName = "toRestrict"
    )
    RestNonResidentialApartmentDto toRestNonResidentialApartmentDto(
        final ExtractAboutPropertyRoom extractAboutPropertyRoom
    );

    @Named("toRightHolder")
    default String toRightHolder(final ExtractAboutPropertyRoom extractAboutPropertyRoom) {
        return EgrnUtils.getRightHolderAsString(extractAboutPropertyRoom);
    }

    @Named("toRestrict")
    default String toRestrict(final ExtractAboutPropertyRoom extractAboutPropertyRoom) {
        return EgrnUtils.getRestrictAsString(extractAboutPropertyRoom);
    }

}
