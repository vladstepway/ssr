package ru.croc.ugd.ssr.dto.tradeaddition;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class NotificationInfoDto {
    private final String eventCode;
    private final LocalDateTime sentDate;
    private final boolean ok;
    private final String errorDescription;
}
