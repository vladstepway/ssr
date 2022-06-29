package ru.croc.ugd.ssr.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

/**
 * DTO для записи истории изменений.
 */
@Value
@Builder
public class DashboardChangelogDto {

    private final LocalDateTime date;
    private final String event;
    private final String assignee;
    private final String comment;

}
