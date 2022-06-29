package ru.croc.ugd.ssr.service.resettlementrequest;

import ru.croc.ugd.ssr.dto.resettlementrequest.RestResettlementDto;

public interface RestResettlementRequestService {

    /**
     * Получить сведения переселении.
     *
     * @param realEstateUnom УНОМ отселяемого дома
     * @return сведения о переселении
     */
    RestResettlementDto fetchByRealEstateUnom(final String realEstateUnom);
}
