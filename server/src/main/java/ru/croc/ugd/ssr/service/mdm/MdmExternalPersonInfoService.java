package ru.croc.ugd.ssr.service.mdm;

import ru.croc.ugd.ssr.dto.mdm.request.MdmRequest;
import ru.croc.ugd.ssr.dto.mdm.response.MdmResponse;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.RealEstateDocument;

/**
 * Сервис MdmExternalPersonInfo.
 */
public interface MdmExternalPersonInfoService {

    /**
     * Получение СНИЛС, ssoId из МДМ и обновление жителя.
     *
     * @param personDocument Документ для обновления.
     * @return Обновленный документ.
     */
    PersonDocument updatePersonFromMdmExternal(final PersonDocument personDocument);

    /**
     * Выполнение произвольного запроса в МДМ с записью ответа в БД.
     *
     * @param mdmRequest Запрос.
     * @return Ответ.
     */
    MdmResponse request(final MdmRequest mdmRequest);

    /**
     * Выполнение запроса в МДМ по дому с записью лога в БД.
     *
     * @param realEstateDocument Документ real-estate
     */
    void requestByRealEstate(final RealEstateDocument realEstateDocument);

    /**
     * Получение ssoId по СНИЛС.
     *
     * @param snils СНИЛС
     * @return ssoId
     */
    String requestSsoIdBySnils(final String snils);
}
