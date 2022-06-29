package ru.croc.ugd.ssr.dto.mqetpmv.flow;

import lombok.Builder;
import lombok.Value;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateTaskData;
import ru.croc.ugd.ssr.model.integrationflow.IntegrationFlowDocument;

@Value
@Builder
public class ImportCoordinateTask {
    private final String eno;
    private final CoordinateTaskData coordinateTaskData;
    private final IntegrationFlowDocument integrationFlowDocument;
    private final String flowMessageProcessorBeanName;
}
