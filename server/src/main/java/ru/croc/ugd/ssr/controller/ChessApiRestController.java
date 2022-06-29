package ru.croc.ugd.ssr.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.model.api.chess.EntrancesResponse;
import ru.croc.ugd.ssr.model.api.chess.FlatInfoResponse;
import ru.croc.ugd.ssr.model.api.chess.FlatsResponse;
import ru.croc.ugd.ssr.model.api.chess.FullOksHousesResponse;
import ru.croc.ugd.ssr.model.api.chess.FullResHousesResponse;
import ru.croc.ugd.ssr.model.api.chess.OksHousesResponse;
import ru.croc.ugd.ssr.model.api.chess.ResHousesResponse;
import ru.croc.ugd.ssr.model.api.chess.StatsHouseResponse;
import ru.croc.ugd.ssr.service.CapitalConstructionObjectService;
import ru.croc.ugd.ssr.service.ChessApiService;

/**
 * Контроллер по получению данных для шахматки.
 */
@Slf4j
@RestController
@RequestMapping("/api/chess")
@Api(description = "Контроллер по получению данных для шахматки")
@RequiredArgsConstructor
public class ChessApiRestController {

    private final ChessApiService chessApiService;
    private final CapitalConstructionObjectService ccoService;

    /**
     * Получение списка отселяемых домов по фрагменту адреса (подстрока).
     *
     * @param addrSubstr Подстрока для поиска в адресе
     * @param selectTop  Количество возвращаемых записей
     * @return список отселяемых домов
     */
    @CrossOrigin(origins = "*", allowCredentials = "true", allowedHeaders = "*")
    @GetMapping(value = "/getResHouses")
    @ApiOperation("Получение списка отселяемых домов по фрагменту адреса (подстрока).")
    public ResHousesResponse getResHouses(
        @ApiParam("Подстрока для поиска в адресе") @RequestParam(required = false) String addrSubstr,
        @ApiParam("Количество возвращаемых записей") @RequestParam(defaultValue = "10") Integer selectTop
    ) {
        return chessApiService.getResHouses(addrSubstr, selectTop);
    }

    /**
     * Получение списка заселяемых домов (ОКС) по фрагменту адреса (подстрока).
     *
     * @param addrSubstr Подстрока для поиска в адресе
     * @param selectTop  Количество возвращаемых записей
     * @return список заселяемых домов
     */
    @CrossOrigin(origins = "*", allowCredentials = "true", allowedHeaders = "*")
    @GetMapping(value = "/getOksHouses")
    @ApiOperation("Получение списка заселяемых домов (ОКС) по фрагменту адреса (подстрока).")
    public OksHousesResponse getOksHouses(
        @ApiParam("Подстрока для поиска в адресе") @RequestParam(required = false) String addrSubstr,
        @ApiParam("Количество возвращаемых записей") @RequestParam(defaultValue = "10") Integer selectTop
    ) {
        return chessApiService.getOksHouses(addrSubstr, selectTop);
    }

    /**
     * Получение данных по заселяемым домам для отселяемого дома.
     *
     * @param unom УНОМ дома
     * @return данные по заселяемому дому
     */
    @CrossOrigin(origins = "*", allowCredentials = "true", allowedHeaders = "*")
    @GetMapping(value = "/getLinkedOksHouses")
    @ApiOperation("Получение данных по заселяемым домам для отселяемого дома.")
    public OksHousesResponse getLinkedOksHouses(
        @ApiParam("УНОМ дома") @RequestParam String unom
    ) {
        return chessApiService.getLinkedOksHouses(unom);
    }

    /**
     * Получение данных по отселяемым домам для заселяемого дома (ОКС).
     *
     * @param unom УНОМ дома
     * @return данные по отселяемому дому
     */
    @CrossOrigin(origins = "*", allowCredentials = "true", allowedHeaders = "*")
    @GetMapping(value = "/getLinkedResHouses")
    @ApiOperation("Получение данных по отселяемым домам для заселяемого дома (ОКС).")
    public ResHousesResponse getLinkedResHouses(
        @ApiParam("УНОМ дома") @RequestParam String unom
    ) {
        return chessApiService.getLinkedResHouses(unom);
    }

    /**
     * Получение данных по подъездам и квартирам в отселяемом доме.
     *
     * @param unom УНОМ дома
     * @return данные по подъездам и квартирам в отселяемом доме
     */
    @CrossOrigin(origins = "*", allowCredentials = "true", allowedHeaders = "*")
    @GetMapping(value = "/getResEntranceInfo")
    @ApiOperation("Получение данных по подъездам и квартирам в отселяемом доме.")
    public EntrancesResponse getResEntranceInfo(
        @ApiParam("УНОМ дома") @RequestParam String unom
    ) {
        return chessApiService.getResEntranceInfo(unom);
    }

    /**
     * Получение данных по подъездам и квартирам в заселяемом доме.
     *
     * @param unom УНОМ дома
     * @return данные по подъездам и квартирам в заселяемом доме
     */
    @CrossOrigin(origins = "*", allowCredentials = "true", allowedHeaders = "*")
    @GetMapping(value = "/getOksEntranceInfo")
    @ApiOperation("Получение данных по подъездам и квартирам в заселяемом доме.")
    public EntrancesResponse getOksEntranceInfo(
        @ApiParam("УНОМ дома") @RequestParam String unom
    ) {
        return chessApiService.getOksEntranceInfo(unom);
    }

    /**
     * Получение данных по квартирам в подъезде в отселяемом доме.
     *
     * @param unom     УНОМ дома
     * @param entrance Номер подъезда
     * @return данные по квартирам в подъезде в отселяемом доме
     */
    @CrossOrigin(origins = "*", allowCredentials = "true", allowedHeaders = "*")
    @GetMapping(value = "/getResFlatInfo")
    @ApiOperation("Получение данных по квартирам в подъезде в отселяемом доме.")
    public FlatsResponse getResFlatInfo(
        @ApiParam("УНОМ дома") @RequestParam String unom,
        @ApiParam("Номер подъезда") @RequestParam String entrance
    ) {
        return chessApiService.getResFlatInfo(unom, entrance);
    }

    /**
     * Получение данных по квартирам в подъезде в заселяемом доме.
     *
     * @param unom     УНОМ дома
     * @param entrance Номер подъезда
     * @return данные по квартирам в подъезде в заселяемом доме
     */
    @CrossOrigin(origins = "*", allowCredentials = "true", allowedHeaders = "*")
    @GetMapping(value = "/getOksFlatInfo")
    @ApiOperation("Получение данных по квартирам в подъезде в заселяемом доме.")
    public FlatsResponse getOksFlatInfo(
        @ApiParam("УНОМ дома") @RequestParam String unom,
        @ApiParam("Номер подъезда") @RequestParam String entrance
    ) {
        return chessApiService.getOksFlatInfo(unom, entrance);
    }

    /**
     * Получение данных квартиры и жителей в отселяемого дома.
     *
     * @param unom       УНОМ дома
     * @param flatNumber Номер квартиры
     * @return данные квартиры и жителей в отселяемом доме
     */
    @CrossOrigin(origins = "*", allowCredentials = "true", allowedHeaders = "*")
    @GetMapping(value = "/getResExtFlatInfo")
    @ApiOperation("Получение данных квартиры и жителей в отселяемого дома.")
    public FlatInfoResponse getResExtFlatInfo(
        @ApiParam("УНОМ дома") @RequestParam String unom,
        @ApiParam("Номер квартиры") @RequestParam String flatNumber
    ) {
        return chessApiService.getResExtFlatInfo(unom, flatNumber);
    }

    /**
     * Получение данных квартиры и жителей в ОКС.
     *
     * @param unom       УНОМ дома
     * @param flatNumber Номер квартиры
     * @return данные квартиры и жителей в заселяемом доме
     */
    @CrossOrigin(origins = "*", allowCredentials = "true", allowedHeaders = "*")
    @GetMapping(value = "/getOksExtFlatInfo")
    @ApiOperation("Получение данных квартиры и жителей в ОКС.")
    public FlatInfoResponse getOksExtFlatInfo(
        @ApiParam("УНОМ дома") @RequestParam String unom,
        @ApiParam("Номер квартиры") @RequestParam String flatNumber,
        @ApiParam(value = "Номер подъезда") @RequestParam(required = false) String entrance,
        @ApiParam(value = "Этаж") @RequestParam(required = false) String floor
    ) {
        return chessApiService.getOksExtFlatInfo(unom, flatNumber, entrance, floor);
    }

    /**
     * Получение статистики по отселяемому дому по УНОМ.
     *
     * @param unom УНОМ дома
     * @return статистика по отселяемому дому
     */
    @CrossOrigin(origins = "*", allowCredentials = "true", allowedHeaders = "*")
    @GetMapping(value = "/getStatResHouses")
    @ApiOperation("Получение статистики по отселяемому дому по УНОМ.")
    public StatsHouseResponse getStatResHouses(
        @ApiParam("УНОМ дома") @RequestParam String unom
    ) {
        return chessApiService.getStatResHouses(unom);
    }

    /**
     * Получение статистики по заселяемому дому по УНОМ.
     *
     * @param unom УНОМ дома
     * @return статистика по заселяемому дому
     */
    @CrossOrigin(origins = "*", allowCredentials = "true", allowedHeaders = "*")
    @GetMapping(value = "/getStatOksHouses")
    @ApiOperation("Получение статистики по заселяемому дому по УНОМ.")
    public StatsHouseResponse getStatOksHouses(
        @ApiParam("УНОМ дома") @RequestParam String unom
    ) {
        return ccoService.getStatOksHouses(unom);
    }

    /**
     * Получение полных данных по отселяемому дому по УНОМ (для отчета).
     *
     * @param unom УНОМ дома
     * @return данные по отселяемому дому
     */
    @CrossOrigin(origins = "*", allowCredentials = "true", allowedHeaders = "*")
    @GetMapping(value = "/getFullResHouses")
    @ApiOperation("Получение полных данных по отселяемому дому по УНОМ (для отчета).")
    public FullResHousesResponse getFullResHouses(
        @ApiParam("УНОМ дома") @RequestParam String unom
    ) {
        return chessApiService.getFullResHouses(unom);
    }

    /**
     * Получение полных данных по заселяемому дому по УНОМ (для отчета).
     *
     * @param unom УНОМ дома
     * @return данные по заселяемому дому
     */
    @CrossOrigin(origins = "*", allowCredentials = "true", allowedHeaders = "*")
    @GetMapping(value = "/getFullOksHouses")
    @ApiOperation("Получение полных данных по заселяемому дому по УНОМ (для отчета).")
    public FullOksHousesResponse getFullOksHouses(
        @ApiParam("УНОМ дома") @RequestParam String unom
    ) {
        return chessApiService.getFullOksHouses(unom);
    }

}
