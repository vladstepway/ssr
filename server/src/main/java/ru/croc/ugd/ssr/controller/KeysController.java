package ru.croc.ugd.ssr.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.service.KeysService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/keys")
public class KeysController {

    private final KeysService keysService;

    /**
     * Обновление данных по сдаче/выдаче ключей.
     *
     * @param unoms UNOM-ы домов
     */
    @ApiOperation(value = "Обновление данных по сдаче/выдаче ключей по UNOM-ам домов")
    @PostMapping(value = "/updateKeysByUnoms")
    public void updateKeysByUnoms(
        @ApiParam(value = "UNOM-ы домов") @RequestBody List<String> unoms
    ) {
        keysService.processUpdateKeysByUnoms(unoms);
    }

    /**
     * Обновление статуса переселения по информации о сдаче/выдаче ключей.
     */
    @ApiOperation(value = "Обновление статуса переселения по информации о сдаче/выдаче ключей")
    @PostMapping(value = "/updateRelocationStatus")
    public void updateRelocationStatus() {
        keysService.processUpdateRelocationStatus();
    }
}
