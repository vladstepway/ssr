package ru.croc.ugd.ssr.service.emailnotificant;

import lombok.Builder;
import lombok.Value;

/**
 * Класс элемента справочника сотрудников для уведомления о событиях.
 */
@Value
@Builder
public class EmailNotificant {

    /**
     * Код округа.
     */
    private final String areaCode;
    /**
     * Округ.
     */
    private final String area;
    /**
     * ФИО.
     */
    private final String fullName;
    /**
     * Должность.
     */
    private final String position;
    /**
     * Отдел.
     */
    private final String department;
    /**
     * Адрес электронной почты.
     */
    private final String email;
    /**
     * Тип уведомления.
     */
    private final NotificationType notificationType;
    /**
     * Всегда ставить в копию.
     * Сотрудник с указанным признаком всегда ставится в копию в отправляемом письме по указанному коду уведомления.
     */
    private final boolean cc;
    /**
     * Не используется, поле для корректной работы справочника.
     */
    private final Integer code;
}
