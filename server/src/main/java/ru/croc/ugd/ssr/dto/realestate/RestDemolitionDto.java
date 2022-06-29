package ru.croc.ugd.ssr.dto.realestate;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
public class RestDemolitionDto {

    /**
     * Дом снесен.
     */
    @JsonProperty("isDemolished")
    private final boolean demolished;
    /**
     * Дата сноса дома.
     */
    private final LocalDate date;
    /**
     * Номер документа о сносе дома.
     */
    private final String documentNumber;
    /**
     * Дата документа о сносе дома.
     */
    private final LocalDate documentDate;
    /**
     * ИД в FileStore файла документа о сносе дома.
     */
    private final String fileStoreId;
    /**
     * Тип документа о сносе дома.
     */
    private final String documentType;
    /**
     * ИД документа о сносе дома.
     */
    private final String documentId;
    /**
     * Контент документа о сносе дома.
     */
    private final String documentContent;
}
