package ru.croc.ugd.ssr.controller.personaldocument;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.dto.personaldocument.RestCreatePersonalDocumentRequestDto;
import ru.croc.ugd.ssr.dto.personaldocument.RestPersonalDocumentRequestDto;
import ru.croc.ugd.ssr.service.personaldocument.RestPersonalDocumentRequestService;

import java.util.List;

/**
 * Контроллер для работы с запросами документов.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/personal-document-requests")
public class PersonalDocumentRequestController {

    private final RestPersonalDocumentRequestService personalDocumentRequestService;

    /**
     * Создать запрос документов.
     *
     * @param body тело запроса
     * @return запрос документов
     */
    @PostMapping
    public RestPersonalDocumentRequestDto create(@RequestBody final RestCreatePersonalDocumentRequestDto body) {
        return personalDocumentRequestService.create(body);
    }

    /**
     * Получить запросы документов по жителю.
     *
     * @param personDocumentId ИД документа жителя
     * @return запросы документов
     */
    @GetMapping
    public List<RestPersonalDocumentRequestDto> findAll(
        @RequestParam("personDocumentId") final String personDocumentId
    ) {
        return personalDocumentRequestService.findAll(personDocumentId);
    }
}
