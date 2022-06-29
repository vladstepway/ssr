package ru.croc.ugd.ssr.service.flatappointment;

import ru.croc.ugd.ssr.generated.dto.flatappointment.ExternalRestFlatAppointmentPersonCheckDto;
import ru.croc.ugd.ssr.model.flatAppointment.FlatAppointmentDocument;

/**
 * FlatAppointmentCheckService.
 */
public interface FlatAppointmentCheckService {

    /**
     * Проверяет персону для подачи заявления на осмотр квартиры.
     *
     * @param snils snils
     * @param ssoId ssoId
     * @return данные по жителю
     */
    ExternalRestFlatAppointmentPersonCheckDto checkPerson(final String snils, final String ssoId);

    /**
     * Проверить возможность подачи заявки на осмотр квартиры.
     *
     * @param flatAppointmentDocument данные заявки
     * @return возможна ли подача заявки
     */
    boolean canCreateApplication(final FlatAppointmentDocument flatAppointmentDocument);

}
