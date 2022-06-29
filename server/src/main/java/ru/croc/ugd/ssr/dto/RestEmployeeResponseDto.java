package ru.croc.ugd.ssr.dto;

import lombok.Builder;
import lombok.Value;

/**
 * Данные сотрудника.
 */
@Value
@Builder
public class RestEmployeeResponseDto {

    /**
     * Логин.
     */
    private final String login;
    /**
     * ФИО пользователя.
     */
    private final String fullName;
    /**
     * Наименование организации пользователя.
     */
    private final String organisation;
    /**
     * Должность пользователя.
     */
    private final String position;
    /**
     * Адрес эл. почты пользователя.
     */
    private final String mail;
}
