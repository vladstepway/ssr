package ru.croc.ugd.ssr.dto.changelog;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO для записи истории изменений.
 */
@Data
public class ChangelogDto {
    private LocalDateTime date;
    private List<ChangelogEvent> events;
    private String assignee;
    private String organization;
}
