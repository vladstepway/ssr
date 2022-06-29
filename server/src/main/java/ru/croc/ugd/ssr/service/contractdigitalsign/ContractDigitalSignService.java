package ru.croc.ugd.ssr.service.contractdigitalsign;

import ru.croc.ugd.ssr.contractdigitalsign.Owner;
import ru.croc.ugd.ssr.dto.ElkUserNotificationDto;
import ru.croc.ugd.ssr.model.contractDigitalSign.ContractDigitalSignDocument;

/**
 * Сервис для работы с многосторонним подписанием договора с использованием УКЭП.
 */
public interface ContractDigitalSignService {

    /**
     * Отправляет уведомления заявителям и правобладателям
     * о необходимости подписать договор с использованием УКЭП в рамках текущего дня.
     */
    void sendContractDigitalSignNotificationsForToday();

    /**
     * Обработка полученного статуса по уведомлению.
     *
     * @param owner  owner
     * @param elkUserNotificationDto elkUserNotificationDto
     * @param contractDigitalSignDocument contractDigitalSignDocument
     */
    void processElkUserNotificationStatus(
        final Owner owner,
        final ElkUserNotificationDto elkUserNotificationDto,
        final ContractDigitalSignDocument contractDigitalSignDocument
    );

    /**
     * Проверяет все ли стороны выполнили подписание, при необходимости формирует файл со всеми подписями
     * и направляет соответствующий статус.
     *
     * @param contractDigitalSignDocument contractDigitalSignDocument
     */
    void sendContractSignedStatusIfNeeded(final ContractDigitalSignDocument contractDigitalSignDocument);

    /**
     * Сохранение статуса по уведомлению.
     *
     * @param elkUserNotificationDto elkUserNotificationDto
     */
    void addNotificationStatusToContractDigitalSign(final ElkUserNotificationDto elkUserNotificationDto);
}
