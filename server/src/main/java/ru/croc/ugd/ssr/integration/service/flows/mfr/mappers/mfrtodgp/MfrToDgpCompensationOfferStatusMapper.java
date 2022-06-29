package ru.croc.ugd.ssr.integration.service.flows.mfr.mappers.mfrtodgp;

import static org.mapstruct.ReportingPolicy.ERROR;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import ru.croc.ugd.ssr.model.integration.mfr.MfrToDgpCompensationOfferStatus;
import ru.croc.ugd.ssr.model.trade.TradeAdditionDocument;
import ru.croc.ugd.ssr.trade.EstateInfoType;
import ru.croc.ugd.ssr.trade.TradeResult;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ERROR)
public interface MfrToDgpCompensationOfferStatusMapper extends MfrToDgpInfoMapper {

    @Mapping(target = "document.tradeAdditionTypeData.mfrFlow", constant = "true")
    @Mapping(target = "document.tradeAdditionTypeData.confirmed", constant = "true")
    @Mapping(target = "document.tradeAdditionTypeData.indexed", constant = "false")
    @Mapping(target = "document.tradeAdditionTypeData.sellId", source = "status.sellId")
    @Mapping(target = "document.tradeAdditionTypeData.affairId", source = "status.affairId")
    @Mapping(target = "document.tradeAdditionTypeData.personsInfo", source = "personIds")
    @Mapping(
        target = "document.tradeAdditionTypeData.tradeResult",
        source = "status.offerStatus",
        qualifiedByName = "toTradeResult"
    )
    @Mapping(target = "document.tradeAdditionTypeData.agreementDate", source = "status.date")
    @Mapping(target = "document.tradeAdditionTypeData.newEstates", source = "status.unoms")
    @Mapping(target = "document.tradeAdditionTypeData.integrationFlowDocumentId", source = "integrationFlowDocumentId")
    TradeAdditionDocument toTradeAdditionDocument(
        @MappingTarget final TradeAdditionDocument tradeAdditionDocument,
        final MfrToDgpCompensationOfferStatus status,
        final String integrationFlowDocumentId,
        final List<String> personIds
    );

    @Named("toTradeResult")
    default TradeResult toTradeResult(final String offerStatus) {
        return TradeResult.fromValue(offerStatus);
    }

    @Mapping(target = "unom", source = "unom")
    @Mapping(target = "flatNumber", source = "flat.flatNumber")
    @Mapping(target = "flatCadNumber", source = "flat.cadNum")
    @Mapping(target = "cadNumber", ignore = true)
    @Mapping(target = "address", ignore = true)
    @Mapping(target = "rooms", ignore = true)
    EstateInfoType toNewEstate(final String unom, final MfrToDgpCompensationOfferStatus.Unoms.Flats flat);

    default List<EstateInfoType> toNewEstates(final List<MfrToDgpCompensationOfferStatus.Unoms> unoms) {
        return unoms.stream()
            .map(this::toNewEstates)
            .flatMap(List::stream)
            .collect(Collectors.toList());
    }

    default List<EstateInfoType> toNewEstates(final MfrToDgpCompensationOfferStatus.Unoms unom) {
        return unom.getFlats()
            .stream()
            .map(flat -> toNewEstate(unom.getUnom(), flat))
            .collect(Collectors.toList());
    }
}
