package ru.croc.ugd.ssr.dto.mqetpmv.flow;

import lombok.Builder;
import lombok.Value;
import ru.croc.ugd.ssr.integrationflow.IntegrationFlowType;

@Value
@Builder
public class FlowTypeDto {
    private final IntegrationFlowType integrationFlowType;
    private final String serviceCode;
    private final String flowMessageProcessorBeanName;
}
