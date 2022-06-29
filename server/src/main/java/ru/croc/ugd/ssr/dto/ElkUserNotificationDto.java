package ru.croc.ugd.ssr.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

/**
 * Данные направленного уведомления.
 */
@Value
@Builder
public class ElkUserNotificationDto {

    /**
     * ИД документа жителя, которому было направлено уведомление.
     */
    private final String personDocumentId;
    /**
     * ЕНО уведомления.
     */
    private final String eno;
    /**
     * Дата и время создания уведомления.
     */
    private final LocalDateTime creationDateTime;
    /**
     * Статус уведомления.
     */
    private final String statusId;
}
