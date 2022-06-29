package ru.croc.ugd.ssr.integration.controller;

import com.google.gson.Gson;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.dto.DummyPersonDto;
import ru.croc.ugd.ssr.integration.parser.SmevResponseParser;
import ru.croc.ugd.ssr.integration.scheduler.SchedulerTask;
import ru.croc.ugd.ssr.integration.service.FlatDetailsCallbackService;
import ru.croc.ugd.ssr.integration.service.IntegrationService;
import ru.croc.ugd.ssr.integration.service.MdmIntegrationService;
import ru.croc.ugd.ssr.integration.service.flows.BeginningRessetlementFlowsService;
import ru.croc.ugd.ssr.integration.service.flows.ContractSignedFlowsService;
import ru.croc.ugd.ssr.integration.service.flows.FlatFreeFlowsService;
import ru.croc.ugd.ssr.integration.service.flows.FlatResettlementStatusInitService;
import ru.croc.ugd.ssr.integration.service.flows.FlatViewStatusService;
import ru.croc.ugd.ssr.integration.service.flows.KeyIssueFlowService;
import ru.croc.ugd.ssr.integration.service.flows.PersonsUpdateFlowsService;
import ru.croc.ugd.ssr.integration.service.flows.SignActStatusFlowService;
import ru.croc.ugd.ssr.integration.service.flows.StatementStatusService;
import ru.croc.ugd.ssr.integration.service.wsdl.flat.FetchFlatDetailsSoapService;
import ru.croc.ugd.ssr.integration.service.wsdl.flat.model.FlatDetails;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPFlatFreeType;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPKeyIssueType;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPSignActStatusType;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPSignAgreementStatusType;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPStartRemovalType;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPStatementStatusType;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPViewStatusType;

import java.util.List;

/**
 * Интеграционный.
 */
@RestController
//TODO @RequestMapping("/integration")
@AllArgsConstructor
public class IntegrationController {

    private static final Logger LOG = LoggerFactory.getLogger(IntegrationController.class);

    private static final String OK = new Gson().toJson("Ok");

    private final IntegrationService integrationService;

    private final SmevResponseParser smevResponseParser;

    private final SchedulerTask schedulerTask;

    private final PersonsUpdateFlowsService personsUpdateFlowsService;

    private final BeginningRessetlementFlowsService beginningRessetlementFlowsService;

    private final FlatViewStatusService flatViewStatusService;

    private final StatementStatusService statementStatusService;

    private final KeyIssueFlowService keyIssueFlowService;

    private final MdmIntegrationService mdmIntegrationService;

    private final FlatFreeFlowsService flatFreeFlowsService;

    private final ContractSignedFlowsService contractSignedFlowsService;

    private final FlatResettlementStatusInitService flatResettlementStatusInitService;

    private final SignActStatusFlowService signActStatusFlowService;

    private final FetchFlatDetailsSoapService fetchFlatDetailsSoapService;

    private final FlatDetailsCallbackService flatDetailsCallbackService;

    /**
     * Контроллер отправки запроса для получения сведения о жителях расселяемого дома.
     *
     * @param unom
     *            UNOM расселяемого дома
     * @param cadastr
     *            Кадастровый номер расселяемого дома
     * @return status
     */
    @ApiOperation("Контроллер отправки запроса для получения сведения о жителях расселяемого дома")
    @PostMapping("/sendResettledHouseTask")
    public ResponseEntity<String> sendResettledHouseTask(
        @ApiParam("UNOM расселяемого дома") @RequestParam(name = "unom") final int unom,
        @ApiParam("Кадастровый номер расселяемого дома") @RequestParam(name = "cadastr",
            required = false) final String cadastr) {
        personsUpdateFlowsService.sendResettledHouseRequest(unom, cadastr);
        LOG.info("Resettled house task was send");
        return ResponseEntity.ok(OK);
    }

    /**
     * Контроллер отправки запроса для получения сведения о жителях многих расселяемых домов.
     *
     * @param unoms UNOMы расселяемого дома
     * @return status
     */
    @ApiOperation("Контроллер отправки запроса для получения сведения о жителях расселяемых домов")
    @PostMapping("/sendResettledHousesTask")
    public ResponseEntity<String> sendResettledHousesTask(
        @ApiParam("UNOM расселяемого дома") @RequestBody final List<Integer> unoms
    ) {
        if (unoms == null || unoms.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(OK);
        }
        unoms.forEach(unom -> personsUpdateFlowsService.sendResettledHouseRequest(unom, null));
        LOG.info("Resettled houses task was sent, unoms = {}", unoms);
        return ResponseEntity.ok(OK);
    }

    /**
     * Отправка сведений о начале переселения.
     *
     * @param info
     *            info
     * @return status
     */
    @ApiOperation(value = "Отправка сведений о начале переселения")
    @PostMapping("/sendInfoBeginningResettlement")
    public ResponseEntity<String> sendInfoBeginningResettlement(@RequestBody SuperServiceDGPStartRemovalType info) {
        beginningRessetlementFlowsService.sendInfoBeginningResettlement(info);
        LOG.info("Info Beginning Resettlement was send");
        return ResponseEntity.ok(OK);
    }

    /**
     * Отправка сведений о статусе просмотра квартир.
     *
     * @param info
     *            info
     * @return status
     */
    @ApiOperation(value = "Отправка сведений о статусе просмотра квартир")
    @PostMapping("/sendFlatViewStatus")
    public ResponseEntity<String> sendFlatViewStatus(@RequestBody SuperServiceDGPViewStatusType info) {
        flatViewStatusService.sendFlatViewStatus(info);
        LOG.info("Flat view status was send");
        return ResponseEntity.ok(OK);
    }

    /**
     * Отправка сведений о статусе подачи заявления.
     *
     * @param id   ид жителя
     * @param info info
     * @return status
     */
    @ApiOperation(value = "Отправка сведений о статусе подачи заявления")
    @PostMapping("/sendStatementStatus/{id}")
    public ResponseEntity<String> sendStatementStatus(
        @PathVariable String id,
        @RequestBody SuperServiceDGPStatementStatusType info
    ) {
        statementStatusService.sendStatementStatus(info, id);
        LOG.info("Statement status was send");
        return ResponseEntity.ok(OK);
    }

    /**
     * Отправка сведений о статусе просмотра квартир.
     *
     * @param id   ид жителя
     * @param info info
     * @return status
     */
    @ApiOperation(value = "Отправка сведений о статусе просмотра квартир")
    @PostMapping("/sendKeyIssue/{id}")
    public ResponseEntity<Void> sendKeyIssue(
        @PathVariable String id,
        @RequestBody SuperServiceDGPKeyIssueType info
    ) {
        keyIssueFlowService.sendKeyIssue(info, id);
        LOG.info("Info key issue was send");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Отправка сведений о статусе освобождения квартир.
     *
     * @param id   ид жителя
     * @param info info
     * @return status
     */
    @ApiOperation(value = "Отправка сведений о статусе освобождения квартир")
    @PostMapping("/sendFreeFlat/{id}")
    public ResponseEntity<Void> sendFreeFlat(
        @PathVariable String id,
        @RequestBody SuperServiceDGPFlatFreeType info
    ) {
        flatFreeFlowsService.sendFlatFree(info, id);
        LOG.info("Info free flat was send");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Отправка сведений о статусе подписания договора.
     *
     * @param personId
     *            ид подписавшего договор.
     * @param info
     *            info
     * @return status
     */
    @ApiOperation(value = "Отправка сведений о статусе подписания договора")
    @PostMapping("/sendSignedContract")
    public ResponseEntity<Void> sendSignedContract(
        @RequestParam final String personId,
        @RequestBody final SuperServiceDGPSignAgreementStatusType info
    ) {
        contractSignedFlowsService.sendSignedContract(personId, info);
        LOG.info("Info signed contract was send");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Обогащение дома данными из ЕЖД.
     *
     * @param id
     *            Id дома
     * @return status
     */
    @ApiOperation(value = "Обогащение дома данными из ЕЖД")
    @GetMapping(value = "/updateRealEstateFromEzd/{id}")
    public ResponseEntity<String> updateRealEstateFromEzd(
        @ApiParam(value = "Id дома", required = true) @PathVariable(value = "id") String id) {
        LOG.info("Запрос на обогащение дома данными из ЕЖД. Id: {}", id);
        integrationService.updateRealEstateFromEzd(id, "Service User");
        return ResponseEntity.ok(OK);
    }

    /**
     * Обогащение квартиры данными из ЕЖД.
     *
     * @param id
     *            Id квартиры
     * @return status
     */
    @ApiOperation(value = "Обогащение квартиры данными из ЕЖД")
    @GetMapping(value = "/updateFlatFromEzd/{id}")
    public ResponseEntity<String> updateFlatFromEzd(
        @ApiParam(value = "Id квартиры", required = true) @PathVariable(value = "id") String id
    ) {
        LOG.info("Запрос на обогащение квартиры данными из ЕЖД. Id: {}", id);
        integrationService.updateFlatFromEzd(id);
        return ResponseEntity.ok(OK);
    }

    /**
     * Тестовый запрос на обогащение дома данными из ЕЖД.
     *
     * @param createTestEstate
     *            С созданием тестового дома
     * @param flatNumber
     *            Номер квартиры, по умолчанию 23
     * @return status
     */
    @ApiOperation(value = "Тестовый запрос на обогащение дома данными из ЕЖД")
    @GetMapping(value = "/testUpdateRealEstateFromEzd")
    public ResponseEntity<String> testUpdateRealEstateFromEzd(
        @ApiParam(value = "С созданием тестового дома") @RequestParam(value = "createTestEstate", required = false,
            defaultValue = "false") Boolean createTestEstate,
        @ApiParam(value = "Номер квартиры, по умолчанию 23") @RequestParam(value = "flatNumber", required = false,
            defaultValue = "23") String flatNumber) {
        LOG.info("Тестовый запрос на обогащение дома данными из ЕЖД.");
        integrationService.testUpdateRealEstateFromEzd(createTestEstate, flatNumber);
        return ResponseEntity.ok("Тестовый запрос отправлен");
    }

    /**
     * Тестовый запрос на получение СНИЛС.
     *
     * @return busMessageId
     */
    @ApiOperation(value = "Тестовый запрос на получение СНИЛС")
    @GetMapping(value = "/testGetSnilsFromPfr")
    public ResponseEntity<String> testGetSnilsFromPfr() {
        LOG.info("Тестовый запрос на получение СНИЛС.");
        String busMessageId = integrationService.testGetSnilsFromPfr();
        return ResponseEntity.ok(busMessageId);
    }

    /**
     * Тестовый вызов шедулера на обновление квартир и жителей, по которым стоял статус Превышен суточный лимит запросов
     * на документ.
     */
    @ApiOperation(value = "Тестовый вызов шедулера на обновление квартир и жителей, "
        + "по которым стоял статус Превышен суточный лимит запросов на документ")
    @GetMapping(value = "/testUpdateLimitedRequests")
    public void testUpdateLimitedRequests() {
        LOG.info("Тестовый запрос на получение обновление квартир и жителей, "
            + "по которым стоял статус \"Превышен суточный лимит запросов на документ\".");
        schedulerTask.updateLimitedRequests();
    }

    /**
     * Тестовый вызов шедулера на обновление статусов интеграции домов и квартир.
     */
    @ApiOperation(value = "Тестовый вызов шедулера на обновление статусов интеграции домов и квартир")
    @GetMapping(value = "/testUpdateStatuses")
    public void testUpdateStatuses() {
        LOG.info("Тестовый вызов шедулера на обновление статусов интеграции домов и квартир.");
        schedulerTask.updateStatuses();
    }

    /**
     * Тестовый вызов шедулера на обновление кол-ва ssoId.
     */
    @ApiOperation(value = "Тестовый вызов шедулера на обновление кол-ва ssoId")
    @GetMapping(value = "/testUpdateSsoIdCount")
    public void testUpdateSsoIdCount() {
        LOG.info("Тестовый вызов шедулера на обновление кол-ва ssoId.");

        LOG.info("Началась запланированная задача на подсчет кол-ва ssoId");
        schedulerTask.updateSsoIdCount();
        LOG.info("Завершилась запланированная задача на подсчет кол-ва ssoId");
    }

    /**
     * Тестовый вызов шедулера на обновление квартир и жителей, по которым стоял статус Превышен суточный лимит запросов
     * на документ по UNOM дома.
     *
     * @param unom
     *            unom
     */
    @ApiOperation(value = "Тестовый вызов шедулера на обновление квартир и жителей, "
        + "по которым стоял статус Превышен суточный лимит запросов на документ по UNOM дома")
    @GetMapping(value = "/testUpdateLimitedRequestsWithUnom")
    public void testUpdateLimitedRequestsWithUnom(@RequestParam String unom) {
        LOG.info("Тестовый запрос на получение обновление квартир и жителей, "
            + "по которым стоял статус \"Превышен суточный лимит запросов на документ\" по UNOM дома.");
        schedulerTask.updateLimitedRequestsWithUnom(unom);
    }

    /**
     * Тестовый вызов шедулера на обновление статусов интеграции домов и квартир по UNOM дома.
     *
     * @param unom
     *            unom
     */
    @ApiOperation(value = "Тестовый вызов шедулера на обновление статусов интеграции домов и квартир по UNOM дома")
    @GetMapping(value = "/testUpdateStatusesWithUnom")
    public void testUpdateStatusesWithUnom(@RequestParam String unom) {
        LOG.info("Тестовый вызов шедулера на обновление статусов интеграции домов и квартир по UNOM дома.");
        schedulerTask.updateStatusesByUnom(unom);
    }

    /**
     * Получение СНИЛС по Фамильной группе жителя.
     *
     * @param id
     *            Id жителя
     * @return status
     */
    @ApiOperation(value = "Получение СНИЛС по Фамильной группе жителя")
    @GetMapping(value = "/getSnilsFromPfr/{id}")
    public void getSnilsFromPfr(
        @ApiParam(value = "Id жителя", required = true) @PathVariable(value = "id") String id
    ) {
        LOG.info("updateSnilsFromPfr: getSnilsFromPfr controller");
        integrationService.updateSnilsFromPfr(id);
    }

    /**
     * Запрос на парсинг сообщения SMEV.
     *
     * @param message
     *            message
     * @return status
     */
    @ApiOperation(value = "Запрос на парсинг сообщения SMEV")
    @PostMapping(value = "/parseMessageFromSmev")
    public ResponseEntity<String> parseMessageFromSmev(@RequestBody String message) {
        smevResponseParser.processMessageSmev(message);
        return ResponseEntity.ok("ОК");
    }

    /**
     * Обновление статусов интеграции.
     *
     * @return status
     */
    @ApiOperation(value = "Обновление статусов интеграции")
    @GetMapping(value = "/updateIntegrationStatuses")
    public ResponseEntity<String> updateIntegrationStatuses() {
        LOG.info("Обновление статусов интеграции запущено!");
        integrationService.updateIntegrationStatuses();
        LOG.info("Обновление статусов интеграции завершено!");
        return ResponseEntity.ok("Обновление статусов интеграции завершено!");
    }

    /**
     * Получение ssoId по СНИЛС.
     *
     * @param snils
     *            СНИЛС
     * @return ssoId
     */
    @ApiOperation(value = "Получение ssoId по СНИЛС")
    @GetMapping(value = "/getSsoIdBySnils/{snils}")
    public String getSsoIdBySnils(
        @ApiParam(value = "СНИЛС", required = true) @PathVariable(value = "snils") String snils
    ) {
        String ssoId = mdmIntegrationService.getSsoId(snils);
        if (ssoId == null) {
            return "При запросе возникла ошибка.";
        } else if (ssoId.equals("")) {
            return "SsoId по заданному СНИЛС не найден.";
        }
        return ssoId;
    }

    /**
     * Обновить данные по ЛК(SsoId) для всех жителей дома.
     *
     * @param id
     *            Id дома
     * @return status
     */
    @ApiOperation(value = "Обновить данные по ЛК(SsoId) для всех жителей дома")
    @GetMapping(value = "/updatePersonsSsoIdByRealEstateId/{id}")
    public ResponseEntity<String> updatePersonsSsoIdByRealEstateId(
        @ApiParam(value = "Id дома", required = true) @PathVariable(value = "id") String id) {
        LOG.info("Запрос на обновление данных по ЛК для всех жителей дома. Id дома: {}", id);
        integrationService.updatePersonsSsoIdByRealEstateId(id);
        return ResponseEntity.ok("ОК");
    }

    /**
     * Инициация статусов квартир в отселяемом доме.
     *
     * @return status
     */
    @ApiOperation(value = "Инициация статусов квартир в отселяемом доме")
    @GetMapping(value = "/initFlatResettlementStatus")
    public ResponseEntity<String> initFlatResettlementStatus() {
        LOG.info("Запрос на инициацию статусов квартир в отселяемом доме");
        flatResettlementStatusInitService.initFlatResettlementStatus();
        return ResponseEntity.ok("ОК");
    }

    /**
     * Отправка сведений о статусе подписания акта.
     *
     * @param personId ид жителя, подписавшего акт.
     * @param info     SuperServiceDGPSignActStatusType
     * @return status
     */
    @ApiOperation(value = "Отправка сведений о статусе подписания акта")
    @PostMapping("/sendSignedAct")
    public ResponseEntity<Void> sendSignedAct(
        @RequestParam String personId,
        @RequestBody SuperServiceDGPSignActStatusType info
    ) {
        signActStatusFlowService.sendSignedActStatus(personId, info);
        LOG.info("Info signed act was send");
        return new ResponseEntity<>(HttpStatus.OK);
    }


    /**
     * fetchFlatDetails.
     *
     * @param unom unom
     * @return flatDetails
     */
    @ApiOperation(value = "Получить данные по квартирам из каталога 28609")
    @GetMapping(value = "/fetchFlatDetails")
    public ResponseEntity<List<FlatDetails>> fetchFlatDetails(
        @ApiParam("unom")
        @RequestParam(value = "unom", defaultValue = "243234") String unom
    ) {
        List<FlatDetails> flatDetails = fetchFlatDetailsSoapService.getFlatDetailsByUnom(unom);
        return ResponseEntity.ok(flatDetails);
    }

    /**
     * Сервис подписки на изменения каталога 28609.
     *
     * @return status
     */
    @ApiOperation(value = "Сервис подписки на изменения каталога 28609")
    @PostMapping("/flatDetails/callback")
    public ResponseEntity<String> flatDetailsCallback(
        @RequestBody String servicePushJson
    ) {
        flatDetailsCallbackService.storeServicePushJson(servicePushJson);
        return ResponseEntity.ok("ОК");
    }


    /**
     * Тестовый запрос в ЕЛК (письмо с предложением).
     *
     * @return статус
     */
    @ApiOperation(value = "Запустить обработку файлов каталога 28609")
    @PostMapping("/flatDetails/triggerFilesProcessing")
    public ResponseEntity<String> triggerFilesProcessing() {
        flatDetailsCallbackService.processStoredFiles();
        return ResponseEntity.ok("OK");
    }

    //TODO Remove integration prefix
    @GetMapping(value = "/integration/search-dummy-persons")
    public List<DummyPersonDto> searchForDummyPersons(
        @RequestParam("firstFlowErrorAnalyticsDocumentId") final String firstFlowErrorAnalyticsDocumentId
    ) {
        return personsUpdateFlowsService.searchForDummyPersons(firstFlowErrorAnalyticsDocumentId);
    }
}
