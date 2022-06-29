package ru.croc.ugd.ssr.dto.flatappointment;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

/**
 * Данные участника осмотра квартиры.
 */
@Value
@Builder
public class RestFlatDemoParticipantDto {

    /**
     * ИД жителя.
     */
    private final String personDocumentId;
    /**
     * Полное имя.
     */
    private final String fullName;
    /**
     * Дата рождения.
     */
    private final LocalDate birthDate;

}
