package ru.croc.ugd.ssr.dto.apartmentdefectconfirmation;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
public class DefectConfirmationRequestDto {
    /**
     * ИД дефекта
     */
    private final String id;
    /**
     * ИД акта осмотра квартиры
     */
    private final String apartmentInspectionId;
    /**
     * Элемент квартиры
     */
    private final String flatElement;
    /**
     * Описание дефекта
     */
    private final String description;
    /**
     * Данные о квартире
     */
    private final DefectFlatDto flatData;
    /**
     * Устранен ли дефект
     */
    private final boolean eliminated;
    /**
     * Текущий плановый срок устранения
     */
    private final LocalDate oldEliminationDate;
    /**
     * Новый плановый срок устранения
     */
    private final LocalDate eliminationDate;
    /**
     * Причина изменения планового срока устранения
     */
    private final String eliminationDateComment;
    /**
     * ИД семьи
     */
    private final String affairId;
}
