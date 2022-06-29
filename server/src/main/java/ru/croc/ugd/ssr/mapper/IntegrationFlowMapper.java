package ru.croc.ugd.ssr.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.croc.ugd.ssr.dto.mqetpmv.flow.FlowTypeDto;
import ru.croc.ugd.ssr.integrationflow.IntegrationFlowStatus;
import ru.croc.ugd.ssr.integrationflow.MfrFlowData;
import ru.croc.ugd.ssr.integrationflow.StatusData;
import ru.croc.ugd.ssr.model.integrationflow.IntegrationFlowDocument;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface IntegrationFlowMapper {

    @Mapping(target = "document.integrationFlowData.createdAt", expression = "java( java.time.LocalDateTime.now() )")
    @Mapping(target = "document.integrationFlowData.flowType", source = "flowTypeDto.integrationFlowType")
    @Mapping(target = "document.integrationFlowData.eno", source = "eno")
    @Mapping(target = "document.integrationFlowData.serviceCode", source = "flowTypeDto.serviceCode")
    @Mapping(target = "document.integrationFlowData.mfrFlowData", source = "mfrFlowData")
    @Mapping(target = "document.integrationFlowData.flowStatus", source = "integrationFlowStatus")
    IntegrationFlowDocument toIntegrationFlowDocument(
        final String eno,
        final FlowTypeDto flowTypeDto,
        final IntegrationFlowStatus integrationFlowStatus,
        final MfrFlowData mfrFlowData
    );

    @Mapping(target = "createdAt", expression = "java( java.time.LocalDateTime.now() )")
    @Mapping(target = "fileStoreId", source = "fileStoreId")
    @Mapping(target = "statusCode", source = "statusCode")
    @Mapping(target = "statusTitle", source = "statusTitle")
    @Mapping(target = "flowStatus", source = "flowStatus")
    @Mapping(target = "etpMessageId", ignore = true)
    StatusData toStatusData(
        final String fileStoreId,
        final String statusCode,
        final String statusTitle,
        final IntegrationFlowStatus flowStatus
    );
}
