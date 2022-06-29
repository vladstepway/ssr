package ru.croc.ugd.ssr.solr.converter.offerletterparsing;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import ru.croc.ugd.ssr.offerletterparsing.OfferLetterParsingType;
import ru.croc.ugd.ssr.solr.UgdSsrOfferLetterParsing;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface SolrOfferLetterParsingMapper {

    @Mapping(target = "sysDocumentId", ignore = true)
    @Mapping(target = "sysType", ignore = true)
    @Mapping(target = "sysAccessTypes", ignore = true)
    @Mapping(
        target = "ugdSsrOfferLetterParsingAddressFrom",
        source = "offerLetterParsing.addressFrom"
    )
    @Mapping(
        target = "ugdSsrOfferLetterParsingLetterId",
        source = "offerLetterParsing.letterId"
    )
    UgdSsrOfferLetterParsing toUgdSsrOfferLetterParsing(
        @MappingTarget final UgdSsrOfferLetterParsing ugdSsrOfferLetterParsing,
        final OfferLetterParsingType offerLetterParsing
    );
}
