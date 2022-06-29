package ru.croc.ugd.ssr.service.flatappointment;

import ru.croc.ugd.ssr.dto.flatappointment.RestCancelFlatAppointmentDto;
import ru.croc.ugd.ssr.dto.flatappointment.RestFlatAppointmentDto;

import java.util.List;

public interface RestFlatAppointmentService {

    RestFlatAppointmentDto fetchById(final String id);

    List<RestFlatAppointmentDto> fetchAll(final String personDocumentId, final boolean includeInactive);

    void cancelAppointmentByOperator(final String id, final RestCancelFlatAppointmentDto dto);

    void closeCancellation(final String id);

    /**
     * Получить содержимое ics файла.
     *
     * @param id ИД документа
     * @return содержимое ics файла
     */
    byte[] getIcsFileContent(final String id);

    /**
     * Актуализировать ссылки на файлы писем с предложениями.
     */
    void actualizeOfferLetterFileLinks();
}
