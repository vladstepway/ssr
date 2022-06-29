
package ru.croc.ugd.ssr.solr;

import java.io.Serializable;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.reinform.cdp.search.model.document.SolrDocument;


/**
 * Заявление на помощь в переезде
 * 
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UgdSsrShippingApplication
    extends SolrDocument
    implements Serializable
{

    /**
     * Бригада
     * 
     */
    @JsonProperty("ugd_ssr_shipping_application_brigade")
    private String ugdSsrShippingApplicationBrigade;
    /**
     * Источник
     * 
     */
    @JsonProperty("ugd_ssr_shipping_application_source")
    private String ugdSsrShippingApplicationSource;
    /**
     * Район
     * 
     */
    @JsonProperty("ugd_ssr_shipping_application_district")
    private String ugdSsrShippingApplicationDistrict;
    /**
     * Дата подачи
     * 
     */
    @JsonProperty("ugd_ssr_shipping_application_created_at")
    @JsonSerialize(using = ru.reinform.cdp.search.util.LocalDateSerializer.class)
    private LocalDate ugdSsrShippingApplicationCreatedAt;
    /**
     * Дата переезда
     * 
     */
    @JsonProperty("ugd_ssr_shipping_application_shipping_date")
    @JsonSerialize(using = ru.reinform.cdp.search.util.LocalDateSerializer.class)
    private LocalDate ugdSsrShippingApplicationShippingDate;
    /**
     * Адрес (откуда)
     * 
     */
    @JsonProperty("ugd_ssr_shipping_application_address_from")
    private String ugdSsrShippingApplicationAddressFrom;
    /**
     * Адрес (куда)
     * 
     */
    @JsonProperty("ugd_ssr_shipping_application_address_to")
    private String ugdSsrShippingApplicationAddressTo;
    /**
     * Номер заявления
     * 
     */
    @JsonProperty("ugd_ssr_shipping_application_eno")
    private String ugdSsrShippingApplicationEno;
    /**
     * Фамилия, имя, отчество
     * 
     */
    @JsonProperty("ugd_ssr_shipping_application_full_name")
    private String ugdSsrShippingApplicationFullName;
    /**
     * Статус
     * 
     */
    @JsonProperty("ugd_ssr_shipping_application_status")
    private String ugdSsrShippingApplicationStatus;
    /**
     * Дата и время переезда
     * 
     */
    @JsonProperty("ugd_ssr_shipping_application_move_date_time")
    private String ugdSsrShippingApplicationMoveDateTime;
    /**
     * Номера уведомлений
     * 
     */
    @JsonProperty("ugd_ssr_shipping_application_message_eno_list")
    private String ugdSsrShippingApplicationMessageEnoList;

}
