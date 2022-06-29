
package ru.croc.ugd.ssr.solr;

import java.io.Serializable;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.reinform.cdp.search.model.document.SolrDocument;


/**
 * Житель дома
 * 
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UgdSsrPerson
    extends SolrDocument
    implements Serializable
{

    /**
     * Дата рождения
     * 
     */
    @JsonProperty("ugd_ssr_person_birthDate")
    @JsonSerialize(using = ru.reinform.cdp.search.util.LocalDateSerializer.class)
    private LocalDate ugdSsrPersonBirthdate;
    /**
     * Адрес заселяемого дома
     * 
     */
    @JsonProperty("ugd_ssr_person_settle_flat_address")
    private String ugdSsrPersonSettleFlatAddress;
    /**
     * Общий статус переселения
     * 
     */
    @JsonProperty("ugd_ssr_person_relocation_status")
    private String ugdSsrPersonRelocationStatus;
    /**
     * Район проживания
     * 
     */
    @JsonProperty("ugd_ssr_person_district")
    private String ugdSsrPersonDistrict;
    /**
     * Округ проживания
     * 
     */
    @JsonProperty("ugd_ssr_person_prefect")
    private String ugdSsrPersonPrefect;
    /**
     * ID жителя
     * 
     */
    @JsonProperty("ugd_ssr_person_person_id")
    private String ugdSsrPersonPersonId;
    /**
     * Основание проживания
     * 
     */
    @JsonProperty("ugd_ssr_person_residence_reason")
    private String ugdSsrPersonResidenceReason;
    /**
     * UNOM дома
     * 
     */
    @JsonProperty("ugd_ssr_person_unom")
    private Long ugdSsrPersonUnom;
    /**
     * Фамилия, имя, отчество
     * 
     */
    @JsonProperty("ugd_ssr_person_fio")
    private String ugdSsrPersonFio;
    /**
     * Адрес расселяемого дома
     * 
     */
    @JsonProperty("ugd_ssr_person_real_estate_address")
    private String ugdSsrPersonRealEstateAddress;
    /**
     * Номер квартиры в расселяемом доме
     * 
     */
    @JsonProperty("ugd_ssr_person_flat_number")
    private String ugdSsrPersonFlatNumber;
    /**
     * Номер комнаты в расселяемом доме
     * 
     */
    @JsonProperty("ugd_ssr_person_room_number")
    private String ugdSsrPersonRoomNumber;
    /**
     * Дата обогащения из ПФР
     * 
     */
    @JsonProperty("ugd_ssr_person_updated_from_pfr_date")
    @JsonSerialize(using = ru.reinform.cdp.search.util.LocalDateSerializer.class)
    private LocalDate ugdSsrPersonUpdatedFromPfrDate;
    /**
     * Дата обогащения из ЕЛК
     * 
     */
    @JsonProperty("ugd_ssr_person_updated_from_elk_date")
    @JsonSerialize(using = ru.reinform.cdp.search.util.LocalDateSerializer.class)
    private LocalDate ugdSsrPersonUpdatedFromElkDate;
    /**
     * Статус обогащения из ЕЛК
     * 
     */
    @JsonProperty("ugd_ssr_person_updated_from_elk_status")
    private String ugdSsrPersonUpdatedFromElkStatus;
    /**
     * Статус обогащения из ПФР
     * 
     */
    @JsonProperty("ugd_ssr_person_updated_from_pfr_status")
    private String ugdSsrPersonUpdatedFromPfrStatus;
    /**
     * SsoId
     * 
     */
    @JsonProperty("ugd_ssr_person_sso_id")
    private String ugdSsrPersonSsoId;
    /**
     * Активный житель (не удален)
     * 
     */
    @JsonProperty("ugd_ssr_person_isNotArchived")
    private Boolean ugdSsrPersonIsnotarchived;
    /**
     * Не обогащен из ДГИ
     * 
     */
    @JsonProperty("ugd_ssr_person_notUpdatedFromDgi")
    private Boolean ugdSsrPersonNotupdatedfromdgi;
    /**
     * Код центра переселения
     * 
     */
    @JsonProperty("ugd_ssr_person_information_center_code")
    private String ugdSsrPersonInformationCenterCode;
    /**
     * Адрес центра переселения
     * 
     */
    @JsonProperty("ugd_ssr_person_information_center_address")
    private String ugdSsrPersonInformationCenterAddress;
    /**
     * Житель без двойников СНИЛС
     * 
     */
    @JsonProperty("ugd_ssr_person_isNotHaveDoublesSnils")
    private Boolean ugdSsrPersonIsnothavedoublessnils;
    /**
     * Адрес расселяемой квартиры
     * 
     */
    @JsonProperty("ugd_ssr_person_real_estate_flat_address")
    private String ugdSsrPersonRealEstateFlatAddress;
    /**
     * СНИЛС
     * 
     */
    @JsonProperty("ugd_ssr_person_snils")
    private String ugdSsrPersonSnils;
    /**
     * N, серия паспорта (св. о рождении), кем и когда выдан
     * 
     */
    @JsonProperty("ugd_ssr_person_passport")
    private String ugdSsrPersonPassport;

}
