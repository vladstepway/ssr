package ru.croc.ugd.ssr.service.excel.person.model;

import static java.util.Objects.nonNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.config.person.PersonLetterAndContractProperties;

import java.util.Objects;
import javax.annotation.PostConstruct;

@Getter
@AllArgsConstructor
public enum PersonLetterAndContractSheetConstants {

    AFFAIR_ID_COLUMN_INDEX("affairId", 0, "AFFAIR_ID"),
    LETTER_DATE_COLUMN_INDEX("Дата письма", 4, "Дата_ Предложение ДГИ"),
    LETTER_ID_COLUMN_INDEX("letterId", 5, "LETTER_ID_ Предложение ДГИ"),
    LETTER_CHED_FILE_ID_COLUMN_INDEX("Ссылка на файл письма", 6, "Ссылка на ЦХЭД_Предложение ДГИ"),
    ADMINISTRATIVE_DOCUMENT_DATE_COLUMN_INDEX("Дата направления РД ДГИ", 11, "Дата направления РД_ ДГИ"),
    ADMINISTRATIVE_DOCUMENT_CHED_FILE_ID_COLUMN_INDEX("Ссылка на файл РД ДГИ", 12, "Ссылка на ЦХЭД_РД ДГИ"),
    ORDER_ID_COLUMN_INDEX("orderId", -1, "ORDER_ID"),
    RTF_CONTRACT_TO_SIGN_CHED_FILE_ID_COLUMN_INDEX(
        "Ссылка на файл письма", 15, "RTF-файл - с проектом договора на подпись - ссылка на ЦХЭД_ДГИ"
    ),
    PDF_CONTRACT_CHED_FILE_ID_COLUMN_INDEX(
        "Ссылка на файл письма", 17, "Pdf-файл с проектом договора на ознакомление- ссылка на ЦХЭД_ДГИ"
    );

    private final String columnReadableName;
    @Setter
    private Integer columnIndex;
    @Setter
    private String columnOriginalPartName;

    public static PersonLetterAndContractSheetConstants findByIndex(final Integer columnIndex) {
        for (PersonLetterAndContractSheetConstants v : values()) {
            if (Objects.equals(columnIndex, v.columnIndex)) {
                return v;
            }
        }
        return null;
    }

    private void setColumnData(final PersonLetterAndContractProperties.ColumnData columnData) {
        if (nonNull(columnData)) {
            columnIndex = columnData.getColumnIndex();
            columnOriginalPartName = columnData.getColumnName();
        }
    }

    @Component
    @AllArgsConstructor
    public static class PersonLetterAndContractPropertiesInjector {
        private PersonLetterAndContractProperties personLetterAndContractProperties;

        @PostConstruct
        public void postConstruct() {
            AFFAIR_ID_COLUMN_INDEX.setColumnData(personLetterAndContractProperties.getAffairId());
            LETTER_DATE_COLUMN_INDEX.setColumnData(personLetterAndContractProperties.getLetterDate());
            LETTER_ID_COLUMN_INDEX.setColumnData(personLetterAndContractProperties.getLetterId());
            LETTER_CHED_FILE_ID_COLUMN_INDEX.setColumnData(personLetterAndContractProperties.getLetterChedFileId());
            ADMINISTRATIVE_DOCUMENT_DATE_COLUMN_INDEX.setColumnData(
                personLetterAndContractProperties.getAdministrativeDocumentDate()
            );
            ADMINISTRATIVE_DOCUMENT_CHED_FILE_ID_COLUMN_INDEX.setColumnData(
                personLetterAndContractProperties.getAdministrativeDocumentChedFileId()
            );
            ORDER_ID_COLUMN_INDEX.setColumnData(personLetterAndContractProperties.getOrderId());
            RTF_CONTRACT_TO_SIGN_CHED_FILE_ID_COLUMN_INDEX.setColumnData(
                personLetterAndContractProperties.getRtfContractToSignChedFileId());
            PDF_CONTRACT_CHED_FILE_ID_COLUMN_INDEX.setColumnData(
                personLetterAndContractProperties.getPdfContractChedFileId()
            );
        }
    }
}
