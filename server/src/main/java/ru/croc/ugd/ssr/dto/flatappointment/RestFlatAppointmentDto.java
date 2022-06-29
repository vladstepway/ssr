package ru.croc.ugd.ssr.dto.flatappointment;

import lombok.Builder;
import lombok.Value;
import ru.croc.ugd.ssr.dto.RestLetterDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Данные записи на осмотр квартиры.
 */
@Value
@Builder
public class RestFlatAppointmentDto {

    /**
     * ИД карточки.
     */
    private final String id;
    /**
     * Номер заявления.
     */
    private final String eno;
    /**
     * ID статуса заявления.
     */
    private final String statusId;
    /**
     * Статус заявления.
     */
    private final String status;
    /**
     * Заявитель.
     */
    private final RestFlatAppointmentApplicantDto applicant;
    /**
     * Дата осмотра.
     */
    private final LocalDateTime appointmentDateTime;
    /**
     * Дата отмены.
     */
    private final LocalDate cancelDate;
    /**
     * Причина отмены.
     */
    private final String cancelReason;
    /**
     * ЦИП ID.
     */
    private final String cipId;
    /**
     * Адрес центра переселения.
     */
    private final String cipAddress;
    /**
     * Количество слотов, доступных для одновременного бронирования.
     */
    private final Integer slotNumber;
    /**
     * Письмо с предложением.
     */
    private final RestLetterDto letter;
    /**
     * Произведен ли осмотр.
     */
    private final Boolean isPerformed;
    /**
     * ID запущенного процесса.
     */
    private final String processInstanceId;
    /**
     * Участники.
     */
    private final List<RestFlatDemoParticipantDto> participants;

}
