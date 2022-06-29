package ru.croc.ugd.ssr.integration.service.flows.mfr.mappers.mfrtodgp;

import static java.util.Optional.ofNullable;
import static org.mapstruct.ReportingPolicy.ERROR;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.util.CollectionUtils;
import ru.croc.ugd.ssr.model.integration.mfr.MfrToDgpClaimInfo;
import ru.croc.ugd.ssr.model.trade.TradeAdditionDocument;
import ru.croc.ugd.ssr.trade.PersonInfoType;
import ru.croc.ugd.ssr.utils.PersonUtils;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ERROR)
public interface MfrToDgpClaimInfoMapper {

    @Mapping(target = "document.tradeAdditionTypeData.mfrFlow", constant = "true")
    @Mapping(target = "document.tradeAdditionTypeData.confirmed", constant = "true")
    @Mapping(target = "document.tradeAdditionTypeData.indexed", constant = "false")
    @Mapping(target = "document.tradeAdditionTypeData.sellId", source = "info.sellId")
    @Mapping(target = "document.tradeAdditionTypeData.affairId", source = "info.affairId")
    @Mapping(target = "document.tradeAdditionTypeData.personsInfo", source = "persons")
    @Mapping(target = "document.tradeAdditionTypeData.integrationFlowDocumentId", source = "integrationFlowDocumentId")
    TradeAdditionDocument toTradeAdditionDocument(
        @MappingTarget final TradeAdditionDocument tradeAdditionDocument,
        final MfrToDgpClaimInfo info,
        final String integrationFlowDocumentId,
        final List<MfrToDgpClaimInfo.Person> persons
    );

    default TradeAdditionDocument toTradeAdditionDocument(
        final TradeAdditionDocument tradeAdditionDocument,
        final MfrToDgpClaimInfo info,
        final String integrationFlowDocumentId
    ) {
        final List<MfrToDgpClaimInfo.Person> personIds = ofNullable(info)
            .map(MfrToDgpClaimInfo::getPerson)
            .filter(collection -> !CollectionUtils.isEmpty(collection))
            .orElse(null);
        return toTradeAdditionDocument(tradeAdditionDocument, info, integrationFlowDocumentId, personIds);
    }

    @Mapping(target = "personId", source = "personId")
    @Mapping(target = "personFio", source = "person", qualifiedByName = "toFullName")
    @Mapping(target = "snils", source = "snils")
    @Mapping(target = "personDocumentId", ignore = true)
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "middleName", source = "middleName")
    PersonInfoType toPersonInfoType(final MfrToDgpClaimInfo.Person person);

    @Named("toFullName")
    default String toFullName(final MfrToDgpClaimInfo.Person person) {
        return PersonUtils.getFullName(person.getLastName(), person.getFirstName(), person.getMiddleName());
    }
}
