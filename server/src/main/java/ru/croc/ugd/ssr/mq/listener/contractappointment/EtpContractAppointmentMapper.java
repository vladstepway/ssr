package ru.croc.ugd.ssr.mq.listener.contractappointment;

import static java.util.Optional.ofNullable;
import static org.mapstruct.ReportingPolicy.IGNORE;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import ru.croc.ugd.ssr.contractappointment.Applicant;
import ru.croc.ugd.ssr.enums.ContractAppointmentSignType;
import ru.croc.ugd.ssr.model.contractappointment.ContractAppointmentDocument;
import ru.croc.ugd.ssr.model.integration.etpmv.BaseDeclarant;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateData;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateMessage;
import ru.croc.ugd.ssr.model.integration.etpmv.RequestContact;
import ru.croc.ugd.ssr.model.integration.etpmv.RequestServiceForSign;
import ru.croc.ugd.ssr.model.integration.etpmv.RequestServiceForSign.CustomAttributes;
import ru.croc.ugd.ssr.utils.PersonUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = IGNORE)
public interface EtpContractAppointmentMapper {

    default ContractAppointmentDocument toContractAppointmentDocument(final CoordinateMessage coordinateMessage) {
        final int signType = toSignType(coordinateMessage);

        final ContractAppointmentDocument contractAppointmentDocument = toContractAppointmentDocument(
            coordinateMessage, signType
        );

        final CustomAttributes customAttributes = ofNullable(coordinateMessage)
            .map(CoordinateMessage::getCoordinateDataMessage)
            .map(CoordinateData::getSignService)
            .map(RequestServiceForSign::getCustomAttributes)
            .orElse(null);

        if (signType == ContractAppointmentSignType.ELECTRONIC.getTypeCode()) {
            return toContractAppointmentDocumentBy086602Service(contractAppointmentDocument, customAttributes);
        } else {
            return toContractAppointmentDocumentBy086601Service(contractAppointmentDocument, customAttributes);
        }
    }

    @Mapping(
        target = "document.contractAppointmentData.applicant",
        source = "coordinateMessage.coordinateDataMessage.signService",
        qualifiedByName = "signServiceToApplicant"
    )
    @Mapping(
        target = "document.contractAppointmentData.eno",
        source = "coordinateMessage.coordinateDataMessage.service.serviceNumber"
    )
    @Mapping(
        target = "document.contractAppointmentData.applicationDateTime",
        source = "coordinateMessage.coordinateDataMessage.service.regDate",
        dateFormat = "yyyy-MM-dd HH:mm:ss"
    )
    @Mapping(
        target = "document.contractAppointmentData.statusId",
        expression = "java(ru.croc.ugd.ssr.dto.contractappointment.ContractAppointmentFlowStatus.REGISTERED.getId())"
    )
    @Mapping(target = "document.contractAppointmentData.cipId", ignore = true)
    @Mapping(target = "document.contractAppointmentData.addressTo", ignore = true)
    @Mapping(target = "document.contractAppointmentData.contractOrderId", ignore = true)
    @Mapping(target = "document.contractAppointmentData.bookingId", ignore = true)
    @Mapping(target = "document.contractAppointmentData.appointmentDateTime", ignore = true)
    @Mapping(target = "document.contractAppointmentData.signType", expression = "java( signType )")
    @Mapping(target = "document.contractAppointmentData.processInstanceId", ignore = true)
    @Mapping(target = "document.contractAppointmentData.cancelDate", ignore = true)
    @Mapping(target = "document.contractAppointmentData.cancelReason", ignore = true)
    ContractAppointmentDocument toContractAppointmentDocument(
        final CoordinateMessage coordinateMessage, @Context final int signType
    );

    @Mapping(
        target = "document.contractAppointmentData.cipId",
        source = "customAttributes.any",
        qualifiedByName = "customBy086601ServiceToCipId"
    )
    @Mapping(
        target = "document.contractAppointmentData.addressTo",
        source = "customAttributes.any",
        qualifiedByName = "customBy086601ServiceToAddressTo"
    )
    @Mapping(
        target = "document.contractAppointmentData.contractOrderId",
        source = "customAttributes.any",
        qualifiedByName = "customBy086601ServiceToContractOrderId"
    )
    @Mapping(
        target = "document.contractAppointmentData.bookingId",
        source = "customAttributes.any",
        qualifiedByName = "customBy086601ServiceToBookingId"
    )
    @Mapping(
        target = "document.contractAppointmentData.appointmentDateTime",
        source = "customAttributes.any",
        qualifiedByName = "customBy086601ServiceToAppointmentDateTime"
    )
    ContractAppointmentDocument toContractAppointmentDocumentBy086601Service(
        @MappingTarget final ContractAppointmentDocument contractAppointmentDocument,
        final CustomAttributes customAttributes
    );

    @Mapping(
        target = "document.contractAppointmentData.cipId",
        source = "customAttributes.any",
        qualifiedByName = "customBy086602ServiceToCipId"
    )
    @Mapping(
        target = "document.contractAppointmentData.addressTo",
        source = "customAttributes.any",
        qualifiedByName = "customBy086602ServiceToAddressTo"
    )
    @Mapping(
        target = "document.contractAppointmentData.contractOrderId",
        source = "customAttributes.any",
        qualifiedByName = "customBy086602ServiceToContractOrderId"
    )
    @Mapping(
        target = "document.contractAppointmentData.appointmentDateTime",
        source = "customAttributes.any",
        qualifiedByName = "customBy086602ServiceToAppointmentDateTime"
    )
    ContractAppointmentDocument toContractAppointmentDocumentBy086602Service(
        @MappingTarget final ContractAppointmentDocument contractAppointmentDocument,
        final CustomAttributes customAttributes
    );

    @Named("signServiceToApplicant")
    default Applicant signServiceToApplicant(final RequestServiceForSign signService, @Context final int signType) {
        final List<BaseDeclarant> baseDeclarants = signService.getContacts().getBaseDeclarant();
        if (baseDeclarants == null || baseDeclarants.size() == 0) {
            return null;
        }
        final RequestContact requestContact = (RequestContact) baseDeclarants.get(0);

        final Object customAttribute = signService.getCustomAttributes().getAny();

        String additionalPhone;
        String personDocumentId;
        if (signType == ContractAppointmentSignType.ELECTRONIC.getTypeCode()) {
            additionalPhone = customAttribute instanceof ru.mos.gu.service._086602.ServiceProperties
                ? ((ru.mos.gu.service._086602.ServiceProperties) customAttribute).getAdditionalPhone()
                : null;
            personDocumentId = customAttribute instanceof ru.mos.gu.service._086602.ServiceProperties
                ? ((ru.mos.gu.service._086602.ServiceProperties) customAttribute).getPersonDocumentId()
                : null;
        } else {
            additionalPhone = customAttribute instanceof ru.mos.gu.service._086601.ServiceProperties
                ? ((ru.mos.gu.service._086601.ServiceProperties) customAttribute).getAdditionalPhone()
                : null;
            personDocumentId = customAttribute instanceof ru.mos.gu.service._086601.ServiceProperties
                ? ((ru.mos.gu.service._086601.ServiceProperties) customAttribute).getPersonDocumentId()
                : null;
        }

        return requestContractToApplicant(requestContact, additionalPhone, personDocumentId);
    }

    @Mapping(target = "personDocumentId", source = "personDocumentId")
    @Mapping(target = "phone", source = "requestContact.mobilePhone")
    @Mapping(target = "email", source = "requestContact.EMail")
    @Mapping(target = "additionalPhone", source = "additionalPhone")
    @Mapping(target = "fio", source = "requestContact", qualifiedByName = "getFioFromRequestContact")
    Applicant requestContractToApplicant(
        final RequestContact requestContact, final String additionalPhone, final String personDocumentId
    );

    @Named("customBy086601ServiceToCipId")
    default String customBy086601ServiceToCipId(final Object customAttributes) {
        return ofNullable(customAttributes)
            .filter(attributes -> attributes instanceof ru.mos.gu.service._086601.ServiceProperties)
            .map(ru.mos.gu.service._086601.ServiceProperties.class::cast)
            .map(ru.mos.gu.service._086601.ServiceProperties::getCipId)
            .orElse(null);
    }

    @Named("customBy086602ServiceToCipId")
    default String customBy086602ServiceToCipId(final Object customAttributes) {
        return ofNullable(customAttributes)
            .filter(attributes -> attributes instanceof ru.mos.gu.service._086602.ServiceProperties)
            .map(ru.mos.gu.service._086602.ServiceProperties.class::cast)
            .map(ru.mos.gu.service._086602.ServiceProperties::getCipId)
            .orElse(null);
    }

    @Named("customBy086601ServiceToAddressTo")
    default String customBy086601ServiceToAddressTo(final Object customAttributes) {
        return ofNullable(customAttributes)
            .filter(attributes -> attributes instanceof ru.mos.gu.service._086601.ServiceProperties)
            .map(ru.mos.gu.service._086601.ServiceProperties.class::cast)
            .map(ru.mos.gu.service._086601.ServiceProperties::getAddress)
            .orElse(null);
    }

    @Named("customBy086602ServiceToAddressTo")
    default String customBy086602ServiceToAddressTo(final Object customAttributes) {
        return ofNullable(customAttributes)
            .filter(attributes -> attributes instanceof ru.mos.gu.service._086602.ServiceProperties)
            .map(ru.mos.gu.service._086602.ServiceProperties.class::cast)
            .map(ru.mos.gu.service._086602.ServiceProperties::getAddress)
            .orElse(null);
    }

    @Named("customBy086601ServiceToContractOrderId")
    default String customBy086601ServiceToContractOrderId(final Object customAttributes) {
        return ofNullable(customAttributes)
            .filter(attributes -> attributes instanceof ru.mos.gu.service._086601.ServiceProperties)
            .map(ru.mos.gu.service._086601.ServiceProperties.class::cast)
            .map(ru.mos.gu.service._086601.ServiceProperties::getContractOrderId)
            .orElse(null);
    }

    @Named("customBy086602ServiceToContractOrderId")
    default String customBy086602ServiceToContractOrderId(final Object customAttributes) {
        return ofNullable(customAttributes)
            .filter(attributes -> attributes instanceof ru.mos.gu.service._086602.ServiceProperties)
            .map(ru.mos.gu.service._086602.ServiceProperties.class::cast)
            .map(ru.mos.gu.service._086602.ServiceProperties::getContractOrderId)
            .orElse(null);
    }

    @Named("customBy086601ServiceToBookingId")
    default String customBy086601ServiceToBookingId(final Object customAttributes) {
        return ofNullable(customAttributes)
            .filter(attributes -> attributes instanceof ru.mos.gu.service._086601.ServiceProperties)
            .map(ru.mos.gu.service._086601.ServiceProperties.class::cast)
            .map(ru.mos.gu.service._086601.ServiceProperties::getBookingId)
            .orElse(null);
    }

    @Named("customBy086601ServiceToAppointmentDateTime")
    default LocalDateTime customBy086601ServiceToAppointmentDateTime(final Object customAttributes) {
        return ofNullable(customAttributes)
            .filter(attributes -> attributes instanceof ru.mos.gu.service._086601.ServiceProperties)
            .map(ru.mos.gu.service._086601.ServiceProperties.class::cast)
            .map(ru.mos.gu.service._086601.ServiceProperties::getAppointmentDateTime)
            .orElse(null);
    }

    @Named("customBy086602ServiceToAppointmentDateTime")
    default LocalDateTime customBy086602ServiceToAppointmentDateTime(final Object customAttributes) {
        return ofNullable(customAttributes)
            .filter(attributes -> attributes instanceof ru.mos.gu.service._086602.ServiceProperties)
            .map(ru.mos.gu.service._086602.ServiceProperties.class::cast)
            .map(ru.mos.gu.service._086602.ServiceProperties::getAppointmentDate)
            .map(LocalDate::atStartOfDay)
            .orElse(null);
    }

    @Named("getFioFromRequestContact")
    default String getFioFromRequestContact(final RequestContact requestContact) {
        return PersonUtils.getFullName(
            requestContact.getLastName(),
            requestContact.getFirstName(),
            requestContact.getMiddleName()
        );
    }

    default int toSignType(final CoordinateMessage coordinateMessage) {
        final ContractAppointmentSignType contractAppointmentSignType = ofNullable(coordinateMessage)
            .map(CoordinateMessage::getCoordinateDataMessage)
            .map(CoordinateData::getSignService)
            .map(RequestServiceForSign::getCustomAttributes)
            .map(CustomAttributes::getAny)
            .filter(attributes -> attributes instanceof ru.mos.gu.service._086602.ServiceProperties)
            .isPresent()
            ? ContractAppointmentSignType.ELECTRONIC
            : ContractAppointmentSignType.PERSONAL;

        return contractAppointmentSignType.getTypeCode();
    }
}
