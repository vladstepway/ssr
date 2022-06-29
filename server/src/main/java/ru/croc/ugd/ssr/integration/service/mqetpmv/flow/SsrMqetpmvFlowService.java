package ru.croc.ugd.ssr.integration.service.mqetpmv.flow;

import ru.croc.ugd.ssr.dto.mqetpmv.flow.ExportFlowMessageDto;
import ru.croc.ugd.ssr.dto.mqetpmv.flow.FlowTypeDto;
import ru.croc.ugd.ssr.dto.mqetpmv.flow.ImportFlowMessageDto;
import ru.croc.ugd.ssr.integrationflow.IntegrationFlowType;
import ru.croc.ugd.ssr.integrationflow.MfrFlowData;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateSendTaskStatusesMessage;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateTaskData;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateTaskMessage;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateTaskStatusData;
import ru.croc.ugd.ssr.model.integration.etpmv.SendTasksMessage;
import ru.croc.ugd.ssr.model.integrationflow.IntegrationFlowDocument;

import java.util.function.Function;
import java.util.function.Supplier;

public interface SsrMqetpmvFlowService {

    /**
     * Отправить сообщение через mqetpmv.
     *
     * @param etpProfile профиль
     * @param message сообщение
     * @param eno ЕНО сообщения
     * @param serviceCode код госуслуги
     * @param integrationFlowType тип потока
     * @param flowMessageDirectory каталог для сохранения сообщения
     * @return данные об отправленном сообщении
     */
    ExportFlowMessageDto sendFlowMessage(
        final String etpProfile,
        final String message,
        final String eno,
        final String serviceCode,
        final IntegrationFlowType integrationFlowType,
        final String flowMessageDirectory
    );

    /**
     * Отправить сообщение по потоку МФР через mqetpmv.
     *
     * @param etpProfile профиль
     * @param message сообщение
     * @param eno ЕНО сообщения
     * @param serviceCode код госуслуги
     * @param integrationFlowType тип потока
     * @param flowMessageDirectory каталог для сохранения сообщения
     * @param mfrFlowData данные по МФР-потоку
     * @return данные об отправленном сообщении
     */
    ExportFlowMessageDto sendFlowMessage(
        final String etpProfile,
        final String message,
        final String eno,
        final String serviceCode,
        final IntegrationFlowType integrationFlowType,
        final String flowMessageDirectory,
        final MfrFlowData mfrFlowData
    );

    /**
     * Отправить сообщение со статусом через mqetpmv.
     *
     * @param integrationFlowDocument документ сообщения по потоку
     * @param etpProfile профиль
     * @param eno ЕНО сообщения
     * @param statusCode код статуса
     * @param statusTitle наименование статуса
     * @param flowStatusMessageDirectory каталог для сохранения сообщения
     * @return данные об отправленном сообщении
     */
    ExportFlowMessageDto sendFlowStatusMessage(
        final IntegrationFlowDocument integrationFlowDocument,
        final String etpProfile,
        final String eno,
        final Integer statusCode,
        final String statusTitle,
        final String flowStatusMessageDirectory
    );

    /**
     * Принять и обработать полученное сообщение.
     *
     * @param messageType тип сообщения
     * @param message сообщение
     * @param serviceCode код госуслуги
     * @param integrationFlowType тип потока
     * @param flowMessageDirectory каталог для сохранения полученного сообщения
     * @param parseCoordinateTaskMessage функция разбора сообщения
     * @return данные о полученном сообщении
     */
    ImportFlowMessageDto receiveFlowCoordinateTaskMessage(
        final String messageType,
        final String message,
        final String serviceCode,
        final IntegrationFlowType integrationFlowType,
        final String flowMessageDirectory,
        final Function<String, CoordinateTaskMessage> parseCoordinateTaskMessage
    );

    /**
     * Принять и обработать полученное сообщение.
     *
     * @param messageType тип сообщения
     * @param message сообщение
     * @param flowMessageDirectory каталог для сохранения полученного сообщения
     * @param parseCoordinateTaskMessage функция разбора сообщения
     * @param retrieveFlowTypeDto функция определения типа потока
     * @return данные о полученном сообщении
     */
    ImportFlowMessageDto receiveFlowCoordinateTaskMessage(
        final String messageType,
        final String message,
        final String flowMessageDirectory,
        final Function<String, CoordinateTaskMessage> parseCoordinateTaskMessage,
        final Function<CoordinateTaskData, FlowTypeDto> retrieveFlowTypeDto
    );

    /**
     * Принять и обработать полученное сообщение.
     *
     * @param messageType тип сообщения
     * @param message сообщение
     * @param serviceCode код госуслуги
     * @param integrationFlowType тип потока
     * @param flowMessageDirectory каталог для сохранения полученного сообщения
     * @param parseSendTasksMessage функция разбора сообщения
     * @return данные о полученном сообщении
     */
    ImportFlowMessageDto receiveFlowSendTasksMessage(
        final String messageType,
        final String message,
        final String serviceCode,
        final IntegrationFlowType integrationFlowType,
        final String flowMessageDirectory,
        final Function<String, SendTasksMessage> parseSendTasksMessage
    );

    /**
     * Принять и сохранить в очередь сообщение.
     *
     * @param messageType тип сообщения
     * @param message сообщение
     * @param serviceCode код госуслуги
     * @param integrationFlowType тип потока
     * @param flowMessageDirectory каталог для сохранения полученного сообщения
     * @param parseSendTasksMessage функция разбора сообщения
     * @param isPartOfAffairCollation функция для определения принадлежности сообщения к процессу перезапроса
     * @param messageClassesSupplier поставщик классов для десериализации сообщения
     */
    void storeInFlowSendTasksMessage(
        final String messageType,
        final String message,
        final String serviceCode,
        final IntegrationFlowType integrationFlowType,
        final String flowMessageDirectory,
        final Function<String, SendTasksMessage> parseSendTasksMessage,
        final Function<CoordinateTaskData, Boolean> isPartOfAffairCollation,
        final Supplier<Class<?>[]> messageClassesSupplier
    );

    /**
     * Принять и сохранить в очередь сообщение.
     *
     * @param messageType тип сообщения
     * @param message сообщение
     * @param serviceCode код госуслуги
     * @param integrationFlowType тип потока
     * @param flowMessageDirectory каталог для сохранения полученного сообщения
     * @param parseCoordinateSendTaskStatusesMessage функция разбора сообщения
     * @param isPartOfAffairCollation функция для определения принадлежности сообщения к процессу перезапроса
     * @param messageClassesSupplier поставщик классов для десериализации сообщения
     */
    void storeInQueueCoordinateSendTaskStatusesMessage(
        final String messageType,
        final String message,
        final String serviceCode,
        final IntegrationFlowType integrationFlowType,
        final String flowMessageDirectory,
        final Function<String, CoordinateSendTaskStatusesMessage> parseCoordinateSendTaskStatusesMessage,
        final Function<CoordinateTaskStatusData, Boolean> isPartOfAffairCollation,
        final Supplier<Class<?>[]> messageClassesSupplier
    );

    /**
     * Принять и сохранить в очередь сообщение.
     *
     * @param messageType тип сообщения
     * @param message сообщение
     * @param serviceCode код госуслуги
     * @param integrationFlowType тип потока
     * @param flowMessageDirectory каталог для сохранения полученного сообщения
     * @param parseCoordinateTaskMessage функция разбора сообщения
     * @param messageClassesSupplier поставщик классов для десериализации сообщения
     */
    void storeInQueueCoordinateTaskMessage(
        final String messageType,
        final String message,
        final String serviceCode,
        final IntegrationFlowType integrationFlowType,
        final String flowMessageDirectory,
        final Function<String, CoordinateTaskMessage> parseCoordinateTaskMessage,
        final Supplier<Class<?>[]> messageClassesSupplier
    );

    /**
     * Принять и обработать полученное сообщение со статусом.
     *
     * @param messageType тип сообщения
     * @param message сообщение со статусом
     * @param flowStatusMessageDirectory каталог для сохранения полученного сообщения
     */
    void receiveFlowStatusMessage(
        final String messageType, final String message, final String flowStatusMessageDirectory
    );
}
