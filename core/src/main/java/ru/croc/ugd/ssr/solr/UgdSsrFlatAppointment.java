
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
 * Реестр заявлений на осмотр квартиры
 *
 */
@Getter
@Setter
@NoArgsConstructor
public class UgdSsrFlatAppointment
    extends SolrDocument
    implements Serializable {

    /**
     * Номер заявления
     *
     */
    @JsonProperty("ugd_ssr_flat_appointment_eno")
    private String ugdSsrFlatAppointmentEno;
    /**
     * Дата и время подачи заявления
     *
     */
    @JsonProperty("ugd_ssr_flat_appointment_application_date_time")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime ugdSsrFlatAppointmentApplicationDateTime;
    /**
     * Фамилия, имя, отчество
     *
     */
    @JsonProperty("ugd_ssr_flat_appointment_full_name")
    private String ugdSsrFlatAppointmentFullName;
    /**
     * Адрес центра переселения
     *
     */
    @JsonProperty("ugd_ssr_flat_appointment_cip_address")
    private String ugdSsrFlatAppointmentCipAddress;
    /**
     * Письмо с предложением
     *
     */
    @JsonProperty("ugd_ssr_flat_appointment_letter_id")
    private String ugdSsrFlatAppointmentLetterId;
    /**
     * Ссылка на письмо с предложением
     *
     */
    @JsonProperty("ugd_ssr_flat_appointment_letter_file_link")
    private String ugdSsrFlatAppointmentLetterFileLink;
    /**
     * Дата и время осмотра
     *
     */
    @JsonProperty("ugd_ssr_flat_appointment_date_time")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime ugdSsrFlatAppointmentDateTime;
    /**
     * Статус
     *
     */
    @JsonProperty("ugd_ssr_flat_appointment_status")
    private String ugdSsrFlatAppointmentStatus;

    /**
     * Код района
     */
    @JsonProperty("ugd_ssr_flat_appointment_district_code")
    private String ugdSsrFlatAppointmentDistrictCode;

    /**
     * Фильтр для округа
     */
    @JsonProperty("ugd_ssr_flat_appointment_area")
    private String ugdSsrFlatAppointmentArea;

}
