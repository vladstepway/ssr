package ru.croc.ugd.ssr.mapper;

import static org.mapstruct.ReportingPolicy.ERROR;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.croc.ugd.ssr.dto.egrn.CreateEgrnBuildingRequestDto;
import ru.croc.ugd.ssr.egrn.BuildingRequestCriteria;
import ru.croc.ugd.ssr.model.egrn.EgrnBuildingRequestDocument;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ERROR)
public interface EgrnBuildingRequestMapper {

    @Mapping(
        target = "document.egrnBuildingRequestData.requestCriteria",
        source = "requestDto"
    )
    @Mapping(
        target = "document.egrnBuildingRequestData.serviceNumber",
        source = "serviceNumber"
    )
    @Mapping(
        target = "document.egrnBuildingRequestData.creationDateTime",
        expression = "java(java.time.LocalDateTime.now())"
    )
    EgrnBuildingRequestDocument toEgrnBuildingRequestDocument(
        final CreateEgrnBuildingRequestDto requestDto,
        final String serviceNumber
    );

    BuildingRequestCriteria toRequestCriteria(final CreateEgrnBuildingRequestDto requestDto);
}
