package ru.croc.ugd.ssr.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.FlatType;
import ru.croc.ugd.ssr.RoomType;
import ru.croc.ugd.ssr.model.DashboardRequestDocument;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.RealEstateDocument;
import ru.croc.ugd.ssr.service.FlatService;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.RealEstateDocumentService;
import ru.croc.ugd.ssr.service.RoomService;
import ru.croc.ugd.ssr.service.dashboard.DashboardRequestDocumentService;
import ru.croc.ugd.ssr.service.document.ExtendedDocumentService;
import ru.reinform.cdp.backend.service.AppIdentity;
import ru.reinform.cdp.document.model.DocumentAbstract;
import ru.reinform.cdp.document.model.DocumentType;
import ru.reinform.cdp.document.service.DocumentTypesService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;

/**
 * Контроллер с кастомными CRUD операциями.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/custom-crud")
public class CustomCrudController {

    private final AppIdentity appIdentity;
    private final DocumentTypesService documentTypesService;
    private final ExtendedDocumentService extendedDocumentService;
    private final FlatService flatService;
    private final RealEstateDocumentService realEstateDocumentService;
    private final RoomService roomService;
    private final PersonDocumentService personDocumentService;
    private final DashboardRequestDocumentService dashboardRequestDocumentService;

    @InitBinder
    public void initBinder(final WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    /**
     * Получить объект квартиры по id.
     *
     * @param id       - идентификатор квартиры
     * @param response - response
     * @return объект квартиры
     */
    @GetMapping(value = "/fetch/flat/{id}")
    @ApiOperation("Получить объект квартиры по id")
    public FlatType fetchFlatById(
        @ApiParam(value = "ID квартиры", required = true) @PathVariable String id,
        HttpServletResponse response
    ) {
        FlatType flat = flatService.fetchFlat(id);
        if (flat == null) {
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        }
        return flat;
    }

    /**
     * Получить объект комнаты по id.
     *
     * @param id       - идентификатор комнаты
     * @param response - response
     * @return объект комнаты
     */
    @GetMapping(value = "/fetch/room/{id}")
    @ApiOperation("Получить объект комнаты по id")
    public RoomType fetchRoomById(
        @ApiParam(value = "ID комнаты", required = true) @PathVariable String id,
        HttpServletResponse response
    ) {
        RoomType room = roomService.fetchRoom(id);
        if (room == null) {
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        }
        return room;
    }

    /**
     * Получить объект недвижимости по unom.
     *
     * @param unom     - UNOM
     * @param response - response
     * @return объект недвижимости
     */
    @GetMapping(value = "/fetch/REAL-ESTATE/unom/{unom}")
    @ApiOperation("Получить объект недвижимости по unom")
    public RealEstateDocument fetchRealEstateByUnom(
        @ApiParam(value = "UNOM", required = true) @PathVariable String unom,
        HttpServletResponse response
    ) {
        RealEstateDocument realEstateDocument = realEstateDocumentService.fetchDocumentByUnom(unom);
        if (realEstateDocument == null) {
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        }
        return realEstateDocument;
    }

    /**
     * Получить UNOM ОН по flatId.
     *
     * @param flatId - идентификатор квартиры
     * @return UNOM дома, к которому относится квартира
     */
    @GetMapping(value = "/getUnom/REAL-ESTATE/flatId/{flatId}")
    @ApiOperation("Получить UNOM ОН по flatId")
    public String getUnomByFlatId(
        @ApiParam(value = "FlatID", required = true) @PathVariable String flatId
    ) {
        return realEstateDocumentService.getUnomByFlatId(flatId);
    }

    /**
     * Получить DashboardRequestDocument по dashboardId.
     *
     * @param dashboardId - идентификатор дашборда
     * @return Список DashboardRequestDocument
     */
    @GetMapping(value = "/fetch/DASHBOARD-REQUEST/{dashboardId}")
    @ApiOperation("Получить DashboardRequestDocument по dashboardId")
    public List<DashboardRequestDocument> fetchDashboardRequestByDashboardId(
        @ApiParam("dashboardId") @PathVariable String dashboardId
    ) {
        return dashboardRequestDocumentService.fetchByDashboardId(dashboardId);
    }

    /**
     * Получить постранично ОН для расселения.
     *
     * @param pageNum  номер страницы
     * @param pageSize размер страницы
     * @param filter   фильтр (адрес или UNOM)
     * @param orderBy  сортировка
     * @return JSON объект со списком ОН и хинтами
     */
    @GetMapping(value = "/fetch/REAL-ESTATE/pageNum/{pageNum}/pageSize/{pageSize}")
    @ApiOperation("Получить постранично ОН для расселения")
    public String fetchRealEstatePage(
        @ApiParam(value = "pageNum", required = true) @PathVariable int pageNum,
        @ApiParam(value = "pageSize", required = true) @PathVariable int pageSize,
        @ApiParam("filter") @RequestParam(required = false) String filter,
        @ApiParam("orderBy") @RequestParam(required = false) String orderBy
    ) {
        return realEstateDocumentService.fetchRealEstatePage(pageNum, pageSize, filter, orderBy);
    }

    /**
     * Получить постранично целые ОН для расселения.
     *
     * @param pageNum  номер страницы
     * @param pageSize размер страницы
     * @param filter   фильтр (адрес или UNOM)
     * @param orderBy  сортировка
     * @return JSON объект со списком ОН и хинтами
     */
    @GetMapping(value = "/fetchFull/REAL-ESTATE/pageNum/{pageNum}/pageSize/{pageSize}")
    @ApiOperation("Получить постранично целые ОН для расселения")
    public String fetchFullRealEstatePage(
        @ApiParam(value = "pageNum", required = true) @PathVariable int pageNum,
        @ApiParam(value = "pageSize", required = true) @PathVariable int pageSize,
        @ApiParam("filter") @RequestParam(required = false) String filter,
        @ApiParam("orderBy") @RequestParam(required = false) String orderBy
    ) {
        return realEstateDocumentService.fetchFullRealEstatePage(pageNum, pageSize, filter, orderBy);
    }

    /**
     * Получить постранично квартиры ОН для расселения.
     *
     * @param pageNum    номер страницы
     * @param pageSize   размер страницы
     * @param unom       UNOM ОН
     * @param roomAmount количество комнат в квартире
     * @param flatNumber номер квартиры
     * @param floor      этаж
     * @param orderBy    строка сортировки
     * @return JSON объект со списком квартир ОН и хинтами
     */
    @GetMapping(value = "/fetch/FLAT/pageNum/{pageNum}/pageSize/{pageSize}/unom/{unom}")
    @ApiOperation("Получить постранично квартиры ОН для расселения")
    public String fetchFlatPage(
        @ApiParam(value = "pageNum", required = true) @PathVariable int pageNum,
        @ApiParam(value = "pageSize", required = true) @PathVariable int pageSize,
        @ApiParam(value = "unom", required = true) @PathVariable String unom,
        @ApiParam("roomAmount") @RequestParam(required = false) String roomAmount,
        @ApiParam("flatNumber") @RequestParam(required = false) String flatNumber,
        @ApiParam("floor") @RequestParam(required = false) Integer floor,
        @ApiParam("orderBy") @RequestParam(required = false) String orderBy
    ) {
        return flatService.fetchFlatPageByUnom(pageNum, pageSize, unom, roomAmount, flatNumber, floor, orderBy);
    }

    /**
     * Возвращает UNOM и адреса всех ОН.
     *
     * @return список JSON-объектов, содержащих UNOM и адрес ОН
     */
    @GetMapping(value = "/fetchUnomAndAddress/REAL-ESTATE")
    @ApiOperation("Получить UNOM и адреса всех ОН")
    public String getUnomAndAddressRealEstates() {
        return realEstateDocumentService.getUnomAndAddressRealEstates();
    }

    /**
     * Возвращает id и адрес ОН по flatId.
     *
     * @param flatId ид квартиры
     * @return id и адрес ОН
     */
    @GetMapping(value = "/getIdAndAddressByFlatId/REAL-ESTATE/{flatId}")
    @ApiOperation("Получить id ОН по flatId")
    public String getIdAndAddressByFlatId(
        @ApiParam(required = true) @PathVariable String flatId
    ) {
        return realEstateDocumentService.getIdAndAddressByFlatId(flatId);
    }

    /**
     * testUpdateInformationServiceCodeOfRealEstates.
     *
     * @param realEstateIds          realEstateIds
     * @param informationServiceCode informationServiceCode
     */
    @GetMapping(value = "/testUpdateInformationServiceCodeOfRealEstates")
    public void updateInformationServiceCodeOfRealEstates(
        @RequestParam List<String> realEstateIds,
        @RequestParam String informationServiceCode
    ) {
        realEstateDocumentService.updateInformationServiceCodeOfRealEstates(realEstateIds, informationServiceCode);
    }

    /**
     * testUpdateInformationServiceCodeOfFlats.
     *
     * @param flatIds                flatIds
     * @param informationServiceCode informationServiceCode
     */
    @GetMapping(value = "/testUpdateInformationServiceCodeOfFlats")
    public void updateInformationServiceCodeOfFlats(
        @RequestParam List<String> flatIds,
        @RequestParam String informationServiceCode
    ) {
        flatService.updateInformationServiceCodeOfRealEstates(flatIds, informationServiceCode);
    }

    /**
     * testGetInformationCenterByCode.
     *
     * @param code code
     * @return String
     */
    @GetMapping(value = "/testGetInformationCenterByCode/{code}")
    public String getInformationCenterByCode(
        @ApiParam(required = true)
        @PathVariable String code
    ) {
        JSONObject informationCenter = realEstateDocumentService.getInformationCenterByCode(code);
        if (informationCenter != null) {
            return informationCenter.toString();
        } else {
            return null;
        }
    }

    /**
     * Получить жителей квартиры по ид.
     *
     * @param flatId ид квартиры
     * @return список жителей
     */
    @GetMapping(value = "/getLiversByFlatId/{flatId}")
    public List<PersonDocument> getLiversByFlatId(
        @ApiParam(required = true)
        @PathVariable final String flatId
    ) {
        return personDocumentService.fetchByFlatId(flatId);
    }

    /**
     * fetchDocumentsPage.
     * @param anyDocumentTypeCode anyDocumentTypeCode
     * @param pageNum pageNum
     * @param pageSize pageSize
     * @param queryNode queryNode
     * @return list of DocumentAbstract
     */
    @ApiOperation(
        value = "Постраничное получение документов заданного типа и критерии",
        notes = "Нумерация начинается с 0-й страницы"
    )
    @RequestMapping(
        path = {"/fetch/{anyDocumentTypeCode}/pageNum/{pageNum}/pageSize/{pageSize}"},
        method = {RequestMethod.POST},
        produces = {"application/json;charset=UTF-8"}
    )
    public List<DocumentAbstract> fetchDocumentsPage(
        @ApiParam(value = "код типа документов (краткий или полный)", required = true)
        @PathVariable String anyDocumentTypeCode,
        @ApiParam(value = "номер страницы", required = true)
        @PathVariable int pageNum,
        @ApiParam(value = "размер страницы", required = true)
        @PathVariable int pageSize,
        @ApiParam("Критерии сортировки через запятую."
            + " Напр. ResettlementRequest.main.startResettlementDate, Test.other.criteria")
        @RequestParam(required = false) String sortCriteria,
        @ApiParam("Направление сортировки. Напр DESC. ASC если пусто.")
        @RequestParam(required = false) String sortDirection,
        @ApiParam(value = "JSON-запрос (напр. {\"dossier\":{\"signDate\":\"2018-05-10\"}})", required = true)
        @RequestBody ObjectNode queryNode
    ) {
        final String documentTypeCode = this.appIdentity.briefDocumentTypeCode(anyDocumentTypeCode);
        final DocumentType<DocumentAbstract> documentType = documentTypesService.fetchType(documentTypeCode);

        return extendedDocumentService.fetchDocumentsPageByQueryNode(
            documentType,
            queryNode,
            splitByComma(sortCriteria),
            sortDirection,
            pageNum,
            pageSize
        );
    }

    /**
     * countDocuments.
     *
     * @param anyDocumentTypeCode anyDocumentTypeCode
     * @param queryNode queryNode
     * @return count
     */
    @ApiOperation("Получение количества документов заданного типа и критерии")
    @RequestMapping(
        path = {"/count/{anyDocumentTypeCode}"},
        method = {RequestMethod.POST},
        produces = {"application/json;charset=UTF-8"}
    )
    public long countDocuments(
        @ApiParam(value = "код типа документов (краткий или полный)", required = true)
        @PathVariable String anyDocumentTypeCode,
        @ApiParam(value = "JSON-запрос (напр. {\"dossier\":{\"signDate\":\"2018-05-10\"}})", required = true)
        @RequestBody ObjectNode queryNode
    ) {
        return extendedDocumentService.countDocuments(anyDocumentTypeCode, queryNode);
    }

    @ApiOperation("Удаление документов заданного типа")
    @RequestMapping(
        path = {"/delete/{anyDocumentTypeCode}"},
        method = {RequestMethod.DELETE},
        produces = {"application/json;charset=UTF-8"}
    )
    public List<DocumentAbstract> deleteDocumentsByIds(
        @ApiParam(value = "тип документа (краткий или полный)", required = true)
        @PathVariable String anyDocumentTypeCode,
        @ApiParam("индексировать документ в поисковом движке")
        @RequestParam(required = false, defaultValue = "true") boolean flagReindex,
        @ApiParam("комментарий к операции")
        @RequestParam(required = false) String notes,
        @RequestBody(required = false) List<String> ids
    ) {
        return extendedDocumentService.deleteDocumentsByIds(anyDocumentTypeCode, ids, flagReindex, notes);
    }

    private List<String> splitByComma(final String sortCriteria) {
        return Optional.ofNullable(sortCriteria)
            .map(s -> Arrays.asList(StringUtils.split(s, ",")))
            .orElse(Collections.emptyList())
            .stream()
            .map(String::trim)
            .collect(Collectors.toList());
    }
}
