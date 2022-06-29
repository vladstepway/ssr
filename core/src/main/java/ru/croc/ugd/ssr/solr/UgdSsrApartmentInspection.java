
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
 * Акт осмотра квартиры
 *
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UgdSsrApartmentInspection
    extends SolrDocument
    implements Serializable {

    /**
     * Адрес заселяемого дома
     *
     */
    @JsonProperty("ugd_ssr_apartment_inspection_address")
    private String ugdSsrApartmentInspectionAddress;
    /**
     * Номер заселяемой квартиры
     *
     */
    @JsonProperty("ugd_ssr_apartment_inspection_flat")
    private String ugdSsrApartmentInspectionFlat;
    /**
     * Фамилия, имя, отчество
     *
     */
    @JsonProperty("ugd_ssr_apartment_inspection_person_fio")
    private String ugdSsrApartmentInspectionPersonFio;
    /**
     * Плановая дата устранения
     *
     */
    @JsonProperty("ugd_ssr_apartment_inspection_planned_fixed_date")
    @JsonSerialize(using = ru.reinform.cdp.search.util.LocalDateSerializer.class)
    private LocalDate ugdSsrApartmentInspectionPlannedFixedDate;
    /**
     * Выполнено/не выполнено
     *
     */
    @JsonProperty("ugd_ssr_apartment_inspection_is_ready")
    private Boolean ugdSsrApartmentInspectionIsReady;
    /**
     * Генеральный подрядчик
     *
     */
    @JsonProperty("ugd_ssr_apartment_inspection_general_contractor")
    private String ugdSsrApartmentInspectionGeneralContractor;
    /**
     * Застройщик
     *
     */
    @JsonProperty("ugd_ssr_apartment_inspection_developer")
    private String ugdSsrApartmentInspectionDeveloper;
    /**
     * Согласие гражданина с устранением дефектов
     *
     */
    @JsonProperty("ugd_ssr_apartment_inspection_has_consent")
    private Boolean ugdSsrApartmentInspectionHasConsent;
    /**
     * Дата заведения акта
     *
     */
    @JsonProperty("ugd_ssr_apartment_inspection_act_creation_date")
    @JsonSerialize(using = ru.reinform.cdp.search.util.LocalDateSerializer.class)
    private LocalDate ugdSsrApartmentInspectionActCreationDate;
    /**
     * Адрес расселяемого дома
     *
     */
    @JsonProperty("ugd_ssr_apartment_inspection_address_from")
    private String ugdSsrApartmentInspectionAddressFrom;
    /**
     * Удален/не удален
     *
     */
    @JsonProperty("ugd_ssr_apartment_inspection_is_deleted")
    private Boolean ugdSsrApartmentInspectionIsDeleted;
    /**
     * Временный акт
     *
     */
    @JsonProperty("ugd_ssr_apartment_inspection_pending")
    private Boolean ugdSsrApartmentInspectionPending;
    /**
     * Наличие дефектов
     *
     */
    @JsonProperty("ugd_ssr_apartment_inspection_has_defects")
    private Boolean ugdSsrApartmentInspectionHasDefects;
    /**
     * Код района
     *
     */
    @JsonProperty("ugd_ssr_apartment_inspection_district_code")
    private String ugdSsrApartmentInspectionDistrictCode;
    /**
     * Район
     *
     */
    @JsonProperty("ugd_ssr_apartment_inspection_district")
    private String ugdSsrApartmentInspectionDistrict;
    /**
     * Округ
     *
     */
    @JsonProperty("ugd_ssr_apartment_inspection_area")
    private String ugdSsrApartmentInspectionArea;

    /**
     * Дефекты приняты/не приняты
     *
     */
    @JsonProperty("ugd_ssr_apartment_inspection_is_defects_accepted")
    private Boolean ugdSsrApartmentInspectionIsDefectsAccepted;
    /**
     * Ответственный сотрудник
     *
     */
    @JsonProperty("ugd_ssr_apartment_inspection_responsible_employee")
    private String ugdSsrApartmentInspectionResponsibleEmployee;
    /**
     * Наименование организации
     *
     */
    @JsonProperty("ugd_ssr_apartment_inspection_organization_name")
    private String ugdSsrApartmentInspectionOrganizationName;
    /**
     * Номер акта
     *
     */
    @JsonProperty("ugd_ssr_apartment_inspection_act_number")
    private String ugdSsrApartmentInspectionActNumber;
    /**
     * ИД акта первичного осмотра в FileStore
     *
     */
    @JsonProperty("ugd_ssr_apartment_inspection_signed_act_file_id")
    private String ugdSsrApartmentInspectionSignedActFileId;
    /**
     * Дефекты
     *
     */
    @JsonProperty("ugd_ssr_apartment_inspection_defects")
    private List<String> ugdSsrApartmentInspectionDefects;
    /**
     * Исключены из дефектов
     *
     */
    @JsonProperty("ugd_ssr_apartment_inspection_excluded_defects")
    private List<String> ugdSsrApartmentInspectionExcludedDefects;
    /**
     * Причина исключения дефектов
     *
     */
    @JsonProperty("ugd_ssr_apartment_inspection_defect_exclusion_reason")
    private String ugdSsrApartmentInspectionDefectExclusionReason;
    /**
     * Заселяемый дом относится к КП УГС
     *
     */
    @JsonProperty("ugd_ssr_apartment_inspection_is_kp_ugs")
    private Boolean ugdSsrApartmentInspectionIsKpUgs;
    /**
     * Гарантийный период
     *
     */
    @JsonProperty("ugd_ssr_apartment_inspection_warranty_period")
    private Boolean ugdSsrApartmentInspectionWarrantyPeriod;
    /**
     * Направлено генподрядчику
     *
     */
    @JsonProperty("ugd_ssr_apartment_inspection_assigned_general_contractor")
    private String ugdSsrApartmentInspectionAssignedGeneralContractor;
    /**
     * Уном
     *
     */
    @JsonProperty("ugd_ssr_apartment_inspection_unom")
    private String ugdSsrApartmentInspectionUnom;
    /**
     * ФИО сотрудника, инициировавшего закрытие акта
     *
     */
    @JsonProperty("ugd_ssr_apartment_inspection_closing_initiator_fio")
    private String ugdSsrApartmentInspectionClosingInitiatorFio;
    /**
     * Наименование организации сотрудника, инициировавшего закрытие акта
     *
     */
    @JsonProperty("ugd_ssr_apartment_inspection_closing_initiator_organization_name")
    private String ugdSsrApartmentInspectionClosingInitiatorOrganizationName;
}
