package ru.croc.ugd.ssr.solr;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.reinform.cdp.search.model.document.SolrDocument;
import ru.reinform.cdp.search.util.LocalDateTimeSerializer;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Сведения о запрошенных зданиях
 *
 */
@NoArgsConstructor
@Getter
@Setter
public class UgdSsrEgrnBuildingRequest extends SolrDocument implements Serializable {

    /**
     * Единый номер обращения
     *
     */
    @JsonProperty("ugd_ssr_egrn_building_request_service_number")
    private String ugdSsrEgrnBuildingRequestServiceNumber;
    /**
     * Дата запроса
     *
     */
    @JsonProperty("ugd_ssr_egrn_building_request_date_time")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime ugdSsrEgrnBuildingRequestDateTime;
    /**
     * Статус запроса
     *
     */
    @JsonProperty("ugd_ssr_egrn_building_request_status")
    private String ugdSsrEgrnBuildingRequestStatus;
    /**
     * Сообщение об ошибке
     *
     */
    @JsonProperty("ugd_ssr_egrn_building_request_error_description")
    private String ugdSsrEgrnBuildingRequestErrorDescription;
    /**
     * Дата выписки
     *
     */
    @JsonProperty("ugd_ssr_egrn_building_request_statement_date_time")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime ugdSsrEgrnBuildingRequestStatementDateTime;
    /**
     * Адрес здания
     *
     */
    @JsonProperty("ugd_ssr_egrn_building_request_address")
    private String ugdSsrEgrnBuildingRequestAddress;
    /**
     * UNOM
     *
     */
    @JsonProperty("ugd_ssr_egrn_building_request_unom")
    private String ugdSsrEgrnBuildingRequestUnom;
    /**
     * Кадастровый номер
     *
     */
    @JsonProperty("ugd_ssr_egrn_building_request_cadastral_number")
    private String ugdSsrEgrnBuildingRequestCadastralNumber;
    /**
     * Выписка
     *
     */
    @JsonProperty("ugd_ssr_egrn_building_request_pdf_filestore_id")
    private String ugdSsrEgrnBuildingRequestPdfFilestoreId;
    /**
     * Пакет ответа
     *
     */
    @JsonProperty("ugd_ssr_egrn_building_request_zip_filestore_id")
    private String ugdSsrEgrnBuildingRequestZipFilestoreId;
}
