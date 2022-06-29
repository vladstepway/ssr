package ru.croc.ugd.ssr.controller.notarycompensation;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.dto.notarycompensation.RestNotaryCompensationDto;
import ru.croc.ugd.ssr.dto.notarycompensation.RestNotaryCompensationMoneyNotPayedDto;
import ru.croc.ugd.ssr.dto.notarycompensation.RestNotaryCompensationRejectDto;
import ru.croc.ugd.ssr.service.notarycompensation.RestNotaryCompensationService;

/**
 * Контроллер для работы с карточкой заявления на возмещения оплаты услуг нотарисуса.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/internal/notary-compensations")
public class NotaryCompensationController {

    private final RestNotaryCompensationService restNotaryCompensationService;

    /**
     * Получить сведения о заялении на возмещение оплаты услуг нотариуса по ИД документа.
     * @param notaryCompensationDocumentId ИД документа
     * @return заявление на возмещение оплаты услуг нотариуса
     */
    @GetMapping("/{id}")
    public RestNotaryCompensationDto fetchById(
        @PathVariable("id") final String notaryCompensationDocumentId
    ) {
        return restNotaryCompensationService.fetchById(notaryCompensationDocumentId);
    }

    @PostMapping("/{id}/confirm")
    public void confirm(
        @PathVariable("id") final String notaryCompensationDocumentId
    ) {
        restNotaryCompensationService.confirm(notaryCompensationDocumentId);
    }

    @PostMapping("/{id}/reject")
    public void reject(
        @PathVariable("id") final String notaryCompensationDocumentId,
        @RequestBody final RestNotaryCompensationRejectDto rejectDto
    ) {
        restNotaryCompensationService.reject(
            notaryCompensationDocumentId,
            rejectDto
        );
    }

    @PostMapping("/{id}/money-not-payed")
    public void moneyNotPayed(
        @PathVariable("id") final String notaryCompensationDocumentId,
        @RequestBody final RestNotaryCompensationMoneyNotPayedDto moneyNotPayedDto
    ) {
        restNotaryCompensationService.moneyNotPayed(
            notaryCompensationDocumentId,
            moneyNotPayedDto
        );
    }
}