
package ru.croc.ugd.ssr.solr;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.reinform.cdp.search.model.document.SolrDocument;
import ru.reinform.cdp.search.util.LocalDateTimeSerializer;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * Реестр заявлений на заключение договора
 *
 */
@Getter
@Setter
@NoArgsConstructor
public class UgdSsrContractAppointment
    extends SolrDocument
    implements Serializable {

    /**
     * Номер заявления
     *
     */
    @JsonProperty("ugd_ssr_contract_appointment_application_number")
    private String ugdSsrContractAppointmentApplicationNumber;
    /**
     * Дата и время подачи заявления
     *
     */
    @JsonProperty("ugd_ssr_contract_appointment_creation_date_time")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime ugdSsrContractAppointmentCreationDateTime;
    /**
     * Фамилия, имя, отчество
     *
     */
    @JsonProperty("ugd_ssr_contract_appointment_full_name")
    private String ugdSsrContractAppointmentFullName;
    /**
     * Адрес центра переселения
     *
     */
    @JsonProperty("ugd_ssr_contract_appointment_cip_address")
    private String ugdSsrContractAppointmentCipAddress;
    /**
     * Адрес (куда)
     *
     */
    @JsonProperty("ugd_ssr_contract_appointment_address_to")
    private String ugdSsrContractAppointmentAddressTo;
    /**
     * Дата и время приема
     *
     */
    @JsonProperty("ugd_ssr_contract_appointment_date_time")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime ugdSsrContractAppointmentDateTime;
    /**
     * Статус
     *
     */
    @JsonProperty("ugd_ssr_contract_appointment_status")
    private String ugdSsrContractAppointmentStatus;

    /**
     * Код района
     */
    @JsonProperty("ugd_ssr_contract_appointment_district_code")
    private String ugdSsrContractAppointmentDistrictCode;

    /**
     * Фильтр для округа
     */
    @JsonProperty("ugd_ssr_contract_appointment_area")
    private String ugdSsrContractAppointmentArea;

    /**
     * Способ подписания
     */
    @JsonProperty("ugd_ssr_contract_appointment_sign_type")
    private String ugdSsrContractAppointmentSignType;

}
