package ru.croc.ugd.ssr.service.apartmentdefectconfirmation;

import ru.croc.ugd.ssr.dto.apartmentdefectconfirmation.DefectConfirmationRequestDto;
import ru.croc.ugd.ssr.model.ApartmentDefectConfirmationDocument;
import ru.croc.ugd.ssr.model.ApartmentInspectionDocument;

import java.util.List;

public interface ApartmentDefectConfirmationService {

    /**
     * Актуализировать список кандидатов активных задач по SsrCco.
     */
    void actualizeTaskCandidatesBySsrCco();

    /**
     * Актуализировать список кандидатов активных задач по SsrCco.
     *
     * @param unom уном
     */
    void actualizeTaskCandidatesBySsrCco(final String unom);

    /**
     * Актуализировать список кандидатов активных задач.
     *
     * @param apartmentDefectConfirmationDocument документ подтверждения сведений об устранении дефектов
     */
    void actualizeTaskCandidates(final ApartmentDefectConfirmationDocument apartmentDefectConfirmationDocument);

    /**
     * Запустить процесс подтверждения устранения дефектов.
     *
     * @param apartmentDefectConfirmationDocument документ подтверждения сведений об устранении дефектов
     */
    void startConfirmationProcess(final ApartmentDefectConfirmationDocument apartmentDefectConfirmationDocument);

    /**
     * Закрыть задачу согласования сведений об устранения дефектов.
     *
     * @param processInstanceId ИД процесса
     * @param areConfirmed согласованы все изменения по дефектам
     */
    void closeConfirmationTask(final String processInstanceId, final boolean areConfirmed);

    /**
     * Закрыть задачу корректировки отклоненных сведений об устранении дефектов.
     *
     * @param processInstanceId ИД процесса
     */
    void closeCorrectionTask(final String processInstanceId);

    /**
     * Создать документ подтверждения сведений об устранении дефектов по изменениям акта осмотра квартиры.
     *
     * @param apartmentInspectionDocument акт осмотра квартиры
     * @param defectConfirmationRequestDtos изменения по дефектам, требующие подтверждения
     * @return документ подтверждения сведений об устранении дефектов
     */
    ApartmentDefectConfirmationDocument createApartmentDefectConfirmation(
        final ApartmentInspectionDocument apartmentInspectionDocument,
        final List<DefectConfirmationRequestDto> defectConfirmationRequestDtos
    );
}
