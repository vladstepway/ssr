package ru.croc.ugd.ssr.mapper;

import ru.croc.ugd.ssr.dayschedule.SlotType;
import ru.croc.ugd.ssr.dto.RestDayScheduleDto;
import ru.croc.ugd.ssr.dto.RestDayScheduleSlotDto;
import ru.reinform.cdp.document.model.DocumentAbstract;

public interface DayScheduleMapper<T extends DocumentAbstract> {

    //TODO move common schedule mappers part to parent
    SlotType toDocumentSlot(final RestDayScheduleSlotDto slot);

    //TODO return document
    void toCopy(final T document, final RestDayScheduleDto dto);

    T toDayScheduleDocument(final RestDayScheduleDto dto);

    RestDayScheduleDto toRestDayScheduleDto(final T document);
}
