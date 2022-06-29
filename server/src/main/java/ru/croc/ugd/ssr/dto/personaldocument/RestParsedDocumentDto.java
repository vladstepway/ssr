package ru.croc.ugd.ssr.dto.personaldocument;

import lombok.Builder;
import lombok.Value;

import java.util.List;

/**
 * Данные о документе, выделенном из единого файла документов.
 */
@Value
@Builder
public class RestParsedDocumentDto {

    /**
     * Код типа документа.
     */
    private final String typeCode;
    /**
     * Комментарий.
     */
    private final String comment;
    /**
     * Идентификатор документа владельца (для документа личного характера).
     */
    private final String personDocumentId;
    /**
     * Номера страниц в едином файле, относящиеся к документу.
     */
    private final List<Integer> pageNumbers;
}
