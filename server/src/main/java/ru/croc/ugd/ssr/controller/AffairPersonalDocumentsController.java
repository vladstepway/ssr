package ru.croc.ugd.ssr.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.dto.personaldocument.RestPersonalDocumentDto;
import ru.croc.ugd.ssr.service.personaldocument.RestPersonalDocumentService;

/**
 * Контроллер для работы со сведениями о документах для конкретного жителя.
 */
@RestController
@AllArgsConstructor
public class AffairPersonalDocumentsController {

    private final RestPersonalDocumentService restPersonalDocumentService;

    /**
     * Получить последние сведения о документах.
     *
     * @param affairId id семьи
     * @param isParsed возвращать только уже разобранные сведения о документах
     * @return сведения о документах
     */
    @GetMapping(value = "/affairs/{affairId}/personal-documents/last")
    public RestPersonalDocumentDto getLastPersonalDocument(
        @PathVariable("affairId") final String affairId,
        @RequestParam(value = "isParsed", required = false, defaultValue = "false") final boolean isParsed
    ) {
        return restPersonalDocumentService.getLastPersonalDocument(affairId, isParsed);
    }
}
