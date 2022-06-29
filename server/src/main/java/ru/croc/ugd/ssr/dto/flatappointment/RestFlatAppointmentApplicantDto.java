package ru.croc.ugd.ssr.dto.flatappointment;

import lombok.Builder;
import lombok.Value;

/**
 * Данные заявителя на осмотр квартиры.
 */
@Value
@Builder
public class RestFlatAppointmentApplicantDto {

    /**
     * ИД жителя.
     */
    private final String personDocumentId;
    /**
     * Фамилия.
     */
    private final String lastName;
    /**
     * Имя.
     */
    private final String firstName;
    /**
     * Отчество.
     */
    private final String middleName;
    /**
     * Электронная почта.
     */
    private final String email;
    /**
     * Телефон.
     */
    private final String phone;
    /**
     * Дополнительный телефон.
     */
    private final String additionalPhone;
    /**
     * Адрес отселяемой квартиры.
     */
    private final String addressFrom;
}
