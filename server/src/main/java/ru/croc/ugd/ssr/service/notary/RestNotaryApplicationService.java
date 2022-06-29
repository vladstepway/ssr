package ru.croc.ugd.ssr.service.notary;

import ru.croc.ugd.ssr.dto.notary.RestNotaryApplicationCreateUpdateRequestDto;
import ru.croc.ugd.ssr.dto.notary.RestNotaryApplicationResponseDto;

/**
 * Сервис для работы с заявлениями на посещение нотариуса.
 */
public interface RestNotaryApplicationService {

    /**
     * Получить карточку заявления.
     *
     * @param applicationId Ид заявки
     * @return карточка
     */
    RestNotaryApplicationResponseDto fetchById(final String applicationId);

    /**
     * Принять в работу.
     *
     * @param applicationId Ид заявки
     * @param comment коментарий
     */
    void accept(final String applicationId, final String comment);

    /**
     * Отказ в регистрации заявления.
     *
     * @param applicationId Ид заявки
     * @param comment коментарий
     */
    void decline(final String applicationId, final String comment);

    /**
     * Отказ в предоставлении услуги.
     *
     * @param applicationId Ид заявки.
     * @param comment Коментарий.
     */
    void refuse(final String applicationId, final String comment);

    /**
     * Отмена записи к нотариусу.
     *
     * @param applicationId Ид заявки
     * @param comment коментарий
     */
    void cancelByNotary(final String applicationId, final String comment);

    /**
     * Подтвердить готовность проекта договора.
     *
     * @param applicationId Ид заявки.
     * @param comment Коментарий.
     */
    void draftContractReady(final String applicationId, final String comment);

    /**
     * Создать заявку.
     *
     * @param body тело запроса
     * @return заявление
     */
    RestNotaryApplicationResponseDto create(RestNotaryApplicationCreateUpdateRequestDto body);

    /**
     * обновить заявку.
     *
     * @param applicationId ИД заявления
     * @param body тело запроса
     * @return заявление
     */
    RestNotaryApplicationResponseDto update(String applicationId, RestNotaryApplicationCreateUpdateRequestDto body);

    /**
     * Подтвердить комплектность документов.
     *
     * @param applicationId Ид заявки.
     */
    void confirmDocuments(final String applicationId);

    /**
     * Запросить дополнительные документы.
     *
     * @param applicationId Ид заявки.
     * @param comment Комментарий с перечнем документов.
     */
    void requestDocuments(final String applicationId, final String comment);

    /**
     * Начать повторный сбор документов.
     *
     * @param applicationId Ид заявки
     */
    void reCollectDocuments(final String applicationId);

    /**
     * Подтвердить готовность справок.
     *
     * @param applicationId Ид заявки.
     */
    void confirmRecollected(final String applicationId);

    /**
     * Договор заключён.
     *
     * @param applicationId Ид заявки.
     */
    void contractSigned(final String applicationId);

    /**
     * Требуется повторный прием.
     *
     * @param applicationId Ид заявки.
     */
    void requestReturnVisit(final String applicationId);

    /**
     * Delete all.
     */
    void deleteAll();

    /**
     * refuseNoDocuments.
     * @param applicationId applicationId
     */
    void refuseNoDocuments(final String applicationId);

    /**
     * refuseNoBooking.
     * @param applicationId applicationId
     */
    void refuseNoBooking(final String applicationId);

    /**
     * bookingClosed.
     * @param applicationId applicationId
     */
    void bookingClosed(final String applicationId);

    /**
     * appointmentReminder.
     * @param applicationId applicationId
     */
    void appointmentReminder(final String applicationId);

    /**
     * cancellationClosed.
     * @param applicationId applicationId
     */
    void cancellationClosed(final String applicationId);

    /**
     * processCancelByApplicant.
     * @param applicationId applicationId
     */
    void processCancelByApplicant(final String applicationId);

    /**
     * Получить содержимое ics файла.
     *
     * @param id ИД документа
     * @return содержимое ics файла
     */
    byte[] getIcsFileContent(final String id);
}
