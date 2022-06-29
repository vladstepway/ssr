package ru.croc.ugd.ssr.controller.personaldocument;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.dto.personaldocument.RestPersonalDocumentApplicationDto;
import ru.croc.ugd.ssr.service.personaldocument.RestPersonalDocumentApplicationService;

import java.util.List;

/**
 * Контроллер для работы с заявлениями на предоставление документов.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/personal-document-applications")
public class PersonalDocumentApplicationController {

    private final RestPersonalDocumentApplicationService restPersonalDocumentApplicationService;

    @GetMapping(value = "/{id}")
    public RestPersonalDocumentApplicationDto fetchById(@PathVariable("id") final String id) {
        return restPersonalDocumentApplicationService.fetchById(id);
    }

    @PostMapping(value = "/{id}/accept")
    public void acceptById(@PathVariable("id") final String id) {
        restPersonalDocumentApplicationService.acceptById(id);
    }

    /**
     * Получить заявления на предоставление документов по жителю.
     *
     * @param personDocumentId ИД документа жителя
     * @param includeRequestedApplications фильтр наличия запроса по заявлению
     * @return заявления на предоставление документов
     */
    @GetMapping
    public List<RestPersonalDocumentApplicationDto> findAll(
        @RequestParam("personDocumentId") final String personDocumentId,
        @RequestParam(value = "includeRequestedApplications", required = false, defaultValue = "true")
        final boolean includeRequestedApplications
    ) {
        return restPersonalDocumentApplicationService.findAll(personDocumentId, includeRequestedApplications);
    }

    /**
     * Отправить письмо о поступлении заявления.
     *
     * @param id ИД заявления
     */
    @PostMapping(value = "/{id}/send-email")
    public void sendEmail(@PathVariable final String id) {
        restPersonalDocumentApplicationService.sendEmail(id);
    }
}
