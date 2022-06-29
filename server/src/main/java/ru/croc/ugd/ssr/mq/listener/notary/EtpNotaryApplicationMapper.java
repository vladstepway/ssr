package ru.croc.ugd.ssr.mq.listener.notary;

import static java.util.Optional.ofNullable;
import static org.mapstruct.ReportingPolicy.IGNORE;

import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.util.StringUtils;
import ru.croc.ugd.ssr.model.integration.etpmv.BaseDeclarant;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateMessage;
import ru.croc.ugd.ssr.model.integration.etpmv.RequestContact;
import ru.croc.ugd.ssr.model.integration.etpmv.RequestServiceForSign;
import ru.croc.ugd.ssr.model.notary.NotaryApplicationDocument;
import ru.croc.ugd.ssr.notary.Apartment;
import ru.croc.ugd.ssr.notary.NotaryApplicationApplicant;
import ru.mos.gu.service._084001.EtpApartment;
import ru.mos.gu.service._084001.EtpNotary;
import ru.mos.gu.service._084001.ServiceProperties;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

@Mapper(componentModel = "spring", unmappedTargetPolicy = IGNORE)
public interface EtpNotaryApplicationMapper {

    @Mapping(
        target = "document.notaryApplicationData.applicant",
        source = "coordinateMessage.coordinateDataMessage.signService",
        qualifiedByName = "signServiceToApplicant"
    )
    @Mapping(
        target = "document.notaryApplicationData.eno",
        source = "coordinateMessage.coordinateDataMessage.service.serviceNumber"
    )
    @Mapping(
        target = "document.notaryApplicationData.affairId",
        source = "coordinateMessage.coordinateDataMessage.signService.customAttributes.any",
        qualifiedByName = "customToAffairId"
    )
    @Mapping(
        target = "document.notaryApplicationData.notaryId",
        source = "coordinateMessage.coordinateDataMessage.signService.customAttributes.any",
        qualifiedByName = "customToNotaryId"
    )
    @Mapping(
        target = "document.notaryApplicationData.applicantComment",
        source = "coordinateMessage.coordinateDataMessage.signService.customAttributes.any",
        qualifiedByName = "customToApplicantComment"
    )
    @Mapping(
        target = "document.notaryApplicationData.applicationDateTime",
        source = "coordinateMessage.coordinateDataMessage.service.regDate",
        dateFormat = "yyyy-MM-dd HH:mm:ss"
    )
    @Mapping(
        target = "document.notaryApplicationData.apartmentFrom",
        source = "coordinateMessage.coordinateDataMessage.signService.customAttributes.any",
        qualifiedByName = "getFromApartment"
    )
    @Mapping(
        target = "document.notaryApplicationData.apartmentTo.apartment",
        source = "coordinateMessage.coordinateDataMessage.signService.customAttributes.any",
        qualifiedByName = "getToApartment"
    )
    @Mapping(
        target = "document.notaryApplicationData.confirmPay",
        source = "coordinateMessage.coordinateDataMessage.signService.customAttributes.any",
        qualifiedByName = "customToConfirmPay"
    )
    @Mapping(
        target = "document.notaryApplicationData.confirmVisit",
        source = "coordinateMessage.coordinateDataMessage.signService.customAttributes.any",
        qualifiedByName = "customToConfirmVisit"
    )
    @Mapping(
        target = "document.notaryApplicationData.confirmOwners",
        source = "coordinateMessage.coordinateDataMessage.signService.customAttributes.any",
        qualifiedByName = "customToConfirmOwners"
    )
    @Mapping(
        target = "document.notaryApplicationData.confirmSign",
        source = "coordinateMessage.coordinateDataMessage.signService.customAttributes.any",
        qualifiedByName = "customToConfirmSign"
    )
    @Mapping(
        target = "document.notaryApplicationData.statusId",
        expression = "java(ru.croc.ugd.ssr.dto.notary.NotaryApplicationFlowStatus.ACCEPTED.getId())"
    )
    @Mapping(
        target = "document.notaryApplicationData.source",
        expression = "java( ru.croc.ugd.ssr.enums.NotaryApplicationSource.MPGU.value() )"
    )
    @Mapping(target = "document.notaryApplicationData.bookingId", ignore = true)
    @Mapping(target = "document.notaryApplicationData.appointmentDateTime", ignore = true)
    @Mapping(target = "document.notaryApplicationData.processInstanceId", ignore = true)
    @Mapping(target = "document.notaryApplicationData.history", ignore = true)
    NotaryApplicationDocument toNotaryApplicationDocument(
        final CoordinateMessage coordinateMessage,
        @Context final Function<String, String> addressResolver
    );

    @Named("signServiceToApplicant")
    default NotaryApplicationApplicant signServiceToApplicant(final RequestServiceForSign signService) {
        final List<BaseDeclarant> baseDeclarants = signService.getContacts().getBaseDeclarant();
        if (baseDeclarants == null || baseDeclarants.size() == 0) {
            return null;
        }
        final RequestContact requestContact = (RequestContact) baseDeclarants.get(0);

        final Object customAttribute = signService.getCustomAttributes().getAny();
        final String additionalPhone = customAttribute instanceof ServiceProperties
            ? ((ServiceProperties) customAttribute).getAdditionalPhone()
            : null;

        return requestContractToApplicant(requestContact, additionalPhone);
    }

    @Named("getFromApartment")
    default Apartment getFromApartment(
        final Object customAttributes, @Context final Function<String, String> addressResolver) {
        return ofNullable(customAttributes)
            .filter(attributes -> attributes instanceof ServiceProperties)
            .map(ServiceProperties.class::cast)
            .map(ServiceProperties::getApartmentFrom)
            .map(etpApartment -> toApartment(etpApartment, addressResolver))
            .orElse(null);
    }

    @Named("getToApartment")
    default List<Apartment> getToApartment(
        final Object customAttributes, @Context final Function<String, String> addressResolver
    ) {
        return ofNullable(customAttributes)
            .filter(attributes -> attributes instanceof ServiceProperties)
            .map(ServiceProperties.class::cast)
            .map(ServiceProperties::getApartmentTo)
            .map(etpApartment -> toApartment(etpApartment, addressResolver))
            .map(Collections::singletonList)
            .orElse(Collections.emptyList());
    }

    @Mapping(
        target = "address",
        expression = "java( getAddress (apartment.getAddress(), apartment.getUnom(), addressResolver) )"
    )
    @Mapping(target = "unom", source = "apartment.unom")
    @Mapping(target = "flatNumber", source = "apartment.flatNumber")
    @Mapping(target = "roomNum", source = "apartment.roomNum")
    Apartment toApartment(final EtpApartment apartment, final Function<String, String> addressResolver);

    @Mapping(target = "phone", source = "requestContact.mobilePhone")
    @Mapping(target = "email", source = "requestContact.EMail")
    @Mapping(target = "ssoId", source = "requestContact.ssoId")
    @Mapping(target = "snils", source = "requestContact.snils")
    @Mapping(target = "additionalPhone", source = "additionalPhone")
    @Mapping(target = "lastName", source = "requestContact.lastName")
    @Mapping(target = "firstName", source = "requestContact.firstName")
    @Mapping(target = "middleName", source = "requestContact.middleName")
    NotaryApplicationApplicant requestContractToApplicant(
        final RequestContact requestContact, final String additionalPhone
    );

    @AfterMapping
    default NotaryApplicationApplicant populatePersonFullName(
        @MappingTarget final NotaryApplicationApplicant applicant
    ) {
        final String fullName = String.join(" ",
            applicant.getLastName(),
            applicant.getFirstName(),
            applicant.getMiddleName()
        );

        applicant.setFullName(fullName);

        return applicant;
    }

    @Named("customToAffairId")
    default String customToAffairId(final Object customAttributes) {
        return customAttributes instanceof ServiceProperties
            ? ((ServiceProperties) customAttributes).getAffairId()
            : null;
    }

    @Named("customToNotaryId")
    default String customToNotaryId(final Object customAttributes) {
        return ofNullable(customAttributes)
            .filter(attributes -> attributes instanceof ServiceProperties)
            .map(ServiceProperties.class::cast)
            .map(ServiceProperties::getNotary)
            .map(EtpNotary::getId)
            .orElse(null);
    }

    @Named("customToApplicantComment")
    default String customToApplicantComment(final Object customAttributes) {
        return customAttributes instanceof ServiceProperties
            ? ((ServiceProperties) customAttributes).getSignHelpers()
            : null;
    }

    @Named("customToConfirmPay")
    default boolean customToConfirmPay(final Object customAttributes) {
        return customAttributes instanceof ServiceProperties
            && ((ServiceProperties) customAttributes).isPaymentConfirm();
    }

    @Named("customToConfirmVisit")
    default boolean customToConfirmVisit(final Object customAttributes) {
        return customAttributes instanceof ServiceProperties
            && ((ServiceProperties) customAttributes).isBookConfirm();
    }

    @Named("customToConfirmOwners")
    default boolean customToConfirmOwners(final Object customAttributes) {
        return customAttributes instanceof ServiceProperties
            && ((ServiceProperties) customAttributes).isOwnersConfirm();
    }

    @Named("customToConfirmSign")
    default boolean customToConfirmSign(final Object customAttributes) {
        return customAttributes instanceof ServiceProperties
            && ((ServiceProperties) customAttributes).isSignConfirm();
    }

    default String getAddress(final String address, final String unom, final Function<String, String> addressResolver) {
        return ofNullable(address)
            .filter(StringUtils::hasText)
            .orElseGet(() -> ofNullable(unom)
                .filter(unomValue -> Objects.nonNull(addressResolver))
                .map(addressResolver)
                .orElse(null)
            );
    }
}
