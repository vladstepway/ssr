package ru.croc.ugd.ssr.service.offerletterparsing;

import ru.croc.ugd.ssr.dto.offerletterparsing.RestOfferLetterParsingDto;

/**
 * Сервис для работы с разборами писем с предложением.
 */
public interface RestOfferLetterParsingService {

    /**
     * Получить данные разбора письма с предложением.
     *
     * @param id ИД документа разбора письма с предложением
     * @return разбор письма с предложением
     */
    RestOfferLetterParsingDto fetchById(final String id);
}
