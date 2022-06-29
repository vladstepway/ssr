package ru.croc.ugd.ssr.solr;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.reinform.cdp.search.model.document.SolrDocument;

import java.io.Serializable;
import java.util.List;


/**
 * ОКС в системе ССР
 *
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UgdSsrSsrCco
    extends SolrDocument
    implements Serializable {

    /**
     * Адрес
     *
     */
    @JsonProperty("ugd_ssr_ssr_cco_address")
    private String ugdSsrSsrCcoAddress;
    /**
     * Округ
     *
     */
    @JsonProperty("ugd_ssr_ssr_cco_area")
    private String ugdSsrSsrCcoArea;
    /**
     * Район
     *
     */
    @JsonProperty("ugd_ssr_ssr_cco_district")
    private String ugdSsrSsrCcoDistrict;
    /**
     * Код района
     *
     */
    @JsonProperty("ugd_ssr_ssr_cco_district_code")
    private String ugdSsrSsrCcoDistrictCode;
    /**
     * ID ОКСа в системе PS
     *
     */
    @JsonProperty("ugd_ssr_ssr_cco_ps_document_id")
    private String ugdSsrSsrCcoPsDocumentId;
    /**
     * Генеральные подрядчики
     *
     */
    @JsonProperty("ugd_ssr_ssr_cco_general_contractors")
    private List<String> ugdSsrSsrCcoGeneralContractors;
    /**
     * Генеральные застройщики
     *
     */
    @JsonProperty("ugd_ssr_ssr_cco_general_developers")
    private List<String> ugdSsrSsrCcoGeneralDevelopers;
    /**
     * Сотрудники подрядчика
     *
     */
    @JsonProperty("ugd_ssr_ssr_cco_general_contractor_employees")
    private List<String> ugdSsrSsrCcoGeneralContractorEmployees;
    /**
     * Логины сотрудников подрядчика
     *
     */
    @JsonProperty("ugd_ssr_ssr_cco_general_contractor_employee_logins")
    private List<String> ugdSsrSsrCcoGeneralContractorEmployeeLogins;
    /**
     * Сотрудники застройщика
     *
     */
    @JsonProperty("ugd_ssr_ssr_cco_general_developer_employees")
    private List<String> ugdSsrSsrCcoGeneralDeveloperEmployees;
    /**
     * Логины сотрудников застройщика
     *
     */
    @JsonProperty("ugd_ssr_ssr_cco_general_developer_employee_logins")
    private List<String> ugdSsrSsrCcoGeneralDeveloperEmployeeLogins;

    /**
     * UNOM
     *
     */
    @JsonProperty("ugd_ssr_ssr_cco_unom")
    private String ugdSsrSsrCcoUnom;

    /**
     * Количество квартир в доме.
     */
    @JsonProperty("ugd_ssr_ssr_cco_flat_count")
    private Long ugdSsrSsrCcoFlatCount;
    /**
     * Количество заселенных квартир без открытых актов.
     */
    @JsonProperty("ugd_ssr_ssr_cco_flat_without_open_act_count")
    private Long ugdSsrSsrCcoFlatWithoutOpenActCount;
    /**
     * Всего актов по дому.
     */
    @JsonProperty("ugd_ssr_ssr_cco_act_count")
    private Long ugdSsrSsrCcoActCount;
    /**
     * Количество просроченных актов.
     */
    @JsonProperty("ugd_ssr_ssr_cco_expired_act_count")
    private Long ugdSsrSsrCcoExpiredActCount;
    /**
     * Количество актов в работе.
     */
    @JsonProperty("ugd_ssr_ssr_cco_act_in_work_count")
    private Long ugdSsrSsrCcoActInWorkCount;
    /**
     * Всего поступило дефектов.
     */
    @JsonProperty("ugd_ssr_ssr_cco_defect_count")
    private Long ugdSsrSsrCcoDefectCount;
    /**
     * Всего устранено дефектов.
     */
    @JsonProperty("ugd_ssr_ssr_cco_fix_defect_count")
    private Long ugdSsrSsrCcoFixDefectCount;
    /**
     * Количество просроченных дефектов.
     */
    @JsonProperty("ugd_ssr_ssr_cco_expired_defect_count")
    private Long ugdSsrSsrCcoExpiredDefectCount;
    /**
     * Даны согласия.
     */
    @JsonProperty("ugd_ssr_ssr_cco_flat_with_agreement_count")
    private Long ugdSsrSsrCcoFlatWithAgreementCount;
    /**
     * Подписаны договоры.
     */
    @JsonProperty("ugd_ssr_ssr_cco_flat_with_contract_count")
    private Long ugdSsrSsrCcoFlatWithContractCount;
    /**
     * Выданы ключи.
     */
    @JsonProperty("ugd_ssr_ssr_cco_flat_with_key_issue_count")
    private Long ugdSsrSsrCcoFlatWithKeyIssueCount;
    /**
     * Осталось (общее количество - выданы ключи).
     */
    @JsonProperty("ugd_ssr_ssr_cco_flat_to_resettle_count")
    private Long ugdSsrSsrCcoFlatToResettleCount;
    /**
     * Квартиры ДГИ.
     */
    @JsonProperty("ugd_ssr_ssr_cco_flat_dgi_count")
    private Long ugdSsrSsrCcoFlatDgiCount;
    /**
     * Квартиры МФР.
     */
    @JsonProperty("ugd_ssr_ssr_cco_flat_mfr_count")
    private Long ugdSsrSsrCcoFlatMfrCount;
    /**
     * Внешние ИД организаций генеральных подрядчиков
     *
     */
    @JsonProperty("ugd_ssr_ssr_cco_general_contractor_external_ids")
    private List<String> ugdSsrSsrCcoGeneralContractorExternalIds;
    /**
     * Внешние ИД организаций генеральных застройщиков
     *
     */
    @JsonProperty("ugd_ssr_ssr_cco_general_developer_external_ids")
    private List<String> ugdSsrSsrCcoGeneralDeveloperExternalIds;
}
