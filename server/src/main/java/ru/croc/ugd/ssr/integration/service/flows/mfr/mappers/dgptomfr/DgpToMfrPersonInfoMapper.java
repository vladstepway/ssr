package ru.croc.ugd.ssr.integration.service.flows.mfr.mappers.dgptomfr;

import static org.mapstruct.ReportingPolicy.ERROR;

import lombok.Builder;
import lombok.Value;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.model.integration.mfr.DgpToMfrPersonInfo;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ERROR)
public interface DgpToMfrPersonInfoMapper {

    @Mapping(target = "affairId", source = "input.affairId")
    @Mapping(target = "sellId", source = "input.sellId")
    @Mapping(target = "person", source = "input.personTypes")
    DgpToMfrPersonInfo toDgpToMfrPersonInfo(final DgpToMfrPersonInfoMapperType input);

    List<DgpToMfrPersonInfo> toDgpToMfrPersonInfoList(final List<DgpToMfrPersonInfoMapperType> input);

    @Mapping(target = "personId", source = "personID")
    @Mapping(target = "snils", source = "SNILS")
    DgpToMfrPersonInfo.Person toDgpToMfrPersonInfoPerson(final PersonType input);

    @Value
    @Builder(toBuilder = true)
    class DgpToMfrPersonInfoMapperType {
        private final List<PersonType> personTypes;
        private final String affairId;
        private final String sellId;
    }
}
