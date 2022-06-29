package ru.croc.ugd.ssr.service.compensation;

import ru.croc.ugd.ssr.dto.compensation.RestCompensationDto;
import ru.croc.ugd.ssr.dto.compensation.RestCreateCompensationDto;

import java.util.List;

public interface RestCompensationService {

    /**
     * Получить карточку.
     *
     * @param id Ид карточки
     * @return карточка
     */
    RestCompensationDto fetchById(final String id);

    /**
     * Получить список карточек по УНОМ отселяемого дома.
     *
     * @param unom УНОМ отселяемого дома
     * @return список карточек
     */
    List<RestCompensationDto> fetchAllByUnom(final String unom);

    /**
     * Создание карточек для всех домов по ResettlementRequest.
     *
     * @param restCreateCompensationDto запрос.
     */
    void createForResettlementRequest(final RestCreateCompensationDto restCreateCompensationDto);

    /**
     * Изменение карточки.
     *
     * @param id                  Уникальный идентификатор.
     * @param restCompensationDto карточка.
     * @return карточка.
     */
    RestCompensationDto update(final String id, final RestCompensationDto restCompensationDto);
}
