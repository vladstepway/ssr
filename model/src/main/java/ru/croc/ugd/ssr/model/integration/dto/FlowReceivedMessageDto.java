package ru.croc.ugd.ssr.model.integration.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class FlowReceivedMessageDto<T> {
    private final String eno;
    private final T parsedMessage;
    private final boolean shouldSendNotifications;
}
