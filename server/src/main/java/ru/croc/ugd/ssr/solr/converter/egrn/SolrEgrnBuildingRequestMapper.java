package ru.croc.ugd.ssr.solr.converter.egrn;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import ru.croc.ugd.ssr.egrn.EgrnBuildingRequestData;
import ru.croc.ugd.ssr.solr.UgdSsrEgrnBuildingRequest;

import java.util.Optional;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface SolrEgrnBuildingRequestMapper {

    @Mapping(target = "sysType", ignore = true)
    @Mapping(target = "sysAccessTypes", ignore = true)
    @Mapping(target = "sysDocumentId", ignore = true)
    @Mapping(target = "ugdSsrEgrnBuildingRequestServiceNumber", source = "egrnBuildingRequestData.serviceNumber")
    @Mapping(target = "ugdSsrEgrnBuildingRequestDateTime", source = "egrnBuildingRequestData.creationDateTime")
    @Mapping(
        target = "ugdSsrEgrnBuildingRequestStatus",
        source = "egrnBuildingRequestData.statusCode",
        qualifiedByName = "toStatusCode")
    @Mapping(target = "ugdSsrEgrnBuildingRequestErrorDescription", source = "egrnBuildingRequestData.errorDescription")
    @Mapping(
        target = "ugdSsrEgrnBuildingRequestStatementDateTime",
        source = "egrnBuildingRequestData.egrnResponse.responseDateTime"
    )
    @Mapping(
        target = "ugdSsrEgrnBuildingRequestAddress",
        source = "egrnBuildingRequestData.egrnResponse.extractAboutPropertyBuild.buildRecord.addressLocation.address."
            + "readableAddress"
    )
    @Mapping(target = "ugdSsrEgrnBuildingRequestUnom", source = "egrnBuildingRequestData.requestCriteria.unom")
    @Mapping(
        target = "ugdSsrEgrnBuildingRequestCadastralNumber",
        source = "egrnBuildingRequestData.requestCriteria.cadastralNumber"
    )
    @Mapping(
        target = "ugdSsrEgrnBuildingRequestPdfFilestoreId",
        source = "egrnBuildingRequestData.egrnResponse.pdfFileStoreId"
    )
    @Mapping(
        target = "ugdSsrEgrnBuildingRequestZipFilestoreId",
        source = "egrnBuildingRequestData.egrnResponse.zipFileStoreId"
    )
    UgdSsrEgrnBuildingRequest toUgdSsrEgrnBuildingRequest(
        @MappingTarget final UgdSsrEgrnBuildingRequest ugdSsrEgrnBuildingRequest,
        final EgrnBuildingRequestData egrnBuildingRequestData
    );

    @Named("toStatusCode")
    default String toStatusCode(final String statusCode) {
        return Optional.ofNullable(statusCode).orElse("0");
    }
}
