package ru.croc.ugd.ssr.solr;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.reinform.cdp.search.model.document.SolrDocument;

import java.io.Serializable;

/**
 * Сведения о документах
 *
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UgdSsrPersonalDocument
    extends SolrDocument
    implements Serializable {

    /**
     * Адрес отселяемой квартиры (откуда)
     *
     */
    @JsonProperty("ugd_ssr_personal_document_address_from")
    private String ugdSsrPersonalDocumentAddressFrom;
    /**
     * Письмо с предложением
     *
     */
    @JsonProperty("ugd_ssr_personal_document_letter_id")
    private String ugdSsrPersonalDocumentLetterId;
}

