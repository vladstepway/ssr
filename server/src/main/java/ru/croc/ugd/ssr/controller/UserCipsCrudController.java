package ru.croc.ugd.ssr.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.service.UserCipsCrudService;

import java.io.IOException;
import java.util.List;

/**
 * Контроллер по работе с ЦИПами юезров в USERPROFILE.
 */
@RestController
@RequiredArgsConstructor
public class UserCipsCrudController {

    private final UserCipsCrudService service;

    /**
     * Получение всех ЦИПов пользователя.
     *
     * @param login логин пользователя
     * @return пользователи ЦИПа
     */
    @ApiOperation(value = "Получение всех ЦИПов пользователя")
    @GetMapping(value = "getAllUserCipsByLogin/{login}")
    public ResponseEntity<List<String>> getAllUserCipsByLogin(
            @ApiParam(value = "логин пользователя", required = true)
            @PathVariable String login
    ) {
        service.getAllUserCipsByLogin(login);
        return ResponseEntity.ok(service.getAllUserCipsByLogin(login));
    }

    /**
     * Добавление ЦИПов пользователю.
     *
     * @param login логин пользователя
     * @param cips  коды ЦИПов через запятую
     * @return status
     * @throws IOException exception
     */
    @ApiOperation(value = "Добавление ЦИПов пользователю")
    @PutMapping(value = "addUserCipsByLogin/{login}")
    public ResponseEntity<String> addUserCipsByLogin(
            @ApiParam(value = "логин пользователя", required = true)
            @PathVariable String login,
            @ApiParam(value = "коды ЦИПов через запятую")
            @RequestBody(required = true) String cips
    ) throws IOException {
        service.addUserCipsByLogin(login, cips);
        return ResponseEntity.ok("ok");
    }

    /**
     * Удаление всех ЦИПов пользователя.
     *
     * @param login логин пользователя
     * @return status
     * @throws IOException exception
     */
    @ApiOperation(value = "Удаление всех ЦИПов пользователя")
    @DeleteMapping(value = "deleteAllUserCipsByLogin/{login}")
    public ResponseEntity<String> deleteAllUserCipsByLogin(
            @ApiParam(value = "логин пользователя", required = true)
            @PathVariable String login
    ) throws IOException {
        service.deleteAllUserCipsByLogin(login);
        return ResponseEntity.ok("ok");
    }

    /**
     * Удаление ЦИПов пользователя.
     *
     * @param login логин пользователя
     * @param cips  коды ЦИПов через запятую
     * @return status
     * @throws IOException exception
     */
    @ApiOperation(value = "Удаление ЦИПов пользователя")
    @DeleteMapping(value = "deleteUserCipsByLogin/{login}")
    public ResponseEntity<String> deleteUserCipsByLogin(
            @ApiParam(value = "логин пользователя", required = true)
            @PathVariable String login,
            @ApiParam(value = "коды ЦИПов через запятую")
            @RequestBody(required = true) String cips
    ) throws IOException {
        service.deleteUserCipsByLogin(login, cips);
        return ResponseEntity.ok("ok");
    }
}
