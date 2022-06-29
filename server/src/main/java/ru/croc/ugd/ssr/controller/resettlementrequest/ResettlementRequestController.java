package ru.croc.ugd.ssr.controller.resettlementrequest;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.dto.resettlementrequest.RestResettlementDto;
import ru.croc.ugd.ssr.service.resettlementrequest.RestResettlementRequestService;

@RestController
@AllArgsConstructor
@RequestMapping("/resettlement-requests")
public class ResettlementRequestController {

    private final RestResettlementRequestService restResettlementRequestService;

    /**
     * Получить данные о переселении.
     *
     * @param realEstateUnom УНОМ отселяемого дома
     * @return данные о переселении
     */
    @GetMapping
    public RestResettlementDto fetchByRealEstateUnom(@RequestParam("realEstateUnom") final String realEstateUnom) {
        return restResettlementRequestService.fetchByRealEstateUnom(realEstateUnom);
    }
}
