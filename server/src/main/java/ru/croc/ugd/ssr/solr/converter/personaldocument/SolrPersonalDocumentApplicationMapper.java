package ru.croc.ugd.ssr.solr.converter.personaldocument;

import org.apache.commons.lang.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import ru.croc.ugd.ssr.personaldocument.PersonalDocumentApplicationType;
import ru.croc.ugd.ssr.solr.UgdSsrPersonalDocumentApplication;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface SolrPersonalDocumentApplicationMapper {

    @Mapping(target = "sysDocumentId", ignore = true)
    @Mapping(target = "sysType", ignore = true)
    @Mapping(target = "sysAccessTypes", ignore = true)
    @Mapping(
        target = "ugdSsrPersonalDocumentApplicationEno",
        source = "personalDocumentApplication.eno"
    )
    @Mapping(
        target = "ugdSsrPersonalDocumentApplicationDateTime",
        source = "personalDocumentApplication.applicationDateTime"
    )
    @Mapping(
        target = "ugdSsrPersonalDocumentApplicationFullName",
        source = "personalDocumentApplication",
        qualifiedByName = "toApplicantFullName"
    )
    @Mapping(
        target = "ugdSsrPersonalDocumentApplicationAddressFrom",
        source = "personalDocumentApplication.addressFrom"
    )
    UgdSsrPersonalDocumentApplication toUgdSsrPersonalDocumentApplication(
        @MappingTarget final UgdSsrPersonalDocumentApplication ugdSsrPersonalDocumentApplication,
        final PersonalDocumentApplicationType personalDocumentApplication
    );

    @Named("toApplicantFullName")
    default String toApplicantFullName(final PersonalDocumentApplicationType personalDocumentApplication) {
        return Stream
            .of(personalDocumentApplication.getLastName(),
                personalDocumentApplication.getFirstName(),
                personalDocumentApplication.getMiddleName()
            )
            .filter(StringUtils::isNotBlank)
            .collect(Collectors.joining(" "));
    }
}
