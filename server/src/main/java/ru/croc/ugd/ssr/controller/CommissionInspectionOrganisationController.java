package ru.croc.ugd.ssr.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.dto.commissionorganisation.RestCommissionInspectionOrganisationDto;
import ru.croc.ugd.ssr.model.commissionorganisation.CommissionInspectionOrganisationDocument;
import ru.croc.ugd.ssr.service.commissionorganisation.RestCommissionInspectionOrganisationService;

import java.util.List;

/**
 * CommissionInspectionOrganisationsController.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/commission-inspection/organisations")
public class CommissionInspectionOrganisationController {

    private final RestCommissionInspectionOrganisationService restCommissionInspectionOrganisationService;

    /**
     * Fetches all commission inspection organisations.
     *
     * @return list of commission inspection organisations
     */
    @GetMapping
    public List<RestCommissionInspectionOrganisationDto> getAll() {
        return restCommissionInspectionOrganisationService.getAll();
    }

    /**
     * Updates all commission inspection organisations.
     *
     * @return list of updated commission inspection organisations
     */
    @PutMapping
    public List<RestCommissionInspectionOrganisationDto> updateAll(
        @RequestBody final List<RestCommissionInspectionOrganisationDto> commissionInspectionOrganisationDtos
    ) {
        return restCommissionInspectionOrganisationService.updateAll(commissionInspectionOrganisationDtos);
    }
}
