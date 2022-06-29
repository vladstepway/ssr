package ru.croc.ugd.ssr.service.commissionorganisation;

import ru.croc.ugd.ssr.dto.commissionorganisation.RestCommissionInspectionOrganisationDto;

import java.util.List;

/**
 * RestCommissionInspectionOrganisationService.
 */
public interface RestCommissionInspectionOrganisationService {

    List<RestCommissionInspectionOrganisationDto> getAll();

    List<RestCommissionInspectionOrganisationDto> updateAll(
        final List<RestCommissionInspectionOrganisationDto> organisationDtoList
    );
}
