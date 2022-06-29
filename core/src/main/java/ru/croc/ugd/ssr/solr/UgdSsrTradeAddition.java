
package ru.croc.ugd.ssr.solr;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.reinform.cdp.search.model.document.SolrDocument;


/**
 * Реестры по докупке и компенсации
 * 
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UgdSsrTradeAddition
    extends SolrDocument
    implements Serializable
{

    /**
     * Номер заявления
     * 
     */
    @JsonProperty("ugd_ssr_trade_addition_application_number")
    private String ugdSsrTradeAdditionApplicationNumber;
    /**
     * Адрес заселения
     * 
     */
    @JsonProperty("ugd_ssr_trade_addition_address_to")
    private String ugdSsrTradeAdditionAddressTo;
    /**
     * Адрес отселения
     * 
     */
    @JsonProperty("ugd_ssr_trade_addition_address_from")
    private String ugdSsrTradeAdditionAddressFrom;
    /**
     * Статус
     * 
     */
    @JsonProperty("ugd_ssr_trade_addition_status")
    private String ugdSsrTradeAdditionStatus;
    /**
     * Тип сделки
     * 
     */
    @JsonProperty("ugd_ssr_trade_addition_type")
    private String ugdSsrTradeAdditionType;
    /**
     * ФИО заявителя
     * 
     */
    @JsonProperty("ugd_ssr_trade_addition_fio")
    private String ugdSsrTradeAdditionFio;
    /**
     * Проиндексирован
     * 
     */
    @JsonProperty("ugd_ssr_trade_addition_indexed")
    private Boolean ugdSsrTradeAdditionIndexed;

}
