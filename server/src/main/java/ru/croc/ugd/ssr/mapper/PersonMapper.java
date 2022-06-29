package ru.croc.ugd.ssr.mapper;

import static java.util.Optional.ofNullable;
import static org.mapstruct.ReportingPolicy.ERROR;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.db.projection.PersonBirthDateProjection;
import ru.croc.ugd.ssr.dto.NewFlatDto;
import ru.croc.ugd.ssr.dto.RestNotResettledPersonDto;
import ru.croc.ugd.ssr.dto.disabledperson.DisabledPersonWithFlatIdDto;
import ru.croc.ugd.ssr.dto.disabledperson.RestDisabledPersonDto;
import ru.croc.ugd.ssr.dto.flat.RestFlatLiverDto;
import ru.croc.ugd.ssr.dto.person.RestPersonBirthDateDto;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPAdministrativeDocumentType;
import ru.croc.ugd.ssr.utils.PersonUtils;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.function.Function;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ERROR)
public interface PersonMapper {

    @Mapping(target = "ccoUnom", source = "newFlat.unom", qualifiedByName = "mapUnomSafely")
    @Mapping(target = "ccoCadNum", source = "newFlat.cadnum")
    @Mapping(target = "ccoFlatNum", source = "newFlat.flatNumber")
    @Mapping(target = "letterId", source = "letterId")
    @Mapping(target = "ccoAddress", expression = "java( retrieveCcoAddress.apply(newFlat) )")
    @Mapping(target = "msgDateTime", ignore = true)
    @Mapping(target = "orderId", ignore = true)
    @Mapping(target = "event", ignore = true)
    @Mapping(target = "agrDate", ignore = true)
    @Mapping(target = "agrType", ignore = true)
    @Mapping(target = "agrNum", ignore = true)
    @Mapping(target = "courtDate", ignore = true)
    @Mapping(target = "signedAgrFile", ignore = true)
    @Mapping(target = "dataId", ignore = true)
    @Mapping(target = "entrance", ignore = true)
    @Mapping(target = "floor", ignore = true)
    @Mapping(target = "roomNum", ignore = true)
    @Mapping(target = "flatRelocationStatus", ignore = true)
    @Mapping(target = "isDgiData", ignore = true)
    PersonType.NewFlatInfo.NewFlat toNewFlat(
        @MappingTarget final PersonType.NewFlatInfo.NewFlat flatType,
        final SuperServiceDGPAdministrativeDocumentType.NewFlats.NewFlat newFlat,
        final String letterId,
        final Function<SuperServiceDGPAdministrativeDocumentType.NewFlats.NewFlat, String> retrieveCcoAddress
    );

    @Mapping(target = "ccoCadNum", source = "newFlatDto.ccoCadNum")
    @Mapping(target = "ccoAddress", source = "newFlatDto.ccoAddress")
    @Mapping(target = "ccoUnom", source = "newFlatDto.ccoUnom")
    @Mapping(target = "ccoFlatNum", source = "newFlatDto.ccoFlatNum")
    @Mapping(target = "letterId", source = "newFlatDto.letterId")
    @Mapping(target = "msgDateTime", ignore = true)
    @Mapping(target = "orderId", ignore = true)
    @Mapping(target = "event", ignore = true)
    @Mapping(target = "agrDate", ignore = true)
    @Mapping(target = "agrType", ignore = true)
    @Mapping(target = "agrNum", ignore = true)
    @Mapping(target = "courtDate", ignore = true)
    @Mapping(target = "signedAgrFile", ignore = true)
    @Mapping(target = "dataId", ignore = true)
    @Mapping(target = "entrance", ignore = true)
    @Mapping(target = "floor", ignore = true)
    @Mapping(target = "roomNum", ignore = true)
    @Mapping(target = "flatRelocationStatus", ignore = true)
    @Mapping(target = "isDgiData", ignore = true)
    PersonType.NewFlatInfo.NewFlat toNewFlat(final NewFlatDto newFlatDto);

    default PersonType.NewFlatInfo.NewFlat toNewFlat(
        final SuperServiceDGPAdministrativeDocumentType.NewFlats.NewFlat newFlat,
        final String letterId,
        final Function<SuperServiceDGPAdministrativeDocumentType.NewFlats.NewFlat, String> retrieveCcoAddress
    ) {
        return toNewFlat(new PersonType.NewFlatInfo.NewFlat(), newFlat, letterId, retrieveCcoAddress);
    }

    @Named("mapUnomSafely")
    default BigInteger mapUnomSafely(final String unom) {
        if (StringUtils.isNumeric(unom)) {
            return new BigInteger(unom);
        }
        return null;
    }

    @Mapping(target = "personDocumentId", source = "document.id")
    @Mapping(target = "fullName", source = "document.document.personData.FIO")
    @Mapping(target = "phone", source = "document.document.personData.phones", qualifiedByName = "toPhone")
    @Mapping(target = "birthDate", source = "document.document.personData.birthDate")
    @Mapping(target = "statusLivingCode", source = "document.document.personData.statusLiving")
    @Mapping(
        target = "statusLiving",
        source = "document.document.personData.statusLiving",
        qualifiedByName = "toStatusLiving"
    )
    @Mapping(target = "passport", source = "document.document.personData.passport")
    RestFlatLiverDto toRestFlatLiverDto(
        final PersonDocument document
    );

    @Named("toStatusLiving")
    default String toStatusLiving(final String statusLivingCode) {
        return PersonUtils.getStatusLiving(statusLivingCode);
    }

    @Named("toPhone")
    default String toPhone(final PersonType.Phones phones) {
        return PersonUtils.getBasePhoneNumber(phones);
    }

    @Mapping(target = "personDocumentId", source = "id")
    @Mapping(target = "fullName", source = "document.personData.FIO")
    @Mapping(target = "flatNumber", source = "document.personData.flatNum")
    @Mapping(target = "birthDate", source = "document.personData.birthDate")
    @Mapping(target = "relocationStatusCode", source = "document.personData.relocationStatus")
    @Mapping(
        target = "relocationStatusName",
        source = "document.personData.relocationStatus",
        qualifiedByName = "toRelocationStatusName"
    )
    RestNotResettledPersonDto toRestNotResettledPersonDto(
        final PersonDocument document, @Context final Function<String, String> retrieveRelocationStatusName
    );

    @Named("toRelocationStatusName")
    default String toRelocationStatusName(
        final String relocationStatusCode, @Context final Function<String, String> retrieveRelocationStatusName
    ) {
        return retrieveRelocationStatusName.apply(relocationStatusCode);
    }

    @Mapping(
        target = "document.personData.firstName",
        source = "disabledPersonWithFlatIdDto.disabledPerson.firstName"
    )
    @Mapping(
        target = "document.personData.lastName",
        source = "disabledPersonWithFlatIdDto.disabledPerson.lastName"
    )
    @Mapping(
        target = "document.personData.middleName",
        source = "disabledPersonWithFlatIdDto.disabledPerson.middleName"
    )
    @Mapping(
        target = "document.personData.FIO",
        source = "disabledPersonWithFlatIdDto.disabledPerson",
        qualifiedByName = "toFullName"
    )
    @Mapping(
        target = "document.personData.birthDate",
        source = "disabledPersonWithFlatIdDto.disabledPerson.birthDate"
    )
    @Mapping(
        target = "document.personData.SNILS",
        source = "disabledPersonWithFlatIdDto.disabledPerson.snils"
    )
    @Mapping(
        target = "document.personData.addInfo.isDisable",
        source = "disabledPersonWithFlatIdDto.disabledPerson.usingWheelchair"
    )
    @Mapping(
        target = "document.personData.UNOM",
        source = "disabledPersonWithFlatIdDto.disabledPerson.unom"
    )
    @Mapping(
        target = "document.personData.flatNum",
        source = "disabledPersonWithFlatIdDto.disabledPerson.flatNumber"
    )
    @Mapping(
        target = "document.personData.flatID",
        source = "disabledPersonWithFlatIdDto.flatId"
    )
    @Mapping(
        target = "document.personData.createdFromDisabledPerson",
        source = "createdFromDisabledPerson"
    )
    PersonDocument toPersonDocument(
        final DisabledPersonWithFlatIdDto disabledPersonWithFlatIdDto,
        final Boolean createdFromDisabledPerson
    );

    @Named("toFullName")
    default String toFullName(final RestDisabledPersonDto dto) {
        return PersonUtils.getFullName(
            dto.getLastName(),
            dto.getFirstName(),
            dto.getMiddleName()
        );
    }

    @Mapping(target = "birthDate", source = "birthDate", qualifiedByName = "toBirthDate")
    RestPersonBirthDateDto toRestPersonBirthDate(final PersonBirthDateProjection projection);

    @Named("toBirthDate")
    default LocalDate toBirthDate(final String birthDate) {
        return ofNullable(birthDate)
            .map(LocalDate::parse)
            .orElse(null);
    }
}
