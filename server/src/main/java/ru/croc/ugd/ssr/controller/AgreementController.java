package ru.croc.ugd.ssr.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.dto.agreement.RestAgreementDto;
import ru.croc.ugd.ssr.service.AgreementService;

import java.util.List;

/**
 * Контроллер для работы с данными о согласиях или отказах.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/agreements")
public class AgreementController {

    private final AgreementService agreementService;

    /**
     * Получить данные о согласиях или отказах по жителю.
     *
     * @param personDocumentId ИД документа жителя
     * @return данные о согласиях или отказах
     */
    @GetMapping
    public List<RestAgreementDto> findAll(@RequestParam("personDocumentId") final String personDocumentId) {
        return agreementService.findAll(personDocumentId);
    }
}
