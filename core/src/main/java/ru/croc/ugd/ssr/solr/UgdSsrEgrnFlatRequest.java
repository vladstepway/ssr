package ru.croc.ugd.ssr.solr;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.reinform.cdp.search.model.document.SolrDocument;

import java.io.Serializable;

/**
 * Сведения о запрошенных квартирах
 *
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UgdSsrEgrnFlatRequest
    extends SolrDocument
    implements Serializable {

    /**
     * Адрес квартиры
     *
     */
    @JsonProperty("ugd_ssr_egrn_flat_address")
    private String ugdSsrEgrnFlatAddress;
    /**
     * Кадастровый номер квартиры
     *
     */
    @JsonProperty("ugd_ssr_egrn_flat_cadastral_number")
    private String ugdSsrEgrnFlatCadastralNumber;
    /**
     * ID документа ОКСа
     *
     */
    @JsonProperty("ugd_ssr_egrn_flat_cco_id")
    private String ugdSsrEgrnFlatCcoId;
    /**
     * Этаж
     *
     */
    @JsonProperty("ugd_ssr_egrn_flat_floor")
    private Long ugdSsrEgrnFlatFloor;
    /**
     * Номер квартиры
     *
     */
    @JsonProperty("ugd_ssr_egrn_flat_number")
    private String ugdSsrEgrnFlatNumber;
    /**
     * ID документа дома
     *
     */
    @JsonProperty("ugd_ssr_egrn_flat_real_estate_id")
    private String ugdSsrEgrnFlatRealEstateId;
    /**
     * Номер комнаты
     *
     */
    @JsonProperty("ugd_ssr_egrn_flat_room_number")
    private String ugdSsrEgrnFlatRoomNumber;
    /**
     * УНОМ дома
     *
     */
    @JsonProperty("ugd_ssr_egrn_flat_unom")
    private String ugdSsrEgrnFlatUnom;
    /**
     * Помещение отселено (для отселяемых) / заселено (для заселяемых)
     *
     */
    @JsonProperty("ugd_ssr_egrn_flat_resettled")
    private Boolean ugdSsrEgrnFlatResettled;
}

