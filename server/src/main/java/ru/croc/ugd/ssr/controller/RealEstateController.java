package ru.croc.ugd.ssr.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.db.projection.ResettledHouseProjection;
import ru.croc.ugd.ssr.dto.realestate.RestDemolitionDto;
import ru.croc.ugd.ssr.dto.realestate.RestHousePreservationDto;
import ru.croc.ugd.ssr.dto.realestate.RestNonResidentialApartmentDto;
import ru.croc.ugd.ssr.dto.realestate.RestResettlementCompletionDto;
import ru.croc.ugd.ssr.dto.realestate.RestUpdateNonResidentialSpaceInfoDto;
import ru.croc.ugd.ssr.model.RealEstateDocument;
import ru.croc.ugd.ssr.service.RealEstateDocumentService;
import ru.croc.ugd.ssr.service.realestate.RestRealEstateService;

import java.util.List;

/**
 * Контроллер по работе с домами.
 */
@RestController
@RequestMapping("/realestate")
@RequiredArgsConstructor
public class RealEstateController {

    private final RealEstateDocumentService service;
    private final RestRealEstateService restRealEstateService;

    /**
     * Получение всех расселяемых домов по id ОКС.
     *
     * @param ccoId Id ОКС
     * @return список расселяемых домов
     */
    @ApiOperation(value = "Получение всех расселяемых домов по id ОКС")
    @GetMapping(value = "/fetchByCcoId/{ccoId}")
    public List<RealEstateDocument> fetchByCcoId(@ApiParam(value = "Id ОКС") @PathVariable String ccoId) {
        return service.fetchDocumentByCcoId(ccoId);
    }

    /**
     * Обновление квартир дома по каталогу.
     *
     * @param unom unom
     * @return список расселяемых домов
     */
    @ApiOperation(value = "Обновление квартир дома по каталогу.")
    @PostMapping(value = "/updateByCatalogFlatDetails/{unom}")
    public void updateByCatalogFlatDetails(@ApiParam(value = "Id ОКС") @PathVariable String unom) {
        service.updateRealEstateFlatsWithCatalogData(unom);
    }

    /**
     * Обновление квартир дома по каталогу.
     */
    @ApiOperation(value = "Обновление квартир дома по каталогу.")
    @PostMapping(value = "/updateByCatalogFlatDetails")
    public void updateByCatalogFlatDetails() {
        service.updateAllRealEstateMissingFlatsWithCatalogData();
    }

    /**
     * Внести информацию о нежилых помещениях.
     *
     * @param id ИД объекта недвижимости
     * @param body Данные о нежилых помещениях
     */
    @PostMapping(value = "/{id}/set-non-residential-space-info")
    public void setNonResidentialSpaceInfo(
        @PathVariable("id") final String id,
        @RequestBody final RestUpdateNonResidentialSpaceInfoDto body
    ) {
        restRealEstateService.setNonResidentialSpaceInfo(id, body);
    }

    /**
     * Внести информацию об окончании расселения.
     *
     * @param id ИД объекта недвижимости
     * @param body Данные о завершении переселения
     */
    @PostMapping(value = "/{id}/complete-resettlement")
    public void completeResettlement(
        @PathVariable("id") final String id,
        @RequestBody final RestResettlementCompletionDto body
    ) {
        restRealEstateService.completeResettlement(id, body);
    }

    /**
     * Внести информацию о сохранении дома.
     *
     * @param id ИД объекта недвижимости
     * @param body Данные о сохранении дома
     */
    @PostMapping(value = "/{id}/preserve-house")
    public void preserveHouse(
        @PathVariable("id") final String id,
        @RequestBody final RestHousePreservationDto body
    ) {
        restRealEstateService.preserveHouse(id, body);
    }

    /**
     * Внести информацию о сносе дома.
     *
     * @param id ИД объекта недвижимости
     * @param body Данные о сносе дома
     */
    @PostMapping(value = "/{id}/set-demolition-data")
    public void setDemolitionData(
        @PathVariable("id") final String id,
        @RequestBody final RestDemolitionDto body
    ) {
        restRealEstateService.setDemolitionData(id, body);
    }

    /**
     * Для каждого объекта недвижимости:
     * - завести папку в FileStore, если она отсутствует;
     * - при наличии даты сноса дома, добавить блок с данными о сносе дома.
     */
    @PostMapping(value = "/actualize-real-estates")
    public void actualizeRealEstates() {
        restRealEstateService.actualizeRealEstates();
    }

    /**
     * Актуализировать статусы объектов недвижимости.
     */
    @PostMapping(value = "/actualize-resettlement-statuses")
    public void actualizeResettlementStatuses() {
        restRealEstateService.actualizeResettlementStatuses();
    }

    /**
     * Получение данных о нежилых помещениях.
     *
     * @param id ИД объекта недвижимости
     */
    @GetMapping(value = "/{id}/non-residential-apartments")
    public List<RestNonResidentialApartmentDto> getNonResidentialApartments(
        @PathVariable("id") final String id
    ) {
        return restRealEstateService.getNonResidentialApartments(id);
    }

    /**
     * Получение отселяемых домов по documentId.
     * @param documentIds - id стартовых площадок.
     * @return отселяемый дом.
     */
    @PostMapping(value = "/getResettledHousesesByIds")
    public  List<ResettledHouseProjection> getResettledHousesesByIds(
            @ApiParam(value = "id стартовых площадок") @RequestBody List<String> documentIds
    ) {
        return service.getResettledHousesByIds(documentIds);
    }
}
