package ru.croc.ugd.ssr.mq.listener;

import static java.util.Optional.ofNullable;

import lombok.extern.slf4j.Slf4j;
import ru.croc.ugd.ssr.integration.util.XmlUtils;
import ru.croc.ugd.ssr.integrationflow.IntegrationFlowQueueData;
import ru.croc.ugd.ssr.integrationflow.IntegrationFlowType;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateTaskData;
import ru.croc.ugd.ssr.model.integration.etpmv.TaskType;
import ru.reinform.cdp.mqetpmv.api.EtpMessageListener;

/**
 * Интерфейс слушателей очереди етпмв.
 * @param <T> тип сообщений.
 * @param <P> тип кастомного атрибута.
 */
@Slf4j
public abstract class EtpQueueListener<T, P> implements EtpMessageListener {

    /**
     * Код статуса при получении сообщения в рамках перезапросов.
     */
    static final Integer AFFAIR_COLLATION_STATUS_CODE = 9999;

    /**
     * Task_id при получении сообщения в рамках перезапросов.
     */
    static final String AFFAIR_COLLATION_TASK_ID = "00000000-0000-0000-0000-000000000000";

    public abstract IntegrationFlowType getIntegrationFlowType();

    /**
     * Метод обработки сообщения.
     *
     * @param message сообщение.
     */
    public abstract void handle(final String message);

    /**
     * Метод обработки сообщения из внутренней очереди.
     *
     * @param message сообщение.
     * @param integrationFlowQueueData сообщение из внутренней очереди.
     */
    public abstract void handle(final String message, final IntegrationFlowQueueData integrationFlowQueueData);

    /**
     * Метод обработки части сообщения, относящейся к конкретному жителю.
     *
     * @param message сообщение.
     * @param affairId ИД семьи.
     * @param personId ИД жителя.
     */
    public void handle(final String message, final String affairId, final String personId) {
        throw new UnsupportedOperationException("Partial message processing is not supported by default");
    }

    /**
     * Метод для получения классов для десериализации сообщения.
     *
     * @return классы для десериализации сообщения
     */
    public abstract Class<?>[] getMessageClasses();

    /**
     * Метод для определения принадлежности сообщения к процессу перезапроса.
     *
     * @param payload кастомный атрибут.
     * @return отправляем ли уведомления.
     */
    public abstract boolean isPartOfAffairCollation(final P payload);

    /**
     * Получение xmlUtils.
     *
     * @return xmlUtils
     */
    public abstract XmlUtils getXmlUtils();

    /**
     * Разбор сообщения.
     *
     * @param message message
     * @return десериализованное сообщение
     */
    protected T parseMessage(final String message) {
        return getXmlUtils().transformXmlToObject(message, getMessageClasses());
    }

    /**
     * Получение ено.
     *
     * @param coordinateTaskData coordinateTaskData
     * @return ено
     */
    protected String extractEno(final CoordinateTaskData coordinateTaskData) {
        return ofNullable(coordinateTaskData)
            .map(CoordinateTaskData::getTask)
            .map(TaskType::getTaskNumber)
            .orElse(null);
    }
}
