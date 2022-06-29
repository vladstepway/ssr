package ru.croc.ugd.ssr.integration.service.flows.mfr.mappers.mfrtodgp;

import static org.mapstruct.ReportingPolicy.ERROR;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import ru.croc.ugd.ssr.dto.tradeaddition.EstateInfoDto;
import ru.croc.ugd.ssr.model.integration.mfr.MfrToDgpBuyInClaimInfo;
import ru.croc.ugd.ssr.model.trade.TradeAdditionDocument;
import ru.croc.ugd.ssr.trade.EstateInfoType;
import ru.croc.ugd.ssr.trade.TradeType;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ERROR)
public interface MfrToDgpBuyInClaimInfoMapper extends MfrToDgpInfoMapper {

    @Mapping(target = "document.tradeAdditionTypeData.mfrFlow", constant = "true")
    @Mapping(target = "document.tradeAdditionTypeData.confirmed", constant = "true")
    @Mapping(target = "document.tradeAdditionTypeData.indexed", constant = "false")
    @Mapping(target = "document.tradeAdditionTypeData.sellId", source = "info.sellId")
    @Mapping(target = "document.tradeAdditionTypeData.affairId", source = "info.affairId")
    @Mapping(target = "document.tradeAdditionTypeData.personsInfo", source = "personIds")
    @Mapping(
        target = "document.tradeAdditionTypeData.tradeType", source = "info.buyInType", qualifiedByName = "toTradeType"
    )
    @Mapping(target = "document.tradeAdditionTypeData.applicationDate", source = "info.claimDate")
    @Mapping(target = "document.tradeAdditionTypeData.oldEstate", source = "oldEstate")
    @Mapping(target = "document.tradeAdditionTypeData.newEstates", source = "info.unoms")
    @Mapping(target = "document.tradeAdditionTypeData.integrationFlowDocumentId", source = "integrationFlowDocumentId")
    @Mapping(target = "document.tradeAdditionTypeData.claimStatus", constant = "ACTIVE")
    TradeAdditionDocument toTradeAdditionDocument(
        @MappingTarget final TradeAdditionDocument tradeAdditionDocument,
        final MfrToDgpBuyInClaimInfo info,
        final String integrationFlowDocumentId,
        final EstateInfoDto oldEstate,
        final List<String> personIds
    );

    @Named("toTradeType")
    default TradeType toTradeType(final String buyInType) {
        return TradeType.fromValue(buyInType);
    }

    @Mapping(target = "unom", source = "unom")
    @Mapping(target = "flatNumber", source = "flat.flatNumber")
    @Mapping(target = "flatCadNumber", source = "flat.cadNum")
    @Mapping(target = "cadNumber", ignore = true)
    @Mapping(target = "address", ignore = true)
    @Mapping(target = "rooms", ignore = true)
    EstateInfoType toNewEstate(final String unom, final MfrToDgpBuyInClaimInfo.Unoms.Flats flat);

    default List<EstateInfoType> toNewEstates(final List<MfrToDgpBuyInClaimInfo.Unoms> unoms) {
        return unoms.stream()
            .map(this::toNewEstates)
            .flatMap(List::stream)
            .collect(Collectors.toList());
    }

    default List<EstateInfoType> toNewEstates(final MfrToDgpBuyInClaimInfo.Unoms unom) {
        return unom.getFlats()
            .stream()
            .map(flat -> toNewEstate(unom.getUnom(), flat))
            .collect(Collectors.toList());
    }
}
