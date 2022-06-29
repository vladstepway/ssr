package ru.croc.ugd.ssr.solr;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.reinform.cdp.search.model.document.SolrDocument;
import ru.reinform.cdp.search.util.LocalDateTimeSerializer;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;


/**
 * Заявления на напись на посещение нотариуса
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class UgdSsrNotaryApplication
    extends SolrDocument
    implements Serializable {

    /**
     * Номер заявления
     *
     */
    @JsonProperty("ugd_ssr_notary_application_number")
    private String ugdSsrNotaryApplicationNumber;
    /**
     * Дата и время подачи заявления
     *
     */
    @JsonProperty("ugd_ssr_notary_application_creation_date_time")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime ugdSsrNotaryApplicationCreationDateTime;
    /**
     * Источник
     *
     */
    @JsonProperty("ugd_ssr_notary_application_source")
    private String ugdSsrNotaryApplicationSource;
    /**
     * ФИО клиента
     *
     */
    @JsonProperty("ugd_ssr_notary_application_person_full_name")
    private String ugdSsrNotaryApplicationPersonFullName;
    /**
     * ФИО нотариуса
     *
     */
    @JsonProperty("ugd_ssr_notary_application_notary_full_name")
    private String ugdSsrNotaryApplicationNotaryFullName;
    /**
     * Дата и время записи
     *
     */
    @JsonProperty("ugd_ssr_notary_application_appointment_date_time")
    private String ugdSsrNotaryApplicationAppointmentDateTime;
    /**
     * Адрес ресселяемого дома
     *
     */
    @JsonProperty("ugd_ssr_notary_application_address_from")
    private String ugdSsrNotaryApplicationAddressFrom;
    /**
     * Адрес заселяемого дома
     *
     */
    @JsonProperty("ugd_ssr_notary_application_address_to")
    private String ugdSsrNotaryApplicationAddressTo;
    /**
     * Статус
     *
     */
    @JsonProperty("ugd_ssr_notary_application_status")
    private String ugdSsrNotaryApplicationStatus;
    /**
     * Логины сотрудников
     *
     */
    @JsonProperty("ugd_ssr_notary_application_employee_logins")
    private List<String> ugdSsrNotaryApplicationEmployeeLogins;

}
