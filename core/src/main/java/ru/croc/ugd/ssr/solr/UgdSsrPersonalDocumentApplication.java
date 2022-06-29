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
 * Реестр заявлений на предоставление документов
 *
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UgdSsrPersonalDocumentApplication
    extends SolrDocument
    implements Serializable {

    /**
     * Номер заявления
     *
     */
    @JsonProperty("ugd_ssr_personal_document_application_eno")
    private String ugdSsrPersonalDocumentApplicationEno;
    /**
     * Дата предоставления документов
     *
     */
    @JsonProperty("ugd_ssr_personal_document_application_date_time")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime ugdSsrPersonalDocumentApplicationDateTime;
    /**
     * Фамилия, имя, отчество
     *
     */
    @JsonProperty("ugd_ssr_personal_document_application_full_name")
    private String ugdSsrPersonalDocumentApplicationFullName;
    /**
     * Адрес отселяемой квартиры (откуда)
     *
     */
    @JsonProperty("ugd_ssr_personal_document_application_address_from")
    private String ugdSsrPersonalDocumentApplicationAddressFrom;
}
