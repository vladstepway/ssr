package ru.croc.ugd.ssr.integration.service.notification;

import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.dto.notary.NotaryApplicationFlowStatus;
import ru.croc.ugd.ssr.model.notary.NotaryApplicationDocument;
import ru.croc.ugd.ssr.notary.NotaryApplicationType;
import ru.croc.ugd.ssr.notary.NotaryType;

import java.util.Map;

/**
 * Сервис отправки сообщений в ЭЛК для записи на приём к натариусу.
 */
public interface NotaryApplicationElkNotificationService {

    /**
     * Отправка сообщений в ЭЛК для записи на приём к натариусу.
     *
     * @param status status
     * @param document document
     */
    void sendStatus(
        final NotaryApplicationFlowStatus status, final NotaryApplicationDocument document
    );

    /**
     * Отправка сообщений в ЭЛК для записи на приём к натариусу.
     *
     * @param status status
     * @param eno Eno
     */
    void sendStatus(final NotaryApplicationFlowStatus status, final String eno);

    /**
     * Отправляет сообщение в очередь BK.
     * @param message Сообщение.
     */
    void sendStatusToBk(final String message);

    /**
     * Отправляет сообщение в очередь BK.
     * @param message Сообщение.
     */
    void sendToBk(final String message);

    /**
     * Заполнение дополнительных параметров для формирования текста уведомления.
     * @param notaryApplicationData заявление на посещение нотариуса
     * @param requesterPersonType данные заявителя
     * @param ownerPersonType данные правообладателя
     * @param notaryType данные нотариуса
     * @return параметры шаблона
     */
    Map<String, String> getExtraTemplateParams(
        final NotaryApplicationType notaryApplicationData,
        final PersonType requesterPersonType,
        final PersonType ownerPersonType,
        final NotaryType notaryType
    );
}
