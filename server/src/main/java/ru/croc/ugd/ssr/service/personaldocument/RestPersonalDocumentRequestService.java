package ru.croc.ugd.ssr.service.personaldocument;

import ru.croc.ugd.ssr.dto.personaldocument.RestCreatePersonalDocumentRequestDto;
import ru.croc.ugd.ssr.dto.personaldocument.RestPersonalDocumentRequestDto;

import java.util.List;

/**
 * Сервис для работы с запросами документов.
 */
public interface RestPersonalDocumentRequestService {

    /**
     * Создать запрос.
     *
     * @param body тело запроса
     * @return запрос документов
     */
    RestPersonalDocumentRequestDto create(final RestCreatePersonalDocumentRequestDto body);

    /**
     * Получить запросы документов по жителю.
     *
     * @param personDocumentId ИД документа жителя
     * @return запросы документов
     */
    List<RestPersonalDocumentRequestDto> findAll(final String personDocumentId);
}
