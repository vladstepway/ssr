package ru.croc.ugd.ssr.mapper;

import static org.mapstruct.ReportingPolicy.ERROR;

import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.generated.dto.RestCommissionInspectionApartmentToDto;
import ru.croc.ugd.ssr.generated.dto.RestCommissionInspectionCipInfoDto;
import ru.croc.ugd.ssr.generated.dto.RestCommissionInspectionFlatStatusDto;
import ru.croc.ugd.ssr.generated.dto.RestCommissionInspectionLetterDto;
import ru.croc.ugd.ssr.generated.dto.RestCommissionInspectionPersonInfoDto;
import ru.croc.ugd.ssr.generated.dto.RestCommissionInspectionTradeTypeDto;
import ru.croc.ugd.ssr.model.CipDocument;
import ru.croc.ugd.ssr.service.validator.impl.person.WarrantyPeriodExpired;
import ru.croc.ugd.ssr.trade.BuyInStatus;
import ru.croc.ugd.ssr.trade.CompensationStatus;
import ru.croc.ugd.ssr.trade.EstateInfoType;
import ru.croc.ugd.ssr.trade.TradeAdditionType;
import ru.croc.ugd.ssr.utils.PersonUtils;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ERROR)
@Slf4j
public abstract class CommissionInspectionCheckMapper {

    /**
     * Статусы по докупке для гарантийного периода.
     */
    private static final List<BuyInStatus> BUY_IN_STATUS_WARRANTY_PERIOD = Arrays.asList(
        BuyInStatus.CONTRACT_SIGNED,
        BuyInStatus.KEYS_ISSUED
    );
    /**
     * Статусы по компенсации для гарантийного периода.
     */
    private static final List<CompensationStatus> COMPENSATION_STATUS_WARRANTY_PERIOD = Arrays.asList(
        CompensationStatus.CONTRACT_SIGNED,
        CompensationStatus.KEYS_ISSUED,
        CompensationStatus.APARTMENT_VACATED
    );
    /**
     * Вид сделки "Предлагаемая квартира".
     */
    private static final RestCommissionInspectionTradeTypeDto OFFERED_APARTMENT = new RestCommissionInspectionTradeTypeDto() {
        {
            setId(1);
            setName("Предлагаемая квартира");
        }
    };
    /**
     * Вид сделки "Ваш выбор по докупке".
     */
    private static final RestCommissionInspectionTradeTypeDto YOUR_CHOICE_FOR_PURCHASE = new RestCommissionInspectionTradeTypeDto() {
        {
            setId(2);
            setName("Ваш выбор по докупке");
        }
    };

    /**
     * Статус квартиры "Заселение".
     */
    public static final RestCommissionInspectionFlatStatusDto CHECK_IN = new RestCommissionInspectionFlatStatusDto() {
        {
            setId(1);
            setName("Заселение");
        }
    };
    /**
     * Статус квартиры "Гарантийный период".
     */
    private static final RestCommissionInspectionFlatStatusDto WARRANTY_PERIOD = new RestCommissionInspectionFlatStatusDto() {
        {
            setId(2);
            setName("Гарантийный период");
        }
    };

    @Mappings({
        @Mapping(target = "address", source = "address"),
        @Mapping(target = "flatNumber", source = "newFlat.ccoFlatNum"),
        @Mapping(target = "unom", source = "newFlat.ccoUnom"),
        @Mapping(target = "person", source = "person"),
        @Mapping(target = "flatStatus", source = "contractSignDate",
            qualifiedByName = "toDefineFlatStatusByContractSignDate"),
        @Mapping(target = "tradeType", expression = "java(getTradeTypeOfferedApartment())"),
        @Mapping(target = "cipInfo", ignore = true),
        @Mapping(target = "letter", ignore = true),
        @Mapping(target = "errorMessage", ignore = true)
    })
    public abstract RestCommissionInspectionApartmentToDto toApartmentTo(
        final PersonType.NewFlatInfo.NewFlat newFlat,
        final Optional<LocalDate> contractSignDate,
        final RestCommissionInspectionPersonInfoDto person,
        final String address
    );

    @Mappings({
        @Mapping(target = "address", source = "doc.newEstates", qualifiedByName = "getAddressFromEstates"),
        @Mapping(target = "unom", source = "doc.newEstates", qualifiedByName = "getUnomFromEstates"),
        @Mapping(target = "flatNumber", source = "doc.newEstates", qualifiedByName = "getFlatNumberFromEstates"),
        @Mapping(target = "person", source = "person"),
        @Mapping(target = "flatStatus", source = "doc", qualifiedByName = "toDefineFlatStatusByTradeAddition"),
        @Mapping(target = "tradeType", source = "doc", qualifiedByName = "toDefineTradeTypeByTradeAddition"),
        @Mapping(target = "cipInfo", source = "cip", qualifiedByName = "toCommissionInspectionCipInfo"),
        @Mapping(target = "letter", ignore = true),
        @Mapping(target = "errorMessage", ignore = true)
    })
    public abstract RestCommissionInspectionApartmentToDto toTradeAddition(
        final TradeAdditionType doc,
        final RestCommissionInspectionPersonInfoDto person,
        final CipDocument cip
    );

    @Mappings({
        @Mapping(target = "address", ignore = true),
        @Mapping(target = "flatNumber", ignore = true),
        @Mapping(target = "unom", ignore = true),
        @Mapping(target = "person", source = "person"),
        @Mapping(target = "flatStatus", expression = "java(getFlatStatusCheckIn())"),
        @Mapping(target = "tradeType", expression = "java(getTradeTypeOfferedApartment())"),
        @Mapping(target = "cipInfo", source = "cip", qualifiedByName = "toCommissionInspectionCipInfo"),
        @Mapping(target = "letter", source = "letter", qualifiedByName = "toRestCommissionInspectionLetter"),
        @Mapping(target = "errorMessage", ignore = true)
    })
    public abstract RestCommissionInspectionApartmentToDto toLetter(
        final PersonType.OfferLetters.OfferLetter letter,
        final CipDocument cip,
        final RestCommissionInspectionPersonInfoDto person
    );

    @Mappings({
        @Mapping(target = "id", source = "letter.letterId"),
        @Mapping(target = "fileId", source = "letter", qualifiedByName = "extractLetterFileIdFromOfferLetter")
    })
    public abstract RestCommissionInspectionLetterDto toRestCommissionInspectionLetter(
        final PersonType.OfferLetters.OfferLetter letter);

    @Mappings({
        @Mapping(target = "address", source = "cip.document.cipData.address"),
        @Mapping(target = "phone", source = "cip.document.cipData.phone")
    })
    public abstract RestCommissionInspectionCipInfoDto toCommissionInspectionCipInfo(final CipDocument cip);

    @Mappings({
        @Mapping(target = "id", source = "personData.personID"),
        @Mapping(target = "affairId", source = "personData.affairId"),
        @Mapping(target = "snils", source = "personData.SNILS"),
        @Mapping(target = "ssoId", source = "personData.ssoID")
    })
    public abstract RestCommissionInspectionPersonInfoDto toPerson(PersonType personData);

    @Named("extractLetterFileIdFromOfferLetter")
    public String extractLetterFileIdFromOfferLetter(final PersonType.OfferLetters.OfferLetter letter) {
        return PersonUtils.extractOfferLetterFileByType(letter, PersonUtils.OFFER_FILE_TYPE)
            .map(PersonType.OfferLetters.OfferLetter.Files.File::getChedFileId)
            .orElse(null);
    }

    /**
     * Определяем статус квартиры для докупке/компенсации
     * Гарантийный период это статусы (5 лет с даты заключения)
     * - Подписан договор
     * - Получены ключи от новой квартиры
     * - Договор подписан
     * - Выданы ключи от новой квартиры
     * - Квартира освобождена
     * @param tradeAdditionType данные по докупке/компенсации
     * @return статус квартиры
     */
    @Named("toDefineFlatStatusByTradeAddition")
    public RestCommissionInspectionFlatStatusDto toDefineFlatStatusByTradeAddition(
        final TradeAdditionType tradeAdditionType
    ) {
        if (tradeAdditionType.getContractSignedDate() == null
            || WarrantyPeriodExpired.isExpired(tradeAdditionType.getContractSignedDate())
        ) {
            return CHECK_IN;
        }
        if (
            (tradeAdditionType.getBuyInStatus() == null
                || !BUY_IN_STATUS_WARRANTY_PERIOD.contains(tradeAdditionType.getBuyInStatus()))
                && (tradeAdditionType.getCompensationStatus() == null
                || !COMPENSATION_STATUS_WARRANTY_PERIOD.contains(tradeAdditionType.getCompensationStatus()))
        ) {
            return CHECK_IN;
        }
        return WARRANTY_PERIOD;
    }

    @Named("toDefineTradeTypeByTradeAddition")
    public RestCommissionInspectionTradeTypeDto toDefineTradeTypeByTradeAddition(
        final TradeAdditionType tradeAdditionType
    ) {
        switch (tradeAdditionType.getTradeType()) {
            case SIMPLE_TRADE:
            case TRADE_WITH_COMPENSATION:
            case TRADE_IN_TWO_YEARS:
                return YOUR_CHOICE_FOR_PURCHASE;
            case COMPENSATION:
            case OUT_OF_DISTRICT:
                return OFFERED_APARTMENT;
            default:
                return null;
        }
    }

    @Named("toDefineFlatStatusByContractSignDate")
    public RestCommissionInspectionFlatStatusDto toDefineFlatStatusByContractSignDate(
        final Optional<LocalDate> contractSignDate
    ) {
        if (contractSignDate.isPresent()) {
            return WARRANTY_PERIOD;
        }
        return CHECK_IN;
    }

    @Named("getFlatStatusCheckIn")
    public RestCommissionInspectionFlatStatusDto getFlatStatusCheckIn() {
        return CHECK_IN;
    }

    @Named("getTradeTypeOfferedApartment")
    public RestCommissionInspectionTradeTypeDto getTradeTypeOfferedApartment() {
        return OFFERED_APARTMENT;
    }

    @Named("getTradeTypeYourChoiceForPurchase")
    public RestCommissionInspectionTradeTypeDto getTradeTypeYourChoiceForPurchase() {
        return YOUR_CHOICE_FOR_PURCHASE;
    }

    @Named("getAddressFromEstates")
    public String getAddressFromEstates(List<EstateInfoType> estateInfo) {
        return Optional.ofNullable(estateInfo)
            .map(List::stream)
            .orElse(Stream.empty())
            .map(EstateInfoType::getAddress)
            .findFirst()
            .orElse(null);
    }

    @Named("getUnomFromEstates")
    public String getUnomFromEstates(List<EstateInfoType> estateInfo) {
        return Optional.ofNullable(estateInfo)
            .map(List::stream)
            .orElse(Stream.empty())
            .map(EstateInfoType::getUnom)
            .findFirst()
            .orElse(null);
    }

    @Named("getFlatNumberFromEstates")
    public String getFlatNumberFromEstates(List<EstateInfoType> estateInfo) {
        return Optional.ofNullable(estateInfo)
            .map(List::stream)
            .orElse(Stream.empty())
            .map(EstateInfoType::getFlatNumber)
            .findFirst()
            .orElse(null);
    }

}
