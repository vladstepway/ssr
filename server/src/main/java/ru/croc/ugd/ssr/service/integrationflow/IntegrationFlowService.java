package ru.croc.ugd.ssr.service.integrationflow;

import ru.croc.ugd.ssr.dto.mqetpmv.flow.FlowTypeDto;
import ru.croc.ugd.ssr.integrationflow.IntegrationFlowStatus;
import ru.croc.ugd.ssr.integrationflow.MfrFlowData;
import ru.croc.ugd.ssr.model.integrationflow.IntegrationFlowDocument;

/**
 * Сервис для работы с сообщениями по потокам.
 */
public interface IntegrationFlowService {

    /**
     * Создать документ сообщения по потоку.
     *
     * @param eno ЕНО сообщения
     * @param flowTypeDto данные о типе потока
     * @param message сообщение
     * @param integrationFlowStatus статус потока
     * @return документ сообщения по потоку
     */
    IntegrationFlowDocument createDocument(
        final String eno,
        final FlowTypeDto flowTypeDto,
        final String message,
        final IntegrationFlowStatus integrationFlowStatus
    );

    /**
     * Создать документ сообщения, содержащий дополнительные данные по МФР-потоку.
     *
     * @param eno ЕНО сообщения
     * @param flowTypeDto данные о типе потока
     * @param message сообщение
     * @param integrationFlowStatus статус потока
     * @param mfrFlowData данные по МФР-потоку
     * @return документ сообщения по потоку
     */
    IntegrationFlowDocument createDocument(
        final String eno,
        final FlowTypeDto flowTypeDto,
        final String message,
        final IntegrationFlowStatus integrationFlowStatus,
        final MfrFlowData mfrFlowData
    );

    /**
     * Сохранить полученный статус по сообщению.
     *
     * @param eno ЕНО сообщения
     * @param message сообщение со статусом
     * @param statusCode код статуса
     * @param statusTitle наименование статуса
     */
    void saveReceivedStatus(final String eno, final String message, final String statusCode, final String statusTitle);

    /**
     * Сохранить подготовленный статус по сообщению.
     *
     * @param integrationFlowDocument  документ сообщения по потоку
     * @param message сообщение со статусом
     * @param statusCode код статуса
     * @param statusTitle наименование статуса
     */
    void savePreparedStatus(
        final IntegrationFlowDocument integrationFlowDocument,
        final String message,
        final String statusCode,
        final String statusTitle
    );

    /**
     * Проверить наличие сообщения по потоку МФР №3 (Сведения о проекте договора при равнозначном переселении).
     *
     * @param affairId ИД семьи
     * @param contractOrderId ИД договора
     * @param contractProjectStatus статус проекта договора
     * @return сообщение по потоку МФР №3 было отправлено
     */
    boolean existsSentDgpToMfrContractProjectInfoFlow(
        final String affairId, final String contractOrderId, final String contractProjectStatus
    );

    /**
     * Проверить наличие сообщения по потоку МФР №4 (Сведения о договоре при равнозначном переселении).
     *
     * @param affairId ИД семьи
     * @param contractOrderId ИД договора
     * @param contractStatus статус договора
     * @return сообщение по потоку МФР №4 было отправлено
     */
    boolean existsSentDgpToMfrContractInfoFlow(
        final String affairId, final String contractOrderId, final String contractStatus
    );

    /**
     * Проверить наличие сообщения по потоку МФР №2 (Сведения о РД при равнозначном переселении).
     *
     * @param affairId ИД семьи
     * @param letterId ИД письма
     * @return сообщение по потоку МФР №2 было отправлено
     */
    boolean existsSentDgpToMfrAdministrativeDocumentsInfoFlow(final String affairId, final String letterId);

    /**
     * Обновить документ с отправленным сообщением по потоку.
     *
     * @param integrationFlowDocument документ сообщения по потоку
     * @param etpMessageId ИД сообщения в ЕТП
     */
    void updateSentDocument(final IntegrationFlowDocument integrationFlowDocument, final String etpMessageId);

    /**
     * Обновить документ с отправленным статусом по потоку.
     *
     * @param integrationFlowDocument документ сообщения по потоку
     * @param statusCode код статуса
     * @param etpMessageId ИД сообщения в ЕТП
     */
    void updateDocumentWithSentStatus(
        final IntegrationFlowDocument integrationFlowDocument, final String statusCode, final String etpMessageId
    );
}
