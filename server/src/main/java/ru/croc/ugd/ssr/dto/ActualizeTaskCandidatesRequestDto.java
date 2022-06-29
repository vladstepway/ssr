package ru.croc.ugd.ssr.dto;

import lombok.Value;

/**
 * Запрос на актуализацию списка кандидатов по задаче.
 */
@Value
public class ActualizeTaskCandidatesRequestDto {

    /**
     * Идентификатор активной задачи.
     */
    private final String processInstanceId;

    /**
     * Список LDAP групп через запятую.
     */
    private final String ldapGroups;

    /**
     * Список логинов через запятую.
     */
    private final String logins;

}
