package ru.croc.ugd.ssr.dto.mqetpmv.flow;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ExportFlowMessageDto {
    private final String etpMessageId;
    private final String response;
    private final String integrationFlowDocumentId;
}
