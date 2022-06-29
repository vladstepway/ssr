package ru.croc.ugd.ssr.dto.agreement;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.util.List;

/**
 * Данные о согласии или отказе.
 */
@Value
@Builder
public class RestAgreementDto {
    /**
     * Уникальный номер письма с предложением.
     */
    private final String letterId;
    /**
     * Дата.
     */
    private final LocalDate date;
    /**
     * Событие.
     */
    private final String event;
    /**
     * Комплектность документов.
     */
    private final String fullDocs;
    /**
     * Коды недостающих документов.
     */
    private final List<String> waitingDocsCodes;
    /**
     * Список недостающих документов - комментарий текстом.
     */
    private final String waitingDocs;
    /**
     * Причина отказа (при отказе) (справочник).
     */
    private final String refuseReason;
    /**
     * Уточнение причины отказа (при отказе).
     */
    private final String refuseReasonText;
    /**
     * Ссылка на файл в хранилище.
     */
    private final String fileLink;
    /**
     * Идентификаторы жильцов на момент просмотра.
     */
    private final List<RestAgreementParticipantDto> participants;
}
