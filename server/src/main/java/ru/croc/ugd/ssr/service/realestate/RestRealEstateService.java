package ru.croc.ugd.ssr.service.realestate;

import ru.croc.ugd.ssr.dto.realestate.RestDemolitionDto;
import ru.croc.ugd.ssr.dto.realestate.RestHousePreservationDto;
import ru.croc.ugd.ssr.dto.realestate.RestNonResidentialApartmentDto;
import ru.croc.ugd.ssr.dto.realestate.RestResettlementCompletionDto;
import ru.croc.ugd.ssr.dto.realestate.RestUpdateNonResidentialSpaceInfoDto;

import java.util.List;

public interface RestRealEstateService {

    /**
     * Внести информацию об окончании расселения.
     *
     * @param id ИД объекта недвижимости
     * @param body Данные о завершении переселения
     */
    void completeResettlement(final String id, final RestResettlementCompletionDto body);

    /**
     * Внести информацию о сохранении дома.
     *
     * @param id ИД объекта недвижимости
     * @param body Данные о сохранении дома
     */
    void preserveHouse(final String id, final RestHousePreservationDto body);

    /**
     * Внести информацию о нежилых помещениях.
     *
     * @param id ИД объекта недвижимости
     * @param body Данные о нежилых помещениях
     */
    void setNonResidentialSpaceInfo(final String id, final RestUpdateNonResidentialSpaceInfoDto body);

    /**
     * Внести информацию о сносе дома.
     *
     * @param id ИД объекта недвижимости
     * @param body Данные о сносе дома
     */
    void setDemolitionData(final String id, final RestDemolitionDto body);

    /**
     * Для каждого объекта недвижимости:
     * - завести папку в FileStore, если она отсутствует;
     * - при наличии даты сноса дома, добавить блок с данными о сносе дома.
     */
    void actualizeRealEstates();

    /**
     * Актуализировать статусы объектов недвижимости.
     */
    void actualizeResettlementStatuses();

    /**
     * Получение данных о нежилых помещениях.
     *
     * @param id ИД объекта недвижимости
     */
    List<RestNonResidentialApartmentDto> getNonResidentialApartments(final String id);
}
