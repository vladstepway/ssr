package ru.croc.ugd.ssr.solr;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.reinform.cdp.search.model.document.SolrDocument;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Сведения о запросе на сверку жителей
 *
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UgdSsrAffairCollation extends SolrDocument implements Serializable {

    /**
     * Дата и время запроса
     */
    @JsonProperty("ugd_ssr_affair_collation_request_date_time")
    @JsonSerialize(using = ru.reinform.cdp.search.util.LocalDateTimeSerializer.class)
    private LocalDateTime ugdSsrAffairCollationRequestDateTime;

    /**
     * UNOM отселяемого дома
     */
    @JsonProperty("ugd_ssr_affair_collation_real_estate_unom")
    private String ugdSsrAffairCollationRealEstateUnom;

    /**
     * Номер квартиры
     */
    @JsonProperty("ugd_ssr_affair_collation_flat_number")
    private String ugdSsrAffairCollationFlatNumber;

    /**
     * ИД семьи
     */
    @JsonProperty("ugd_ssr_affair_collation_affair_id")
    private String ugdSsrAffairCollationAffairId;

    /**
     * Дата и время ответа на запрос
     */
    @JsonProperty("ugd_ssr_affair_collation_response_date_time")
    @JsonSerialize(using = ru.reinform.cdp.search.util.LocalDateTimeSerializer.class)
    private LocalDateTime ugdSsrAffairCollationResponseDateTime;

    /**
     * Список ИД ServiceNumber поступивших в ответ
     */
    @JsonProperty("ugd_ssr_affair_collation_response_service_numbers")
    private List<String> ugdSsrAffairCollationResponseServiceNumbers;
}

