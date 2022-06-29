package ru.croc.ugd.ssr.mapper;

import static org.mapstruct.ReportingPolicy.ERROR;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.croc.ugd.ssr.dto.affaircollation.RestAffairCollationDto;
import ru.croc.ugd.ssr.model.affairCollation.AffairCollationDocument;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPAffairCollationType;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ERROR)
public interface AffairCollationMapper {

    @Mapping(target = "document.affairCollationData.unom", source = "dto.unom")
    @Mapping(target = "document.affairCollationData.flatNumber", source = "dto.flatNumber")
    @Mapping(target = "document.affairCollationData.affairId", source = "dto.affairId")
    @Mapping(target = "document.affairCollationData.operatorRequest", source = "isOperatorRequest")
    AffairCollationDocument toAffairCollationDocument(
        final RestAffairCollationDto dto, final Boolean isOperatorRequest
    );

    @Mapping(target = "requestId", source = "id")
    @Mapping(target = "unom", source = "document.affairCollationData.unom")
    @Mapping(target = "affairId", source = "document.affairCollationData.affairId")
    @Mapping(target = "flatNumber", source = "document.affairCollationData.flatNumber")
    SuperServiceDGPAffairCollationType toSuperServiceDgpAffairCollationType(
        final AffairCollationDocument affairCollationDocument
    );
}
