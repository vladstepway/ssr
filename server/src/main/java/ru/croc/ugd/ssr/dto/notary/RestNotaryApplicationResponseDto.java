package ru.croc.ugd.ssr.dto.notary;

import lombok.Builder;
import lombok.Value;
import ru.croc.ugd.ssr.dto.PersonDto;
import ru.croc.ugd.ssr.dto.ResettlementInfoDto;
import ru.croc.ugd.ssr.dto.commissioninspection.RestApplicantDto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Заявление на посещение нотариуса.
 */
@Builder
@Value
public class RestNotaryApplicationResponseDto {

    /**
     * ИД карточки.
     */
    private final String id;
    /**
     * Номер заявления.
     */
    private final String eno;
    /**
     * Идентификатор семьи.
     */
    private final String affairId;
    /**
     * Дата и время подачи заявления.
     */
    private final LocalDateTime applicationDate;
    /**
     * Данные о заявителе.
     */
    private final RestApplicantDto applicant;
    /**
     * Адрес квартиры откуда переезжают.
     */
    private final RestNotaryApplicationApartmentDto apartmentFrom;
    /**
     * Адрес квартиры куда переезжают.
     */
    private final List<RestNotaryApplicationApartmentDto> apartmentTo;
    /**
     * Данные нотариуса.
     */
    private final RestNotaryInfoDto notary;
    /**
     * Комментарий заявителя.
     */
    private final String applicantComment;
    /**
     * Дата и время приема.
     */
    private final LocalDateTime appointmentDateTime;
    /**
     * Жители квартиры.
     */
    private final List<PersonDto> otherLivers;
    /**
     * ID статуса заявления.
     */
    private final String statusId;
    /**
     * Статус заявления.
     */
    private final String status;
    /**
     * Источник подачи документа.
     */
    private final String source;
    /**
     * ID запущенного процесса.
     */
    private final String processInstanceId;
    /**
     * Файлы.
     */
    private final List<RestNotaryApplicationFileDto> files;
    /**
     * Сведения о переселении.
     */
    private final ResettlementInfoDto resettlementInfo;
    /**
     * Изменения в заявлении.
     */
    private final List<RestNotaryApplicationHistoryEventDto> history;
}
