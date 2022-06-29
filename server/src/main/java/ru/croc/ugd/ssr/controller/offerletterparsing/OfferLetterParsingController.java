package ru.croc.ugd.ssr.controller.offerletterparsing;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.dto.offerletterparsing.RestOfferLetterParsingDto;
import ru.croc.ugd.ssr.service.offerletterparsing.RestOfferLetterParsingService;

/**
 * Контроллер для работы с разборами писем с предложением.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/offer-letter-parsing")
public class OfferLetterParsingController {

    private final RestOfferLetterParsingService restOfferLetterParsingService;

    /**
     * Получить данные разбора письма с предложением.
     *
     * @param id ИД документа разбора письма с предложением
     * @return разбор письма с предложением
     */
    @GetMapping(value = "/{id}")
    public RestOfferLetterParsingDto fetchById(@PathVariable("id") final String id) {
        return restOfferLetterParsingService.fetchById(id);
    }
}
