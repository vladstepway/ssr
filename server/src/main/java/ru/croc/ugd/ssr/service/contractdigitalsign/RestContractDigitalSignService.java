package ru.croc.ugd.ssr.service.contractdigitalsign;

import ru.croc.ugd.ssr.dto.contractdigitalsign.ContractDigitalSignMoveDateDto;
import ru.croc.ugd.ssr.dto.contractdigitalsign.RestEmployeeContractDigitalSignDto;
import ru.croc.ugd.ssr.dto.contractdigitalsign.RestSaveSignatureDto;

import java.util.List;

public interface RestContractDigitalSignService {

    /**
     * Получить данные заявлений для электронного подписания сотрудником.
     *
     * @return список данных о заявлениях для электронного подписания.
     */
    List<RestEmployeeContractDigitalSignDto> fetchAll();

    /**
     * Изменить дату запланированного подписания договора с использованием УКЭП.
     * @param dto данные для изменения даты
     */
    void moveAppointmentDate(final ContractDigitalSignMoveDateDto dto);

    /**
     * Сохранение информации об актуальной подписи сотрудника.
     *
     * @param id ИД документа многостороннего подписания договора с использованием УКЭП
     * @param restSaveSignatureDto данные для сохранения подписи
     */
    void saveSignatureData(final String id, final RestSaveSignatureDto restSaveSignatureDto);
}
