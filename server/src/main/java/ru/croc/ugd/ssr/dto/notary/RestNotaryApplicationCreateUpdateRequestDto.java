package ru.croc.ugd.ssr.dto.notary;

import lombok.Builder;
import lombok.Value;
import ru.croc.ugd.ssr.dto.commissioninspection.RestApplicantDto;

import java.util.List;

/**
 * Данные нотариуса.
 */
@Builder
@Value
public class RestNotaryApplicationCreateUpdateRequestDto {

    /**
     * Данные о заявителе.
     */
    private final RestApplicantDto applicant;
    /**
     * ID семьи.
     */
    private final String affairId;
    /**
     * ИД нотариуса.
     */
    private final String notaryId;
    /**
     * Комментарий заявителя.
     */
    private final String applicantComment;
    /**
     * Данные о квартире откуда переезжают.
     */
    private final RestNotaryApplicationApartmentDto apartmentFrom;
    /**
     * Данные о квартирах куда переезжают.
     */
    private final List<RestNotaryApplicationApartmentDto> apartmentTo;
}
