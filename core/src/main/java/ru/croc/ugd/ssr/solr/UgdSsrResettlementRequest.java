
package ru.croc.ugd.ssr.solr;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.reinform.cdp.search.model.document.SolrDocument;


/**
 * Запрос на переселение домов/квартир
 * 
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UgdSsrResettlementRequest
    extends SolrDocument
    implements Serializable
{

    /**
     * Первая строка содержания списка Мои задачи
     * 
     */
    @JsonProperty("resettlementRequestFirstRow")
    private String resettlementRequestFirstRow;

}
