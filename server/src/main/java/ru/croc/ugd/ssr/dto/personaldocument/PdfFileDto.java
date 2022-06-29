package ru.croc.ugd.ssr.dto.personaldocument;

import lombok.Builder;
import lombok.Value;

import java.util.List;

/**
 * Данные о файле документа, необходимые для его сохранения.
 */
@Value
@Builder
public class PdfFileDto {
    /**
     * ИД единого файла документов в FileStore.
     */
    private final String unionFileStoreId;
    /**
     * ИД папки в FileStore.
     */
    private final String folderId;
    /**
     * Номера страниц в едином файле, относящиеся к документу.
     */
    private final List<Integer> pageNumbers;
    /**
     * Наименование файла.
     */
    private final String fileName;
}
