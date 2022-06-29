package ru.croc.ugd.ssr.solr.converter.guardianship;

import static java.util.Optional.ofNullable;
import static org.mapstruct.ReportingPolicy.ERROR;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import ru.croc.ugd.ssr.NsiType;
import ru.croc.ugd.ssr.dto.RealEstateDataAndFlatInfoDto;
import ru.croc.ugd.ssr.guardianship.GuardianshipRequestData;
import ru.croc.ugd.ssr.solr.UgdSsrGuardianship;
import ru.croc.ugd.ssr.utils.RealEstateUtils;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ERROR)
public interface SolrGuardianshipMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(
        target = "ugdSsrGuardianshipFio",
        source = "guardianshipRequest.fio"
    )
    @Mapping(
        target = "ugdSsrGuardianshipAddressTo",
        source = "guardianshipRequest.addressTo"
    )
    @Mapping(
        target = "ugdSsrGuardianshipArea",
        source = "realEstateDataAndFlatInfo.realEstateData.munOkrugP5.name"
    )
    @Mapping(
        target = "ugdSsrGuardianshipAddressFrom",
        source = "realEstateDataAndFlatInfo",
        qualifiedByName = "toAddressFrom"
    )
    UgdSsrGuardianship toUgdSsrGuardianship(
        @MappingTarget final UgdSsrGuardianship ugdSsrGuardianship,
        final GuardianshipRequestData guardianshipRequest,
        final RealEstateDataAndFlatInfoDto realEstateDataAndFlatInfo
    );

    @Named("toAddressFrom")
    default String toAddressFrom(final RealEstateDataAndFlatInfoDto realEstateDataAndFlatInfo) {
        return RealEstateUtils.getFlatAddress(realEstateDataAndFlatInfo);
    }

    @AfterMapping
    default UgdSsrGuardianship populateDistrictAndAddressFrom(
        @MappingTarget final UgdSsrGuardianship ugdSsrGuardianship,
        final RealEstateDataAndFlatInfoDto realEstateDataAndFlatInfo
    ) {
        ofNullable(realEstateDataAndFlatInfo)
            .map(RealEstateDataAndFlatInfoDto::getRealEstateData)
            .ifPresent(realEstate -> {
                final NsiType admArea = realEstate.getADMAREA();
                final NsiType district = realEstate.getDISTRICT();
                if (admArea != null && StringUtils.isNotBlank(admArea.getName())) {
                    ugdSsrGuardianship.setUgdSsrGuardianshipDistrict(admArea.getName());
                } else {
                    ugdSsrGuardianship.setUgdSsrGuardianshipDistrict(district.getName());
                }
            });

        return ugdSsrGuardianship;
    }
}
