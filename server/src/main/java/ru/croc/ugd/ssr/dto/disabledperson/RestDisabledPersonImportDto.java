package ru.croc.ugd.ssr.dto.disabledperson;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class RestDisabledPersonImportDto {
    /**
     * ИД документа
     */
    private final String id;
    /**
     * ИД папки в FileStore
     */
    private final String folderId;
    /**
     * Процент готовности обработки
     */
    private final int percentageReady;
    /**
     * Сведения о маломобильных гражданах
     */
    private final List<RestDisabledPersonRowDto> disabledPersons;
}
