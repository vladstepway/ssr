package ru.croc.ugd.ssr.controller;

import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.dto.DashboardChangelogDto;
import ru.croc.ugd.ssr.dto.changelog.ChangelogDto;
import ru.croc.ugd.ssr.service.changelog.apartmentinspection.ApartmentInspectionChangelogService;
import ru.croc.ugd.ssr.service.changelog.cip.CipChangelogService;
import ru.croc.ugd.ssr.service.changelog.contractappointment.ContractAppointmentChangelogService;
import ru.croc.ugd.ssr.service.changelog.flatappointment.FlatAppointmentChangelogService;
import ru.croc.ugd.ssr.service.changelog.notary.NotaryApplicationChangelogService;
import ru.croc.ugd.ssr.service.changelog.notarycompensation.NotaryCompensationChangelogService;
import ru.croc.ugd.ssr.service.changelog.person.PersonChangelogService;
import ru.croc.ugd.ssr.service.changelog.realestate.RealEstateChangelogService;
import ru.croc.ugd.ssr.service.dashboard.DashboardChangelogService;
import ru.reinform.cdp.ldap.model.UserBean;

import java.time.LocalDate;
import java.util.List;

/**
 * Контроллер по получению изменений объекта.
 */
@RestController
@RequestMapping("/changelog-controller")
@RequiredArgsConstructor
public class ChangelogController {

    private final DashboardChangelogService dashboardChangelogService;
    private final PersonChangelogService personChangelogService;
    private final NotaryApplicationChangelogService notaryApplicationChangelogService;
    private final ContractAppointmentChangelogService contractAppointmentChangelogService;
    private final FlatAppointmentChangelogService flatAppointmentChangelogService;
    private final CipChangelogService cipChangelogService;
    private final ApartmentInspectionChangelogService apartmentInspectionChangelogService;
    private final RealEstateChangelogService realEstateChangelogService;
    private final NotaryCompensationChangelogService notaryCompensationChangelogService;

    /**
     * Получение изменений дашборда по ид.
     *
     * @param docId ид документа
     * @return список изменений
     */
    @GetMapping(value = "/dashboard-changelog/{docId}")
    public List<DashboardChangelogDto> getDashboardChangelog(
        @ApiParam(required = true)
        @PathVariable String docId
    ) {
        return dashboardChangelogService.getChangelog(docId);
    }

    /**
     * Получение изменений жителя по ид.
     *
     * @param id               ид жителя
     * @param page             номер страницы
     * @param pageSize         размер страницы
     * @param dateFrom         фильтр - дата от
     * @param dateTo           фильтр - дата до
     * @param logins           фильтр - логины пользователей
     * @param pathToAttribute  фильтр - Атрибут
     * @return список изменений
     */
    @PostMapping(value = "/person-changelog/{id}")
    public Page<ChangelogDto> getPersonChangelog(
        @ApiParam(required = true)
        @PathVariable String id,
        @RequestParam int page,
        @RequestParam int pageSize,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateFrom,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateTo,
        @RequestParam(required = false) String pathToAttribute,
        @RequestBody(required = false) List<String> logins
    ) {
        return personChangelogService.getChangelog(id, page, pageSize, dateFrom, dateTo, logins, pathToAttribute);
    }

    /**
     * Получение изменений заявления на посещение нотариуса по ид.
     *
     * @param id               ид заявления
     * @param page             номер страницы
     * @param pageSize         размер страницы
     * @param dateFrom         фильтр - дата от
     * @param dateTo           фильтр - дата до
     * @param logins           фильтр - логины пользователей
     * @param pathToAttribute  фильтр - Атрибут
     * @return список изменений
     */
    @GetMapping(value = "/notary-application-changelog/{id}")
    public Page<ChangelogDto> getNotaryApplicationChangelog(
        @ApiParam(required = true)
        @PathVariable String id,
        @RequestParam int page,
        @RequestParam int pageSize,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateFrom,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateTo,
        @RequestParam(required = false) String pathToAttribute,
        @RequestBody(required = false) List<String> logins
    ) {
        return notaryApplicationChangelogService
            .getChangelog(id, page, pageSize, dateFrom, dateTo, logins, pathToAttribute);
    }

    /**
     * Получение изменений заявления на возмещение услуг нотариуса по ид.
     *
     * @param id               ид заявления
     * @param page             номер страницы
     * @param pageSize         размер страницы
     * @param dateFrom         фильтр - дата от
     * @param dateTo           фильтр - дата до
     * @param logins           фильтр - логины пользователей
     * @param pathToAttribute  фильтр - Атрибут
     * @return список изменений
     */
    @GetMapping(value = "/notary-compensation-changelog/{id}")
    public Page<ChangelogDto> getNotaryCompensationChangelog(
        @ApiParam(required = true)
        @PathVariable String id,
        @RequestParam int page,
        @RequestParam int pageSize,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateFrom,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateTo,
        @RequestParam(required = false) String pathToAttribute,
        @RequestBody(required = false) List<String> logins
    ) {
        return notaryCompensationChangelogService
            .getChangelog(id, page, pageSize, dateFrom, dateTo, logins, pathToAttribute);
    }

    /**
     * Получение изменений заявления на посещение нотариуса по ид.
     *
     * @param id               ид записи
     * @param page             номер страницы
     * @param pageSize         размер страницы
     * @param dateFrom         фильтр - дата от
     * @param dateTo           фильтр - дата до
     * @param logins           фильтр - логины пользователей
     * @param pathToAttribute  фильтр - Атрибут
     * @return список изменений
     */
    @GetMapping(value = "/contract-appointment-changelog/{id}")
    public Page<ChangelogDto> getContractAppointmentChangelog(
        @ApiParam(required = true)
        @PathVariable String id,
        @RequestParam int page,
        @RequestParam int pageSize,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateFrom,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateTo,
        @RequestParam(required = false) String pathToAttribute,
        @RequestBody(required = false) List<String> logins
    ) {
        return contractAppointmentChangelogService
            .getChangelog(id, page, pageSize, dateFrom, dateTo, logins, pathToAttribute);
    }

    /**
     * Получение изменений заявления на осмотр квартиры по ид.
     *
     * @param id               ид осмотра
     * @param page             номер страницы
     * @param pageSize         размер страницы
     * @param dateFrom         фильтр - дата от
     * @param dateTo           фильтр - дата до
     * @param logins           фильтр - логины пользователей
     * @param pathToAttribute  фильтр - Атрибут
     * @return список изменений
     */
    @GetMapping(value = "/flat-appointment-changelog/{id}")
    public Page<ChangelogDto> getFlatAppointmentChangelog(
        @ApiParam(required = true)
        @PathVariable String id,
        @RequestParam int page,
        @RequestParam int pageSize,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateFrom,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateTo,
        @RequestParam(required = false) String pathToAttribute,
        @RequestBody(required = false) List<String> logins
    ) {
        return flatAppointmentChangelogService
            .getChangelog(id, page, pageSize, dateFrom, dateTo, logins, pathToAttribute);
    }

    @GetMapping(value = "/apartment-inspection-changelog/{id}")
    public Page<ChangelogDto> getApartmentInspectionChangelog(
        @ApiParam(required = true)
        @PathVariable String id,
        @RequestParam int page,
        @RequestParam int pageSize,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateFrom,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateTo,
        @RequestParam(required = false) String pathToAttribute,
        @RequestBody(required = false) List<String> logins
    ) {
        return apartmentInspectionChangelogService
            .getChangelog(id, page, pageSize, dateFrom, dateTo, logins, pathToAttribute);
    }

    /**
     * Получение изменений центра информирования о переселении.
     *
     * @param id               ид ЦИП
     * @param page             номер страницы
     * @param pageSize         размер страницы
     * @param dateFrom         фильтр - дата от
     * @param dateTo           фильтр - дата до
     * @param logins           фильтр - логины пользователей
     * @param pathToAttribute  фильтр - Атрибут
     * @return список изменений
     */
    @GetMapping(value = "/cip-changelog/{id}")
    public Page<ChangelogDto> getCipChangelog(
        @ApiParam(required = true)
        @PathVariable String id,
        @RequestParam int page,
        @RequestParam int pageSize,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateFrom,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateTo,
        @RequestParam(required = false) String pathToAttribute,
        @RequestBody(required = false) List<String> logins
    ) {
        return cipChangelogService.getChangelog(id, page, pageSize, dateFrom, dateTo, logins, pathToAttribute);
    }

    /**
     * Получение изменений расселяемых домов.
     *
     * @param id               ид дома
     * @param page             номер страницы
     * @param pageSize         размер страницы
     * @param dateFrom         фильтр - дата от
     * @param dateTo           фильтр - дата до
     * @param logins           фильтр - логины пользователей
     * @param pathToAttribute  фильтр - Атрибут
     * @return список изменений
     */
    @GetMapping(value = "/real-estate-changelog/{id}")
    public Page<ChangelogDto> getRealEstateChangelog(
        @ApiParam(required = true)
        @PathVariable String id,
        @RequestParam int page,
        @RequestParam int pageSize,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateFrom,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateTo,
        @RequestParam(required = false) String pathToAttribute,
        @RequestBody(required = false) List<String> logins
    ) {
        return realEstateChangelogService.getChangelog(id, page, pageSize, dateFrom, dateTo, logins, pathToAttribute);
    }

    /**
     * Получает UserBean-ы всех пользователей, вносивших изменения в документ.
     *
     * @param id id документа
     * @return список пользователей
     */
    @GetMapping(value = "/users-who-change-document/{id}")
    public List<UserBean> getUsersWhoChangedDocument(
        @PathVariable String id
    ) {
        return personChangelogService.getUsersWhoChangedDocument(id);
    }
}
