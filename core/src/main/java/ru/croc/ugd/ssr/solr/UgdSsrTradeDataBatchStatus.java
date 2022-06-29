
package ru.croc.ugd.ssr.solr;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.reinform.cdp.search.model.document.SolrDocument;


/**
 * Информация о загруженной партии данных о сделках.
 * 
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UgdSsrTradeDataBatchStatus
    extends SolrDocument
    implements Serializable
{

    /**
     * Название загруженного файла в системе FileStore
     * 
     */
    @JsonProperty("ugd_ssr_trade_addition_batch_status_file_name")
    private String ugdSsrTradeAdditionBatchStatusFileName;

}
