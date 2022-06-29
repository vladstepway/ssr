package ru.croc.ugd.ssr.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.dto.commissioninspection.RestCommissionInspectionConfirmDateDto;
import ru.croc.ugd.ssr.dto.commissioninspection.RestCommissionInspectionDto;
import ru.croc.ugd.ssr.dto.commissioninspection.RestCommissionInspectionMoveDateDto;
import ru.croc.ugd.ssr.dto.commissioninspection.RestCommissionInspectionRefuseByLetterDto;
import ru.croc.ugd.ssr.dto.commissioninspection.RestCommissionInspectionRefuseDto;
import ru.croc.ugd.ssr.dto.commissioninspection.RestCommissionInspectionVisitsDto;
import ru.croc.ugd.ssr.dto.commissioninspection.RestCommissionInspectionWithdrawDto;
import ru.croc.ugd.ssr.generated.api.CommissionInspectionApi;
import ru.croc.ugd.ssr.generated.dto.RestCommissionInspectionCheckResponseDto;
import ru.croc.ugd.ssr.generated.dto.RestDefectDto;
import ru.croc.ugd.ssr.model.ApartmentInspectionDocument;
import ru.croc.ugd.ssr.service.commissioninspection.RestCommissionInspectionService;

import java.time.LocalDate;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * CommissionInspectionController.
 */
@RestController
@AllArgsConstructor
public class CommissionInspectionController implements CommissionInspectionApi {

    private final RestCommissionInspectionService restCommissionInspectionService;

    @Override
    public ResponseEntity<RestCommissionInspectionCheckResponseDto> commissionInspectionCheck(
        @NotNull @Valid String snils, @Valid String ssoId
    ) {
        return ResponseEntity.ok(
            restCommissionInspectionService.check(snils, ssoId)
        );
    }

    @Override
    public ResponseEntity<List<RestDefectDto>> getAllDefects() {
        return ResponseEntity.ok(
            restCommissionInspectionService.getAllDefects()
        );
    }

    /**
     * Returns all commission inspections.
     *
     * @param personId filters by personId
     * @param statuses filters by status
     * @return list of commission inspections
     */
    @GetMapping("/commission-inspection")
    public List<RestCommissionInspectionDto> findAll(
        @RequestParam(required = false) final String personId,
        @RequestParam(required = false) final String statuses
    ) {
        return restCommissionInspectionService.findAll(personId, statuses);
    }

    /**
     * Fetches commission inspection by id.
     * @param commissionInspectionId commissionInspectionId
     * @return restCommissionInspectionDto
     */
    @GetMapping("/commission-inspection/{id}")
    public RestCommissionInspectionDto fetchById(
        @PathVariable("id") final String commissionInspectionId
    ) {
        return restCommissionInspectionService.fetchById(commissionInspectionId);
    }

    /**
     * Fetches current apartment inspection by commission inspection id.
     * @param commissionInspectionId commissionInspectionId
     * @return restCommissionInspectionDto
     */
    @GetMapping("/commission-inspection/{id}/current-apartment-inspection")
    public ApartmentInspectionDocument fetchCurrentApartmentInspection(
        @PathVariable("id") final String commissionInspectionId
    ) {
        return restCommissionInspectionService.fetchCurrentApartmentInspection(commissionInspectionId);
    }

    /**
     * Deletes commission inspection by id.
     * @param commissionInspectionId commissionInspectionId
     */
    @DeleteMapping("/commission-inspection/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
        @PathVariable("id") final String commissionInspectionId
    ) {
        restCommissionInspectionService.delete(commissionInspectionId);
    }

    /**
     * Confirms inspection date.
     * @param commissionInspectionId commissionInspectionId
     * @param body body
     */
    @PostMapping(value = "/commission-inspection/{id}/confirmDate")
    public void confirmDate(
        @PathVariable("id") final String commissionInspectionId,
        @RequestBody final RestCommissionInspectionConfirmDateDto body
    ) {
        restCommissionInspectionService.confirmDate(commissionInspectionId, body);
    }

    /**
     * Move inspection date.
     * @param commissionInspectionId commissionInspectionId
     * @param body body
     */
    @PostMapping(value = "/commission-inspection/{id}/move")
    public void moveDate(
        @PathVariable("id") final String commissionInspectionId,
        @RequestBody final RestCommissionInspectionMoveDateDto body
    ) {
        restCommissionInspectionService.moveDate(commissionInspectionId, body);
    }


    /**
     * Refuse commission inspection.
     *
     * @param commissionInspectionId commissionInspectionId
     * @param body body
     */
    @PostMapping(value = "/commission-inspection/{id}/refuse")
    public void refuseById(
        @PathVariable("id") final String commissionInspectionId,
        @RequestBody final RestCommissionInspectionRefuseDto body
    ) {
        restCommissionInspectionService.refuse(commissionInspectionId, body.getReason(), body.getRefusalStatus());
    }

    /**
     * Withdraw commission inspection.
     *
     * @param commissionInspectionId commissionInspectionId
     * @param body body
     */
    @PostMapping(value = "/commission-inspection/{id}/withdraw")
    public void withdraw(
        @PathVariable("id") final String commissionInspectionId,
        @RequestBody final RestCommissionInspectionWithdrawDto body
    ) {
        restCommissionInspectionService.withdraw(commissionInspectionId, body.getReason());
    }

    @GetMapping(value = "/commission-inspection/visits")
    public List<RestCommissionInspectionVisitsDto> getVisits(
        @RequestParam("unom") final String unom,
        @RequestParam("date") final String date
    ) {
        return restCommissionInspectionService.getVisits(unom, LocalDate.parse(date));
    }

    @PostMapping(value = "/commission-inspection/actualize-task-candidates")
    public void actualizeTaskCandidates() {
        restCommissionInspectionService.actualizeTaskCandidates();
    }

    @PostMapping(value = "/commission-inspection/{id}/actualize-task-candidates")
    public void actualizeTaskCandidates(@PathVariable("id") final String commissionInspectionId) {
        restCommissionInspectionService.actualizeTaskCandidates(commissionInspectionId);
    }

    /**
     * Отправка статуса при продлении срока устранения дефектов.
     * @param commissionInspectionId commissionInspectionId
     */
    @PostMapping(value = "/commission-inspection/{id}/defects-prolongation")
    public void defectsProlongation(@PathVariable("id") final String commissionInspectionId) {
        restCommissionInspectionService.defectsProlongation(commissionInspectionId);
    }

    /**
     * Отказ по заявлению на комиссионный осмотр из-за отказа по письму с предложением.
     */
    @PostMapping(value = "/commission-inspection/refuse-by-letter")
    public void refuseByLetter(
        @RequestBody final RestCommissionInspectionRefuseByLetterDto refuseByLetterDto
    ) {
        restCommissionInspectionService.refuseByLetterId(
            refuseByLetterDto.getLetterId(),
            refuseByLetterDto.getReason()
        );
    }

    //TODO Konstantin: remove temporary method
    @DeleteMapping(value = "/commission-inspection")
    public void deleteAll() {
        restCommissionInspectionService.deleteAll();
    }
}
