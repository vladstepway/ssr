package ru.croc.ugd.ssr.dto.apartmentdefectconfirmation;

import lombok.Builder;
import lombok.Value;
import ru.croc.ugd.ssr.dto.RestWorkConfirmationFile;

import java.util.List;

@Value
@Builder
public class RestCorrectDefectDto {
    /**
     * ИД дефектов, по которым сотрудник Генподрядчика отказался от внесенных изменений
     */
    private final List<String> excludedDefectIds;
    /**
     * Исправленные данные о дефектах
     */
    private final List<RestDefectDto> correctedDefects;
    /**
     * Файлы документов, подтверждающие проделанную работу
     */
    private final List<RestWorkConfirmationFile> workConfirmationFiles;
}
