package ru.croc.ugd.ssr.controller.flatappointment;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.dto.flatappointment.RestCancelFlatAppointmentDto;
import ru.croc.ugd.ssr.dto.flatappointment.RestFlatAppointmentDto;
import ru.croc.ugd.ssr.service.flatappointment.RestFlatAppointmentService;

import java.util.List;

/**
 * Контроллер для работы с карточками записи на осмотр квартиры.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/internal/flat-appointments")
public class FlatAppointmentController {

    private final RestFlatAppointmentService restFlatAppointmentService;

    /**
     * Получить карточку.
     *
     * @param id Ид карточки
     * @return карточка
     */
    @GetMapping(value = "/{id}")
    public RestFlatAppointmentDto fetchById(@PathVariable("id") String id) {
        return restFlatAppointmentService.fetchById(id);
    }

    /**
     * Получить список карточек по идентификатору жителя.
     *
     * @param personDocumentId personDocumentId
     * @param includeInactive includeInactive
     * @return список карточек
     */
    @GetMapping
    public List<RestFlatAppointmentDto> fetchAll(
        @RequestParam("personDocumentId") final String personDocumentId,
        @RequestParam(value = "includeInactive", defaultValue = "true") final boolean includeInactive
    ) {
        return restFlatAppointmentService.fetchAll(personDocumentId, includeInactive);
    }

    /**
     * Отменить запись на осмотр квартиры.
     *
     * @param id карточки
     */
    @PostMapping(value = "/{id}/cancel")
    public void cancelAppointmentByOperator(
        @PathVariable("id") final String id,
        @RequestBody final RestCancelFlatAppointmentDto dto
    ) {
        restFlatAppointmentService.cancelAppointmentByOperator(id, dto);
    }

    /**
     * Закрыть возможность отмены записи на осмотр.
     *
     * @param id Ид карточки
     */
    @PostMapping(value = "/{id}/close-cancellation")
    public void closeCancellation(
        @PathVariable("id") final String id
    ) {
        restFlatAppointmentService.closeCancellation(id);
    }

    /**
     * Актуализировать ссылки на файлы писем с предложениями.
     */
    @PostMapping(value = "/actualize-offer-letter-file-links")
    public void actualizeOfferLetterFileLinks() {
        restFlatAppointmentService.actualizeOfferLetterFileLinks();
    }
}
