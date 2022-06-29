package ru.croc.ugd.ssr.mapper;

import static org.mapstruct.ReportingPolicy.ERROR;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.croc.ugd.ssr.dto.mdm.request.MdmRequest;
import ru.croc.ugd.ssr.dto.mdm.response.MdmResponse;
import ru.croc.ugd.ssr.model.MdmExternalPersonInfoDocument;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ERROR)
public interface MdmExternalPersonInfoMapper {

    @Mapping(
        target = "document.mdmExternalPersonInfoData.personDocumentId",
        source = "personDocumentId"
    )
    @Mapping(
        target = "document.mdmExternalPersonInfoData.creationDateTime",
        expression = "java(java.time.LocalDateTime.now())"
    )
    @Mapping(
        target = "document.mdmExternalPersonInfoData.request",
        source = "mdmRequest"
    )
    @Mapping(
        target = "document.mdmExternalPersonInfoData.json",
        source = "json"
    )
    @Mapping(
        target = "document.mdmExternalPersonInfoData.response",
        source = "mdmResponse"
    )
    MdmExternalPersonInfoDocument toMdmExternalPersonInfoDocument(
        final String json,
        final MdmRequest mdmRequest,
        final MdmResponse mdmResponse,
        final String personDocumentId
    );
}
