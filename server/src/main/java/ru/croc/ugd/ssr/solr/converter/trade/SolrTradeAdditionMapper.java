package ru.croc.ugd.ssr.solr.converter.trade;

import static java.util.Optional.ofNullable;
import static org.mapstruct.ReportingPolicy.ERROR;
import static ru.croc.ugd.ssr.service.trade.utils.TradeAdditionEnumTranslations.BUY_IN_STATUS_TRANSLATIONS;
import static ru.croc.ugd.ssr.service.trade.utils.TradeAdditionEnumTranslations.COMPENSATION_STATUS_TRANSLATIONS;
import static ru.croc.ugd.ssr.service.trade.utils.TradeAdditionEnumTranslations.TRADE_TYPE_TRANSLATIONS;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import ru.croc.ugd.ssr.solr.UgdSsrTradeAddition;
import ru.croc.ugd.ssr.trade.EstateInfoType;
import ru.croc.ugd.ssr.trade.TradeAdditionType;
import ru.croc.ugd.ssr.trade.TradeType;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ERROR)
public interface SolrTradeAdditionMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(
        target = "ugdSsrTradeAdditionFio",
        source = "personNames"
    )
    @Mapping(
        target = "ugdSsrTradeAdditionAddressFrom",
        source = "tradeAdditionType.oldEstate",
        qualifiedByName = "toAddressFrom"
    )
    @Mapping(
        target = "ugdSsrTradeAdditionAddressTo",
        source = "tradeAdditionType.newEstates",
        qualifiedByName = "toAddressesTo"
    )
    @Mapping(
        target = "ugdSsrTradeAdditionType",
        source = "tradeAdditionType.tradeType",
        qualifiedByName = "toTradeType"
    )
    @Mapping(
        target = "ugdSsrTradeAdditionStatus",
        source = "tradeAdditionType",
        qualifiedByName = "toClaimStatus"
    )
    @Mapping(
        target = "ugdSsrTradeAdditionApplicationNumber",
        source = "tradeAdditionType.contractNumber"
    )
    @Mapping(
        target = "ugdSsrTradeAdditionIndexed",
        source = "tradeAdditionType.indexed"
    )
    UgdSsrTradeAddition toUgdSsrTradeAddition(
        @MappingTarget final UgdSsrTradeAddition ugdSsrTradeAddition,
        final TradeAdditionType tradeAdditionType,
        final String personNames
    );

    @Named("toAddressFrom")
    default String toAddressFrom(final EstateInfoType estateInfoType) {
        return getAddress(estateInfoType);
    }

    @Named("toAddressesTo")
    default String toAddressesTo(final List<EstateInfoType> estateInfoTypes) {
        return estateInfoTypes
            .stream()
            .map(this::getAddress)
            .collect(Collectors.joining("\n"));
    }

    default String getAddress(final EstateInfoType estateInfoType) {
        final String address = ofNullable(estateInfoType.getAddress())
            .orElse("");
        final String flatNumber = ofNullable(estateInfoType.getFlatNumber())
            .map(flatNum -> "кв. " + flatNum)
            .orElse("");

        return String.join(", ", address, flatNumber);
    }

    default String toTradeType(final TradeType tradeType) {
        return ofNullable(tradeType)
            .map(TRADE_TYPE_TRANSLATIONS::get)
            .orElse(null);
    }

    default String toClaimStatus(final TradeAdditionType tradeAdditionType) {
        final String buyInStatusTranslated = ofNullable(tradeAdditionType.getBuyInStatus())
            .map(BUY_IN_STATUS_TRANSLATIONS::get)
            .orElse("");
        final String claimStatusTranslated = ofNullable(tradeAdditionType.getCompensationStatus())
            .map(COMPENSATION_STATUS_TRANSLATIONS::get)
            .orElse("");
        return buyInStatusTranslated + claimStatusTranslated;
    }

}
