package ru.croc.ugd.ssr.controller.courtinfo;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.dto.courtinfo.RestCourtInfoDto;
import ru.croc.ugd.ssr.service.courtinfo.CourtInfoService;

import java.util.List;

/**
 * Контроллер для работы со сведениями по судам.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/court-infos")
public class CourtInfoController {

    private final CourtInfoService courtInfoService;

    /**
     * Получить сведения о судах по affairId.
     *
     * @param affairId ИД семьи
     * @return сведения
     */
    @GetMapping
    public List<RestCourtInfoDto> fetchAll(@RequestParam("affairId") final String affairId) {
        return courtInfoService.fetchAllByAffairId(affairId);
    }
}
