package ru.croc.ugd.ssr.dto.contractappointment;

import lombok.Builder;
import lombok.Value;

/**
 * Данные заявителя на заключение договора.
 */
@Value
@Builder
public class RestContractAppointmentApplicantDto {

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
