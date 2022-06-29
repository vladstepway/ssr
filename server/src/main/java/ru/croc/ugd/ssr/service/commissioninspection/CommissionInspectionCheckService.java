package ru.croc.ugd.ssr.service.commissioninspection;

import ru.croc.ugd.ssr.dto.commissioninspection.CommissionInspectionCheckResponse;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.commissioninspection.CommissionInspectionDocument;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateMessage;

/**
 * CommissionInspectionCheckService.
 */
public interface CommissionInspectionCheckService {

    /**
     * Проверяет персону для подачи заявления на комиссионную проверку.
     *
     * @param snils snils
     * @param ssoId ssoId
     * @return данные по жителю
     */
    CommissionInspectionCheckResponse checkPerson(final String snils, final String ssoId);

    /**
     * Проверить возможность подачи заявки на КО.
     * @param commissionInspectionDocument данные заявки
     * @param person данные персоны
     * @return возможна ли подача заявки
     */
    boolean canCreateApplication(CommissionInspectionDocument commissionInspectionDocument, PersonDocument person);

    /**
     * Retrieve person from CoordinateMessage.
     * @param coordinateMessage coordinateMessage
     * @return person
     */
    PersonDocument retrievePerson(final CoordinateMessage coordinateMessage);

}
