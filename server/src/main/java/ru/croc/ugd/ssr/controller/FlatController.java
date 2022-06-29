package ru.croc.ugd.ssr.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.dto.flat.RestFlatDto;
import ru.croc.ugd.ssr.dto.flat.RestFlatLiverDto;
import ru.croc.ugd.ssr.service.PersonDocumentService;

import java.util.List;

/**
 * Контроллер по работе с квартирами.
 */
@RestController
@RequestMapping("/flats")
@AllArgsConstructor
public class FlatController {

    private final PersonDocumentService personDocumentService;

    /**
     * Получение правообладателей квартиры.
     *
     * @param personId id жителя
     * @param includePerson включение в список текущего жителя
     * @return список правообладателей
     */
    @GetMapping(value = "/owners")
    public List<RestFlatLiverDto> fetchFlatOwners(
        @RequestParam("personId") final String personId,
        @RequestParam(value = "includePerson", required = false) final boolean includePerson
    ) {
        return personDocumentService.fetchOtherFlatOwnersByPersonId(personId, includePerson);
    }

    /**
     * Получение жильцов квартиры.
     *
     * @param affairId id семьи
     * @param isOwner возвращать только правообладателей
     * @return список жильцов
     */
    @GetMapping(value = "/livers")
    public List<RestFlatLiverDto> fetchFlatLivers(
        @RequestParam("affairId") final String affairId,
        @RequestParam(value = "isOwner", required = false, defaultValue = "false") final boolean isOwner
    ) {
        return personDocumentService.fetchFlatLiversByAffairId(affairId, isOwner);
    }

    /**
     * Получение всех квартир расселяемого дома по уном.
     *
     * @param unom УНОМ
     * @param includeFlatsWithoutLivers включая квартиры без жителей
     * @return список квартир
     */
    @GetMapping
    public List<RestFlatDto> fetchFlatsWithLivers(
        @RequestParam("unom") final String unom,
        @RequestParam(value = "includeFlatsWithoutLivers", required = false, defaultValue = "true")
        final boolean includeFlatsWithoutLivers
    ) {
        return personDocumentService.fetchFlatsWithLivers(unom, includeFlatsWithoutLivers);
    }
}
