package ru.croc.ugd.ssr.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.dto.NewFlatDto;
import ru.croc.ugd.ssr.service.PersonDocumentService;

/**
 * Контроллер для работы с заселяемыми квартирами.
 */
@RestController
@AllArgsConstructor
public class NewFlatController {

    private final PersonDocumentService personDocumentService;

    /**
     * Сохранение информации о заселяемой квартире.
     *
     * @param affairId id семьи
     * @param newFlatDto информация о новой квартире
     */
    @PostMapping(value = "/affairs/{id}/new-flats")
    public void create(@PathVariable("id") final String affairId, @RequestBody final NewFlatDto newFlatDto) {
        personDocumentService.createNewFlatAndUpdateOfferLetter(affairId, newFlatDto);
    }
}
