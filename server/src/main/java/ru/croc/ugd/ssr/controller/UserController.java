package ru.croc.ugd.ssr.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.service.UserService;
import ru.reinform.cdp.ldap.model.OrganizationBean;
import ru.reinform.cdp.ldap.model.UserBean;

import java.util.List;

/**
 * Контролер для работы с пользователями.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    /**
     * Получение всех пользователей.
     *
     * @param departmentCode Код подразделения пользователя
     * @param fio            ФИО
     * @param position       Должность
     * @return список пользователей
     */
    @ApiOperation(value = "Получение всех пользователей")
    @GetMapping(value = "getAllLdapUsers")
    public ResponseEntity<List<UserBean>> getAllUserCipsByLogin(
        @ApiParam("Код подразделения пользователя") @RequestParam(name = "departmentCode",
            required = false) String departmentCode,
        @ApiParam("ФИО") @RequestParam(name = "fio", required = false) String fio,
        @ApiParam("Должность") @RequestParam(name = "position", required = false) String position) {
        return ResponseEntity.ok(userService.getAllLdapUsers(departmentCode, fio, position));
    }

    /**
     * Получение пользователей по логинам.
     *
     * @param login Список логинов пользователей
     * @return Список пользователей
     */
    @ApiOperation(value = "Получение пользователей по логинам")
    @GetMapping(value = "getLdapUsersByLogin")
    public ResponseEntity<List<UserBean>> getLdapUsersByLogin(
        @ApiParam("Список логинов пользователей") @RequestParam(name = "login") List<String> login) {
        return ResponseEntity.ok(userService.getAllLdapUsers(login));
    }

    /**
     * Получение пользователей по логину.
     *
     * @param login Логин пользователя
     * @return Список пользователей
     */
    @ApiOperation(value = "Получение пользователей по логину")
    @GetMapping(value = "getLdapUsersByLoginLike")
    public ResponseEntity<List<UserBean>> getLdapUsersByLoginLike(
        @ApiParam("Логин пользователя") @RequestParam(name = "login") String login
    ) {
        return ResponseEntity.ok(userService.getAllLdapUsersLoginLike(login));
    }

    /**
     * Получение всех организаций пользователей.
     *
     * @return список организаций.
     */
    @ApiOperation(value = "Получение всех организаций")
    @GetMapping(value = "getAllUserOrganization")
    public ResponseEntity<List<OrganizationBean>> getAllOrganizations() {
        return ResponseEntity.ok(userService.getAllOrganization());
    }

    /**
     * Получение организации пользователя.
     *
     * @param login логин пользователя
     * @return организация
     */
    @ApiOperation(value = "Получение организации пользователя")
    @GetMapping(value = "getOrganization")
    public ResponseEntity<OrganizationBean> getOrganization(
        @ApiParam("Логин пользователя") @RequestParam(name = "login", required = false) final String login
    ) {
        return ResponseEntity.ok(userService.getOrganization(login));
    }
}
