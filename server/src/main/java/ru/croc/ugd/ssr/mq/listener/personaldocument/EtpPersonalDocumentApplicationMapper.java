package ru.croc.ugd.ssr.mq.listener.personaldocument;

import static java.util.Optional.ofNullable;
import static org.mapstruct.ReportingPolicy.ERROR;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateMessage;
import ru.croc.ugd.ssr.model.integration.etpmv.RequestContact;
import ru.croc.ugd.ssr.model.personaldocument.PersonalDocumentApplicationDocument;
import ru.mos.gu.service._088201.EtpTenant;
import ru.mos.gu.service._088201.ServiceProperties;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ERROR)
public interface EtpPersonalDocumentApplicationMapper {

    @Mapping(
        target = "document.personalDocumentApplicationData.eno",
        source = "coordinateMessage.coordinateDataMessage.service.serviceNumber"
    )
    @Mapping(
        target = "document.personalDocumentApplicationData.applicationDateTime",
        source = "coordinateMessage.coordinateDataMessage.service.regDate",
        dateFormat = "yyyy-MM-dd HH:mm:ss"
    )
    @Mapping(
        target = "document.personalDocumentApplicationData.personDocumentId",
        source = "applicantEtpTenant.personDocumentId"
    )
    @Mapping(
        target = "document.personalDocumentApplicationData.lastName",
        source = "applicantRequestContract.lastName"
    )
    @Mapping(
        target = "document.personalDocumentApplicationData.firstName",
        source = "applicantRequestContract.firstName"
    )
    @Mapping(
        target = "document.personalDocumentApplicationData.middleName",
        source = "applicantRequestContract.middleName"
    )
    @Mapping(
        target = "document.personalDocumentApplicationData.addressFrom",
        source = "coordinateMessage.coordinateDataMessage.signService.customAttributes.any",
        qualifiedByName = "customToAddressFrom"
    )
    @Mapping(
        target = "document.personalDocumentApplicationData.requestDocumentId",
        source = "coordinateMessage.coordinateDataMessage.signService.customAttributes.any",
        qualifiedByName = "customToRequestDocumentId"
    )
    PersonalDocumentApplicationDocument toPersonalDocumentApplicationDocument(
        final CoordinateMessage coordinateMessage,
        final RequestContact applicantRequestContract,
        final EtpTenant applicantEtpTenant
    );

    @Named("customToAddressFrom")
    default String customToAddressFrom(final Object customAttributes) {
        return ofNullable(customAttributes)
            .filter(attributes -> attributes instanceof ServiceProperties)
            .map(ServiceProperties.class::cast)
            .map(ServiceProperties::getAddressFrom)
            .orElse(null);
    }

    @Named("customToRequestDocumentId")
    default String customToRequestDocumentId(final Object customAttributes) {
        return ofNullable(customAttributes)
            .filter(attributes -> attributes instanceof ServiceProperties)
            .map(ServiceProperties.class::cast)
            .map(ServiceProperties::getRequestDocumentId)
            .orElse(null);
    }
}
