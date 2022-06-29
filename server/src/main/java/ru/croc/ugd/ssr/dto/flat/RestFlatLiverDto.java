package ru.croc.ugd.ssr.dto.flat;

import lombok.Builder;
import lombok.Value;

/**
 * Данные жильцов жилого помещения.
 */
@Value
@Builder
public class RestFlatLiverDto {

    /**
     * ИД жителя.
     */
    private final String personDocumentId;
    /**
     * Фамилия, имя, отчество.
     */
    private final String fullName;
    /**
     * Дата рождения
     */
    private final String birthDate;
    /**
     * Телефон.
     */
    private final String phone;
    /**
     * Статус проживания.
     */
    private final String statusLiving;

    /**
     * Код статуса проживания.
     */
    private final String statusLivingCode;
    /**
     * Паспортные данные
     */
    private final String passport;

}
