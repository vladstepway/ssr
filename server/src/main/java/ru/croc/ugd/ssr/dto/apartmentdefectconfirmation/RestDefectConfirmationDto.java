package ru.croc.ugd.ssr.dto.apartmentdefectconfirmation;

import lombok.Builder;
import lombok.Value;
import ru.croc.ugd.ssr.dto.RestWorkConfirmationFile;

import java.util.List;

@Value
@Builder
public class RestDefectConfirmationDto {
    /**
     * ИД документа
     */
    private final String id;
    /**
     * ИД папки в FileStore
     */
    private final String folderId;
    /**
     * Логин сотрудника Генподрядчика, запросившего подтверждение
     */
    private final String generalContractorLogin;
    /**
     * Данные о заселяемом доме
     */
    private final RestCcoDto ccoData;
    /**
     * Файлы документов, подтверждающие проделанную работу
     */
    private final List<RestWorkConfirmationFile> workConfirmationFiles;
    /**
     * Данные о дефектах
     */
    private final List<RestDefectDto> defects;
    /**
     * Причина отклонения
     */
    private final String rejectionReason;
}
