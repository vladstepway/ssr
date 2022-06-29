package ru.croc.ugd.ssr.solr.converter.affaircollation;

import static org.mapstruct.ReportingPolicy.ERROR;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.croc.ugd.ssr.affaircollation.AffairCollationData;
import ru.croc.ugd.ssr.solr.UgdSsrAffairCollation;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ERROR)
public interface SolrAffairCollationMapper {

    @Mapping(target = "sysType", ignore = true)
    @Mapping(target = "sysAccessTypes", ignore = true)
    @Mapping(target = "sysDocumentId", ignore = true)
    @Mapping(
        target = "ugdSsrAffairCollationRequestDateTime",
        source = "requestDateTime"
    )
    @Mapping(
        target = "ugdSsrAffairCollationRealEstateUnom",
        source = "unom"
    )
    @Mapping(
        target = "ugdSsrAffairCollationFlatNumber",
        source = "flatNumber"
    )
    @Mapping(
        target = "ugdSsrAffairCollationAffairId",
        source = "affairId"
    )
    @Mapping(
        target = "ugdSsrAffairCollationResponseDateTime",
        source = "responseDateTime"
    )
    @Mapping(
        target = "ugdSsrAffairCollationResponseServiceNumbers",
        source = "responseServiceNumbers"
    )
    UgdSsrAffairCollation toUgdSsrAffairCollation(
        @MappingTarget final UgdSsrAffairCollation affairCollation, final AffairCollationData data
    );
}
