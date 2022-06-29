package ru.croc.ugd.ssr.mapper;

import static org.mapstruct.ReportingPolicy.ERROR;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.croc.ugd.ssr.dto.pfr.CreatePfrSnilsRequestDto;
import ru.croc.ugd.ssr.model.PfrSnilsRequestDocument;
import ru.croc.ugd.ssr.pfr.PfrSnilsRequestCriteria;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ERROR)
public interface PfrSnilsRequestMapper {

    @Mapping(
        target = "document.pfrSnilsRequestData.requestCriteria",
        source = "requestDto"
    )
    @Mapping(
        target = "document.pfrSnilsRequestData.serviceNumber",
        source = "serviceNumber"
    )
    @Mapping(
        target = "document.pfrSnilsRequestData.creationDateTime",
        expression = "java(java.time.LocalDateTime.now())"
    )
    PfrSnilsRequestDocument toPfrSnilsRequestDocument(
        final CreatePfrSnilsRequestDto requestDto, final String serviceNumber
    );

    PfrSnilsRequestCriteria toRequestCriteria(final CreatePfrSnilsRequestDto requestDto);
}
