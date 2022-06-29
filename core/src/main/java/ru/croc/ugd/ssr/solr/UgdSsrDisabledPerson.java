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

/**
 * Сведения о маломобильности жителя
 *
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UgdSsrDisabledPerson extends SolrDocument implements Serializable {

    /**
     * UNOM
     *
     */
    @JsonProperty("ugd_ssr_disabled_person_unom")
    private String ugdSsrDisabledPersonUnom;
    /**
     * Округ
     *
     */
    @JsonProperty("ugd_ssr_disabled_person_area")
    private String ugdSsrDisabledPersonArea;
    /**
     * Код района
     *
     */
    @JsonProperty("ugd_ssr_disabled_person_district_code")
    private String ugdSsrDisabledPersonDistrictCode;
    /**
     * Район
     *
     */
    @JsonProperty("ugd_ssr_disabled_person_district")
    private String ugdSsrDisabledPersonDistrict;
    /**
     * Адрес отселяемого дома
     *
     */
    @JsonProperty("ugd_ssr_disabled_person_address_from")
    private String ugdSsrDisabledPersonAddressFrom;
    /**
     * Номер квартиры
     *
     */
    @JsonProperty("ugd_ssr_disabled_person_flat_number")
    private String ugdSsrDisabledPersonFlatNumber;
    /**
     * ФИО жителя
     *
     */
    @JsonProperty("ugd_ssr_disabled_person_full_name")
    private String ugdSsrDisabledPersonFullName;
    /**
     * Имя
     *
     */
    @JsonProperty("ugd_ssr_disabled_person_first_name")
    private String ugdSsrDisabledPersonFirstName;
    /**
     * Фамилия
     *
     */
    @JsonProperty("ugd_ssr_disabled_person_last_name")
    private String ugdSsrDisabledPersonLastName;
    /**
     * Отчество
     *
     */
    @JsonProperty("ugd_ssr_disabled_person_middle_name")
    private String ugdSsrDisabledPersonMiddleName;

    /**
     * ИД жителя
     *
     */
    @JsonProperty("ugd_ssr_disabled_person_person_document_id")
    private String ugdSsrDisabledPersonPersonDocumentId;
    /**
     * Дата рождения
     *
     */
    @JsonProperty("ugd_ssr_disabled_person_birth_date")
    @JsonSerialize(using = ru.reinform.cdp.search.util.LocalDateSerializer.class)
    private LocalDate ugdSsrDisabledPersonBirthDate;
    /**
     * СНИЛС
     *
     */
    @JsonProperty("ugd_ssr_disabled_person_snils")
    private String ugdSsrDisabledPersonSnils;
    /**
     * Использование кресла коляски
     *
     */
    @JsonProperty("ugd_ssr_disabled_person_is_using_wheelchair")
    private Boolean ugdSsrDisabledPersonIsUsingWheelchair;

    /**
     * Признак удаления сведений
     *
     */
    @JsonProperty("ugd_ssr_disabled_person_is_deleted")
    private Boolean ugdSsrDisabledPersonIsDeleted;
}
