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
 * Сведения о выписке по помещению
 *
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UgdSsrEgrnFlatRequestStatement extends SolrDocument implements Serializable {

    /**
     * Адрес квартиры
     *
     */
    @JsonProperty("ugd_ssr_egrn_flat_request_statement_address")
    private String ugdSsrEgrnFlatRequestStatementAddress;
    /**
     * Кадастровый номер квартиры
     *
     */
    @JsonProperty("ugd_ssr_egrn_flat_request_statement_cadastral_number")
    private String ugdSsrEgrnFlatRequestStatementCadastralNumber;
    /**
     * Номер квартиры
     *
     */
    @JsonProperty("ugd_ssr_egrn_flat_request_statement_flat_number")
    private String ugdSsrEgrnFlatRequestStatementFlatNumber;
    /**
     * УНОМ дома
     *
     */
    @JsonProperty("ugd_ssr_egrn_flat_request_statement_unom")
    private String ugdSsrEgrnFlatRequestStatementUnom;
    /**
     * Единый номер обращения
     *
     */
    @JsonProperty("ugd_ssr_egrn_flat_request_statement_service_number")
    private String ugdSsrEgrnFlatRequestStatementServiceNumber;
    /**
     * Дата запроса
     *
     */
    @JsonProperty("ugd_ssr_egrn_flat_request_statement_request_date_time")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime ugdSsrEgrnFlatRequestStatementRequestDateTime;
    /**
     * Статус запроса
     *
     */
    @JsonProperty("ugd_ssr_egrn_flat_request_statement_status")
    private String ugdSsrEgrnFlatRequestStatementStatus;
    /**
     * Сообщение об ошибке
     *
     */
    @JsonProperty("ugd_ssr_egrn_flat_request_statement_error_description")
    private String ugdSsrEgrnFlatRequestStatementErrorDescription;
    /**
     * Дата выписки
     *
     */
    @JsonProperty("ugd_ssr_egrn_flat_request_statement_response_date_time")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime ugdSsrEgrnFlatRequestStatementResponseDateTime;
    /**
     * Тип помещения
     *
     */
    @JsonProperty("ugd_ssr_egrn_flat_request_statement_flat_type")
    private String ugdSsrEgrnFlatRequestStatementFlatType;
    /**
     * Выписка
     *
     */
    @JsonProperty("ugd_ssr_egrn_flat_request_statement_pdf_filestore_id")
    private String ugdSsrEgrnFlatRequestStatementPdfFilestoreId;
    /**
     * Пакет ответа
     *
     */
    @JsonProperty("ugd_ssr_egrn_flat_request_statement_zip_filestore_id")
    private String ugdSsrEgrnFlatRequestStatementZipFilestoreId;
}

