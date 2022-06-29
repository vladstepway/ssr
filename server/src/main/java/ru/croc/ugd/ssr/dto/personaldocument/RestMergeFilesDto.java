package ru.croc.ugd.ssr.dto.personaldocument;

import lombok.Builder;
import lombok.Value;

import java.util.List;

/**
 * Данные для объединения файлов в единый файл.
 */
@Value
@Builder
public class RestMergeFilesDto {

    /**
     * Файлы правоустанавливающих документов.
     */
    private final List<RestMergeDocumentFileDto> titleDocumentFiles;
    /**
     * Файлы личных документов.
     */
    private final List<List<RestMergeDocumentFileDto>> tenantDocumentFilesList;
    /**
     * ИД файла заявления в FileStore.
     */
    private final String applicationFileStoreId;
    /**
     * ИД единого файла документов в FileStore.
     */
    private final String unionFileStoreId;
}
