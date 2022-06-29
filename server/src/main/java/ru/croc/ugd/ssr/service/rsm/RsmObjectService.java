package ru.croc.ugd.ssr.service.rsm;

import ru.croc.ugd.ssr.dto.rsm.RsmRequestDto;

public interface RsmObjectService {

    /**
     * Обработка запроса сведений по ОКСам.
     *
     * @param request Запрос сведений по ОКСам.
     */
    void processRequest(final RsmRequestDto request);

}
