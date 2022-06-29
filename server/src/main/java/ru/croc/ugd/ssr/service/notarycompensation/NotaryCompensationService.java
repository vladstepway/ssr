package ru.croc.ugd.ssr.service.notarycompensation;

import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateMessage;

/**
 * Сервис для работы с заявлениями на возмещение оплаты услуг нотариуса.
 */
public interface NotaryCompensationService {

    /**
     * Зарегистрировать заявление на возмещение оплаты услуг нотариуса из очереди.
     *
     * @param coordinateMessage сообщение из очереди
     */
    void processRegistration(final CoordinateMessage coordinateMessage);

}
