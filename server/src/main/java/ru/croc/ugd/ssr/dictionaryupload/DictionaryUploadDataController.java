package ru.croc.ugd.ssr.dictionaryupload;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.service.DictionaryUploadDataService;

import java.util.List;

/**
 * Контроллер для выгрузки данных.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/dictionary")
public class DictionaryUploadDataController {

    private final DictionaryUploadDataService dictionaryUploadDataService;

    /**
     * Генерация справочников.
     */
    @PostMapping(value = "/generate")
    @ApiOperation("Генерирует справочники")
    public void generateDictionaries() {
        dictionaryUploadDataService.generateAddressFromDictionary();
        dictionaryUploadDataService.generateAddressToDictionary();
        dictionaryUploadDataService.generatePeopleDictionary();
    }

    /**
     * Получение справочников из выгруженных данных.
     *
     * @return список справочников
     */
    @GetMapping(value = "/upload")
    @ApiOperation("Возвращает справочники")
    public List<DictionaryUploadDataDto> getDictionariesUploadData() {
        return dictionaryUploadDataService.getDictionaryUploadDataList();
    }

}
