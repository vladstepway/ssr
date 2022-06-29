package ru.croc.ugd.ssr.solr;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.reinform.cdp.search.model.document.SolrDocument;

import java.io.Serializable;
import java.util.List;


/**
 * Реестр нотариусов
 * 
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UgdSsrNotary
    extends SolrDocument
    implements Serializable
{

    /**
     * ФИО нотариуса
     * 
     */
    @JsonProperty("ugd_ssr_notary_full_name")
    private String ugdSsrNotaryFullName;
    /**
     * Адрес
     * 
     */
    @JsonProperty("ugd_ssr_notary_office_address")
    private String ugdSsrNotaryOfficeAddress;
    /**
     * Дополнительная информация
     * 
     */
    @JsonProperty("ugd_ssr_notary_additional_info")
    private String ugdSsrNotaryAdditionalInfo;
    /**
     * Телефон
     * 
     */
    @JsonProperty("ugd_ssr_notary_phone")
    private String ugdSsrNotaryPhone;
    /**
     * Статус
     * 
     */
    @JsonProperty("ugd_ssr_notary_status")
    private String ugdSsrNotaryStatus;
    /**
     * Логины сотрудников
     *
     */
    @JsonProperty("ugd_ssr_notary_employee_logins")
    private List<String> ugdSsrNotaryEmployeeLogins;

}
