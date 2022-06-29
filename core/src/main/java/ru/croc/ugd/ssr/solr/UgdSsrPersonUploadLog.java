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
 * Журнал загрузки жителей
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class UgdSsrPersonUploadLog
    extends SolrDocument
    implements Serializable {

    /**
     * Дата загрузки
     *
     */
    @JsonProperty("ugd_ssr_person_upload_log_start_date")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime ugdSsrPersonUploadLogStartDate;
    /**
     * Пользователь
     *
     */
    @JsonProperty("ugd_ssr_person_upload_log_username")
    private String ugdSsrPersonUploadLogUsername;
    /**
     * Имя файла
     *
     */
    @JsonProperty("ugd_ssr_person_upload_log_filename")
    private String ugdSsrPersonUploadLogFilename;
    /**
     * Статус
     *
     */
    @JsonProperty("ugd_ssr_person_upload_log_status")
    private String ugdSsrPersonUploadLogStatus;
    /**
     * Дата завершения
     *
     */
    @JsonProperty("ugd_ssr_person_upload_log_end_date")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime ugdSsrPersonUploadLogEndDate;
    /**
     * Журнал загрузки
     *
     */
    @JsonProperty("ugd_ssr_person_upload_log_link")
    private String ugdSsrPersonUploadLogLink;

}
