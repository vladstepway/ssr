
package ru.croc.ugd.ssr.solr;

import java.io.Serializable;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.reinform.cdp.search.model.document.SolrDocument;


/**
 * Запрос на скорое переселение домов
 * 
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UgdSsrSoonResettlementRequest
    extends SolrDocument
    implements Serializable
{

    /**
     * ID запроса
     * 
     */
    @JsonProperty("ugd_ssr_soon_resettlement_request_id")
    private String ugdSsrSoonResettlementRequestId;
    /**
     * Идентификаторы ОН
     * 
     */
    @JsonProperty("ugd_ssr_soon_resettlement_request_real_estates")
    private List<String> ugdSsrSoonResettlementRequestRealEstates;

}
