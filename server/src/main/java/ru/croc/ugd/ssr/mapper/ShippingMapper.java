package ru.croc.ugd.ssr.mapper;

import static java.util.Optional.ofNullable;
import static org.mapstruct.ReportingPolicy.IGNORE;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.croc.ugd.ssr.dto.shipping.ApartmentFromDto;
import ru.croc.ugd.ssr.dto.shipping.ApartmentToDto;
import ru.croc.ugd.ssr.dto.shipping.BookingInformation;
import ru.croc.ugd.ssr.dto.shipping.CheckResponseApplicantDto;
import ru.croc.ugd.ssr.dto.shipping.MoveShippingDateRequestDto;
import ru.croc.ugd.ssr.dto.shipping.ValidateShippingDateRequestDto;
import ru.croc.ugd.ssr.dto.shipping.ValidateShippingDateResponseDto;
import ru.croc.ugd.ssr.generated.dto.RestCheckResponseApplicantDto;
import ru.croc.ugd.ssr.generated.dto.RestCheckResponseDto;
import ru.croc.ugd.ssr.generated.dto.RestCopingDaySchedulesRequestDto;
import ru.croc.ugd.ssr.generated.dto.RestDayOrIntervalValidationResponseDto;
import ru.croc.ugd.ssr.generated.dto.RestMoveShippingDateRequestDto;
import ru.croc.ugd.ssr.model.ShippingApplicationDocument;
import ru.croc.ugd.ssr.model.integration.etpmv.BaseDeclarant;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateMessage;
import ru.croc.ugd.ssr.model.integration.etpmv.RequestContact;
import ru.croc.ugd.ssr.model.integration.etpmv.RequestServiceForSign;
import ru.croc.ugd.ssr.model.integration.shipping.Address;
import ru.croc.ugd.ssr.model.integration.shipping.ServiceProperties;
import ru.croc.ugd.ssr.shipping.Apartment;
import ru.croc.ugd.ssr.shipping.ShippingApplicant;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = IGNORE, uses = {ApartmentMapper.class})
public interface ShippingMapper {

    @Mapping(target = "ISPOSSBLE", source = "isPossible")
    @Mapping(target = "REASON", source = "reason")
    @Mapping(target = "from", source = "fromApartment")
    @Mapping(target = "to", source = "toApartment")
    RestCheckResponseDto toRestCheckResponseDto(BookingInformation bookingInformation);

    @Mapping(target = "SSOID", source = "ssoId")
    @Mapping(target = "SNILS", source = "snils")
    @Mapping(target = "LASTNAME", source = "lastName")
    @Mapping(target = "FIRSTNAME", source = "firstName")
    @Mapping(target = "MIDDLENAME", source = "middleName")
    @Mapping(target = "DOB", source = "dob")
    @Mapping(target = "RIGHTTYPE", source = "rightType")
    @Mapping(target = "ISDISABLE", source = "isDisable")
    RestCheckResponseApplicantDto toRestCheckResponseApplicantDto(CheckResponseApplicantDto checkResponseApplicantDto);

    @Mapping(
        target = "document.shippingApplicationData.applicant",
        source = "coordinateDataMessage.signService",
        qualifiedByName = "signServiceToApplicant"
    )
    @Mapping(
        target = "document.shippingApplicationData.eno",
        source = "coordinateDataMessage.service.serviceNumber"
    )
    @Mapping(
        target = "document.shippingApplicationData.apartmentFrom",
        source = "coordinateDataMessage.signService.customAttributes.any",
        qualifiedByName = "customToApartmentFrom"
    )
    @Mapping(
        target = "document.shippingApplicationData.apartmentTo",
        source = "coordinateDataMessage.signService.customAttributes.any",
        qualifiedByName = "customToApartmentTo"
    )
    @Mapping(
        target = "document.shippingApplicationData.additionalInfo",
        source = "coordinateDataMessage.signService.customAttributes.any",
        qualifiedByName = "customToAdditionalInfo"
    )
    @Mapping(
        target = "document.shippingApplicationData.bookingId",
        source = "coordinateDataMessage.signService.customAttributes.any",
        qualifiedByName = "customToBookingId"
    )
    @Mapping(
        target = "document.shippingApplicationData.shippingDateEnd",
        source = "coordinateDataMessage.signService.customAttributes.any",
        qualifiedByName = "customToShippingDateEnd"
    )
    @Mapping(
        target = "document.shippingApplicationData.shippingDateStart",
        source = "coordinateDataMessage.signService.customAttributes.any",
        qualifiedByName = "customToShippingDateStart"
    )
    @Mapping(
        target = "document.shippingApplicationData.source",
        expression = "java( ru.croc.ugd.ssr.enums.ShippingApplicationSource.MPGU.value() )"
    )
    ShippingApplicationDocument toShippingApplicationDocument(final CoordinateMessage coordinateMessage);

    @Named("signServiceToApplicant")
    default ShippingApplicant signServiceToApplicant(final RequestServiceForSign signService) {
        final List<BaseDeclarant> baseDeclarants = signService.getContacts().getBaseDeclarant();
        if (baseDeclarants == null || baseDeclarants.size() == 0) {
            return null;
        }
        final RequestContact requestContact = (RequestContact) baseDeclarants.get(0);

        final Object customAttribute = signService.getCustomAttributes().getAny();
        final String additionalPhone = customAttribute instanceof ServiceProperties
                ? ((ServiceProperties) customAttribute).getExtPhone()
                : null;

        final ShippingApplicant shippingApplicant = requestContractToShippingApplicant(requestContact);
        shippingApplicant.setAdditionalPhone(additionalPhone);
        return shippingApplicant;
    }

    @Mapping(target = "phone", source = "mobilePhone")
    @Mapping(target = "email", source = "EMail")
    @Mapping(target = "fullName", source = "requestContact", qualifiedByName = "toFullName")
    ShippingApplicant requestContractToShippingApplicant(final RequestContact requestContact);

    @Named("toFullName")
    default String toFullName(final RequestContact requestContact) {
        final String lastName = ofNullable(requestContact.getLastName()).orElse("");
        final String firstName = ofNullable(requestContact.getFirstName()).orElse("");
        final String middleName = ofNullable(requestContact.getMiddleName()).orElse("");

        return lastName + " " + firstName + " " + middleName;
    }

    @Named("customToApartmentFrom")
    default Apartment customToApartmentFrom(final Object serviceProperties) {
        return serviceProperties instanceof ServiceProperties
            ? addressToApartment(((ServiceProperties) serviceProperties).getAddressOld())
            : null;
    }

    @Named("customToApartmentTo")
    default Apartment customToApartmentTo(final Object serviceProperties) {
        return serviceProperties instanceof ServiceProperties
            ? addressToApartment(((ServiceProperties) serviceProperties).getAddressNew())
            : null;
    }

    @Mapping(target = "stringAddress", source = "text")
    @Mapping(target = "rooms", source = "room")
    Apartment addressToApartment(final Address address);

    @Named("customToAdditionalInfo")
    default String customToAdditionalInfo(final Object serviceProperties) {
        return serviceProperties instanceof ServiceProperties
            ? ((ServiceProperties) serviceProperties).getNotes()
            : null;
    }

    @Named("customToBookingId")
    default String customToBookingId(final Object serviceProperties) {
        return serviceProperties instanceof ServiceProperties
            ? ((ServiceProperties) serviceProperties).getBookingUid()
            : null;
    }

    @Named("customToShippingDateEnd")
    default LocalDateTime customToShippingDateEnd(final Object serviceProperties) {
        return serviceProperties instanceof ServiceProperties
            ? ((ServiceProperties) serviceProperties).getRemovalDateTimeEnd()
            : null;
    }

    @Named("customToShippingDateStart")
    default LocalDateTime customToShippingDateStart(final Object serviceProperties) {
        return serviceProperties instanceof ServiceProperties
            ? ((ServiceProperties) serviceProperties).getRemovalDateTimeStart()
            : null;
    }

    @Mapping(target = "enoServiceNumber", source = "document.shippingApplicationData.eno")
    @Mapping(target = "applicant.ssoId", source = "document.shippingApplicationData.applicant.ssoId")
    @Mapping(target = "applicant.snils", source = "document.shippingApplicationData.applicant.snils")
    @Mapping(
        target = "fromApartment",
        source = "document.shippingApplicationData.apartmentFrom",
        qualifiedByName = "toApartmentFromDtoList"
    )
    @Mapping(
        target = "toApartment",
        source = "document.shippingApplicationData.apartmentTo",
        qualifiedByName = "toApartmentToDtoList"
    )
    BookingInformation toBookingInformation(final ShippingApplicationDocument document);

    @Named("toApartmentFromDtoList")
    default List<ApartmentFromDto> toApartmentFromDtoList(final Apartment apartment) {
        return Collections.singletonList(toApartmentFromDto(apartment));
    }

    @Mapping(target = "address", source = "stringAddress")
    @Mapping(target = "unomOld", source = "unom")
    @Mapping(target = "roomNumberOld", source = "roomNumber")
    @Mapping(target = "floorOld", source = "floor")
    @Mapping(target = "entranceOld", source = "entrance")
    @Mapping(target = "roomNumber", ignore = true)
    ApartmentFromDto toApartmentFromDto(final Apartment apartment);

    @Named("toApartmentToDtoList")
    default List<ApartmentToDto> toApartmentToDtoList(final Apartment apartment) {
        return Collections.singletonList(toApartmentToDto(apartment));
    }

    @Mapping(target = "address", source = "stringAddress")
    @Mapping(target = "unomNew", source = "unom")
    @Mapping(target = "roomNumberNew", source = "roomNumber")
    @Mapping(target = "floorNew", source = "floor")
    @Mapping(target = "entranceNew", source = "entrance")
    ApartmentToDto toApartmentToDto(final Apartment apartment);

    MoveShippingDateRequestDto toMoveShippingDateRequestDto(
        final RestMoveShippingDateRequestDto restMoveShippingDateRequestDto
    );

    ValidateShippingDateRequestDto toValidateShippingDateRequestDto(
        final RestCopingDaySchedulesRequestDto restCopingDaySchedulesRequestDto
    );

    RestDayOrIntervalValidationResponseDto toRestDayOrIntervalValidationResponseDto(
        final ValidateShippingDateResponseDto validateShippingDateResponseDto
    );
}
