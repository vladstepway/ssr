package ru.croc.ugd.ssr.solr.converter.apartmentdefectconfirmation;

import static org.mapstruct.ReportingPolicy.ERROR;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.croc.ugd.ssr.ApartmentDefectConfirmationData;
import ru.croc.ugd.ssr.solr.UgdSsrApartmentDefectConfirmation;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ERROR)
public interface SolrApartmentDefectConfirmationMapper {
    @BeanMapping(ignoreByDefault = true)
    @Mapping(
        target = "ugdSsrApartmentDefectConfirmationAddress",
        source = "apartmentDefectConfirmationData.ccoData.address"
    )
    UgdSsrApartmentDefectConfirmation toUgdSsrApartmentDefectConfirmation(
        @MappingTarget final UgdSsrApartmentDefectConfirmation ugdSsrApartmentDefectConfirmation,
        final ApartmentDefectConfirmationData apartmentDefectConfirmationData
    );
}
