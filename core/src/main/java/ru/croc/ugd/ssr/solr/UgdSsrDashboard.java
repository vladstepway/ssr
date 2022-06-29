
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
 * Статистическая информация о переселении
 * 
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UgdSsrDashboard
    extends SolrDocument
    implements Serializable
{

    /**
     * Всего новостроек
     * 
     */
    @JsonProperty("totalNewBuildings")
    private Long totalNewBuildings;
    /**
     * Архивная запись
     * 
     */
    @JsonProperty("isArchived")
    private Boolean isArchived;
    /**
     * Дата заполнения
     * 
     */
    @JsonProperty("fillInDate")
    @JsonSerialize(using = ru.reinform.cdp.search.util.LocalDateSerializer.class)
    private LocalDate fillInDate;
    /**
     * Имя пользователя
     * 
     */
    @JsonProperty("userName")
    private String userName;
    /**
     * Всего отселяемых домов
     * 
     */
    @JsonProperty("totalResettlementsBuildings")
    private Long totalResettlementsBuildings;
    /**
     * Информация на дату
     * 
     */
    @JsonProperty("informationDate")
    @JsonSerialize(using = ru.reinform.cdp.search.util.LocalDateSerializer.class)
    private LocalDate informationDate;
    /**
     * Не автоматически сформировано
     * 
     */
    @JsonProperty("notAuto")
    private Boolean notAuto;
    /**
     * Опубликовано
     * 
     */
    @JsonProperty("published")
    private Boolean published;

}
