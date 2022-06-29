package ru.croc.ugd.ssr.mapper;

import static org.mapstruct.ReportingPolicy.ERROR;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.croc.ugd.ssr.dto.guardianship.CreateGuardianshipRequestDto;
import ru.croc.ugd.ssr.dto.guardianship.GuardianshipRequestDto;
import ru.croc.ugd.ssr.model.guardianship.GuardianshipRequestDocument;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ERROR)
public interface GuardianshipMapper {

    @Mapping(source = "document.id", target = "id")
    @Mapping(source = "document.guardianshipRequestData.requestDate", target = "requestDate")
    @Mapping(source = "document.guardianshipRequestData.requestFileId", target = "requestFileId")
    @Mapping(source = "document.guardianshipRequestData.requesterPersonId", target = "requesterPersonId")
    @Mapping(source = "document.guardianshipRequestData.affairId", target = "affairId")
    @Mapping(source = "document.guardianshipRequestData.decisionDate", target = "decisionDate")
    @Mapping(source = "document.guardianshipRequestData.decisionFileId", target = "decisionFileId")
    @Mapping(source = "document.guardianshipRequestData.decisionType", target = "decisionType")
    @Mapping(source = "document.guardianshipRequestData.declineReasonType", target = "declineReasonType")
    @Mapping(source = "document.guardianshipRequestData.declineReason", target = "declineReason")
    @Mapping(source = "document.guardianshipRequestData.processInstanceId", target = "processInstanceId")
    GuardianshipRequestDto toGuardianshipRequestDto(
        final GuardianshipRequestDocument document
    );

    @Mapping(source = "createDto.requestDate", target = "document.guardianshipRequestData.requestDate")
    @Mapping(source = "createDto.requestFileId", target = "document.guardianshipRequestData.requestFileId")
    @Mapping(source = "createDto.requesterPersonId", target = "document.guardianshipRequestData.requesterPersonId")
    @Mapping(source = "createDto.affairId", target = "document.guardianshipRequestData.affairId")
    @Mapping(source = "now", target = "document.guardianshipRequestData.creationDateTime")
    GuardianshipRequestDocument toGuardianshipRequestDocument(
        final CreateGuardianshipRequestDto createDto, final LocalDateTime now
    );
}
