package ru.croc.ugd.ssr.dto.apartmentdefectconfirmation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
public class RestEliminationDto {
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
     * Требуется заказная позиция
     */
    private final boolean itemRequired;
    /**
     * Причина необходимости заказной позиции
     */
    private final String itemRequiredComment;
    /**
     * Не дефект
     */
    @JsonProperty("isNotDefect")
    private final boolean notDefect;
    /**
     * Комментарий к признаку "Не дефект"
     */
    private final String notDefectComment;
}
