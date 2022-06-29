package ru.croc.ugd.ssr.solr;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.reinform.cdp.search.model.document.SolrDocument;

import java.io.Serializable;

/**
 * Разбор письма с предложением
 *
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UgdSsrOfferLetterParsing
    extends SolrDocument
    implements Serializable {

    /**
     * Адрес отселяемой квартиры (откуда)
     *
     */
    @JsonProperty("ugd_ssr_offer_letter_parsing_address_from")
    private String ugdSsrOfferLetterParsingAddressFrom;
    /**
     * Письмо с предложением
     *
     */
    @JsonProperty("ugd_ssr_offer_letter_parsing_letter_id")
    private String ugdSsrOfferLetterParsingLetterId;
}
