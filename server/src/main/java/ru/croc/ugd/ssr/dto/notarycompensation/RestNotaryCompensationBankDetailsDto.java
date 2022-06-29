package ru.croc.ugd.ssr.dto.notarycompensation;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class RestNotaryCompensationBankDetailsDto {
    /**
     * ФИО получателя.
     */
    private final String recipientFio;
    /**
     * ИНН банка.
     */
    private final String inn;
    /**
     * КПП банка.
     */
    private final String kpp;
    /**
     * Расчетный счет заявителя.
     */
    private final String account;
    /**
     * БИК.
     */
    private final String bankBik;
    /**
     * Наименование банка.
     */
    private final String bankName;
    /**
     * Корреспондентский счет.
     */
    private final String correspondentAccount;
    /**
     * Банковская выписка.
     */
    private final String bankStatementFileLink;
}