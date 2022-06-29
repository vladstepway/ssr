
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
 * Реестр заявлений на устранение строительных дефектов
 *
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UgdSsrCommissionInspection
    extends SolrDocument
    implements Serializable {

    /**
     * Номер заявления
     *
     */
    @JsonProperty("ugd_ssr_commission_inspection_application_number")
    private String ugdSsrCommissionInspectionApplicationNumber;
    /**
     * Заселяемый адрес
     *
     */
    @JsonProperty("ugd_ssr_commission_inspection_address_to")
    private String ugdSsrCommissionInspectionAddressTo;
    /**
     * Статус
     *
     */
    @JsonProperty("ugd_ssr_commission_inspection_status")
    private String ugdSsrCommissionInspectionStatus;
    /**
     * Дата и время подачи заявления
     *
     */
    @JsonProperty("ugd_ssr_commission_inspection_creation_date_time")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime ugdSsrCommissionInspectionCreationDateTime;
    /**
     * Фамилия, имя, отчество
     *
     */
    @JsonProperty("ugd_ssr_commission_inspection_fio")
    private String ugdSsrCommissionInspectionFio;
    /**
     * Заселяемая квартира
     *
     */
    @JsonProperty("ugd_ssr_commission_inspection_flat_to")
    private String ugdSsrCommissionInspectionFlatTo;
    /**
     * Дата и время осмотра
     *
     */
    @JsonProperty("ugd_ssr_commission_inspection_date")
    private String ugdSsrCommissionInspectionDate;
    /**
     * Письмо с предложением
     *
     */
    @JsonProperty("ugd_ssr_commission_inspection_offer_letter_file_link")
    private String ugdSsrCommissionInspectionOfferLetterFileLink;

    /**
     * Код района
     */
    @JsonProperty("ugd_ssr_commission_inspection_district_code")
    private String ugdSsrCommissionInspectionDistrictCode;

    /**
     * Район
     */
    @JsonProperty("ugd_ssr_commission_inspection_district")
    private String ugdSsrCommissionInspectionDistrict;

    /**
     * Округ
     */
    @JsonProperty("ugd_ssr_commission_inspection_area")
    private String ugdSsrCommissionInspectionArea;

    /**
     * Уном заселяемого дома
     */
    @JsonProperty("ugd_ssr_commission_inspection_unom")
    private String ugdSsrCommissionInspectionUnom;

    /**
     * Тип организации
     */
    @JsonProperty("ugd_ssr_commission_inspection_organization_type")
    private String ugdSsrCommissionInspectionOrganizationType;

    /**
     * ИД задачи
     */
    @JsonProperty("ugd_ssr_commission_inspection_task_id")
    private String ugdSsrCommissionInspectionTaskId;
}
