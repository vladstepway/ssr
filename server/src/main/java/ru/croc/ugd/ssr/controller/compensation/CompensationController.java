package ru.croc.ugd.ssr.controller.compensation;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.dto.compensation.RestCompensationDto;
import ru.croc.ugd.ssr.dto.compensation.RestCreateCompensationDto;
import ru.croc.ugd.ssr.service.compensation.RestCompensationService;

import java.util.List;

/**
 * Контроллер для работы с квартирами на компенсацию.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/compensation")
public class CompensationController {

    private final RestCompensationService restCompensationService;

    /**
     * Получить карточку.
     *
     * @param id Ид карточки
     * @return карточка
     */
    @GetMapping(value = "/{id}")
    public RestCompensationDto fetchById(@PathVariable("id") final String id) {
        return restCompensationService.fetchById(id);
    }

    /**
     * Получить список карточек по УНОМ отселяемого дома.
     *
     * @param unom УНОМ отселяемого дома
     * @return список карточек
     */
    @GetMapping
    public List<RestCompensationDto> fetchAllByUnom(
        @RequestParam("unom") final String unom
    ) {
        return restCompensationService.fetchAllByUnom(unom);
    }

    /**
     * Создание карточек для всех домов по ResettlementRequest.
     *
     * @param body запрос.
     */
    @PostMapping
    public void createForResettlementRequest(
        @RequestBody final RestCreateCompensationDto body
    ) {
        restCompensationService.createForResettlementRequest(body);
    }

    /**
     * Изменение карточки.
     *
     * @param id   Уникальный идентификатор.
     * @param body карточка.
     * @return карточка.
     */
    @PutMapping(value = "/{id}")
    public RestCompensationDto update(
        @PathVariable("id") final String id,
        @RequestBody final RestCompensationDto body
    ) {
        return restCompensationService.update(id, body);
    }

}
