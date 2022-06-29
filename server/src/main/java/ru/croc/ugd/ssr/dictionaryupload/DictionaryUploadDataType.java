package ru.croc.ugd.ssr.dictionaryupload;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;

/**
 * DictionaryUploadDataType.
 */
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum DictionaryUploadDataType {

    /**
     * ADDRESS_FROM.
     */
    ADDRESS_FROM("Справочник адресов расселения"),
    /**
     * ADDRESS_TO.
     */
    ADDRESS_TO("Справочник адресов заселения"),
    /**
     * PERSON.
     */
    PEOPLE("Справочник жителей");

    private final String value;

    /**
     * getValue.
     *
     * @return String value
     */
    public String value() {
        return value;
    }

}
