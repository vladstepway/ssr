package ru.croc.ugd.ssr.solr.converter.disabledperson;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import ru.croc.ugd.ssr.disabledperson.DisabledPersonData;
import ru.croc.ugd.ssr.enums.DistrictCode;
import ru.croc.ugd.ssr.solr.UgdSsrDisabledPerson;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface SolrDisabledPersonMapper {

    @Mapping(target = "sysType", ignore = true)
    @Mapping(target = "sysAccessTypes", ignore = true)
    @Mapping(target = "sysDocumentId", ignore = true)
    @Mapping(
        target = "ugdSsrDisabledPersonUnom",
        source = "disabledPersonData.unom"
    )
    @Mapping(
        target = "ugdSsrDisabledPersonArea",
        source = "disabledPersonData.area"
    )
    @Mapping(
        target = "ugdSsrDisabledPersonDistrictCode",
        source = "disabledPersonData.district",
        qualifiedByName = "toDistrictCode"
    )
    @Mapping(
        target = "ugdSsrDisabledPersonDistrict",
        source = "disabledPersonData.district"
    )
    @Mapping(
        target = "ugdSsrDisabledPersonAddressFrom",
        source = "disabledPersonData.addressFrom"
    )
    @Mapping(
        target = "ugdSsrDisabledPersonFlatNumber",
        source = "disabledPersonData.flatNumber"
    )
    @Mapping(
        target = "ugdSsrDisabledPersonFullName",
        source = "disabledPersonData.fullName"
    )
    @Mapping(
        target = "ugdSsrDisabledPersonSnils",
        source = "disabledPersonData.snils"
    )
    @Mapping(
        target = "ugdSsrDisabledPersonIsUsingWheelchair",
        source = "disabledPersonData.usingWheelchair"
    )
    @Mapping(
        target = "ugdSsrDisabledPersonFirstName",
        source = "disabledPersonData.firstName"
    )
    @Mapping(
        target = "ugdSsrDisabledPersonLastName",
        source = "disabledPersonData.lastName"
    )
    @Mapping(
        target = "ugdSsrDisabledPersonMiddleName",
        source = "disabledPersonData.middleName"
    )
    @Mapping(
        target = "ugdSsrDisabledPersonBirthDate",
        source = "disabledPersonData.birthDate"
    )
    @Mapping(
        target = "ugdSsrDisabledPersonIsDeleted",
        source = "disabledPersonData.deleted"
    )
    @Mapping(
        target = "ugdSsrDisabledPersonPersonDocumentId",
        source = "disabledPersonData.personDocumentId"
    )
    UgdSsrDisabledPerson toUgdSsrDisabledPerson(
        @MappingTarget final UgdSsrDisabledPerson ugdSsrDisabledPerson,
        final DisabledPersonData disabledPersonData
    );

    @Named("toDistrictCode")
    default String toDistrictCode(final String district) {
        return DistrictCode.ofName(district)
            .map(DistrictCode::getCode)
            .orElse(null);
    }
}
