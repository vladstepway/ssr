package ru.croc.ugd.ssr.mapper;

import static org.mapstruct.ReportingPolicy.ERROR;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.croc.ugd.ssr.compensationflat.CompensationFlatType;
import ru.croc.ugd.ssr.dto.compensation.RestCompensationDto;
import ru.croc.ugd.ssr.dto.compensation.RestCompensationFlatDto;
import ru.croc.ugd.ssr.model.compensation.CompensationDocument;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ERROR)
public interface CompensationMapper {

    @Mapping(target = "id", source = "document.id")
    @Mapping(target = "realEstateId", source = "document.document.compensationData.realEstateId")
    @Mapping(target = "resettlementRequestId", source = "document.document.compensationData.resettlementRequestId")
    @Mapping(target = "unom", source = "document.document.compensationData.unom")
    @Mapping(target = "flats", source = "document.document.compensationData.flats.flat")
    RestCompensationDto toRestCompensationDto(final CompensationDocument document);

    @Mapping(target = "document.document.compensationData.realEstateId", source = "dto.realEstateId")
    @Mapping(target = "document.document.compensationData.resettlementRequestId", source = "dto.resettlementRequestId")
    @Mapping(target = "document.document.compensationData.unom", source = "dto.unom")
    @Mapping(target = "document.document.compensationData.flats.flat", source = "dto.flats")
    CompensationDocument toCompensationDocument(
        @MappingTarget final CompensationDocument document,
        final RestCompensationDto dto
    );

    List<RestCompensationFlatDto> toRestCompensationFlatDtoList(final List<CompensationFlatType> flats);

    @Mapping(target = "flatId", source = "flat.flatId")
    @Mapping(target = "flatNum", source = "flat.flatNum")
    @Mapping(target = "affairId", source = "flat.affairId")
    @Mapping(target = "roomNum", source = "flat.roomNum")
    RestCompensationFlatDto toRestCompensationFlatDto(final CompensationFlatType flat);

    @Mapping(target = "flatId", source = "flat.flatId")
    @Mapping(target = "flatNum", source = "flat.flatNum")
    @Mapping(target = "affairId", source = "flat.affairId")
    @Mapping(target = "roomNum", source = "flat.roomNum")
    CompensationFlatType toCompensationFlatType(final RestCompensationFlatDto flat);
}
