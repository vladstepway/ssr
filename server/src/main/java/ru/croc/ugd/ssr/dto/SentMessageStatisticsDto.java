package ru.croc.ugd.ssr.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SentMessageStatisticsDto {

    private final String name;
    private final long elk;
    private final long sended;
    private final long handed;
    private final int percentage;
}
