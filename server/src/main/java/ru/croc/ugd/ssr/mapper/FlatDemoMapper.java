package ru.croc.ugd.ssr.mapper;

import static org.mapstruct.ReportingPolicy.ERROR;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.croc.ugd.ssr.PersonType.FlatDemo.FlatDemoItem;
import ru.croc.ugd.ssr.dto.flatappointment.RestFlatAppointmentDto;
import ru.croc.ugd.ssr.dto.flatappointment.RestFlatDemoParticipantDto;
import ru.croc.ugd.ssr.dto.flatdemo.RestCreateFlatDemoDto;
import ru.croc.ugd.ssr.dto.flatdemo.RestFlatDemoDto;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.flatAppointment.FlatAppointmentDocument;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPViewStatusType;
import ru.croc.ugd.ssr.utils.PersonUtils;
import ru.reinform.cdp.ldap.model.UserBean;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ERROR)
public interface FlatDemoMapper {

    @Mapping(target = "dataId", expression = "java( java.util.UUID.randomUUID().toString() )")
    @Mapping(target = "demoId", expression = "java( java.util.UUID.randomUUID().toString() )")
    @Mapping(target = "letterId", source = "createFlatDemoDto.letterId")
    @Mapping(target = "date", source = "createFlatDemoDto.date")
    @Mapping(target = "annotation", source = "createFlatDemoDto.annotation")
    @Mapping(target = "participants", source = "createFlatDemoDto.participants")
    @Mapping(target = "participantSummary", ignore = true)
    @Mapping(target = "techInfo", source = "user")
    FlatDemoItem toFlatDemoItem(
        final RestCreateFlatDemoDto createFlatDemoDto,
        final UserBean user
    );

    default FlatDemoItem.Participants toParticipants(final List<RestFlatDemoParticipantDto> participantDtos) {
        final FlatDemoItem.Participants participants = new FlatDemoItem.Participants();

        final List<String> personDocumentIdList = participantDtos.stream()
            .map(RestFlatDemoParticipantDto::getPersonDocumentId)
            .collect(Collectors.toList());

        participants.getPersonID().addAll(personDocumentIdList);

        return participants;
    }

    @Mapping(target = "personId", source = "person.document.personData.personID")
    @Mapping(target = "affairId", source = "person.document.personData.affairId")
    @Mapping(target = "letterId", source = "flatDemoItem.letterId")
    @Mapping(target = "viewDate", source = "flatDemoItem.date")
    @Mapping(target = "annotation", source = "flatDemoItem.annotation")
    @Mapping(target = "techInfo", source = "flatDemoItem.techInfo")
    SuperServiceDGPViewStatusType toViewStatus(final PersonDocument person, final FlatDemoItem flatDemoItem);

    SuperServiceDGPViewStatusType.TechInfo toTechInfo(final FlatDemoItem.TechInfo techInfo);

    @Mapping(target = "userID", source = "user.accountName")
    @Mapping(target = "userFIO", source = "user.displayName")
    @Mapping(target = "actionDateTime", expression = "java( java.time.LocalDateTime.now() )")
    FlatDemoItem.TechInfo toTechInfo(final UserBean user);

    @Mapping(target = "flatAppointment", source = "flatAppointmentDocument")
    @Mapping(target = "date", source = "flatDemoItem.date")
    @Mapping(target = "letterId", source = "flatDemoItem.letterId")
    @Mapping(target = "annotation", source = "flatDemoItem.annotation")
    @Mapping(target = "participants", source = "participantDtos")
    RestFlatDemoDto toRestFlatDemoDto(
        final FlatDemoItem flatDemoItem,
        final FlatAppointmentDocument flatAppointmentDocument,
        final List<RestFlatDemoParticipantDto> participantDtos
    );

    @Mapping(target = "flatAppointment", source = "flatAppointmentDocument")
    @Mapping(target = "date", ignore = true)
    @Mapping(target = "letterId", source = "document.flatAppointmentData.offerLetterId")
    @Mapping(target = "annotation", source = "document.flatAppointmentData.cancelReason")
    @Mapping(target = "participants", ignore = true)
    RestFlatDemoDto toRestFlatDemoDto(final FlatAppointmentDocument flatAppointmentDocument);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "eno", source = "document.flatAppointmentData.eno")
    @Mapping(target = "statusId", source = "document.flatAppointmentData.statusId")
    @Mapping(target = "appointmentDateTime", source = "document.flatAppointmentData.appointmentDateTime")
    RestFlatAppointmentDto toRestFlatAppointmentDto(final FlatAppointmentDocument flatAppointmentDocument);

    @Mapping(target = "personDocumentId", source = "personDocument.id")
    @Mapping(target = "fullName", source = "personDocument", qualifiedByName = "toPersonFullName")
    @Mapping(target = "birthDate", source = "personDocument.document.personData.birthDate")
    RestFlatDemoParticipantDto toRestFlatDemoParticipantDto(final PersonDocument personDocument);

    @Named("toPersonFullName")
    default String toPersonFullName(final PersonDocument personDocument) {
        return PersonUtils.getFullName(personDocument);
    }
}
