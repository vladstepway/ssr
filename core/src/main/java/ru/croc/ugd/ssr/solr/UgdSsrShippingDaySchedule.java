
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
import ru.reinform.cdp.search.util.LocalDateSerializer;


/**
 * Расписание дней для переезда
 * 
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UgdSsrShippingDaySchedule
    extends SolrDocument
    implements Serializable
{

    /**
     * Район
     * 
     */
    @JsonProperty("ugd_ssr_shipping_day_schedule_area")
    private String ugdSsrShippingDayScheduleArea;
    /**
     * День недели
     * 
     */
    @JsonProperty("ugd_ssr_shipping_day_schedule_weekday")
    private String ugdSsrShippingDayScheduleWeekday;
    /**
     * Кол-во бригад
     * 
     */
    @JsonProperty("ugd_ssr_shipping_day_schedule_brigades_amount")
    private Long ugdSsrShippingDayScheduleBrigadesAmount;
    /**
     * Режим работы
     * 
     */
    @JsonProperty("ugd_ssr_shipping_day_schedule_schedule")
    private String ugdSsrShippingDayScheduleSchedule;
    /**
     * Дата работы
     * 
     */
    @JsonProperty("ugd_ssr_shipping_day_schedule_workingdate")
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate ugdSsrShippingDayScheduleWorkingdate;
    /**
     * Всего записалось
     * 
     */
    @JsonProperty("ugd_ssr_shipping_day_schedule_total_applied")
    private Long ugdSsrShippingDayScheduleTotalApplied;

}
