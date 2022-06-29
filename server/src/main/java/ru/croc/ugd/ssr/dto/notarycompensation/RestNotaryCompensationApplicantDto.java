package ru.croc.ugd.ssr.dto.notarycompensation;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class RestNotaryCompensationApplicantDto {
    /**
     * ИД документа заявителя.
     */
    private final String personDocumentId;
    /**
     * Фамилия заявителя.
     */
    private final String lastName;
    /**
     * Имя заявителя.
     */
    private final String firstName;
    /**
     * Отчество заявителя.
     */
    private final String middleName;
    /**
     *  Email заявителя.
     */
    private final String email;
    /**
     * Номер телефона заявителя.
     */
    private final String phone;
    /**
     * Дополнительный номер телефона заявителя.
     */
    private final String additionalPhone;
}