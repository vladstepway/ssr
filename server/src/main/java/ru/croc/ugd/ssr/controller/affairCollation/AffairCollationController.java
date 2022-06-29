package ru.croc.ugd.ssr.controller.affaircollation;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.dto.affaircollation.RestAffairCollationDto;
import ru.croc.ugd.ssr.service.affaircollation.AffairCollationService;

/**
 * Контроллер для создания запроса на сверку жителей.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/affair-collation")
public class AffairCollationController {

    private final AffairCollationService affairCollationService;

    /**
     * Создать запрос на сверку жителей.
     *
     * @param restAffairCollationDto restAffairCollationDto
     */
    @PostMapping
    public void create(@RequestBody final RestAffairCollationDto restAffairCollationDto) {
        affairCollationService.create(restAffairCollationDto, true);
    }
}
