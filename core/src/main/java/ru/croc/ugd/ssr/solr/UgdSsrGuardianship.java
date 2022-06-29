package ru.croc.ugd.ssr.solr;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.reinform.cdp.search.model.document.SolrDocument;

import java.io.Serializable;

/**
 * Заявление жителя в органы опеки.
 *
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UgdSsrGuardianship
    extends SolrDocument
    implements Serializable {

    /**
     * ФИО заявителя
     */
    @JsonProperty("ugd_ssr_guardianship_fio")
    private String ugdSsrGuardianshipFio;
    /**
     * Адрес заселения
     */
    @JsonProperty("ugd_ssr_guardianship_address_to")
    private String ugdSsrGuardianshipAddressTo;

    @JsonProperty("ugd_ssr_guardianship_address_from")
    private String ugdSsrGuardianshipAddressFrom;

    @JsonProperty("ugd_ssr_guardianship_district")
    private String ugdSsrGuardianshipDistrict;

    @JsonProperty("ugd_ssr_guardianship_area")
    private String ugdSsrGuardianshipArea;
}
