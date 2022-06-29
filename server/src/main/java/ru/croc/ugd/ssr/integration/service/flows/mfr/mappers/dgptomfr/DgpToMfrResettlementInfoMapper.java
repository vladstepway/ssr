package ru.croc.ugd.ssr.integration.service.flows.mfr.mappers.dgptomfr;

import static org.mapstruct.ReportingPolicy.ERROR;

import lombok.Builder;
import lombok.Value;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.croc.ugd.ssr.CipType;
import ru.croc.ugd.ssr.FlatType;
import ru.croc.ugd.ssr.HouseToResettle;
import ru.croc.ugd.ssr.HouseToSettle;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.ResettlementRequestType;
import ru.croc.ugd.ssr.model.integration.mfr.DgpToMfrResettlementInfo;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ERROR)
public interface DgpToMfrResettlementInfoMapper {

    @Mapping(target = "cip", source = "input.cipType")
    @Mapping(target = "resettlementInfo", source = "input")
    @Mapping(target = "fromFlats", source = "input.flatInputs")
    DgpToMfrResettlementInfo toDgpToMfrResettlementInfo(final DgpToMfrResettlementInfoMapperInput input);

    List<DgpToMfrResettlementInfo> toDgpToMfrResettlementInfoList(
        final List<DgpToMfrResettlementInfoMapperInput> input
    );

    @Mapping(target = "addressCip", source = "address")
    @Mapping(target = "phoneCip", source = "phone")
    @Mapping(target = "workCip", source = "workTime")
    @Mapping(target = "dateCip", source = "cipDateStart")
    @Mapping(target = "unomCip", constant = "0")
    DgpToMfrResettlementInfo.Cip toDgpToMfrResettlementInfoCip(final CipType cipType);

    @Mapping(
        target = "resettlementType",
        source = "input.houseToResettle.resettlementBy",
        qualifiedByName = "toResettlementType"
    )
    @Mapping(target = "fromUnom", source = "input.houseToResettle.realEstateUnom")
    @Mapping(
        target = "toUnom",
        source = "input.houseToSettle.capitalConstructionObjectUnom",
        qualifiedByName = "toUnomListFromSingle"
    )
    @Mapping(target = "startDate", source = "input.resettlementRequestType.startResettlementDate")
    @Mapping(target = "fromCadNum", source = "input.fromCadastralNumber")
    DgpToMfrResettlementInfo.ResettlementInfo toDgpToMfrResettlementInfoResettlementInfo(
        final DgpToMfrResettlementInfoMapperInput input
    );

    @Mapping(target = "flatNumber", source = "input.flatType.flatNumber")
    @Mapping(target = "roomNumbers", source = "input.personType.roomNum")
    @Mapping(target = "roomAmount", source = "input.flatType.roomsCount")
    @Mapping(target = "floor", source = "input.flatType.floor")
    @Mapping(target = "totalSquare", source = "input.flatType.SAll")
    @Mapping(target = "livingSquare", source = "input.flatType.SGil")
    @Mapping(target = "resettlementDirection", source = "input.resettlementDirection")
    @Mapping(target = "affairId", source = "input.personType.affairId")
    @Mapping(target = "statusLiving", source = "input.personType.statusLiving", qualifiedByName = "toStatusLiving")
    @Mapping(target = "hasDisabledPerson", source = "input.hasDisablePerson")
    @Mapping(target = "hasWaiterPerson", source = "input.hasWaiterPerson")
    DgpToMfrResettlementInfo.FromFlats toDgpToMfrResettlementInfoFromFlats(
        final FromFlatInput input
    );

    List<DgpToMfrResettlementInfo.FromFlats> toDgpToMfrResettlementInfoFromFlatsList(
        final List<FromFlatInput> input
    );

    /**
     *  1 - Собственник (частная собственность),
     *  2 - Пользователь (частная собственность),
     *  3 - Наниматель (найм/пользование),
     *  4 - Проживающий (найм/пользователь)
     *  5 - Свободная
     *  0 - Отсутствует
     */
    @Named("toStatusLiving")
    default String toStatusLiving(final String statusLiving) {
        if ("1".equals(statusLiving) || "2".equals(statusLiving)) {
            return "Собственность";
        }
        if ("3".equals(statusLiving) || "4".equals(statusLiving)) {
            return "Социальный найм";
        }
        return null;
    }

    @Named("toResettlementType")
    default String toResettlementType(final String resettlementBy) {
        if ("full".equalsIgnoreCase(resettlementBy)) {
            return "1";
        }
        if ("part".equalsIgnoreCase(resettlementBy)) {
            return "2";
        }
        return null;
    }

    @Named("toUnomListFromSingle")
    default List<String> toUnomListFromSingle(final String unom) {
        return Collections.singletonList(unom);
    }

    @Value
    @Builder
    class DgpToMfrResettlementInfoMapperInput {
        private final CipType cipType;
        private final ResettlementRequestType resettlementRequestType;
        private final HouseToSettle houseToSettle;
        private final HouseToResettle houseToResettle;
        private final List<FromFlatInput> flatInputs;
        private final String fromCadastralNumber;
    }

    @Value
    @Builder
    class FromFlatInput {
        private final PersonType personType;
        private final FlatType flatType;
        private final boolean hasDisablePerson;
        private final boolean hasWaiterPerson;
        private final String resettlementDirection;
    }
}
