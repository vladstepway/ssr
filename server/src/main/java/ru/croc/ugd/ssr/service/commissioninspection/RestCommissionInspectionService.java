package ru.croc.ugd.ssr.service.commissioninspection;

import ru.croc.ugd.ssr.dto.commissioninspection.RestCommissionInspectionConfirmDateDto;
import ru.croc.ugd.ssr.dto.commissioninspection.RestCommissionInspectionDto;
import ru.croc.ugd.ssr.dto.commissioninspection.RestCommissionInspectionMoveDateDto;
import ru.croc.ugd.ssr.dto.commissioninspection.RestCommissionInspectionVisitsDto;
import ru.croc.ugd.ssr.generated.dto.RestCommissionInspectionCheckResponseDto;
import ru.croc.ugd.ssr.generated.dto.RestDefectDto;
import ru.croc.ugd.ssr.model.ApartmentInspectionDocument;

import java.time.LocalDate;
import java.util.List;

/**
 * CommissionInspectionService.
 */
public interface RestCommissionInspectionService {

    /**
     * Returns all commission inspections.
     *
     * @param personId filters by personId
     * @param statuses filters by status
     * @return list of commission inspections
     */
    List<RestCommissionInspectionDto> findAll(final String personId, final String statuses);

    /**
     * Fetches commission inspection by id.
     * @param commissionInspectionId commissionInspectionId
     * @return CommissionInspectionDocument
     */
    RestCommissionInspectionDto fetchById(final String commissionInspectionId);

    /**
     * Fetches current apartment inspection.
     * @param commissionInspectionId commissionInspectionId
     * @return current apartment inspection
     */
    ApartmentInspectionDocument fetchCurrentApartmentInspection(final String commissionInspectionId);

    /**
     * Deletes commission inspection by id.
     * @param commissionInspectionId commissionInspectionId
     */
    void delete(final String commissionInspectionId);

    /**
     * Checks person to apply for commission inspection.
     * @param snils snils
     * @param ssoId ssoId
     * @return checks result
     */
    RestCommissionInspectionCheckResponseDto check(final String snils, final String ssoId);

    /**
     * Returns all defects.
     * @return defects
     */
    List<RestDefectDto> getAllDefects();

    /**
     * Confirms inspection date.
     * @param commissionInspectionId commissionInspectionId
     * @param commissionInspectionConfirmDateDto commissionInspectionConfirmDateDto
     */
    void confirmDate(
        final String commissionInspectionId,
        final RestCommissionInspectionConfirmDateDto commissionInspectionConfirmDateDto
    );

    /**
     * Refuse commission inspection.
     *
     * @param commissionInspectionId commissionInspectionId
     * @param reason reason
     * @param refusalStatusId refusalStatusId
     */
    void refuse(final String commissionInspectionId, final String reason, final String refusalStatusId);

    /**
     * Withdraw commission inspection.
     * @param commissionInspectionId commissionInspectionId.
     * @param reason reason.
     */
    void withdraw(final String commissionInspectionId, final String reason);

    /**
     * Move inspection date.
     * @param commissionInspectionId commissionInspectionId
     * @param restCommissionInspectionMoveDateDto commissionInspectionDateRequestDto
     */
    void moveDate(
        final String commissionInspectionId,
        final RestCommissionInspectionMoveDateDto restCommissionInspectionMoveDateDto
    );

    /**
     * Delete all.
     */
    void deleteAll();

    /**
     * Get visits.
     */
    List<RestCommissionInspectionVisitsDto> getVisits(final String unom, final LocalDate date);

    /**
     * Actualize task candidates.
     */
    void actualizeTaskCandidates();

    /**
     * Actualize task candidates.
     * @param commissionInspectionId commissionInspectionId
     */
    void actualizeTaskCandidates(final String commissionInspectionId);

    /**
     * Отправка статуса при продлении срока устранения дефектов.
     * @param commissionInspectionId commissionInspectionId
     */
    void defectsProlongation(final String commissionInspectionId);

    /**
     * Отказ по заявлению на комиссионный осмотр из-за отказа по письму с предложением.
     *
     * @param letterId id письма с предложением
     * @param reason причина
     */
    void refuseByLetterId(final String letterId, final String reason);
}
