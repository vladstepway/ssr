package ru.croc.ugd.ssr.service.personaldocument;

import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateMessage;

/**
 * Сервис для работы с заявлениями на предоставление документов.
 */
public interface PersonalDocumentApplicationService {

    /**
     * Зарегистрировать заявление на предоставление документов из очереди.
     * @param coordinateMessage сообщение из очереди
     */
    void processRegistration(final CoordinateMessage coordinateMessage);
}
