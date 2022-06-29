package ru.croc.ugd.ssr.service.ipev;

import ru.croc.ugd.ssr.dto.ipev.IpevOrrResponseDto;
import ru.croc.ugd.ssr.model.ipev.IpevLogDocument;

public interface IpevEventService {

    /**
     * Обработка входящего события.
     *
     * @param ipevOrrResponseDto Данные ответа ORR.
     * @param ipevLogDocument    Документ лога.
     */
    void processEvent(final IpevOrrResponseDto ipevOrrResponseDto, final IpevLogDocument ipevLogDocument);

}
