package ru.croc.ugd.ssr.integration.service.mapper;

import static org.mapstruct.ReportingPolicy.IGNORE;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.croc.ugd.ssr.FlatType;
import ru.croc.ugd.ssr.NsiType;
import ru.croc.ugd.ssr.integration.service.wsdl.flat.model.FlatDetails;

import java.math.BigInteger;
import java.util.UUID;

@Mapper(componentModel = "spring", unmappedTargetPolicy = IGNORE)
public interface FlatDetailsMapper {
    String FLAT_APARTMENT_TYPE = "Квартира";
    String FLAT_TYPE_SEPARATE = "Отдельная";

    @BeanMapping(nullValuePropertyMappingStrategy =  NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "roomsCount", source = "amountOfLivingRooms", qualifiedByName = "mapBigIntegerSafely")
    @Mapping(target = "flatNumber", source = "flatNumber")
    @Mapping(target = "apartmentL4VALUE", source = "flatNumber")
    @Mapping(target = "entrance", source = "sectionNumber")
    @Mapping(target = "floor", source = "floor", qualifiedByName = "mapIntegerSafely")
    @Mapping(target = "totalSquare", source = "fullSquare", qualifiedByName = "mapFloatSafely")
    @Mapping(target = "SAll", source = "fullSquare", qualifiedByName = "mapFloatSafely")
    @Mapping(target = "SAlllet", source = "calculatedSquare", qualifiedByName = "mapFloatSafely")
    @Mapping(target = "SGil", source = "livingSquare", qualifiedByName = "mapFloatSafely")
    FlatType toFlatType(
            @MappingTarget final FlatType flatType,
            final FlatDetails flatDetails
    );

    default FlatType toFlatTypeWithDefaultValue(
            final FlatType flatType,
            final FlatDetails flatDetails
    ) {
        final FlatType mapped = toFlatType(flatType, flatDetails);
        if (mapped == null) {
            return null;
        }
        if (mapped.getApartmentL4TYPE() == null) {
            final NsiType nsiType = new NsiType();
            nsiType.setName(FLAT_APARTMENT_TYPE);
            mapped.setApartmentL4TYPE(nsiType);
        }
        if (StringUtils.isEmpty(mapped.getApartmentL4TYPE().getName())) {
            mapped.getApartmentL4TYPE().setName(FLAT_APARTMENT_TYPE);
        }
        if (StringUtils.isEmpty(mapped.getFlatType())) {
            mapped.setFlatType(FLAT_TYPE_SEPARATE);
        }
        if (StringUtils.isEmpty(mapped.getFlatID())) {
            mapped.setFlatID(UUID.randomUUID().toString());
        }
        return mapped;
    }


    default FlatType toFlatTypeWithDefaultValue(final FlatDetails flatDetails) {
        return toFlatTypeWithDefaultValue(new FlatType(), flatDetails);
    }

    @Named("mapBigIntegerSafely")
    default BigInteger mapBigIntegerSafely(final String stringNumber) {
        try {
            return new BigInteger(stringNumber);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    @Named("mapIntegerSafely")
    default Integer mapIntegerSafely(final String stringNumber) {
        try {
            return new Integer(stringNumber);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    @Named("mapFloatSafely")
    default Float mapFloatSafely(final String stringNumber) {
        try {
            return new Float(StringUtils.replaceAll(stringNumber, ",", "."));
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
