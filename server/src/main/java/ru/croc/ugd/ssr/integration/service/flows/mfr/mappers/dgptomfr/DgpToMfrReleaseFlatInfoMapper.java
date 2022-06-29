package ru.croc.ugd.ssr.integration.service.flows.mfr.mappers.dgptomfr;

import static org.mapstruct.ReportingPolicy.ERROR;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPFlatFreeType;
import ru.croc.ugd.ssr.model.integration.mfr.DgpToMfrReleaseFlatInfo;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ERROR)
public interface DgpToMfrReleaseFlatInfoMapper {

    @Mapping(target = "affairId", source = "affairId")
    @Mapping(target = "actNum", source = "actNumber")
    @Mapping(target = "actDate", source = "actData")
    DgpToMfrReleaseFlatInfo toDgpToMfrReleaseFlatInfo(final SuperServiceDGPFlatFreeType info);
}
