package ru.croc.ugd.ssr.dto.courtinfo;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

/**
 * Данные о решении суда.
 */
@Value
@Builder
public class RestCourtInfoDto {
    /**
     * ИД семьи
     */
    private final String affairId;
    /**
     * ИД письма
     */
    private final String letterId;
    /**
     * Идентификатор дела
     */
    private final String caseId;
    /**
     * Дата назначения судебного заседания
     */
    private final LocalDate courtDate;
    /**
     * Дата вынесения решения суда
     */
    private final LocalDate courtResultDate;
    /**
     * Дата вступления суда в законную силу;
     */
    private final LocalDate courtLawDate;
    /**
     * Решение
     */
    private final String courtResult;
}
