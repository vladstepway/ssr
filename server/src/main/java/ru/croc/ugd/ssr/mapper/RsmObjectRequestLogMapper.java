package ru.croc.ugd.ssr.mapper;

import static org.mapstruct.ReportingPolicy.ERROR;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.croc.ugd.ssr.dto.rsm.RsmRequestDto;
import ru.croc.ugd.ssr.model.rsm.RsmObjectRequestLogDocument;
import ru.croc.ugd.ssr.rsm.RsmObjectRequest;
import ru.croc.ugd.ssr.rsm.RsmObjectResponse;
import ru.croc.ugd.ssr.rsm.RsmRequest;
import ru.croc.ugd.ssr.rsm.RsmResponse;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ERROR)
public interface RsmObjectRequestLogMapper {

    @Mapping(
        target = "document.rsmObjectRequestLogData.startDateTime",
        expression = "java(java.time.LocalDateTime.now())"
    )
    @Mapping(
        target = "document.rsmObjectRequestLogData.status",
        expression = "java(ru.croc.ugd.ssr.rsm.RsmObjectRequestLogStatus.IN_PROGRESS)"
    )
    @Mapping(target = "document.rsmObjectRequestLogData.requestEno", source = "request.requestEno")
    @Mapping(target = "document.rsmObjectRequestLogData.requestEtpmvMessageId", source = "request.etpmvMessageId")
    @Mapping(target = "document.rsmObjectRequestLogData.request", source = "request.rsmObjectRequest")
    @Mapping(target = "document.rsmObjectRequestLogData.endDateTime", ignore = true)
    @Mapping(target = "document.rsmObjectRequestLogData.responseEno", ignore = true)
    @Mapping(target = "document.rsmObjectRequestLogData.response", ignore = true)
    RsmObjectRequestLogDocument toRsmObjectRequestLogDocument(final RsmRequestDto request);

    RsmRequest toRsmRequest(final RsmObjectRequest rsmObjectRequest);

    RsmResponse toRsmResponse(final RsmObjectResponse rsmObjectResponse);
}
