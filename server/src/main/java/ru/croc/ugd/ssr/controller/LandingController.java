package ru.croc.ugd.ssr.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.service.LandingService;

/**
 * Контроллер для лэндинга.
 */
@Slf4j
@RestController
@AllArgsConstructor
public class LandingController {

    private final LandingService service;

    /**
     * Получить данные жителя для лэндоса по СНИЛС.
     *
     * @param snils СНИЛС
     * @return статус
     */
    @ApiOperation(value = "Получить данные жителя для лэндоса по СНИЛС")
    @GetMapping(value = "/getPersonLandingInfoBySnils/{snils}")
    public ResponseEntity<String> getPersonLandingInfoBySnils(
        @ApiParam(value = "snils", required = true)
        @PathVariable(value = "snils") String snils
    ) {
        log.debug("Получен запрос на предоставление информации по жителю для лэндоса со СНИЛС: {}", snils);
        return service.getPersonLandingInfoBySnils(snils);
    }

}
