package ru.croc.ugd.ssr.solr.converter.ipev;

import static org.mapstruct.ReportingPolicy.ERROR;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import ru.croc.ugd.ssr.ipev.IpevLogData;
import ru.croc.ugd.ssr.solr.UgdSsrIpevLog;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ERROR)
public interface SolrIpevLogMapper {

    @Mapping(target = "sysDocumentId", ignore = true)
    @Mapping(target = "sysType", ignore = true)
    @Mapping(target = "sysAccessTypes", ignore = true)
    @Mapping(target = "ugdSsrIpevLogEventDateTime", source = "ipevLogData.eventDateTime")
    @Mapping(target = "ugdSsrIpevLogEgrnRequestDateTime", source = "ipevLogData.egrnRequestDateTime")
    @Mapping(target = "ugdSsrIpevLogStatus", source = "ipevLogData.status")
    @Mapping(target = "ugdSsrIpevLogOrrFileStoreId", source = "ipevLogData.orrFileStoreId")
    @Mapping(
        target = "ugdSsrIpevLogCadastralNumbersCount",
        source = "ipevLogData.cadastralNumbers",
        qualifiedByName = "toUgdSsrIpevLogCadastralNumbersCount"
    )
    @Mapping(
        target = "ugdSsrIpevLogFilteredCadastralNumbersCount",
        source = "ipevLogData.filteredCadastralNumbers",
        qualifiedByName = "toUgdSsrIpevLogFilteredCadastralNumbersCount"
    )
    UgdSsrIpevLog toUgdSsrIpevLog(
        @MappingTarget final UgdSsrIpevLog ugdSsrIpevLog,
        final IpevLogData ipevLogData
    );

    @Named("toUgdSsrIpevLogCadastralNumbersCount")
    default Long toUgdSsrIpevLogCadastralNumbersCount(final List<String> cadastralNumbers) {
        return (long) cadastralNumbers.size();
    }

    @Named("toUgdSsrIpevLogFilteredCadastralNumbersCount")
    default Long toUgdSsrIpevLogFilteredCadastralNumbersCount(final List<String> filteredCadastralNumbers) {
        return (long) filteredCadastralNumbers.size();
    }

}
