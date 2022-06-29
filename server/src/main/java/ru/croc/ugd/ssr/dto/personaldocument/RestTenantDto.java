package ru.croc.ugd.ssr.dto.personaldocument;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.util.List;

/**
 * Данные о жильце.
 */
@Value
@Builder
public class RestTenantDto {

    /**
     * Идентификатор документа жителя.
     */
    private final String personDocumentId;
    /**
     * ФИО жителя.
     */
    private final String fullName;
    /**
     * Дата рождения.
     */
    private final LocalDate birthDate;
    /**
     * Статус проживания.
     */
    private final String status;
    /**
     * Ненадлежащий.
     */
    @JsonProperty("isSuspicious")
    private final Boolean suspicious;
    /**
     * Комментарий с причиной, почему житель является ненадлежащим.
     */
    private final String suspicionReason;
    /**
     * Документы личного характера.
     */
    private final List<RestDocumentDto> documents;
}
