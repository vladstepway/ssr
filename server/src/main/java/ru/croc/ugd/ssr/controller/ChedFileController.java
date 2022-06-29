package ru.croc.ugd.ssr.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.service.ChedFileService;

/**
 * Контроллер по работе с ЦХЭД.
 */
@RestController
@RequiredArgsConstructor
public class ChedFileController {

    private final ChedFileService chedFileService;

    /**
     * Выложить файл в (Р)ЦХЭД.
     *
     * @param fileId            ид файла в альфреско
     * @param asgufCode         код документа в АС ГУФ
     * @param chedDocumentClass тип класса документа в ЦХЭД
     * @return ид документа (Р)ЦХЭД
     */
    @GetMapping(value = "uploadFileToChed")
    @ApiOperation("Выложить файл в (Р)ЦХЭД.")
    public String uploadFileToChed(
        @ApiParam("Ид файла в альфреско") @RequestParam String fileId,
        @ApiParam("Код документа в АС ГУФ") @RequestParam String asgufCode,
        @ApiParam("Тип класса документа в ЦХЭД") @RequestParam String chedDocumentClass
    ) {
        return chedFileService.uploadFileToChed(fileId, asgufCode, chedDocumentClass);
    }

}
