package ru.croc.ugd.ssr.service.bpm;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SsrTasksRequest {
    private final String processInstanceId;
    private final String processDefinitionKeyLike;
    private final boolean includeProcessVariables;
    private final boolean includeTaskLocalVariables;
}
