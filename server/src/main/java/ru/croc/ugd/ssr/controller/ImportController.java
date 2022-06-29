package ru.croc.ugd.ssr.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.croc.ugd.ssr.service.ImportService;

/**
 * Контроллер по импорту.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/import")
public class ImportController {

    private final ImportService importService;

    /**
     * Первоначальное наполнение справочника RealEstate. Выполняется единожды.
     *
     * @param stringPathRealEstates Путь до xlsx файла с Объектами недвижимости
     * @param stringPathFlats       Путь до csv файла с квартирами
     */
    @PostMapping(value = "/realEstates")
    @ApiOperation("Функция загрузки данных об отселяемых домах, перечисленных в 497-ПП. "
        + "Каталог 27451 ОДОПМ")
    public void importRealEstatesWithFlats(
        @ApiParam("Путь до xlsx файла с отселяемыми домами") @RequestParam String stringPathRealEstates,
        @ApiParam("Путь до csv файла с отселяемыми домами") @RequestParam String stringPathFlats
    ) {
        importService.importRealEstatesWithFlats(stringPathRealEstates, stringPathFlats);
    }

    /**
     * Первоначальное наполнение справочника RealEstate. Выполняется единожды.
     *
     * @param stringPathFlats       Путь до csv файла с квартирами
     */
    @PostMapping(value = "/realEstatesWithFlats")
    @ApiOperation("Функция загрузки данных о квартирах и комнатах отселяемых домов, перечисленных в 497-ПП. "
        + "Каталог 27451 ОДОПМ")
    public void importRealEstates(
        @ApiParam("Путь до csv файла с квартирами и комнатами отселяемых домов") @RequestParam String stringPathFlats
    ) {
    }

    /**
     * Донаполнение справочника RealEstate кадастровыми номерами ОН. Выполняется единожды.
     *
     * @param stringPathCadastralNums Путь до csv файла с кадастровыми номерами ОН
     */
    @PostMapping(value = "/realEstateCadastralNums")
    @ApiOperation("Функция загрузки данных о кадастровых номерах отселяемых домов, перечисленных в 497-ПП. "
        + "Каталог 27455 ОДОПМ")
    public void importRealEstateCadatralNums(
        @ApiParam("Путь до csv файла с кадастровыми номерами ОН") @RequestParam String stringPathCadastralNums
    ) {
        importService.importRealEstateCadastralNums(stringPathCadastralNums);
    }

    /**
     * Донаполнение справочника RealEstate кадастровыми номерами ЗУ. Выполняется единожды.
     *
     * @param stringPathCadastralNumsZu Путь до csv файла с кадастровыми номерами ЗУ
     */
    @PostMapping(value = "/realEstateCadastralNumsZu")
    @ApiOperation("Функция загрузки данных о кадастровых номерах земельных участков отселяемых домов, перечисленных "
        + "в 497-ПП. Каталог 27456 ОДОПМ")
    public void importRealEstateCadatralNumsZu(
        @ApiParam("Путь до csv файла с кадастровыми номерами ЗУ") @RequestParam String stringPathCadastralNumsZu
    ) {
        importService.importRealEstateCadastralNumsZu(stringPathCadastralNumsZu);
    }

    /**
     * Формирование JSON с данными по координатам, износу RealEstate.
     *
     * @param path27451csv Путь до csv файла каталога 27451.
     * @param path28609csv Путь до csv файла каталога 28609.
     * @param path28608csv Путь до csv файла каталога 28608.
     * @param pathNpcXml   Путь до xml файла НПЦ.
     * @return json.
     */
    @PostMapping(value = "/realEstateJsonAddInfo")
    @ApiOperation("Формирование JSON с данными по координатам, износу RealEstate.")
    public ResponseEntity<String> realEstateJsonAddInfo(
        @ApiParam("Путь до csv файла каталога 27451") @RequestParam String path27451csv,
        @ApiParam("Путь до csv файла каталога 28609") @RequestParam String path28609csv,
        @ApiParam("Путь до csv файла каталога 28608") @RequestParam String path28608csv,
        @ApiParam("Путь до xml файла НПЦ") @RequestParam String pathNpcXml
    ) {
        return ResponseEntity.ok(
            importService.realEstateJsonAddInfo(path27451csv, path28609csv, path28608csv, pathNpcXml)
        );
    }

    /**
     * Загрузка доп информации RealEstate из json.
     *
     * @param json json.
     * @return ok.
     */
    @PostMapping(value = "/loadAddInfoForRealEstate")
    @ApiOperation("Загрузка доп информации RealEstate из json.")
    public ResponseEntity<String> loadAddInfoForRealEstate(
        @ApiParam(value = "json", required = true) @RequestParam MultipartFile json
    ) {
        importService.loadAddInfoForRealEstate(json);
        return ResponseEntity.ok("ОК");
    }

    /**
     * Формирование JSON RealEstate по unom.
     *
     * @param path27451csv Путь до csv файла каталога 27451.
     * @param path27455csv Путь до csv файла каталога 27455.
     * @param path27456csv Путь до csv файла каталога 27456.
     * @param pathNpcXml   Путь до xml файла НПЦ.
     * @param unom         Уном.
     * @param address      Адрес из ОДОПМ.
     * @return json.
     */
    @PostMapping(value = "/createRealEstateJsonByUnom")
    @ApiOperation("Формирование JSON RealEstate по unom.")
    public ResponseEntity<String> createRealEstateJsonByUnom(
        @ApiParam("Путь до csv файла каталога 27451") @RequestParam String path27451csv,
        @ApiParam("Путь до csv файла каталога 27455") @RequestParam String path27455csv,
        @ApiParam("Путь до csv файла каталога 27456") @RequestParam String path27456csv,
        @ApiParam("Путь до xml файла НПЦ") @RequestParam String pathNpcXml,
        @ApiParam("Уном") @RequestParam String unom,
        @ApiParam("Адрес из ОДОПМ") @RequestParam String address
    ) {
        return ResponseEntity.ok(
            importService.createRealEstateJsonByUnom(
                path27451csv, path27455csv, path27456csv, pathNpcXml, unom, address
            )
        );
    }

    /**
     * Формирование JSON с данными по этажности, подъездности RealEstate.
     *
     * @param pathNpcXml   Путь до xml файла НПЦ.
     * @return json.
     */
    @PostMapping(value = "/realEstateJsonEntranceAddInfo")
    @ApiOperation("Формирование JSON с данными по этажности, подъездности RealEstate.")
    public ResponseEntity<String> realEstateJsonEntranceAddInfo(
        @ApiParam("Путь до xml файла НПЦ") @RequestParam String pathNpcXml
    ) {
        return ResponseEntity.ok(
            importService.realEstateJsonEntranceAddInfo(pathNpcXml)
        );
    }

    /**
     * Загрузка доп информации(подъездность, этажность) RealEstate из json.
     *
     * @param json json.
     * @return ok.
     */
    @PostMapping(value = "/loadEntranceAddInfoForRealEstate")
    @ApiOperation("Загрузка доп информации(подъездность, этажность) RealEstate из json.")
    public ResponseEntity<String> loadEntranceAddInfoForRealEstate(
        @ApiParam(value = "json", required = true) @RequestParam MultipartFile json
    ) {
        importService.loadEntranceAddInfoForRealEstate(json);
        return ResponseEntity.ok("ОК");
    }

}
