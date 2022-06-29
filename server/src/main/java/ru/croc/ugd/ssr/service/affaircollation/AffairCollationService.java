package ru.croc.ugd.ssr.service.affaircollation;

import ru.croc.ugd.ssr.dto.affaircollation.RestAffairCollationDto;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPAffairCollationReportType;

/**
 * Сервис для работы с запросами на сверку жителей.
 */
public interface AffairCollationService {

    /**
     * Создать запрос на сверку жителей.
     *
     * @param restAffairCollationDto restAffairCollationDto
     * @param isOperatorRequest isOperatorRequest
     */
    void create(final RestAffairCollationDto restAffairCollationDto, final boolean isOperatorRequest);

    /**
     * Обработка отчета.
     * @param reportEno reportEno
     * @param superServiceDgpAffairCollationReportType superServiceDgpAffairCollationReportType
     */
    void processReport(
        final String reportEno,
        final SuperServiceDGPAffairCollationReportType superServiceDgpAffairCollationReportType
    );
}
