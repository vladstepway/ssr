package ru.croc.ugd.ssr.mapper;

import static org.mapstruct.ReportingPolicy.ERROR;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.croc.ugd.ssr.dto.bus.CreateBusRequestDto;
import ru.croc.ugd.ssr.model.bus.BusRequestDocument;
import ru.reinform.cdp.bus.rest.model.SyncResponse;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ERROR)
public interface BusRequestDocumentMapper {

    @Mapping(
        target = "document.busRequestData.ssrRequestTypeCode",
        source = "createBusRequestDto.busRequestType.code"
    )
    @Mapping(
        target = "document.busRequestData.serviceNumber",
        source = "createBusRequestDto.serviceNumber"
    )
    @Mapping(
        target = "document.busRequestData.serviceTypeCode",
        source = "createBusRequestDto.serviceTypeCode"
    )
    @Mapping(
        target = "document.busRequestData.ochdFolderGuid",
        source = "createBusRequestDto.ochdFolderGuid"
    )
    @Mapping(
        target = "document.busRequestData.requestPayload",
        source = "requestPayload"
    )
    @Mapping(
        target = "document.busRequestData.cdpBusResponse.cdpBusMessageId",
        source = "busResponse.messageID"
    )
    @Mapping(
        target = "document.busRequestData.cdpBusResponse.cdpBusMessageDocument",
        source = "busResponse.document"
    )
    @Mapping(
        target = "document.busRequestData.cdpBusResponse.cdpBusMessageStatus",
        source = "busResponse.status"
    )
    @Mapping(
        target = "document.busRequestData.creationDateTime",
        expression = "java(java.time.LocalDateTime.now())"
    )
    BusRequestDocument toBusRequestDocument(
        final CreateBusRequestDto createBusRequestDto,
        final String requestPayload,
        final SyncResponse busResponse
    );
}
