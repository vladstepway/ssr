package ru.croc.ugd.ssr.builder;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.IntegrationLog;
import ru.croc.ugd.ssr.PersonType;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Строитель объектов интеграционных событий в Person и ApartmentInspection.
 */
//TODO: Get rid of spring bean management on builder
@Component
@Scope(scopeName = SCOPE_PROTOTYPE)
public class IntegrationLogBuilder {

    private LocalDateTime eventDateTime;

    private String messageId;

    private String eventId;

    private String eno;

    private String fileId;

    /**
     * Конструктор.
     */
    public IntegrationLogBuilder() {
        eventDateTime = LocalDateTime.now();
        messageId = UUID.randomUUID().toString();
    }

    /**
     * Добавление время события.
     *
     * @param localDateTime время события.
     * @return строитель.
     */
    public IntegrationLogBuilder addEventDateTime(LocalDateTime localDateTime) {
        this.eventDateTime = localDateTime;
        return this;
    }

    /**
     * Добавление идентификатора события.
     *
     * @param eventId ид события.
     * @return строитель
     */
    public IntegrationLogBuilder addEventId(String eventId) {
        this.eventId = eventId;
        return this;
    }

    /**
     * Добавление имдентификатора.
     *
     * @param messageId идентификатор.
     * @return строитель.
     */
    public IntegrationLogBuilder addMessageId(String messageId) {
        this.messageId = messageId;
        return this;
    }

    /**
     * Добавление eno.
     *
     * @param eno eno.
     * @return строитель.
     */
    public IntegrationLogBuilder addEno(String eno) {
        this.eno = eno;
        return this;
    }

    /**
     * Добавление fileId.
     *
     * @param fileId fileId.
     * @return строитель.
     */
    public IntegrationLogBuilder addFileId(String fileId) {
        this.fileId = fileId;
        return this;
    }

    /**
     * Создание объекта.
     *
     * @return объект истории.
     */
    public IntegrationLog.Item build() {
        final IntegrationLog.Item item = new IntegrationLog.Item();
        item.setEventDateTime(eventDateTime);
        item.setEventId(eventId);
        item.setMessageID(messageId);
        item.setEno(eno);
        item.setFileId(fileId);
        return item;
    }

}
