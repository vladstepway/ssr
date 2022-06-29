package ru.croc.ugd.ssr.mapper.mq;

import static org.mapstruct.ReportingPolicy.IGNORE;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import ru.croc.ugd.ssr.integration.command.CommissionInspectionPublishReasonCommand;
import ru.croc.ugd.ssr.integration.command.ContractAppointmentPublishReasonCommand;
import ru.croc.ugd.ssr.integration.command.ContractDigitalSignPublishNotificationReasonCommand;
import ru.croc.ugd.ssr.integration.command.ContractDigitalSignPublishReasonCommand;
import ru.croc.ugd.ssr.integration.command.FlatAppointmentPublishReasonCommand;
import ru.croc.ugd.ssr.integration.command.NotaryApplicationPublishReasonCommand;
import ru.croc.ugd.ssr.integration.command.NotaryCompensationPublishReasonCommand;
import ru.croc.ugd.ssr.integration.command.PersonalDocumentApplicationPublishReasonCommand;
import ru.croc.ugd.ssr.integration.command.PublishReasonCommand;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateSendTaskStatusesMessage;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateStatusData;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateStatusMessage;

import java.time.LocalDateTime;
import java.util.Optional;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

@Mapper(uses = DateMapper.class, componentModel = "spring", unmappedTargetPolicy = IGNORE)
public interface ToCoordinateStatusMessageReasonMapper {

    @Mapping(target = "coordinateStatusDataMessage.serviceNumber", source = "bookingInformation.enoServiceNumber")
    @Mapping(target = "coordinateStatusDataMessage.status.statusCode",
        source = "edpResponseStatusData.shippingFlowStatus.code")
    @Mapping(target = "coordinateStatusDataMessage.note",
        source = "edpResponseStatusData.edpResponseStatusText")
    @Mapping(target = "coordinateStatusDataMessage.status.statusTitle",
        source = "edpResponseStatusData.shippingFlowStatus.description")
    @Mapping(target = "coordinateStatusDataMessage.status.statusDate", source = "responseReasonDate")
    @Mapping(target = "coordinateStatusDataMessage.reason.code",
        source = "edpResponseStatusData.shippingFlowStatus.subCode")
    @Mapping(target = "coordinateStatusDataMessage.reason.name", source = "bookingInformation.reason")
    CoordinateStatusMessage toCoordinateStatusMessage(PublishReasonCommand publishReasonCommand);

    @Mapping(target = "coordinateStatusDataMessage.serviceNumber", source = "commissionInspectionData.eno")
    @Mapping(target = "coordinateStatusDataMessage.status.statusCode", source = "status.code")
    @Mapping(target = "coordinateStatusDataMessage.note", source = "elkStatusText")
    @Mapping(target = "coordinateStatusDataMessage.status.statusTitle", source = "status.description")
    @Mapping(
        target = "coordinateStatusDataMessage.status.statusDate",
        source = "responseReasonDate",
        qualifiedByName = "toXmlGregorianCalendar"
    )
    @Mapping(target = "coordinateStatusDataMessage.reason.code", source = "status.subCode")
    CoordinateStatusMessage toCoordinateStatusMessage(CommissionInspectionPublishReasonCommand publishReasonCommand);

    @Mapping(
        target = "coordinateStatusDataMessage.serviceNumber",
        source = "publishReasonCommand.notaryApplication.eno"
    )
    @Mapping(target = "coordinateStatusDataMessage.status.statusCode", source = "publishReasonCommand.status.code")
    @Mapping(target = "coordinateStatusDataMessage.note", source = "publishReasonCommand.elkStatusText")
    @Mapping(
        target = "coordinateStatusDataMessage.status.statusTitle",
        source = "publishReasonCommand.status.description"
    )
    @Mapping(
        target = "coordinateStatusDataMessage.status.statusDate",
        source = "publishReasonCommand.responseReasonDate",
        qualifiedByName = "toXmlGregorianCalendar"
    )
    @Mapping(target = "coordinateStatusDataMessage.statusId", ignore = true)
    @Mapping(target = "coordinateStatusDataMessage.reason.code", source = "publishReasonCommand.status.subCode")
    CoordinateStatusMessage toCoordinateStatusMessage(NotaryApplicationPublishReasonCommand publishReasonCommand);

    @Mapping(
        target = "coordinateStatusDataMessage.serviceNumber",
        source = "publishReasonCommand.contractAppointment.eno"
    )
    @Mapping(target = "coordinateStatusDataMessage.status.statusCode", source = "publishReasonCommand.status.code")
    @Mapping(target = "coordinateStatusDataMessage.note", source = "publishReasonCommand.elkStatusText")
    @Mapping(
        target = "coordinateStatusDataMessage.status.statusTitle",
        source = "publishReasonCommand.status.description"
    )
    @Mapping(
        target = "coordinateStatusDataMessage.status.statusDate",
        source = "publishReasonCommand.responseReasonDate",
        qualifiedByName = "toXmlGregorianCalendar"
    )
    @Mapping(target = "coordinateStatusDataMessage.statusId", ignore = true)
    @Mapping(target = "coordinateStatusDataMessage.reason.code", source = "publishReasonCommand.status.subCode")
    CoordinateStatusMessage toCoordinateStatusMessage(ContractAppointmentPublishReasonCommand publishReasonCommand);

    @Mapping(
        target = "coordinateStatusDataMessage.serviceNumber",
        source = "publishReasonCommand.contractAppointment.eno"
    )
    @Mapping(target = "coordinateStatusDataMessage.status.statusCode", source = "publishReasonCommand.status.code")
    @Mapping(target = "coordinateStatusDataMessage.note", source = "publishReasonCommand.elkStatusText")
    @Mapping(
        target = "coordinateStatusDataMessage.status.statusTitle",
        source = "publishReasonCommand.status.description"
    )
    @Mapping(
        target = "coordinateStatusDataMessage.status.statusDate",
        source = "publishReasonCommand.responseReasonDate",
        qualifiedByName = "toXmlGregorianCalendar"
    )
    @Mapping(target = "coordinateStatusDataMessage.statusId", ignore = true)
    @Mapping(target = "coordinateStatusDataMessage.reason.code", source = "publishReasonCommand.status.subCode")
    CoordinateStatusMessage toCoordinateStatusMessage(ContractDigitalSignPublishReasonCommand publishReasonCommand);


    @Mapping(target = "coordinateStatusDataMessage.serviceNumber", source = "publishNotificationReasonCommand.eno")
    @Mapping(
        target = "coordinateStatusDataMessage.status.statusCode",
        source = "publishNotificationReasonCommand.status.code"
    )
    @Mapping(target = "coordinateStatusDataMessage.note", source = "publishNotificationReasonCommand.elkStatusText")
    @Mapping(
        target = "coordinateStatusDataMessage.status.statusTitle",
        source = "publishNotificationReasonCommand.status.description"
    )
    @Mapping(
        target = "coordinateStatusDataMessage.status.statusDate",
        source = "publishNotificationReasonCommand.responseReasonDate",
        qualifiedByName = "toXmlGregorianCalendar"
    )
    @Mapping(
        target = "coordinateStatusDataMessage.reason.code", source = "publishNotificationReasonCommand.reasonCode"
    )
    CoordinateStatusMessage toCoordinateStatusMessage(
        final ContractDigitalSignPublishNotificationReasonCommand publishNotificationReasonCommand
    );

    @Mapping(
        target = "coordinateStatusDataMessage.serviceNumber",
        source = "publishReasonCommand.flatAppointment.eno"
    )
    @Mapping(target = "coordinateStatusDataMessage.status.statusCode", source = "publishReasonCommand.status.code")
    @Mapping(target = "coordinateStatusDataMessage.note", source = "publishReasonCommand.elkStatusText")
    @Mapping(
        target = "coordinateStatusDataMessage.status.statusTitle",
        source = "publishReasonCommand.status.description"
    )
    @Mapping(
        target = "coordinateStatusDataMessage.status.statusDate",
        source = "publishReasonCommand.responseReasonDate",
        qualifiedByName = "toXmlGregorianCalendar"
    )
    @Mapping(target = "coordinateStatusDataMessage.statusId", ignore = true)
    @Mapping(target = "coordinateStatusDataMessage.reason.code", source = "publishReasonCommand.status.subCode")
    CoordinateStatusMessage toCoordinateStatusMessage(FlatAppointmentPublishReasonCommand publishReasonCommand);

    @Mapping(
        target = "coordinateStatusDataMessage.serviceNumber",
        source = "publishReasonCommand.personalDocumentApplication.eno"
    )
    @Mapping(target = "coordinateStatusDataMessage.status.statusCode", source = "publishReasonCommand.status.code")
    @Mapping(target = "coordinateStatusDataMessage.note", source = "publishReasonCommand.elkStatusText")
    @Mapping(
        target = "coordinateStatusDataMessage.status.statusTitle",
        source = "publishReasonCommand.status.description"
    )
    @Mapping(
        target = "coordinateStatusDataMessage.status.statusDate",
        source = "publishReasonCommand.responseReasonDate",
        qualifiedByName = "toXmlGregorianCalendar"
    )
    @Mapping(target = "coordinateStatusDataMessage.statusId", ignore = true)
    @Mapping(target = "coordinateStatusDataMessage.reason.code", source = "publishReasonCommand.status.subCode")
    CoordinateStatusMessage toCoordinateStatusMessage(
        PersonalDocumentApplicationPublishReasonCommand publishReasonCommand
    );

    @Mapping(
        target = "coordinateStatusDataMessage.serviceNumber",
        source = "publishReasonCommand.notaryCompensation.eno"
    )
    @Mapping(target = "coordinateStatusDataMessage.status.statusCode", source = "publishReasonCommand.status.code")
    @Mapping(target = "coordinateStatusDataMessage.note", source = "publishReasonCommand.elkStatusText")
    @Mapping(
        target = "coordinateStatusDataMessage.status.statusTitle",
        source = "publishReasonCommand.status.description"
    )
    @Mapping(
        target = "coordinateStatusDataMessage.status.statusDate",
        source = "publishReasonCommand.responseReasonDate",
        qualifiedByName = "toXmlGregorianCalendar"
    )
    @Mapping(target = "coordinateStatusDataMessage.statusId", ignore = true)
    @Mapping(target = "coordinateStatusDataMessage.reason.code", source = "publishReasonCommand.status.subCode")
    CoordinateStatusMessage toCoordinateStatusMessage(
        NotaryCompensationPublishReasonCommand publishReasonCommand
    );

    @Mapping(target = "coordinateTaskStatusDataMessage.taskId", source = "eno")
    @Mapping(target = "coordinateTaskStatusDataMessage.status.statusCode", source = "statusCode")
    @Mapping(target = "coordinateTaskStatusDataMessage.status.statusTitle", source = "statusTitle")
    @Mapping(
        target = "coordinateTaskStatusDataMessage.status.statusDate",
        expression = "java( toXmlGregorianCalendar(java.time.LocalDateTime.now()) )"
    )
    CoordinateSendTaskStatusesMessage toCoordinateSendTaskStatusesMessage(
        final String eno, final Integer statusCode, final String statusTitle
    );

    @AfterMapping
    default CoordinateStatusData doAfterMapping(@MappingTarget CoordinateStatusData entity) {
        if (entity != null
            && entity.getReason() != null
            && entity.getReason().getCode() == null
            && entity.getReason().getName() == null) {
            entity.setReason(null);
        }
        return entity;
    }

    @Named("toXmlGregorianCalendar")
    default XMLGregorianCalendar toXmlGregorianCalendar(final LocalDateTime localDateTime) {
        try {
            final DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
            return Optional.ofNullable(localDateTime)
                .map(LocalDateTime::toString)
                .map(datatypeFactory::newXMLGregorianCalendar)
                .orElse(null);
        } catch (Exception e) {
            return null;
        }
    }
}
