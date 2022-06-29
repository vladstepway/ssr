package ru.croc.ugd.ssr.controller.notary;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.dto.notary.RestNotaryApplicationChangeStatusDto;
import ru.croc.ugd.ssr.dto.notary.RestNotaryApplicationCreateUpdateRequestDto;
import ru.croc.ugd.ssr.dto.notary.RestNotaryApplicationResponseDto;
import ru.croc.ugd.ssr.service.notary.RestNotaryApplicationService;

import javax.validation.Valid;

/**
 * Контроллер для работы с заявлениями на посещение нотариуса.
 */
@RestController
@AllArgsConstructor
public class NotaryApplicationController {

    private final RestNotaryApplicationService restNotaryApplicationService;

    /**
     * Создать заявку.
     *
     * @param body тело запроса
     * @return заявление
     */
    @PostMapping(value = "/notary-application")
    public RestNotaryApplicationResponseDto create(
        @Valid @RequestBody final RestNotaryApplicationCreateUpdateRequestDto body
    ) {
        return restNotaryApplicationService.create(body);
    }

    /**
     * Обновить заявку.
     *
     * @param applicationId ИД заявления
     * @param body тело запроса
     * @return заявление
     */
    @PutMapping(value = "/notary-application/{applicationId}")
    public RestNotaryApplicationResponseDto update(
        @PathVariable("applicationId") String applicationId,
        @Valid @RequestBody final RestNotaryApplicationCreateUpdateRequestDto body
    ) {
        return restNotaryApplicationService.update(applicationId, body);
    }

    /**
     * Получить карточку заявления.
     *
     * @param applicationId Ид заявки
     * @return карточка
     */
    @GetMapping(value = "/notary-application/{applicationId}")
    public RestNotaryApplicationResponseDto fetchById(
        @PathVariable("applicationId") final String applicationId
    ) {
        return restNotaryApplicationService.fetchById(applicationId);
    }

    /**
     * Принять в работу.
     *
     * @param applicationId Ид заявки
     */
    @PostMapping(value = "/notary-application/{applicationId}/accept")
    public void accept(
        @PathVariable("applicationId") final String applicationId,
        @RequestBody final RestNotaryApplicationChangeStatusDto body
    ) {
        restNotaryApplicationService.accept(applicationId, body.getComment());
    }

    /**
     * Отказ в регистрации заявления.
     *
     * @param applicationId Ид заявки
     */
    @PostMapping(value = "/notary-application/{applicationId}/decline")
    public void decline(
        @PathVariable("applicationId") final String applicationId,
        @RequestBody final RestNotaryApplicationChangeStatusDto body
    ) {
        restNotaryApplicationService.decline(applicationId, body.getComment());
    }

    /**
     * Отказ в предоставлении услуги.
     *
     * @param applicationId Ид заявки.
     * @param body Причина отказа в предоставлении услуги.
     */
    @PostMapping(value = "/notary-application/{applicationId}/refuse")
    public void refuse(
        @PathVariable("applicationId") final String applicationId,
        @RequestBody final RestNotaryApplicationChangeStatusDto body
    ) {
        restNotaryApplicationService.refuse(applicationId, body.getComment());
    }

    /**
     * Отмена записи к нотариусу.
     *
     * @param applicationId Ид заявки
     * @param body коментарий
     */
    @PostMapping(value = "/notary-application/{applicationId}/cancel")
    public void cancelByNotary(
        @PathVariable("applicationId") final String applicationId,
        @RequestBody final RestNotaryApplicationChangeStatusDto body
    ) {
        restNotaryApplicationService.cancelByNotary(applicationId, body.getComment());
    }

    /**
     * Подтвердить готовность проекта договора.
     *
     * @param applicationId Ид заявки.
     * @param body Коментарий.
     */
    @PostMapping(value = "/notary-application/{applicationId}/draft-contract-ready")
    public void draftContractReady(
        @PathVariable("applicationId") final String applicationId,
        @RequestBody final RestNotaryApplicationChangeStatusDto body
    ) {
        restNotaryApplicationService.draftContractReady(applicationId, body.getComment());
    }

    /**
     * Подтвердить комплектность документов.
     *
     * @param applicationId Ид заявки.
     */
    @PostMapping(value = "/notary-application/{applicationId}/confirm-documents")
    public void confirmDocuments(
        @PathVariable("applicationId") final String applicationId
    ) {
        restNotaryApplicationService.confirmDocuments(applicationId);
    }

    /**
     * Запросить дополнительные документы.
     *
     * @param applicationId Ид заявки.
     * @param body Комментарий с перечнем документов.
     */
    @PostMapping(value = "/notary-application/{applicationId}/request-documents")
    public void requestDocuments(
        @PathVariable("applicationId") final String applicationId,
        @RequestBody final RestNotaryApplicationChangeStatusDto body
    ) {
        restNotaryApplicationService.requestDocuments(applicationId, body.getComment());
    }

    /**
     * Начать повторный сбор документов.
     *
     * @param applicationId Ид заявки.
     */
    @PostMapping(value = "/notary-application/{applicationId}/recollect-documents")
    public void requestDocuments(
        @PathVariable("applicationId") final String applicationId
    ) {
        restNotaryApplicationService.reCollectDocuments(applicationId);
    }

    /**
     * Подтвердить готовность справок.
     *
     * @param applicationId Ид заявки.
     */
    @PostMapping(value = "/notary-application/{applicationId}/confirm-recollected")
    public void confirmRecollected(
        @PathVariable("applicationId") final String applicationId
    ) {
        restNotaryApplicationService.confirmRecollected(applicationId);
    }

    /**
     * Договор заключён.
     *
     * @param applicationId Ид заявки.
     */
    @PostMapping(value = "/notary-application/{applicationId}/contract-signed")
    public void contractSigned(
        @PathVariable("applicationId") final String applicationId
    ) {
        restNotaryApplicationService.contractSigned(applicationId);
    }

    /**
     * Требуется повторный прием.
     *
     * @param applicationId Ид заявки.
     */
    @PostMapping(value = "/notary-application/{applicationId}/request-return-visit")
    public void requestReturnVisit(
        @PathVariable("applicationId") final String applicationId
    ) {
        restNotaryApplicationService.requestReturnVisit(applicationId);
    }

    /**
     * refuseNoDocuments 1080.3.
     *
     * @param applicationId Ид заявки
     */
    @PostMapping(value = "/notary-application/{applicationId}/refuse-no-documents")
    public void refuseNoDocuments(
        @PathVariable("applicationId") final String applicationId
    ) {
        restNotaryApplicationService.refuseNoDocuments(applicationId);
    }

    /**
     * refuseNoBooking 1080.2.
     *
     * @param applicationId Ид заявки
     */
    @PostMapping(value = "/notary-application/{applicationId}/refuse-no-booking")
    public void refuseNoBooking(
        @PathVariable("applicationId") final String applicationId
    ) {
        restNotaryApplicationService.refuseNoBooking(applicationId);
    }

    /**
     * bookingClosed 8031.1.
     *
     * @param applicationId Ид заявки
     */
    @PostMapping(value = "/notary-application/{applicationId}/booking-closed")
    public void bookingClosed(
        @PathVariable("applicationId") final String applicationId
    ) {
        restNotaryApplicationService.bookingClosed(applicationId);
    }

    /**
     * appointmentReminder 8021.3.
     *
     * @param applicationId Ид заявки
     */
    @PostMapping(value = "/notary-application/{applicationId}/appointment-reminder")
    public void appointmentReminder(
        @PathVariable("applicationId") final String applicationId
    ) {
        restNotaryApplicationService.appointmentReminder(applicationId);
    }

    @PostMapping(value = "/notary-application/{applicationId}/cancel-by-applicant")
    public void cancelByApplicant(
        @PathVariable("applicationId") final String applicationId
    ) {
        restNotaryApplicationService.processCancelByApplicant(applicationId);
    }

    /**
     * cancellationClosed 8031.2.
     *
     * @param applicationId Ид заявки
     */
    @PostMapping(value = "/notary-application/{applicationId}/cancellation-closed")
    public void cancellationClosed(
        @PathVariable("applicationId") final String applicationId
    ) {
        restNotaryApplicationService.cancellationClosed(applicationId);
    }

    //TODO Konstantin: remove temporary method
    @DeleteMapping(value = "/notary-application")
    public void deleteAll() {
        restNotaryApplicationService.deleteAll();
    }
}
