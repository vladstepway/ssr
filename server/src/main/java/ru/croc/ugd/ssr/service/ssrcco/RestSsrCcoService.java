package ru.croc.ugd.ssr.service.ssrcco;

import ru.croc.ugd.ssr.dto.ssrcco.CreateSsrCcoEmployeesDto;
import ru.croc.ugd.ssr.dto.ssrcco.SsrCcoDto;
import ru.croc.ugd.ssr.dto.ssrcco.SsrCcoEmployeePeriod;
import ru.croc.ugd.ssr.ssrcco.SsrCcoOrganization;

import java.util.List;

public interface RestSsrCcoService {

    /**
     * Получить сср окс.
     *
     * @param id id сср окса
     * @return карточка
     */
    SsrCcoDto fetchById(final String id);

    /**
     * Получить карточку по идентификатору ОКСа в системе PS.
     *
     * @param psDocumentId идентификатор ОКСа в системе PS
     * @return карточка
     */
    SsrCcoDto fetchByPsDocumentId(final String psDocumentId);

    /**
     * Привязать содтрудников к оксам.
     * @param createEmployeesDto информация о привязке сотрудников к оксам
     */
    void createEmployees(CreateSsrCcoEmployeesDto createEmployeesDto);

    /**
     * Удалить сотрудника окса.
     * @param id id сср окса
     * @param employeeId id сотрудника
     */
    void deleteEmployee(final String id, final String employeeId);

    /**
     * Изменить период назначения сотрудника окса.
     * @param id id сср окса
     * @param employeeId id сотрудника
     * @param employeePeriod период назначения сотрудника
     */
    void changeEmployeePeriod(final String id, final String employeeId, final SsrCcoEmployeePeriod employeePeriod);

    /**
     * Получить организации относящиеся к оксу по уному.
     * @param unom unom
     */
    List<SsrCcoOrganization> fetchSsrCcoOrganizationsByUnom(final String unom);
}
