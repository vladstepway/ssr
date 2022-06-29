package ru.croc.ugd.ssr.mq.listener.flatappointment;

import static java.util.Optional.ofNullable;
import static org.mapstruct.ReportingPolicy.IGNORE;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.croc.ugd.ssr.flatappointment.Applicant;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.flatAppointment.FlatAppointmentDocument;
import ru.croc.ugd.ssr.model.integration.etpmv.BaseDeclarant;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateMessage;
import ru.croc.ugd.ssr.model.integration.etpmv.RequestContact;
import ru.croc.ugd.ssr.model.integration.etpmv.RequestServiceForSign;
import ru.croc.ugd.ssr.utils.PersonUtils;
import ru.mos.gu.service._084901.ServiceProperties;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

@Mapper(componentModel = "spring", unmappedTargetPolicy = IGNORE)
public interface EtpFlatAppointmentMapper {

    @Mapping(
        target = "document.flatAppointmentData.applicant",
        source = "coordinateMessage.coordinateDataMessage.signService",
        qualifiedByName = "signServiceToApplicant"
    )
    @Mapping(
        target = "document.flatAppointmentData.eno",
        source = "coordinateMessage.coordinateDataMessage.service.serviceNumber"
    )
    @Mapping(
        target = "document.flatAppointmentData.applicationDateTime",
        source = "coordinateMessage.coordinateDataMessage.service.regDate",
        dateFormat = "yyyy-MM-dd HH:mm:ss"
    )
    @Mapping(
        target = "document.flatAppointmentData.statusId",
        expression = "java(ru.croc.ugd.ssr.dto.flatappointment.FlatAppointmentFlowStatus.REGISTERED.getId())"
    )
    @Mapping(
        target = "document.flatAppointmentData.cipId",
        source = "coordinateMessage.coordinateDataMessage.signService.customAttributes.any",
        qualifiedByName = "customToCipId"
    )
    @Mapping(
        target = "document.flatAppointmentData.offerLetterId",
        source = "coordinateMessage.coordinateDataMessage.signService.customAttributes.any",
        qualifiedByName = "customToOfferLetterId"
    )
    @Mapping(
        target = "document.flatAppointmentData.offerLetterFileLink",
        source = "coordinateMessage.coordinateDataMessage.signService.customAttributes.any",
        qualifiedByName = "customToOfferLetterFileLink"
    )
    @Mapping(
        target = "document.flatAppointmentData.bookingId",
        source = "coordinateMessage.coordinateDataMessage.signService.customAttributes.any",
        qualifiedByName = "customToBookingId"
    )
    @Mapping(
        target = "document.flatAppointmentData.appointmentDateTime",
        source = "coordinateMessage.coordinateDataMessage.signService.customAttributes.any",
        qualifiedByName = "customToAppointmentDateTime"
    )
    FlatAppointmentDocument toFlatAppointmentDocument(
        final CoordinateMessage coordinateMessage,
        @Context final Function<String, PersonDocument> retrievePersonDocument
    );

    @Named("signServiceToApplicant")
    default Applicant signServiceToApplicant(final RequestServiceForSign signService) {
        final List<BaseDeclarant> baseDeclarants = signService.getContacts().getBaseDeclarant();
        if (baseDeclarants == null || baseDeclarants.size() == 0) {
            return null;
        }
        final RequestContact requestContact = (RequestContact) baseDeclarants.get(0);

        final Object customAttribute = signService.getCustomAttributes().getAny();
        final String additionalPhone = customAttribute instanceof ServiceProperties
            ? ((ServiceProperties) customAttribute).getAdditionalPhone()
            : null;
        final String personDocumentId = customAttribute instanceof ServiceProperties
            ? ((ServiceProperties) customAttribute).getPersonDocumentId()
            : null;

        return requestFlatToApplicant(requestContact, additionalPhone, personDocumentId);
    }

    @Mapping(target = "personDocumentId", source = "personDocumentId")
    @Mapping(target = "phone", source = "requestContact.mobilePhone")
    @Mapping(target = "email", source = "requestContact.EMail")
    @Mapping(target = "additionalPhone", source = "additionalPhone")
    Applicant requestFlatToApplicant(
        final RequestContact requestContact, final String additionalPhone, final String personDocumentId
    );

    @Named("customToCipId")
    default String customToCipId(final Object customAttributes) {
        return ofNullable(customAttributes)
            .filter(attributes -> attributes instanceof ServiceProperties)
            .map(ServiceProperties.class::cast)
            .map(ServiceProperties::getCipId)
            .orElse(null);
    }

    @Named("customToOfferLetterId")
    default String customToOfferLetterId(final Object customAttributes) {
        return ofNullable(customAttributes)
            .filter(attributes -> attributes instanceof ServiceProperties)
            .map(ServiceProperties.class::cast)
            .map(ServiceProperties::getOfferLetterId)
            .orElse(null);
    }

    @Named("customToOfferLetterFileLink")
    default String customToOfferLetterFileLink(
        final Object customAttributes, @Context final Function<String, PersonDocument> retrievePersonDocument
    ) {
        final String letterId = customToOfferLetterId(customAttributes);

        return ofNullable(customAttributes)
            .filter(attributes -> attributes instanceof ServiceProperties)
            .map(ServiceProperties.class::cast)
            .map(ServiceProperties::getPersonDocumentId)
            .map(retrievePersonDocument)
            .flatMap(personDocument -> PersonUtils.getOfferLetter(personDocument, letterId))
            .flatMap(PersonUtils::getOfferLetterFileLink)
            .orElse(null);
    }

    @Named("customToBookingId")
    default String customToBookingId(final Object customAttributes) {
        return ofNullable(customAttributes)
            .filter(attributes -> attributes instanceof ServiceProperties)
            .map(ServiceProperties.class::cast)
            .map(ServiceProperties::getBookingId)
            .orElse(null);
    }

    @Named("customToAppointmentDateTime")
    default LocalDateTime customToAppointmentDateTime(final Object customAttributes) {
        return ofNullable(customAttributes)
            .filter(attributes -> attributes instanceof ServiceProperties)
            .map(ServiceProperties.class::cast)
            .map(ServiceProperties::getAppointmentDateTime)
            .orElse(null);
    }
}
