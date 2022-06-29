package ru.croc.ugd.ssr.config.person;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Настройки загрузки данных о письмах с предложениями и договорах из excel.
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = PersonLetterAndContractProperties.PREFIX)
public class PersonLetterAndContractProperties {
    /**
     * Префикс конфигурации.
     */
    static final String PREFIX = "ugd.ssr.person-letter-and-contract-upload";

    private ColumnData affairId;
    private ColumnData letterId;
    private ColumnData letterDate;
    private ColumnData letterChedFileId;
    private ColumnData administrativeDocumentDate;
    private ColumnData administrativeDocumentChedFileId;
    private ColumnData orderId;
    private ColumnData rtfContractToSignChedFileId;
    private ColumnData pdfContractChedFileId;

    @Getter
    @Setter
    public static class ColumnData {
        /**
         * Наименование заголовка столбца.
         */
        private String columnName;
        /**
         * Индекс столбца.
         */
        private Integer columnIndex;
    }
}
