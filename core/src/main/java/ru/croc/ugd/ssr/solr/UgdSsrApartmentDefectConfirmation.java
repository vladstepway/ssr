package ru.croc.ugd.ssr.solr;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.reinform.cdp.search.model.document.SolrDocument;

import java.io.Serializable;

/**
 * Подтверждение сведений об устранении дефектов
 *
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UgdSsrApartmentDefectConfirmation extends SolrDocument implements Serializable {

    /**
     * Адрес заселяемого дома
     *
     */
    @JsonProperty("ugd_ssr_apartment_defect_confirmation_address")
    private String ugdSsrApartmentDefectConfirmationAddress;

}
