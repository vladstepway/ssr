package ru.croc.ugd.ssr.controller.notary;

import lombok.AllArgsConstructor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.dto.notary.RestCreateUpdateNotaryRequestDto;
import ru.croc.ugd.ssr.dto.notary.RestNotaryInfoDto;
import ru.croc.ugd.ssr.dto.notary.RestNotaryResponseDto;
import ru.croc.ugd.ssr.generated.dto.notary.RestNotaryPersonCheckDto;
import ru.croc.ugd.ssr.service.notary.RestNotaryService;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Контроллер для работы с карточками нотариуса.
 */
@RestController
@AllArgsConstructor
public class NotaryController {

    private final RestNotaryService notaryService;

    @InitBinder
    public void initBinder(final WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    /**
     * Получить карточку.
     *
     * @param notaryId Ид карточки
     * @return карточка
     */
    @GetMapping(value = "/notary/{notaryId}")
    public RestNotaryResponseDto fetchById(@PathVariable("notaryId") String notaryId) {
        return notaryService.fetchById(notaryId);
    }

    /**
     * Получение списка карточек.
     *
     * @param fullName Фильтр по фио натариуса.
     * @param login Фильтр по login.
     * @param includeUnassignedEmployee Фильтр по наличию закрепленного за нотариусом сотрудника.
     * @return Список карточек.
     */
    @GetMapping(value = "/notary")
    public List<RestNotaryInfoDto> findAll(
        @RequestParam(value = "fullName", required = false) String fullName,
        @RequestParam(value = "login", required = false) String login,
        @RequestParam(value = "includeUnassignedEmployee", required = false, defaultValue = "true")
        final boolean includeUnassignedEmployee
    ) {
        return notaryService.findAll(fullName, login, includeUnassignedEmployee);
    }

    /**
     * Создание карточки нотариуса.
     *
     * @param body Данные нотариуса (required)
     * @return карточка
     */
    @PostMapping(value = "/notary")
    public RestNotaryResponseDto createNotary(
        @Valid @RequestBody RestCreateUpdateNotaryRequestDto body) {
        return notaryService.create(body);
    }

    /**
     * Отправить в архив.
     *
     * @param notaryId Ид карточки
     */
    @PutMapping(value = "/notary/{notaryId}/archive")
    public void sendNotaryToArchive(@PathVariable("notaryId") String notaryId) {
        notaryService.archive(notaryId);
    }

    /**
     * Редакторивание карточки нотариуса.
     *
     * @param notaryId Ид карточки
     * @param body Данные нотариуса
     * @return карточка
     */
    @PutMapping(value = "/notary/{notaryId}")
    public RestNotaryResponseDto updateNotary(
        @PathVariable("notaryId") String notaryId,
        @Valid @RequestBody RestCreateUpdateNotaryRequestDto body) {
        return notaryService.update(notaryId, body);
    }

    /**
     * Сервис проверки.
     *
     * @param personId Ид жителя
     * @return результат проверки
     */
    @GetMapping(value = "/internal/notary/check")
    public RestNotaryPersonCheckDto check(@NotNull @Valid final String personId) {
        return notaryService.check(personId);
    }

}
