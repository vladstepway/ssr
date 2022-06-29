package ru.croc.ugd.ssr.service.notary;

import ru.croc.ugd.ssr.dto.notary.RestCreateUpdateNotaryRequestDto;
import ru.croc.ugd.ssr.dto.notary.RestNotaryInfoDto;
import ru.croc.ugd.ssr.dto.notary.RestNotaryResponseDto;
import ru.croc.ugd.ssr.generated.dto.notary.RestNotaryPersonCheckDto;

import java.util.List;

/**
 * Сервис для работы с карточками нотариуса.
 */
public interface RestNotaryService {

    /**
     * Получить карточку.
     *
     * @param notaryId Ид карточки
     * @return карточка
     */
    RestNotaryResponseDto fetchById(String notaryId);

    /**
     * Получить информацию по натариусу.
     *
     * @param notaryId Ид карточки
     * @return информация по натариусу
     */
    RestNotaryInfoDto fetchNotaryInfo(String notaryId);

    /**
     * Создание карточки нотариуса.
     *
     * @param body Данные нотариуса (required)
     * @return карточка
     */
    RestNotaryResponseDto create(RestCreateUpdateNotaryRequestDto body);

    /**
     * Редакторивание карточки нотариуса.
     *
     * @param notaryId Ид карточки
     * @param body Данные нотариуса
     * @return карточка
     */
    RestNotaryResponseDto update(String notaryId, RestCreateUpdateNotaryRequestDto body);

    /**
     * Отправить в архив.
     *
     * @param notaryId Ид карточки
     */
    void archive(String notaryId);

    /**
     * Получение списка карточек.
     *
     * @param fullName Фильтр по фио натариуса.
     * @param login Фильтр по логину.
     * @param includeUnassignedEmployee Фильтр по наличию закрепленного за нотариусом сотрудника.
     * @return Список карточек.
     */
    List<RestNotaryInfoDto> findAll(final String fullName, final String login, final boolean includeUnassignedEmployee);

    /**
     * Сервис проверки.
     *
     * @param personId Идентификатор жителя.
     * @return результат проверки.
     */
    RestNotaryPersonCheckDto check(final String personId);
}
