package ru.croc.ugd.ssr.service.courtinfo;

import ru.croc.ugd.ssr.dto.courtinfo.RestCourtInfoDto;
import ru.croc.ugd.ssr.model.courtinfo.CourtInfoDocument;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPCourtInfoType;

import java.util.List;

/**
 * Сервис для работы со сведениями по судам.
 */
public interface CourtInfoService {

    /**
     * Получить сведения о судах по affairId.
     *
     * @param affairId ИД семьи
     * @return сведения.
     */
    List<RestCourtInfoDto> fetchAllByAffairId(final String affairId);

    /**
     * Создать или обновить сведения о суде.
     *
     * @param courtInfoType сведения о суде
     */
    void createOrUpdateCourtInfo(final SuperServiceDGPCourtInfoType courtInfoType);
}
