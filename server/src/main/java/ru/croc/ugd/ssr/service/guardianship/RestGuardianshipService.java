package ru.croc.ugd.ssr.service.guardianship;

import ru.croc.ugd.ssr.dto.guardianship.CreateGuardianshipRequestDto;
import ru.croc.ugd.ssr.dto.guardianship.GuardianshipHandleRequestDto;
import ru.croc.ugd.ssr.dto.guardianship.GuardianshipRequestDto;
import ru.croc.ugd.ssr.guardianship.GuardianshipRequestData;

import java.util.List;
import java.util.Map;

/**
 * Сервис для работы с заявлениями по опеке.
 */
public interface RestGuardianshipService {

    /**
     * Создать заявление на опеку.
     * @param createGuardianshipRequestDto новый запрос.
     */
    void create(final CreateGuardianshipRequestDto createGuardianshipRequestDto);

    /**
     * Согласование заявления на опеку.
     * @param id  id заявления.
     * @param guardianshipHandleRequestDto данные по решению по сделке.
     */
    void handleRequest(final String id, final GuardianshipHandleRequestDto guardianshipHandleRequestDto);

    /**
     * Заполнение дополнительных параметров для формирования текста уведомления.
     * @param guardianshipRequestData сведения о заявлении жителя в органы опеки
     * @return параметры шаблона
     */
    Map<String, String> getNotificationTemplateParams(GuardianshipRequestData guardianshipRequestData);

    /**
     * Получить согласование заявления на опеку по affairId.
     * @param affairId affairId.
     * @param skipInactive skipInactive.
     */
    List<GuardianshipRequestDto> find(final String affairId, final boolean skipInactive);

    /**
     * Получить согласование заявления на опеку по id.
     * @param id id.
     */
    GuardianshipRequestDto findById(final String id);
}
