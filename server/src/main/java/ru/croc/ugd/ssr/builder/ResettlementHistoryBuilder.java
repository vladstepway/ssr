package ru.croc.ugd.ssr.builder;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.ResettlementHistory;

import java.time.LocalDateTime;

/**
 * Строитель объектов событий в Person.
 */
@Component
@Scope(scopeName = SCOPE_PROTOTYPE)
public class ResettlementHistoryBuilder {

    private LocalDateTime eventDateTime;

    private String eventId;

    private String dataId;

    private String annotation;

    /**
     * Конструктор.
     */
    public ResettlementHistoryBuilder() {
        eventDateTime = LocalDateTime.now();
    }

    /**
     * Добавление время события.
     *
     * @param localDateTime
     *            время события.
     * @return строитель.
     */
    public ResettlementHistoryBuilder addEventDateTime(LocalDateTime localDateTime) {
        this.eventDateTime = localDateTime;
        return this;
    }

    /**
     * Добавление идентификатора события.
     *
     * @param eventId
     *            ид события.
     * @return строитель
     */
    public ResettlementHistoryBuilder addEventId(String eventId) {
        this.eventId = eventId;
        return this;
    }

    /**
     * Добавление имдентификатора.
     *
     * @param dataId
     *            идентификатор.
     * @return строитель.
     */
    public ResettlementHistoryBuilder addDataId(String dataId) {
        this.dataId = dataId;
        return this;
    }

    /**
     * Добавление коментария.
     *
     * @param annotation
     *            коментарий.
     * @return комментарий.
     */
    public ResettlementHistoryBuilder addAnnotation(String annotation) {
        this.annotation = annotation;
        return this;
    }

    /**
     * Создание объекта.
     *
     * @return объект истории.
     */
    public ResettlementHistory build() {
        final ResettlementHistory resettlementHistory = new ResettlementHistory();
        resettlementHistory.setEventDateTime(eventDateTime);
        resettlementHistory.setAnnotation(annotation);
        resettlementHistory.setDataId(dataId);
        resettlementHistory.setEventId(eventId);
        return resettlementHistory;
    }
}
