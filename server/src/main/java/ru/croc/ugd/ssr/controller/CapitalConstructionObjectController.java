package ru.croc.ugd.ssr.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.dto.CcoDto;
import ru.croc.ugd.ssr.model.api.oks.Organization;
import ru.croc.ugd.ssr.service.CapitalConstructionObjectService;

import java.util.Collections;
import java.util.List;

/**
 * CapitalConstructionObjectController.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/cco")
public class CapitalConstructionObjectController {

    private final CapitalConstructionObjectService capitalConstructionObjectService;

    /**
     * Получение списка всех ОКСов привязанных к ЦИПам.
     * @return список ОКСов
     */
    @ApiOperation(value = "Получение списка всех ОКСов учавствующих в реновации")
    @GetMapping
    public List<CcoDto> fetchCcoList() {
        return capitalConstructionObjectService.fetchCcoList();
    }

    @GetMapping("/{ccoId}/organizations")
    public List<Organization> getOrganizationsFromCco(
        @ApiParam(value = "ccoId", required = true) @PathVariable String ccoId
    ) {
        return capitalConstructionObjectService
            .psGetOrganizationsFromCco(Collections.singletonList(ccoId));
    }
}
