package ru.croc.ugd.ssr.controller.disabledperson;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.dto.disabledperson.RestDisabledPersonImportDto;
import ru.croc.ugd.ssr.service.disabledperson.RestDisabledPersonImportService;

/**
 * Контроллер для загрузки сведений по маломобильным гражданам.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/disabled-person-imports")
public class DisabledPersonImportController {

    private final RestDisabledPersonImportService restDisabledPersonImportService;

    /**
     * Создать пустой документ загрузки сведений по маломобильным гражданам.
     *
     * @return данные о загрузке сведений по маломобильным гражданам
     */
    @PostMapping
    public RestDisabledPersonImportDto create() {
        return restDisabledPersonImportService.create();
    }

    /**
     * Выполнить разбор файла.
     *
     * @param id ИД документа загрузки сведений по маломобильным гражданам
     * @param fileStoreId ИД в FileStore файла со сведениями по маломобильным гражданам
     */
    @PostMapping(value = "/{id}/parse-file")
    public void parseFile(
        @PathVariable final String id,
        @RequestParam final String fileStoreId
    ) {
        restDisabledPersonImportService.parseFile(id, fileStoreId);
    }

    /**
     * Получить документ загрузки сведений по маломобильным гражданам.
     *
     * @param id ИД документа загрузки сведений по маломобильным гражданам
     * @return данные о загрузке сведений по маломобильным гражданам
     */
    @GetMapping(value = "/{id}")
    public RestDisabledPersonImportDto fetchById(@PathVariable final String id) {
        return restDisabledPersonImportService.fetchById(id);
    }
}
