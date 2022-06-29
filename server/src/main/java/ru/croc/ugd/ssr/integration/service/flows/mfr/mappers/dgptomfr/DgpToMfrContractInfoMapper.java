package ru.croc.ugd.ssr.integration.service.flows.mfr.mappers.dgptomfr;

import static org.mapstruct.ReportingPolicy.ERROR;

import lombok.Builder;
import lombok.Value;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.integrationflow.MfrFlowData;
import ru.croc.ugd.ssr.model.integration.mfr.DgpToMfrContractInfo;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ERROR)
public interface DgpToMfrContractInfoMapper {

    @Mapping(target = "affairId", source = "input.personType.affairId")
    @Mapping(target = "orderId", source = "input.contract.orderId")
    @Mapping(target = "newUnom", source = "input.newFlat.ccoUnom")
    @Mapping(target = "newFlatNumber", source = "input.newFlat.ccoFlatNum")
    @Mapping(target = "contractDate", source = "input.contract.contractSignDate")
    @Mapping(target = "unom", source = "input.personType.UNOM")
    @Mapping(target = "fromFlat.flatNumber", source = "input.personType.flatNum")
    @Mapping(target = "fromFlat.roomNumbers", source = "input.personType.roomNum")
    @Mapping(target = "contractStatus", source = "input.contract", qualifiedByName = "toContractStatus")
    @Mapping(target = "actDate", source = "input.contract.actSignDate")
    DgpToMfrContractInfo toDgpToMfrContractInfo(final DgpToMfrContractInfoMapperInput input);

    @Named("toContractStatus")
    default String toContractStatus(final PersonType.Contracts.Contract contract) {
        if (contract == null) {
            return null;
        }
        if ("1".equals(contract.getContractStatus())) {
            return "Аннулирован";
        }
        if (contract.getContractSignDate() != null) {
            return "Подписан";
        }
        return null;
    }

    @Mapping(target = "affairId", source = "affairId")
    @Mapping(target = "contractOrderId", source = "orderId")
    @Mapping(target = "contractStatus", source = "contractStatus")
    @Mapping(target = "contractProjectStatus", ignore = true)
    @Mapping(target = "letterId", ignore = true)
    MfrFlowData toMfrFlowData(final DgpToMfrContractInfo dgpToMfrContractInfo);

    @Value
    @Builder
    class DgpToMfrContractInfoMapperInput {
        private final PersonType personType;
        private final PersonType.Contracts.Contract contract;
        private final PersonType.NewFlatInfo.NewFlat newFlat;
    }
}
