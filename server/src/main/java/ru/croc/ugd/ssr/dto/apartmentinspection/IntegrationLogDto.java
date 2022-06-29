package ru.croc.ugd.ssr.dto.apartmentinspection;

import lombok.Builder;
import lombok.Value;

/**
 * Лог межведомственной интеграции.
 */
@Value
@Builder
public class IntegrationLogDto {

    private final String messageId;
    private final String eventDateTime;
    private final String eventId;
    private final String eno;
    private final String fileStoreId;
}
