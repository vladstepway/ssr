
package ru.croc.ugd.ssr.solr;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.reinform.cdp.search.model.document.SolrDocument;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;


/**
 * Объект недвижимости
 *
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UgdSsrRealEstate
    extends SolrDocument
    implements Serializable
{

    /**
     * Тип номера корпуса
     *
     */
    @JsonProperty("ugd_ssr_real_estate_corp_l2_type")
    private String ugdSsrRealEstateCorpL2Type;
    /**
     * Город
     *
     */
    @JsonProperty("ugd_ssr_real_estate_town_p4")
    private String ugdSsrRealEstateTownP4;
    /**
     * Идентификатор записи ОН (ОДОПМ)
     *
     */
    @JsonProperty("ugd_ssr_real_estate_global_id")
    private Long ugdSsrRealEstateGlobalId;
    /**
     * Уникальный номер адреса в государственном адресном реестре
     *
     */
    @JsonProperty("ugd_ssr_real_estate_n_fias")
    private String ugdSsrRealEstateNFias;
    /**
     * Номер строения, сооружения
     *
     */
    @JsonProperty("ugd_ssr_real_estate_building_l3_value")
    private String ugdSsrRealEstateBuildingL3Value;
    /**
     * Идентификатор адреса
     *
     */
    @JsonProperty("ugd_ssr_real_estate_aid")
    private Long ugdSsrRealEstateAid;
    /**
     * Поселение
     *
     */
    @JsonProperty("ugd_ssr_real_estate_settlement_p3")
    private String ugdSsrRealEstateSettlementP3;
    /**
     * Муниципальный округ
     *
     */
    @JsonProperty("ugd_ssr_real_estate_mun_okrug_p5")
    private String ugdSsrRealEstateMunOkrugP5;
    /**
     * Наименование элемента планировочной структуры или улично-дорожной сети
     *
     */
    @JsonProperty("ugd_ssr_real_estate_element_p7")
    private String ugdSsrRealEstateElementP7;
    /**
     * Учётный номер объекта адресации в БД БТИ
     *
     */
    @JsonProperty("ugd_ssr_real_estate_unom")
    private String ugdSsrRealEstateUnom;
    /**
     * Тип номера дома, владения, участка
     *
     */
    @JsonProperty("ugd_ssr_real_estate_house_l1_type")
    private String ugdSsrRealEstateHouseL1Type;
    /**
     * Уникальный номер адреса в Адресном реестре
     *
     */
    @JsonProperty("ugd_ssr_real_estate_nreg")
    private Long ugdSsrRealEstateNreg;
    /**
     * Номер дома, владения, участка
     *
     */
    @JsonProperty("ugd_ssr_real_estate_house_l1_value")
    private String ugdSsrRealEstateHouseL1Value;
    /**
     * Дополнительный адресообразующий элемент
     *
     */
    @JsonProperty("ugd_ssr_real_estate_additional_addr_document_p90")
    private String ugdSsrRealEstateAdditionalAddrDocumentP90;
    /**
     * Уточнение дополнительного адресообразующего элемента
     *
     */
    @JsonProperty("ugd_ssr_real_estate_refinement_addr_p91")
    private String ugdSsrRealEstateRefinementAddrP91;
    /**
     * Населённый пункт
     *
     */
    @JsonProperty("ugd_ssr_real_estate_locality_p6")
    private String ugdSsrRealEstateLocalityP6;
    /**
     * Субъект РФ
     *
     */
    @JsonProperty("ugd_ssr_real_estate_subject_rf_p1")
    private String ugdSsrRealEstateSubjectRfP1;
    /**
     * Геоданные
     *
     */
    @JsonProperty("ugd_ssr_real_estate_geo")
    private String ugdSsrRealEstateGeo;
    /**
     * Вид адреса
     *
     */
    @JsonProperty("ugd_ssr_real_estate_vid")
    private String ugdSsrRealEstateVid;
    /**
     * Номер корпуса
     *
     */
    @JsonProperty("ugd_ssr_real_estate_corp_l2_value")
    private String ugdSsrRealEstateCorpL2Value;
    /**
     * Тип номера строения, сооружения
     *
     */
    @JsonProperty("ugd_ssr_real_estate_building_l3_type")
    private String ugdSsrRealEstateBuildingL3Type;
    /**
     * Муниципальный округ, поселение
     *
     */
    @JsonProperty("ugd_ssr_real_estate_district")
    private String ugdSsrRealEstateDistrict;
    /**
     * Тип адреса
     *
     */
    @JsonProperty("ugd_ssr_real_estate_adr_type")
    private String ugdSsrRealEstateAdrType;
    /**
     * Кадастровые номера объекта недвижимости
     *
     */
    @JsonProperty("ugd_ssr_real_estate_cadastal_nums")
    private List<Long> ugdSsrRealEstateCadastalNums;
    /**
     * Состояние адреса
     *
     */
    @JsonProperty("ugd_ssr_real_estate_sostad")
    private String ugdSsrRealEstateSostad;
    /**
     * Статус адреса
     *
     */
    @JsonProperty("ugd_ssr_real_estate_status")
    private String ugdSsrRealEstateStatus;
    /**
     * Тип объекта адресации
     *
     */
    @JsonProperty("ugd_ssr_real_estate_obj_type")
    private String ugdSsrRealEstateObjType;
    /**
     * Серия дома
     *
     */
    @JsonProperty("ugd_ssr_real_estate_seria")
    private String ugdSsrRealEstateSeria;
    /**
     * Кадастровые номера земельного участка (для ОКС)
     *
     */
    @JsonProperty("ugd_ssr_real_estate_cadastal_nums_zu")
    private List<Long> ugdSsrRealEstateCadastalNumsZu;
    /**
     * Статус обогащения из  ЕИРЦ
     *
     */
    @JsonProperty("ugd_ssr_real_estate_updated_from_eirz")
    @JsonSerialize(using = ru.reinform.cdp.search.util.LocalDateSerializer.class)
    private LocalDate ugdSsrRealEstateUpdatedFromEirz;
    /**
     * Год постройки дома
     *
     */
    @JsonProperty("ugd_ssr_real_estate_house_year")
    private Long ugdSsrRealEstateHouseYear;
    /**
     * Адрес и телефон организации, выполняющей функции управления домом
     *
     */
    @JsonProperty("ugd_ssr_real_estate_service_company_addr_phone")
    private String ugdSsrRealEstateServiceCompanyAddrPhone;
    /**
     * Дата обогащения
     *
     */
    @JsonProperty("ugd_ssr_real_estate_updated_date")
    @JsonSerialize(using = ru.reinform.cdp.search.util.LocalDateSerializer.class)
    private LocalDate ugdSsrRealEstateUpdatedDate;
    /**
     * Признак "Скоро начнется переселение дома"
     *
     */
    @JsonProperty("ugd_ssr_real_estate_resettlement_soon")
    @JsonSerialize(using = ru.reinform.cdp.search.util.LocalDateSerializer.class)
    private LocalDate ugdSsrRealEstateResettlementSoon;
    /**
     * Административный округ
     *
     */
    @JsonProperty("ugd_ssr_real_estate_adm_area")
    private String ugdSsrRealEstateAdmArea;
    /**
     * Полное юридическое написание адреса или описание местоположения
     *
     */
    @JsonProperty("ugd_ssr_real_estate_address")
    private String ugdSsrRealEstateAddress;
    /**
     * Статус обогащения
     *
     */
    @JsonProperty("ugd_ssr_real_estate_updated_status")
    private String ugdSsrRealEstateUpdatedStatus;
    /**
     * Организация, выполняющая функции управления домом
     *
     */
    @JsonProperty("ugd_ssr_real_estate_service_company")
    private String ugdSsrRealEstateServiceCompany;
    /**
     * Число квартир в доме
     *
     */
    @JsonProperty("ugd_ssr_real_estate_flatCount")
    private Long ugdSsrRealEstateFlatcount;
    /**
     * Отношение количества жителей с ssoId к общему количеству жителей
     *
     */
    @JsonProperty("ugd_ssr_real_estate_ssoid_count")
    private String ugdSsrRealEstateSsoidCount;
    /**
     * Муниципальный округ
     *
     */
    @JsonProperty("ugd_ssr_real_estate_mun_okrug")
    private String ugdSsrRealEstateMunOkrug;
    /**
     * Значение кадастровых номеров объекта недвижимости
     *
     */
    @JsonProperty("ugd_ssr_real_estate_cadastral_numbers_value")
    private String ugdSsrRealEstateCadastralNumbersValue;
    /**
     * Статус отселяемого дома
     *
     */
    @JsonProperty("ugd_ssr_real_estate_resettlement_status")
    private String ugdSsrRealEstateResettlementStatus;

    /**
     * Номера квартир в доме
     *
     */
    @JsonProperty("ugd_ssr_real_estate_flat_numbers")
    private List<String> ugdSsrRealEstateFlatNumbers;

    /**
     * Уномы заселяемых домов
     *
     */
    @JsonProperty("ugd_ssr_real_estate_cco_unoms")
    private List<String> ugdSsrRealEstateCcoUnoms;

    /**
     * Отселено квартир (ДГИ)
     *
     */
    @JsonProperty("ugd_ssr_real_estate_resettled_flat_count_dgi")
    private Long ugdSsrRealEstateResettledFlatCountDgi;

    /**
     * Отселено квартир (МФР)
     *
     */
    @JsonProperty("ugd_ssr_real_estate_resettled_flat_count_mfr")
    private Long ugdSsrRealEstateResettledFlatCountMfr;

    /**
     * Судебные квартиры (ДГИ)
     *
     */
    @JsonProperty("ugd_ssr_real_estate_court_flat_count_dgi")
    private Long ugdSsrRealEstateCourtFlatCountDgi;

    /**
     * Судебные квартиры (МФР)
     *
     */
    @JsonProperty("ugd_ssr_real_estate_court_flat_count_mfr")
    private Long ugdSsrRealEstateCourtFlatCountMfr;

    /**
     * Квартиры в процессе переселения (ДГИ)
     *
     */
    @JsonProperty("ugd_ssr_real_estate_active_resettlement_flat_count_dgi")
    private Long ugdSsrRealEstateActiveResettlementFlatCountDgi;

    /**
     * Квартиры в процессе переселения (МФР)
     *
     */
    @JsonProperty("ugd_ssr_real_estate_active_resettlement_flat_count_mfr")
    private Long ugdSsrRealEstateActiveResettlementFlatCountMfr;
}
