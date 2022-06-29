package ru.croc.ugd.ssr.utils;

import static java.util.Optional.ofNullable;

import org.apache.commons.lang.StringUtils;
import ru.croc.ugd.ssr.FlatType;
import ru.croc.ugd.ssr.NsiType;
import ru.croc.ugd.ssr.RealEstate;
import ru.croc.ugd.ssr.RealEstateDataType;
import ru.croc.ugd.ssr.dto.RealEstateDataAndFlatInfoDto;
import ru.croc.ugd.ssr.model.RealEstateDocument;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;


/**
 * Real estate utils.
 */
public class RealEstateUtils {

    /**
     * get address from real estate.
     *
     * @param realEstate realEstate
     * @return address
     */
    public static String getRealEstateAddress(final RealEstateDataType realEstate) {
        return ofNullable(realEstate)
            .map(RealEstateUtils::retrieveRealEstateAddress)
            .orElse(null);
    }

    /**
     * get address from real estate.
     *
     * @param realEstateDataAndFlatInfoDto realEstateDataAndFlatInfoDto
     * @return address
     */
    public static String getRealEstateAddress(final RealEstateDataAndFlatInfoDto realEstateDataAndFlatInfoDto) {
        return ofNullable(realEstateDataAndFlatInfoDto)
            .map(RealEstateDataAndFlatInfoDto::getRealEstateData)
            .map(RealEstateUtils::retrieveRealEstateAddress)
            .orElse(null);
    }

    /**
     * get full address from real estate and flat.
     *
     * @param realEstateDataAndFlatInfoDto realEstate and flat
     * @return address
     */
    public static String getFlatAddress(final RealEstateDataAndFlatInfoDto realEstateDataAndFlatInfoDto) {
        return ofNullable(realEstateDataAndFlatInfoDto)
            .map(info -> getFlatAddress(info.getRealEstateData(), info.getFlat()))
            .orElse(null);
    }

    /**
     * get full address from real estate and flat.
     *
     * @param realEstate realEstate
     * @return address
     */
    public static String getFlatAddress(final RealEstateDataType realEstate, final FlatType flat) {
        return ofNullable(realEstate)
            .map(RealEstateUtils::getRealEstateAddress)
            .map(address -> address.concat(
                ofNullable(flat)
                    .map(FlatType::getApartmentL4VALUE)
                    .map(flatNumber -> ", квартира " + flatNumber)
                    .orElse("")
            ))
            .orElse(
                ofNullable(flat)
                    .map(FlatType::getAddress)
                    .orElse(null)
            );
    }

    /**
     * get flat from real estate by flatId.
     *
     * @param realEstate realEstate
     * @param flatId flatId
     * @return flat
     */
    public static FlatType getFlatByFlatId(final RealEstateDataType realEstate, final String flatId) {
        if (StringUtils.isBlank(flatId)) {
            return null;
        }

        return ofNullable(realEstate)
            .map(RealEstateDataType::getFlats)
            .map(RealEstateDataType.Flats::getFlat)
            .orElse(Collections.emptyList())
            .stream()
            .filter(flat -> flatId.equals(flat.getFlatID()))
            .findFirst()
            .orElse(null);
    }

    /**
     * Get flat from real estate by flat number.
     *
     * @param realEstateDocument realEstateDocument
     * @param flatNumber flatNumber
     * @return flat
     */
    public static Optional<FlatType> getFlatByFlatNumber(
        final RealEstateDocument realEstateDocument, final String flatNumber
    ) {
        if (StringUtils.isBlank(flatNumber)) {
            return Optional.empty();
        }

        return ofNullable(realEstateDocument)
            .map(RealEstateDocument::getDocument)
            .map(RealEstate::getRealEstateData)
            .map(RealEstateDataType::getFlats)
            .map(RealEstateDataType.Flats::getFlat)
            .orElse(Collections.emptyList())
            .stream()
            .filter(flatType -> StringUtils.equalsIgnoreCase(flatType.getFlatNumber(), flatNumber)
                || StringUtils.equalsIgnoreCase(flatType.getApartmentL4VALUE(), flatNumber))
            .findFirst();
    }

    /**
     * Находит квартиру по flatId в указанном доме.
     *
     * @param flatId id квартиры
     * @param realEstateDocument дом
     * @return квартира
     */
    public static FlatType findFlat(String flatId, RealEstateDocument realEstateDocument) {
        if (StringUtils.isBlank(flatId)
            || !ofNullable(realEstateDocument)
            .map(RealEstateDocument::getDocument)
            .map(RealEstate::getRealEstateData)
            .map(RealEstateDataType::getFlats).isPresent()) {
            return null;
        }
        Optional<FlatType> optFlatType = realEstateDocument.getDocument().getRealEstateData().getFlats().getFlat()
            .stream()
            .filter(f -> StringUtils.isNotBlank(f.getFlatID()))
            .filter(f -> f.getFlatID().equals(flatId))
            .findAny();
        return optFlatType.orElse(null);
    }

    public static String buildAddressFromJsonElements(
        final NsiType subjectRfp1Json,
        final NsiType elementP7Json,
        final NsiType houseL1TypeJson,
        final String houseL1Value
    ) {
        final List<String> tokens = Arrays.asList(
            getNsiName(subjectRfp1Json),
            getNsiName(elementP7Json),
            getNsiName(houseL1TypeJson),
            houseL1Value
        );
        return buildAddressFromElements(tokens);
    }

    private static String retrieveRealEstateAddress(final RealEstateDataType realEstate) {
        return ofNullable(realEstate.getAddress())
            .orElseGet(() -> buildAddressFromRealEstate(realEstate));
    }

    private static String buildAddressFromRealEstate(final RealEstateDataType realEstate) {
        return buildAddressFromElements(
            realEstate.getTownP4(),
            realEstate.getSettlementP3(),
            realEstate.getLocalityP6(),
            realEstate.getElementP7(),
            realEstate.getHouseL1TYPE(),
            realEstate.getHouseL1VALUE(),
            realEstate.getCorpL2TYPE(),
            realEstate.getCorpL2VALUE(),
            realEstate.getBuildingL3TYPE(),
            realEstate.getBuildingL3VALUE()
        );
    }

    public static String buildAddressFromElements(
        final NsiType townP4,
        final NsiType settlementP3,
        final NsiType localityP6,
        final NsiType elementP7,
        final NsiType houseL1Type,
        final String houseL1Value,
        final NsiType corpL2Type,
        final String corpL2Value,
        final NsiType buildingL3Type,
        final String buildingL3Value
    ) {
        final String house = concatTypeAndValue(getNsiName(houseL1Type), houseL1Value);
        final String corp = concatTypeAndValue(getNsiName(corpL2Type), corpL2Value);
        final String building = concatTypeAndValue(getNsiName(buildingL3Type), buildingL3Value);
        final String settlement = ofNullable(getNsiName(townP4)).orElse(getNsiName(settlementP3));

        final List<String> tokens = Arrays.asList(
            settlement,
            getNsiName(localityP6),
            getNsiName(elementP7),
            house,
            corp,
            building
        );
        return buildAddressFromElements(tokens);
    }

    private static String buildAddressFromElements(final List<String> tokens) {
        final StringJoiner joiner = new StringJoiner(", ");
        for (String token : tokens) {
            if (!StringUtils.isBlank(token)) {
                joiner.add(token);
            }
        }
        return joiner.toString();
    }

    private static String concatTypeAndValue(final String type, final String value) {
        if (!StringUtils.isBlank(type) && !StringUtils.isBlank(value)) {
            return String.format("%s %s", type, value);
        }
        return null;
    }

    private static String getNsiName(final NsiType nsiType) {
        return ofNullable(nsiType)
            .map(NsiType::getName)
            .orElse(null);
    }

}
