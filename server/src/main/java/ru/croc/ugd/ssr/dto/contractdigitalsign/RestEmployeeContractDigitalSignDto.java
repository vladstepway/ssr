package ru.croc.ugd.ssr.dto.contractdigitalsign;

import lombok.Builder;
import lombok.Value;

import java.util.List;

/**
 * Данные для электронного подписания сотрудником.
 */
@Value
@Builder
public class RestEmployeeContractDigitalSignDto {

    /**
     * ИД документа многостороннего подписания договора с использованием УКЭП.
     */
    private final String contractDigitalSignId;
    /**
     * Округ.
     */
    private final String area;
    /**
     * Район.
     */
    private final String district;
    /**
     * Адрес отселяемой квартиры.
     */
    private final String addressFrom;
    /**
     * Адрес заселяемой квартиры.
     */
    private final String addressTo;
    /**
     * Правообладатели жилого помещения.
     */
    private final List<RestOwnerDto> owners;
    /**
     * Данные по файлу договора.
     */
    private final RestFileDataDto contractFileData;
    /**
     * Данные по файлу акта.
     */
    private final RestFileDataDto actFileData;
    /**
     * Данные заявления.
     */
    private final RestContractAppointmentDto contractAppointment;
}
