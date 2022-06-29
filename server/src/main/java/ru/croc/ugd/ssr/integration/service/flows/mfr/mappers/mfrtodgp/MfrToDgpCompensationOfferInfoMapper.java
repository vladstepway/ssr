package ru.croc.ugd.ssr.integration.service.flows.mfr.mappers.mfrtodgp;

import static java.util.Optional.ofNullable;
import static org.mapstruct.ReportingPolicy.ERROR;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import ru.croc.ugd.ssr.dto.tradeaddition.EstateInfoDto;
import ru.croc.ugd.ssr.model.integration.mfr.MfrToDgpCompensationOfferInfo;
import ru.croc.ugd.ssr.model.trade.TradeAdditionDocument;
import ru.croc.ugd.ssr.trade.EstateInfoType;
import ru.croc.ugd.ssr.trade.TradeType;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ERROR)
public interface MfrToDgpCompensationOfferInfoMapper extends MfrToDgpInfoMapper {

    @Mapping(target = "document.tradeAdditionTypeData.mfrFlow", constant = "true")
    @Mapping(target = "document.tradeAdditionTypeData.confirmed", constant = "true")
    @Mapping(target = "document.tradeAdditionTypeData.indexed", constant = "false")
    @Mapping(target = "document.tradeAdditionTypeData.sellId", source = "info.sellId")
    @Mapping(target = "document.tradeAdditionTypeData.affairId", source = "info.affairId")
    @Mapping(target = "document.tradeAdditionTypeData.personsInfo", source = "personIds")
    @Mapping(target = "document.tradeAdditionTypeData.oldEstate", source = "oldEstate")
    @Mapping(
        target = "document.tradeAdditionTypeData.newEstates", source = "info", qualifiedByName = "toNewEstates"
    )
    @Mapping(target = "document.tradeAdditionTypeData.offerLetterDate", source = "info.offerLetterDate")
    @Mapping(
        target = "document.tradeAdditionTypeData.tradeType", source = "info.offerType", qualifiedByName = "toTradeType"
    )
    @Mapping(target = "document.tradeAdditionTypeData.integrationFlowDocumentId", source = "integrationFlowDocumentId")
    @Mapping(target = "document.tradeAdditionTypeData.claimStatus", constant = "ACTIVE")
    TradeAdditionDocument toTradeAdditionDocument(
        @MappingTarget final TradeAdditionDocument tradeAdditionDocument,
        final MfrToDgpCompensationOfferInfo info,
        final String integrationFlowDocumentId,
        final EstateInfoDto oldEstate,
        final List<String> personIds
    );

    @Mapping(target = "unom", source = "unom")
    @Mapping(target = "flatNumber", source = "flatNumber")
    @Mapping(target = "flatCadNumber", ignore = true)
    @Mapping(target = "cadNumber", ignore = true)
    @Mapping(target = "address", ignore = true)
    @Mapping(target = "rooms", ignore = true)
    EstateInfoType toNewEstate(final String unom, final String flatNumber);

    @Named("toNewEstates")
    default List<EstateInfoType> toNewEstates(final MfrToDgpCompensationOfferInfo info) {
        final String unom = ofNullable(info)
            .map(MfrToDgpCompensationOfferInfo::getNewUnom)
            .orElse(null);
        final String flatNumber = ofNullable(info)
            .map(MfrToDgpCompensationOfferInfo::getNewFlatNumber)
            .orElse(null);
        return Collections.singletonList(toNewEstate(unom, flatNumber));
    }

    @Named("toTradeType")
    default TradeType toTradeType(final String offerType) {
        switch (offerType) {
            case "1":
                return TradeType.COMPENSATION;
            case "2":
                return TradeType.OUT_OF_DISTRICT;
            default:
                return null;
        }
    }
}
