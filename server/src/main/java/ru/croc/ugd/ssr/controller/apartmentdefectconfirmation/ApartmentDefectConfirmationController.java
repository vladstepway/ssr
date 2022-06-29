package ru.croc.ugd.ssr.controller.apartmentdefectconfirmation;

import lombok.AllArgsConstructor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.dto.apartmentdefectconfirmation.RestCorrectDefectDto;
import ru.croc.ugd.ssr.dto.apartmentdefectconfirmation.RestDefectConfirmationDto;
import ru.croc.ugd.ssr.dto.apartmentdefectconfirmation.RestDefectDto;
import ru.croc.ugd.ssr.dto.apartmentdefectconfirmation.RestProcessReviewDefectDto;
import ru.croc.ugd.ssr.service.apartmentdefectconfirmation.RestApartmentDefectConfirmationService;

/**
 * Контроллер для работы с подтверждением сведений об устранении дефектов.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/apartment-defect-confirmations")
public class ApartmentDefectConfirmationController {

    private final RestApartmentDefectConfirmationService restApartmentDefectConfirmationService;

    @InitBinder
    public void initBinder(final WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    /**
     * Запросить у застройщика подтверждение устранения дефектов.
     *
     * @param id ИД документа подтверждения сведений об устранении дефектов
     * @param restDefectConfirmationDto данные об устранении дефектов
     */
    @PostMapping(value = "/{id}/request-confirmation")
    public void requestConfirmation(
        @PathVariable("id") final String id,
        @RequestBody final RestDefectConfirmationDto restDefectConfirmationDto
    ) {
        restApartmentDefectConfirmationService.requestConfirmation(id, restDefectConfirmationDto);
    }

    /**
     * Получить данные о дефектах.
     *
     * @param id ИД документа подтверждения сведений об устранении дефектов
     * @param pageNum номер страницы
     * @param pageSize размер страницы
     * @param flat фильтр - квартира
     * @param flatElement фильтр - вид помещения
     * @param description фильтр - вид дефекта
     * @param isEliminated фильтр - устранен ли дефект
     * @param skipApproved не возвращать дефекты, изменения по которым уже согласованы
     * @param skipExcluded не возвращать дефекты, изменения по которым исключены из списка на согласование
     * @param sort строка сортировки
     * @return данные о дефектах
     */
    @GetMapping(value = "/{id}/defects")
    public Page<RestDefectDto> fetchDefects(
        @PathVariable("id") final String id,
        @RequestParam(value = "pageNum", required = false, defaultValue = "0") final int pageNum,
        @RequestParam(value = "pageSize", required = false, defaultValue = "50") final int pageSize,
        @RequestParam(value = "flat", required = false) final String flat,
        @RequestParam(value = "flatElement", required = false) final String flatElement,
        @RequestParam(value = "description", required = false) final String description,
        @RequestParam(value = "isEliminated", required = false) final Boolean isEliminated,
        @RequestParam(value = "skipApproved", required = false, defaultValue = "false") final boolean skipApproved,
        @RequestParam(value = "skipExcluded", required = false, defaultValue = "true") final boolean skipExcluded,
        @RequestParam(value = "sort", required = false) final String sort
    ) {
        return restApartmentDefectConfirmationService.fetchDefects(
            id, pageNum, pageSize, flat, flatElement, description, isEliminated, skipApproved, skipExcluded, sort
        );
    }

    /**
     * Согласовать или отклонить изменения по дефектам.
     *
     * @param id ИД документа подтверждения сведений об устранении дефектов
     * @param restProcessReviewDefectDto данные о согласовании/отклонении изменений по дефектам
     */
    @PostMapping(value = "/{id}/process-review")
    public void processReview(
        @PathVariable("id") final String id,
        @RequestBody final RestProcessReviewDefectDto restProcessReviewDefectDto
    ) {
        restApartmentDefectConfirmationService.processReview(id, restProcessReviewDefectDto);
    }

    /**
     * Сохранить подтверждение устранения дефектов.
     *
     * @param id ИД документа подтверждения сведений об устранении дефектов
     * @param restDefectConfirmationDto данные об устранении дефектов
     */
    @PostMapping(value = "/{id}/save-confirmation")
    public void saveConfirmation(
        @PathVariable("id") final String id, @RequestBody final RestDefectConfirmationDto restDefectConfirmationDto
    ) {
        restApartmentDefectConfirmationService.saveConfirmation(id, restDefectConfirmationDto);
    }

    /**
     * Скорректировать сведения и при необходимости запросить у застройщика подтверждение устранения дефектов.
     *
     * @param id ИД документа подтверждения сведений об устранении дефектов
     * @param restCorrectDefectDto скорректированные данные об устранении дефектов
     */
    @PostMapping(value = "/{id}/correct")
    public void correct(
        @PathVariable("id") final String id, @RequestBody final RestCorrectDefectDto restCorrectDefectDto
    ) {
        restApartmentDefectConfirmationService.correct(id, restCorrectDefectDto);
    }

    /**
     * Создать пустой документ подтверждения сведений об устранении дефектов.
     *
     * @return подтверждение сведений об устранении дефектов
     */
    @PostMapping
    public RestDefectConfirmationDto create() {
        return restApartmentDefectConfirmationService.create();
    }

    /**
     * Получить документ подтверждения сведений об устранении дефектов.
     *
     * @param id ИД документа подтверждения сведений об устранении дефектов
     * @return подтверждение сведений об устранении дефектов
     */
    @GetMapping(value = "/{id}")
    public RestDefectConfirmationDto fetchById(@PathVariable("id") final String id) {
        return restApartmentDefectConfirmationService.fetchById(id);
    }
}
