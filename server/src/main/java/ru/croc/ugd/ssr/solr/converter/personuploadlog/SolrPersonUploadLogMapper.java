package ru.croc.ugd.ssr.solr.converter.personuploadlog;

import static java.util.Optional.ofNullable;
import static org.mapstruct.ReportingPolicy.ERROR;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.croc.ugd.ssr.personuploadlog.PersonUploadLogData;
import ru.croc.ugd.ssr.personuploadlog.PersonUploadLogStatus;
import ru.croc.ugd.ssr.solr.UgdSsrPersonUploadLog;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ERROR)
public interface SolrPersonUploadLogMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(
        target = "ugdSsrPersonUploadLogStartDate",
        source = "personUploadLogData.startDateTime"
    )
    @Mapping(
        target = "ugdSsrPersonUploadLogUsername",
        source = "personUploadLogData.username"
    )
    @Mapping(
        target = "ugdSsrPersonUploadLogFilename",
        source = "personUploadLogData.filename"
    )
    @Mapping(
        target = "ugdSsrPersonUploadLogStatus",
        source = "personUploadLogData.status"
    )
    @Mapping(
        target = "ugdSsrPersonUploadLogEndDate",
        source = "personUploadLogData.endDateTime"
    )
    @Mapping(
        target = "ugdSsrPersonUploadLogLink",
        source = "personUploadLogData.logFileId"
    )
    UgdSsrPersonUploadLog toUgdSsrPersonUploadLog(
        @MappingTarget final UgdSsrPersonUploadLog ugdSsrPersonUploadLog,
        final PersonUploadLogData personUploadLogData
    );

    default String toStatusValue(final PersonUploadLogStatus status) {
        return ofNullable(status)
            .map(PersonUploadLogStatus::value)
            .orElse(null);
    }
}
