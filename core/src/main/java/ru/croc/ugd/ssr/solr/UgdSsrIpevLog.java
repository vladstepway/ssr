package ru.croc.ugd.ssr.solr;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.reinform.cdp.search.model.document.SolrDocument;
import ru.reinform.cdp.search.util.LocalDateTimeSerializer;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Журнал обмена с ИПЭВ
 *
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UgdSsrIpevLog extends SolrDocument implements Serializable {

    /**
     * Дата сообщения об изменении
     *
     */
    @JsonProperty("ugd_ssr_ipev_log_event_date_time")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime ugdSsrIpevLogEventDateTime;
    /**
     * Количество кадастровых номеров с изменениями
     *
     */
    @JsonProperty("ugd_ssr_ipev_log_cadastral_numbers_count")
    private Long ugdSsrIpevLogCadastralNumbersCount;
    /**
     * Количество кадастровых номеров с изменениями по которым запрошено обновление данных
     *
     */
    @JsonProperty("ugd_ssr_ipev_log_filtered_cadastral_numbers_count")
    private Long ugdSsrIpevLogFilteredCadastralNumbersCount;
    /**
     * Дата запроса на обновление данных
     *
     */
    @JsonProperty("ugd_ssr_ipev_log_egrn_request_date_time")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime ugdSsrIpevLogEgrnRequestDateTime;
    /**
     * Статус
     *
     */
    @JsonProperty("ugd_ssr_ipev_log_status")
    private String ugdSsrIpevLogStatus;
    /**
     * Пакет ответа
     *
     */
    @JsonProperty("ugd_ssr_ipev_log_orr_file_store_id")
    private String ugdSsrIpevLogOrrFileStoreId;
}
