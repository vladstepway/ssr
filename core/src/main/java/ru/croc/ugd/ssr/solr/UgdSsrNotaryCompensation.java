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
import java.util.List;

/**
 * Заявления на возмещение оплаты услуг нотариуса
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class UgdSsrNotaryCompensation extends SolrDocument implements Serializable {

    /**
     * Номер заявления
     *
     */
    @JsonProperty("ugd_ssr_notary_compensation_eno")
    private String ugdSsrNotaryCompensationEno;
    /**
     * Дата и время подачи заявления
     *
     */
    @JsonProperty("ugd_ssr_notary_compensation_date_time")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime ugdSsrNotaryCompensationDateTime;
    /**
     * ФИО заявителя
     *
     */
    @JsonProperty("ugd_ssr_notary_compensation_applicant_full_name")
    private String ugdSsrNotaryCompensationApplicantFullName;
    /**
     * Правообладатели
     *
     */
    @JsonProperty("ugd_ssr_notary_compensation_owners_full_name")
    private List<String> ugdSsrNotaryCompensationOwnersFullName;
    /**
     * Статус
     *
     */
    @JsonProperty("ugd_ssr_notary_compensation_status")
    private String ugdSsrNotaryCompensationStatus;
}
