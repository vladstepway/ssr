package ru.croc.ugd.ssr.solr.converter.personaldocument;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import ru.croc.ugd.ssr.personaldocument.PersonalDocumentType;
import ru.croc.ugd.ssr.solr.UgdSsrPersonalDocument;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface SolrPersonalDocumentMapper {

    @Mapping(target = "sysDocumentId", ignore = true)
    @Mapping(target = "sysType", ignore = true)
    @Mapping(target = "sysAccessTypes", ignore = true)
    @Mapping(
        target = "ugdSsrPersonalDocumentAddressFrom",
        source = "personalDocument.addressFrom"
    )
    @Mapping(
        target = "ugdSsrPersonalDocumentLetterId",
        source = "personalDocument.letterId"
    )
    UgdSsrPersonalDocument toUgdSsrPersonalDocument(
        @MappingTarget final UgdSsrPersonalDocument ugdSsrPersonalDocument,
        final PersonalDocumentType personalDocument
    );
}
