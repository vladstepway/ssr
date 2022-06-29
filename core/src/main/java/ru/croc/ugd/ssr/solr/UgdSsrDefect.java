
package ru.croc.ugd.ssr.solr;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.reinform.cdp.search.model.document.SolrDocument;


/**
 * Дефект квартиры
 * 
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UgdSsrDefect
    extends SolrDocument
    implements Serializable
{

    /**
     * Описание дефекта
     * 
     */
    @JsonProperty("ugd_ssr_defect_description")
    private String ugdSsrDefectDescription;
    /**
     * Элемент квартиры
     * 
     */
    @JsonProperty("ugd_ssr_defect_flat_element")
    private String ugdSsrDefectFlatElement;

}
