package ru.croc.ugd.ssr.service.personaldocument.type;

import lombok.Builder;
import lombok.Data;

/**
 * Класс типов документов.
 */
@Data
@Builder
public class SsrPersonalDocumentType {

    /**
     * Код типа документа.
     */
    private String code;
    /**
     * Наименование типа документа.
     */
    private String name;
    /**
     * Код АС ГУФ.
     */
    private String kindCode;
    /**
     * Порядковый номер сортировки.
     */
    private int sortNumber;
}
