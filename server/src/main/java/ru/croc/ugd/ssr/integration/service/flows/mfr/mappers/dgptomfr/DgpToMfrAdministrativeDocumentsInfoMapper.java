package ru.croc.ugd.ssr.integration.service.flows.mfr.mappers.dgptomfr;

import static org.mapstruct.ReportingPolicy.ERROR;

import lombok.Builder;
import lombok.Value;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.integrationflow.MfrFlowData;
import ru.croc.ugd.ssr.model.api.chess.CcoFlatInfoResponse;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPAdministrativeDocumentType;
import ru.croc.ugd.ssr.model.integration.mfr.DgpToMfrAdministrativeDocumentsInfo;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ERROR)
public interface DgpToMfrAdministrativeDocumentsInfoMapper {

    @Mapping(target = "affairId", source = "input.personType.affairId")
    @Mapping(target = "newUnom", source = "input.newFlat.unom")
    @Mapping(target = "newFlatNumber", source = "input.newFlat.flatNumber")
    @Mapping(target = "newRoomAmount", source = "input.ccoFlatInfoResponse.roomAmount")
    @Mapping(target = "newLivingSquare", source = "input.ccoFlatInfoResponse.livingSquare")
    @Mapping(target = "newTotalSquare", source = "input.ccoFlatInfoResponse.totalSquareWithSummer")
    @Mapping(target = "unom", source = "input.personType.UNOM")
    @Mapping(target = "fromFlat.flatNumber", source = "input.personType.flatNum")
    @Mapping(target = "fromFlat.roomNumbers", source = "input.personType.roomNum")
    @Mapping(target = "flowReceiveDate", expression = "java( java.time.LocalDate.now() )")
    DgpToMfrAdministrativeDocumentsInfo toDgpToMfrAdministrativeDocumentsInfo(
        final DgpToMfrAdministrativeDocumentsInfoMapperInput input
    );

    @Mapping(target = "affairId", source = "info.affairId")
    @Mapping(target = "letterId", source = "letterId")
    @Mapping(target = "contractOrderId", ignore = true)
    @Mapping(target = "contractProjectStatus", ignore = true)
    @Mapping(target = "contractStatus", ignore = true)
    MfrFlowData toMfrFlowData(final DgpToMfrAdministrativeDocumentsInfo info, final String letterId);

    @Value
    @Builder
    class DgpToMfrAdministrativeDocumentsInfoMapperInput {
        private final PersonType personType;
        private final SuperServiceDGPAdministrativeDocumentType.NewFlats.NewFlat newFlat;
        private final CcoFlatInfoResponse ccoFlatInfoResponse;
    }
}
