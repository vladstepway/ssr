package ru.croc.ugd.ssr.service.contractappointment;

import ru.croc.ugd.ssr.generated.dto.contractappointment.ExternalRestContractAppointmentPersonCheckDto;
import ru.croc.ugd.ssr.model.contractappointment.ContractAppointmentDocument;

/**
 * ContractAppointmentCheckService.
 */
public interface ContractAppointmentCheckService {

    /**
     * Проверяет персону для подачи заявления на заключение договора.
     *
     * @param snils snils
     * @param ssoId ssoId
     * @return данные по жителю
     */
    ExternalRestContractAppointmentPersonCheckDto checkPerson(final String snils, final String ssoId);

    /**
     * Проверить возможность подачи заявки на заключение договора.
     *
     * @param contractAppointmentDocument данные заявки
     * @return возможна ли подача заявки
     */
    boolean canCreateApplication(final ContractAppointmentDocument contractAppointmentDocument);

}
