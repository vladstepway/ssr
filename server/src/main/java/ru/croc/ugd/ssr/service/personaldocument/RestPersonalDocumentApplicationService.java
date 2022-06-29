package ru.croc.ugd.ssr.service.personaldocument;

import ru.croc.ugd.ssr.dto.personaldocument.RestPersonalDocumentApplicationDto;

import java.util.List;

/**
 * Сервис для работы с заявлениями на предоставление документов.
 */
public interface RestPersonalDocumentApplicationService {

    /**
     * Получить заявление.
     *
     * @param id ИД документа заявления
     * @return заявление на предоставление документов
     */
    RestPersonalDocumentApplicationDto fetchById(final String id);

    /**
     * Принять документы, поступившие в рамках заявления.
     *
     * @param id ИД документа заявления
     */
    void acceptById(final String id);

    /**
     * Получить заявления на предоставление документов по жителю.
     *
     * @param personDocumentId ИД документа жителя
     * @param includeRequestedApplications фильтр наличия запроса по заявлению
     * @return заявления на предоставление документов
     */
    List<RestPersonalDocumentApplicationDto> findAll(
        final String personDocumentId, final boolean includeRequestedApplications
    );

    /**
     * Отправить письмо о поступлении заявления.
     *
     * @param id ИД заявления
     */
    void sendEmail(final String id);
}
