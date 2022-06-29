package ru.croc.ugd.ssr.solr.converter.egrn;

import static ru.croc.ugd.ssr.service.egrn.EgrnFlatRequestService.BUS_REQUEST_SENT_STATUS;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import ru.croc.ugd.ssr.egrn.EgrnFlatRequestData;
import ru.croc.ugd.ssr.egrn.ExtractAboutPropertyRoom;
import ru.croc.ugd.ssr.solr.UgdSsrEgrnFlatRequestStatement;

import java.util.Optional;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface SolrEgrnFlatRequestStatementMapper {

    @Mapping(target = "sysType", ignore = true)
    @Mapping(target = "sysAccessTypes", ignore = true)
    @Mapping(target = "sysDocumentId", ignore = true)
    @Mapping(
        target = "ugdSsrEgrnFlatRequestStatementAddress",
        source = "extractAboutPropertyRoom.roomRecord.addressRoom.address.address.readableAddress"
    )
    @Mapping(
        target = "ugdSsrEgrnFlatRequestStatementCadastralNumber",
        source = "flatRequestData.requestCriteria.cadastralNumber"
    )
    @Mapping(
        target = "ugdSsrEgrnFlatRequestStatementFlatNumber",
        source = "flatRequestData.requestCriteria.flatNumber"
    )
    @Mapping(
        target = "ugdSsrEgrnFlatRequestStatementUnom",
        source = "flatRequestData.requestCriteria.unom"
    )
    @Mapping(target = "ugdSsrEgrnFlatRequestStatementServiceNumber", source = "flatRequestData.serviceNumber")
    @Mapping(target = "ugdSsrEgrnFlatRequestStatementRequestDateTime", source = "flatRequestData.creationDateTime")
    @Mapping(
        target = "ugdSsrEgrnFlatRequestStatementStatus",
        source = "flatRequestData.statusCode",
        qualifiedByName = "toStatusCode"
    )
    @Mapping(target = "ugdSsrEgrnFlatRequestStatementErrorDescription", source = "flatRequestData.errorDescription")
    @Mapping(
        target = "ugdSsrEgrnFlatRequestStatementResponseDateTime",
        source = "flatRequestData.egrnResponse.responseDateTime"
    )
    @Mapping(
        target = "ugdSsrEgrnFlatRequestStatementPdfFilestoreId",
        source = "flatRequestData.egrnResponse.pdfFileStoreId"
    )
    @Mapping(
        target = "ugdSsrEgrnFlatRequestStatementZipFilestoreId",
        source = "flatRequestData.egrnResponse.zipFileStoreId"
    )
    @Mapping(
        target = "ugdSsrEgrnFlatRequestStatementFlatType",
        source = "extractAboutPropertyRoom.roomRecord.params.purpose.value"
    )
    UgdSsrEgrnFlatRequestStatement toUgdSsrEgrnFlatRequestStatement(
        @MappingTarget final UgdSsrEgrnFlatRequestStatement ugdSsrEgrnFlatRequestStatement,
        final ExtractAboutPropertyRoom extractAboutPropertyRoom,
        final EgrnFlatRequestData flatRequestData
    );

    @Named("toStatusCode")
    default String toStatusCode(final String statusCode) {
        return Optional.ofNullable(statusCode).orElse(BUS_REQUEST_SENT_STATUS);
    }
}
