package ru.croc.ugd.ssr.mapper;

import static org.mapstruct.ReportingPolicy.ERROR;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.croc.cdp.eventmanager.model.BusEventResponse;
import ru.croc.ugd.ssr.dto.ipev.IpevOrrResponseDto;
import ru.croc.ugd.ssr.model.ipev.IpevLogDocument;

import java.math.BigInteger;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ERROR)
public interface IpevLogMapper {

    @Mapping(
        target = "document.ipevLogData.status",
        expression = "java(ru.croc.ugd.ssr.enums.IpevStatus.NEW.getStatus())"
    )
    @Mapping(target = "document.ipevLogData.eventId", source = "eventId")
    @Mapping(target = "document.ipevLogData.eventDocumentId", source = "event.documentID")
    @Mapping(target = "document.ipevLogData.eventDateTime", source = "event.date")
    @Mapping(target = "document.ipevLogData.orrFileStoreId", source = "dto.fileStoreId")
    @Mapping(target = "document.ipevLogData.cadastralNumbers", source = "dto.cadastralNumbers")
    @Mapping(target = "document.ipevLogData.egrnRequestDateTime", ignore = true)
    @Mapping(target = "document.ipevLogData.filteredCadastralNumbers", ignore = true)
    IpevLogDocument toIpevLogDocument(
        final BigInteger eventId, final BusEventResponse event, final IpevOrrResponseDto dto
    );
}
