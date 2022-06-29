package ru.croc.ugd.ssr.service.ssrcco;

import ru.croc.ugd.ssr.enums.SsrCcoOrganizationType;

public interface SsrCcoService {

    /**
     * Проверка того, что пользователь в данный момент является сотрудником по дому.
     *
     * @param unom UNOM заселяемого дома
     * @param ssrCcoOrganizationType тип организации
     * @return пользователь является ответственным сотрудником по дому
     */
    boolean existsCurrentUserAsEmployee(final String unom, final SsrCcoOrganizationType ssrCcoOrganizationType);

    /**
     * Рассчитать количественные значения, связанные с ОКСами и актами по дефектам.
     *
     */
    void calculateDefectActTotalsData();
}
