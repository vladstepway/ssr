
package ru.croc.ugd.ssr.solr;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.reinform.cdp.search.model.document.SolrDocument;


/**
 * Обработка ошибок загрузки данных из ДГИ
 * 
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UgdSsrFirstFlowErrorAnalytics
    extends SolrDocument
    implements Serializable
{

    /**
     * Первая строка содержания списка Мои задачи
     * 
     */
    @JsonProperty("firstRow")
    private String firstRow;
    /**
     * Вторая строка содержания списка Мои задачи
     * 
     */
    @JsonProperty("secondRow")
    private String secondRow;

}
