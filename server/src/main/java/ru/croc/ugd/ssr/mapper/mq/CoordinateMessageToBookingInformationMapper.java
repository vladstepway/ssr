package ru.croc.ugd.ssr.mapper.mq;

import static org.mapstruct.ReportingPolicy.IGNORE;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.croc.ugd.ssr.dto.shipping.ApartmentFromDto;
import ru.croc.ugd.ssr.dto.shipping.ApartmentToDto;
import ru.croc.ugd.ssr.dto.shipping.BookingInformation;
import ru.croc.ugd.ssr.model.integration.etpmv.BaseDeclarant;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateMessage;
import ru.croc.ugd.ssr.model.integration.etpmv.RequestAccount;
import ru.croc.ugd.ssr.model.integration.etpmv.RequestContact;
import ru.croc.ugd.ssr.model.integration.shipping.ServiceProperties;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = IGNORE)
public interface CoordinateMessageToBookingInformationMapper {

    @Mapping(target = "applicant.ssoId", source = "coordinateDataMessage.signService.contacts.baseDeclarant",
            qualifiedByName = "extractSsoIdFromDeclarants")
    @Mapping(target = "applicant.snils", source = "coordinateDataMessage.signService.contacts.baseDeclarant",
            qualifiedByName = "extractSnilsFromDeclarants")
    @Mapping(target = "enoServiceNumber", source = "coordinateDataMessage.service.serviceNumber")
    @Mapping(target = "fromApartment", source = "coordinateDataMessage.signService.customAttributes.any",
            qualifiedByName = "extractFromApartmentFromCustom")
    @Mapping(target = "toApartment", source = "coordinateDataMessage.signService.customAttributes.any",
            qualifiedByName = "extractToApartmentFromCustom")
    BookingInformation toBookingInformation(final CoordinateMessage coordinateMessage);

    @Mapping(target = "uid", source = "bookingUid")
    ApartmentFromDto toApartmentFromDto(ServiceProperties serviceProperties);

    @Mapping(target = "uid", source = "bookingUid")
    ApartmentToDto toApartmentToDto(ServiceProperties serviceProperties);

    @Named("extractSsoIdFromDeclarants")
    default String extractSsoIdFromDeclarants(final List<BaseDeclarant> baseDeclarants) {
        if (baseDeclarants != null && baseDeclarants.size() > 0) {
            final BaseDeclarant baseDeclarant = baseDeclarants.get(0);
            if (baseDeclarant instanceof RequestAccount) {
                return ((RequestAccount) baseDeclarant).getSsoId();
            }
            if (baseDeclarant instanceof RequestContact) {
                return ((RequestContact) baseDeclarant).getSsoId();
            }
        }
        return null;
    }

    @Named("extractSnilsFromDeclarants")
    default String extractSnilsFromDeclarants(final List<BaseDeclarant> baseDeclarants) {
        if (baseDeclarants != null && baseDeclarants.size() > 0) {
            final BaseDeclarant baseDeclarant = baseDeclarants.get(0);
            if (baseDeclarant instanceof RequestContact) {
                return ((RequestContact) baseDeclarant).getSnils();
            }
        }
        return null;
    }

    @Named("extractFromApartmentFromCustom")
    default List<ApartmentFromDto> extractFromApartmentFromCustom(final Object servicePropertiesObject) {
        final List<ApartmentFromDto> apartmentFromDtos = new ArrayList<>();
        if (servicePropertiesObject != null
                && servicePropertiesObject instanceof ServiceProperties) {
            final ServiceProperties serviceProperties = (ServiceProperties) servicePropertiesObject;
            apartmentFromDtos.add(toApartmentFromDto(serviceProperties));
        }
        return apartmentFromDtos;
    }

    @Named("extractToApartmentFromCustom")
    default List<ApartmentToDto> extractToApartmentFromCustom(final Object servicePropertiesObject) {
        final List<ApartmentToDto> apartmentFromDtos = new ArrayList<>();
        if (servicePropertiesObject != null
                && servicePropertiesObject instanceof ServiceProperties) {
            final ServiceProperties serviceProperties = (ServiceProperties) servicePropertiesObject;
            apartmentFromDtos.add(toApartmentToDto(serviceProperties));
        }
        return apartmentFromDtos;
    }

}
