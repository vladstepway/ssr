package ru.croc.ugd.ssr.solr.converter.notary;

import static java.util.Optional.ofNullable;
import static org.mapstruct.ReportingPolicy.ERROR;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import ru.croc.ugd.ssr.notary.NotaryStatus;
import ru.croc.ugd.ssr.notary.NotaryType;
import ru.croc.ugd.ssr.solr.UgdSsrNotary;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ERROR)
public interface SolrNotaryMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(
        target = "ugdSsrNotaryFullName",
        source = "notaryType.fullName"
    )
    @Mapping(
        target = "ugdSsrNotaryOfficeAddress",
        source = "notaryType.address.address"
    )
    @Mapping(
        target = "ugdSsrNotaryAdditionalInfo",
        source = "notaryType.address.additionalInformation"
    )
    @Mapping(
        target = "ugdSsrNotaryPhone",
        source = "notaryType.phones.phone",
        qualifiedByName = "toPhone"
    )
    @Mapping(
        target = "ugdSsrNotaryStatus",
        source = "notaryType.status"
    )
    @Mapping(
        target = "ugdSsrNotaryEmployeeLogins",
        source = "notaryEmployeeLogins"
    )
    UgdSsrNotary toUgdSsrNotary(
        @MappingTarget final UgdSsrNotary ugdSsrNotary,
        final NotaryType notaryType,
        final List<String> notaryEmployeeLogins
    );

    @Named("toPhone")
    default String toPhone(final List<String> phones) {
        return String.join("\n", phones);
    }

    default String toNotaryStatusValue(final NotaryStatus notaryStatus) {
        return ofNullable(notaryStatus)
            .map(NotaryStatus::value)
            .orElse(null);
    }
}
