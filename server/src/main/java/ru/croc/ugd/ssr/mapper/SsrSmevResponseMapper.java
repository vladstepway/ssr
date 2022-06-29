package ru.croc.ugd.ssr.mapper;

import static org.mapstruct.ReportingPolicy.ERROR;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.croc.ugd.ssr.model.SsrSmevResponseDocument;
import ru.croc.ugd.ssr.model.integration.busresponse.SmevResponse;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ERROR)
public interface SsrSmevResponseMapper {

    @Mapping(target = "document.ssrSmevResponseData.messageId", source = "smevResponse.messageId")
    @Mapping(target = "document.ssrSmevResponseData.serviceNumber", source = "smevResponse.serviceNumber")
    @Mapping(target = "document.ssrSmevResponseData.statusCode", source = "smevResponse.statusCode")
    @Mapping(target = "document.ssrSmevResponseData.note", source = "smevResponse.note")
    @Mapping(target = "document.ssrSmevResponseData.resultCode", source = "smevResponse.resultCode")
    @Mapping(target = "document.ssrSmevResponseData.resultDescription", source = "smevResponse.resultDescription")
    @Mapping(target = "document.ssrSmevResponseData.fileName", source = "fileName")
    @Mapping(target = "document.ssrSmevResponseData.ochdFolderGuid", source = "smevResponse.ochdFolderGuid")
    @Mapping(
        target = "document.ssrSmevResponseData.creationDateTime",
        expression = "java( java.time.LocalDateTime.now() )"
    )
    @Mapping(target = "document.ssrSmevResponseData.processStartDateTime", ignore = true)
    @Mapping(target = "document.ssrSmevResponseData.processEndDateTime", ignore = true)
    SsrSmevResponseDocument toSsrSmevResponseDocument(final SmevResponse smevResponse, final String fileName);

    @Mapping(target = "messageId", source = "document.ssrSmevResponseData.messageId")
    @Mapping(target = "serviceNumber", source = "document.ssrSmevResponseData.serviceNumber")
    @Mapping(target = "statusCode", source = "document.ssrSmevResponseData.statusCode")
    @Mapping(target = "note", source = "document.ssrSmevResponseData.note")
    @Mapping(target = "resultCode", source = "document.ssrSmevResponseData.resultCode")
    @Mapping(target = "resultDescription", source = "document.ssrSmevResponseData.resultDescription")
    @Mapping(target = "ochdFolderGuid", source = "document.ssrSmevResponseData.ochdFolderGuid")
    SmevResponse toSmevResponse(final SsrSmevResponseDocument ssrSmevResponseDocument);
}
