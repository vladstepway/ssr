
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
 * Центры информирования по переселению
 * 
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UgdSsrCip
    extends SolrDocument
    implements Serializable
{

    /**
     * Код ЦИП
     * 
     */
    @JsonProperty("ugd_ssr_cip_code")
    private String ugdSsrCipCode;
    /**
     * Телефон ЦИП
     * 
     */
    @JsonProperty("ugd_ssr_cip_phone")
    private String ugdSsrCipPhone;
    /**
     * Дата начала работы ЦИП
     * 
     */
    @JsonProperty("ugd_ssr_cip_start_date")
    @JsonSerialize(using = ru.reinform.cdp.search.util.LocalDateSerializer.class)
    private LocalDate ugdSsrCipStartDate;
    /**
     * Район
     * 
     */
    @JsonProperty("ugd_ssr_cip_district")
    private String ugdSsrCipDistrict;
    /**
     * Округ
     * 
     */
    @JsonProperty("ugd_ssr_cip_area")
    private String ugdSsrCipArea;
    /**
     * Причина удаления
     * 
     */
    @JsonProperty("ugd_ssr_cip_del_reason")
    private String ugdSsrCipDelReason;
    /**
     * Режим работы
     * 
     */
    @JsonProperty("ugd_ssr_cip_work_time")
    private String ugdSsrCipWorkTime;
    /**
     * Статус ЦИП
     * 
     */
    @JsonProperty("ugd_ssr_cip_status")
    private String ugdSsrCipStatus;
    /**
     * Количество расселяемых домов
     * 
     */
    @JsonProperty("ugd_ssr_cip_resettelment_count")
    private Long ugdSsrCipResettelmentCount;
    /**
     * Количество заселяемых домов
     * 
     */
    @JsonProperty("ugd_ssr_cip_settelment_count")
    private Long ugdSsrCipSettelmentCount;
    /**
     * Адрес ЦИП
     * 
     */
    @JsonProperty("ugd_ssr_cip_address")
    private String ugdSsrCipAddress;
    /**
     * Дата окончания работы ЦИП
     * 
     */
    @JsonProperty("ugd_ssr_cip_end_date")
    @JsonSerialize(using = ru.reinform.cdp.search.util.LocalDateSerializer.class)
    private LocalDate ugdSsrCipEndDate;

}
