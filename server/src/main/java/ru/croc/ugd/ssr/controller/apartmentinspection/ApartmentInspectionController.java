package ru.croc.ugd.ssr.controller.apartmentinspection;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.dto.apartmentdefectconfirmation.RestDefectDto;
import ru.croc.ugd.ssr.dto.apartmentinspection.AcceptedDefectsActDto;
import ru.croc.ugd.ssr.dto.apartmentinspection.ApartmentInspectionDto;
import ru.croc.ugd.ssr.dto.apartmentinspection.CloseActWithoutConsentDto;
import ru.croc.ugd.ssr.dto.apartmentinspection.DelayReasonDto;
import ru.croc.ugd.ssr.dto.apartmentinspection.IntegrationLogDto;
import ru.croc.ugd.ssr.dto.apartmentinspection.PersonConsentDto;
import ru.croc.ugd.ssr.dto.apartmentinspection.RestApartmentInspectionDto;
import ru.croc.ugd.ssr.dto.apartmentinspection.RestApartmentInspectionReportDto;
import ru.croc.ugd.ssr.dto.apartmentinspection.RestCloseActsWithoutConsentDto;
import ru.croc.ugd.ssr.dto.apartmentinspection.RestConfirmClosingWithoutConsentDto;
import ru.croc.ugd.ssr.dto.apartmentinspection.RestDefectsEliminationDto;
import ru.croc.ugd.ssr.dto.apartmentinspection.RestFileDto;
import ru.croc.ugd.ssr.dto.apartmentinspection.RestSendTasksToGeneralContractorDto;
import ru.croc.ugd.ssr.dto.apartmentinspection.RestUpdateDefectDto;
import ru.croc.ugd.ssr.generated.api.ApartmentInspectionApi;
import ru.croc.ugd.ssr.generated.dto.RestCreateApartmentInspectionActResponseDto;
import ru.croc.ugd.ssr.model.ApartmentInspectionDocument;
import ru.croc.ugd.ssr.service.ApartmentInspectionService;
import ru.croc.ugd.ssr.task.FillInApartmentInspectionMissingDataTask;
import ru.reinform.cdp.utils.mapper.JsonMapper;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Контроллер для устранения дефектов в квартире.
 */
@RestController
@AllArgsConstructor
@Slf4j
public class ApartmentInspectionController implements ApartmentInspectionApi {

    private final ApartmentInspectionService apartmentInspectionService;
    private final JsonMapper jsonMapper;
    private final FillInApartmentInspectionMissingDataTask fillInApartmentInspectionMissingDataTask;

    @InitBinder
    public void initBinder(final WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    //TODO replace with generic create document service that starts processes.
    @Override
    public ResponseEntity<RestCreateApartmentInspectionActResponseDto> createApartmentInspectionActAndStartProcess(
        @RequestBody @Valid String apartmentInspectionActDocument) {
        final ApartmentInspectionDocument apartmentInspectionDocument = jsonMapper.readObject(
            apartmentInspectionActDocument,
            ApartmentInspectionDocument.class);
        final String startedProcessId =
            apartmentInspectionService.createDocumentAndStartDefectEliminationProcess(
                apartmentInspectionDocument);
        final RestCreateApartmentInspectionActResponseDto
            restCreateApartmentInspectionActResponseDto =
            new RestCreateApartmentInspectionActResponseDto();
        restCreateApartmentInspectionActResponseDto.setId(startedProcessId);
        return new ResponseEntity<>(restCreateApartmentInspectionActResponseDto, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> findApartmentInspectionActByPersonId(
        @PathVariable("personId") String personId) {
        final ApartmentInspectionDocument apartmentInspectionDocument =
            apartmentInspectionService.findDocumentWithStartedProcessByPersonId(personId);
        if (apartmentInspectionDocument == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        final String stringifiedJson = jsonMapper.writeObject(apartmentInspectionDocument);
        return new ResponseEntity<>(stringifiedJson, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> safeDeleteActiveActByActId(@PathVariable("actId") String actId) {
        apartmentInspectionService.safeDeleteActByActId(actId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> forceFinishProcessOfApartmentInspectionDocument(
        @PathVariable("apartmentInspectionId") String apartmentInspectionId,
        @RequestParam(
            value = "sendDefectsFixedIfNeeded",
            required = false,
            defaultValue = "true"
        ) Boolean sendDefectsFixedIfNeeded
    ) {
        apartmentInspectionService.forceFinishProcess(apartmentInspectionId, sendDefectsFixedIfNeeded);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> actualizeTaskCandidates(@NotNull @Valid String documentId) {
        apartmentInspectionService.actualizeTasksCandidates(documentId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Получить все акты по дефектам.
     *
     * @param personId               personId
     * @param commissionInspectionId commissionInspectionId
     * @return apartment inspection defects
     */
    @GetMapping(value = "/apartment-inspection")
    public List<ApartmentInspectionDto> findAll(
        @RequestParam(value = "personId", required = false) final String personId,
        @RequestParam(value = "commissionInspectionId", required = false) final String commissionInspectionId
    ) {
        return apartmentInspectionService.findAll(personId, commissionInspectionId);
    }

    /**
     * Уведомить об устраненых дефектах.
     */
    @PostMapping(value = "/apartment-inspection/{id}/defects-fixed")
    public void defectsFixed(@PathVariable("id") final String apartmentInspectionId) {
        apartmentInspectionService.defectsFixed(apartmentInspectionId);
    }

    /**
     * Закрыть акт по дефектам.
     *
     * @param apartmentInspectionId apartmentInspectionId
     * @param personConsentDto      personConsentDto
     */
    @PostMapping(value = "/apartment-inspection/{id}/close")
    public void close(
        @PathVariable("id") final String apartmentInspectionId,
        @RequestBody final PersonConsentDto personConsentDto
    ) {
        apartmentInspectionService.close(apartmentInspectionId, personConsentDto);
    }

    /**
     * Внести согласие жителя с устранением дефектов.
     *
     * @param apartmentInspectionId apartmentInspectionId
     * @param personConsentDto      personConsentDto
     */
    @PostMapping(value = "/apartment-inspection/{id}/person-consent")
    public void addPersonConsent(
        @PathVariable("id") final String apartmentInspectionId, @RequestBody final PersonConsentDto personConsentDto
    ) {
        apartmentInspectionService.addPersonConsent(apartmentInspectionId, personConsentDto);
    }

    /**
     * Заполнить акт недостоющими данными.
     */
    @GetMapping(value = "/apartmentinspection/fillData")
    public void fillInApartmentInspectionsWithMissingData() {
        fillInApartmentInspectionMissingDataTask.runTask();
    }

    //TODO Konstantin: remove temporary method
    @DeleteMapping(value = "/apartment-inspection")
    public void deleteAll() {
        apartmentInspectionService.deleteAll();
    }

    /**
     * Обновить акт принятых устранений дефектов.
     *
     * @param apartmentInspectionId ИД документа акта осмотра квартиры
     * @param acceptedDefectsActDto акт принятых устранений дефектов
     * @return акт осмотра квартиры
     */
    @PutMapping(value = "/apartment-inspection/{id}/accepted-defects-act")
    public ApartmentInspectionDto updateAcceptedDefectsAct(
        @PathVariable("id") final String apartmentInspectionId,
        @RequestBody final AcceptedDefectsActDto acceptedDefectsActDto
    ) {
        return apartmentInspectionService.updateAcceptedDefectsAct(apartmentInspectionId, acceptedDefectsActDto);
    }

    /**
     * Закрыть акт по дефектам без получения согласия жителя.
     *
     * @param apartmentInspectionId     apartmentInspectionId
     * @param closeActWithoutConsentDto closeActWithoutConsentDto
     */
    @PostMapping(value = "/apartment-inspection/{id}/close-without-consent")
    public void closeWithoutConsent(
        @PathVariable("id") final String apartmentInspectionId,
        @RequestBody final CloseActWithoutConsentDto closeActWithoutConsentDto
    ) {
        apartmentInspectionService.closeWithoutConsent(apartmentInspectionId, closeActWithoutConsentDto);
    }

    /**
     * Закрыть акты по дефектам без получения согласия жителя.
     *
     * @param restCloseActsWithoutConsentDto restCloseActsWithoutConsentDto
     */
    @PostMapping(value = "/apartment-inspections/close-without-consent")
    public void closeWithoutConsent(@RequestBody final RestCloseActsWithoutConsentDto restCloseActsWithoutConsentDto) {
        apartmentInspectionService.closeWithoutConsent(restCloseActsWithoutConsentDto);
    }

    /**
     * Подтвердить закрытие акта по дефектам без получения согласия жителя.
     *
     * @param apartmentInspectionId               apartmentInspectionId
     * @param restConfirmClosingWithoutConsentDto restConfirmClosingWithoutConsentDto
     */
    @PostMapping(value = "/apartment-inspection/{id}/confirm-closing-without-consent")
    public void confirmClosingWithoutConsent(
        @PathVariable("id") final String apartmentInspectionId,
        @RequestBody final RestConfirmClosingWithoutConsentDto restConfirmClosingWithoutConsentDto
    ) {
        apartmentInspectionService.confirmClosingWithoutConsent(
            apartmentInspectionId, restConfirmClosingWithoutConsentDto
        );
    }

    /**
     * Продление срока устранения дефектов.
     *
     * @param apartmentInspectionId apartmentInspectionId
     * @param delayReasonDto        Данные о сроке и причине продления
     */
    @PostMapping(value = "/apartment-inspection/{id}/defects-prolongation")
    public void defectsProlongation(
        @PathVariable("id") final String apartmentInspectionId,
        @RequestBody final DelayReasonDto delayReasonDto
    ) {
        apartmentInspectionService.defectsProlongation(apartmentInspectionId, delayReasonDto);
    }

    /**
     * Продление срока устранения дефектов и уведомление об устраненных дефектах.
     *
     * @param apartmentInspectionId apartmentInspectionId
     * @param delayReasonDto        Данные о сроке и причине продления
     */
    @PostMapping(value = "/apartment-inspection/{id}/defects-elimination")
    public void defectsElimination(
        @PathVariable("id") final String apartmentInspectionId,
        @RequestBody final DelayReasonDto delayReasonDto
    ) {
        apartmentInspectionService.defectsElimination(apartmentInspectionId, delayReasonDto);
    }

    /**
     * Установка планового срока устранения дефектов по списку актов.
     *
     * @param restDefectsEliminationDto Данные о плановом сроке устранения дефектов по актам
     */
    @PostMapping(value = "/apartment-inspections/defects-elimination")
    public void defectsElimination(@RequestBody final RestDefectsEliminationDto restDefectsEliminationDto) {
        apartmentInspectionService.defectsElimination(restDefectsEliminationDto);
    }

    /**
     * Назначение задач на генподрядчика.
     *
     * @param restSendTasksToGeneralContractorDto Данные о генподрядчике по актам
     */
    @PostMapping(value = "/apartment-inspections/send-tasks-to-general-contractor")
    public void sendTasksToGeneralContractor(
        @RequestBody final RestSendTasksToGeneralContractorDto restSendTasksToGeneralContractorDto
    ) {
        apartmentInspectionService.sendTasksToGeneralContractor(restSendTasksToGeneralContractorDto);
    }

    /**
     * Назначение задачи на генподрядчика.
     *
     * @param apartmentInspectionId ИД документа акта осмотра квартиры
     * @param generalContractorId   ИД генерального подрядчика
     */
    @PostMapping(value = "/apartment-inspections/{id}/send-task-to-general-contractor")
    public void sendTaskToGeneralContractor(
        @PathVariable("id") final String apartmentInspectionId,
        @RequestParam("generalContractorId") final String generalContractorId
    ) {
        apartmentInspectionService.sendTaskToGeneralContractor(apartmentInspectionId, generalContractorId);
    }

    /**
     * Актуализировать список кандидатов активных задач по всем актам.
     */
    @PostMapping(value = "/apartment-inspections/actualize-task-candidates")
    public void actualizeTaskCandidatesForAllApartmentInspections() {
        apartmentInspectionService.actualizeTaskCandidates();
    }

    /**
     * Получить список логов межведомственной интеграции.
     *
     * @param apartmentInspectionId ИД документа акта осмотра квартиры
     * @return логи межведомственной интеграции
     */
    @GetMapping(value = "/apartment-inspection/{id}/integration-logs")
    public List<IntegrationLogDto> fetchIntegrationLogs(
        @PathVariable("id") final String apartmentInspectionId
    ) {
        return apartmentInspectionService.fetchIntegrationLogs(apartmentInspectionId);
    }

    /**
     * Обновить список дефектов.
     *
     * @param apartmentInspectionId ИД документа акта осмотра квартиры
     * @param restUpdateDefectDto   данные о дефектах
     * @return акт осмотра квартиры
     */
    @PutMapping(value = "/apartment-inspection/{id}/defects")
    public ApartmentInspectionDto updateDefects(
        @PathVariable("id") final String apartmentInspectionId,
        @RequestBody final RestUpdateDefectDto restUpdateDefectDto
    ) {
        return apartmentInspectionService.updateDefects(apartmentInspectionId, restUpdateDefectDto);
    }

    /**
     * Добавить ИД дефектов.
     */
    @PostMapping(value = "/apartment-inspections/actualize-defect-ids")
    public void actualizeDefectIds() {
        apartmentInspectionService.actualizeDefectIds();
    }

    /**
     * Получить данные о дефектах.
     *
     * @param unom         UNOM заселяемого дома
     * @param pageNum      номер страницы
     * @param pageSize     размер страницы
     * @param flat         фильтр - квартира
     * @param flatElement  фильтр - вид помещения
     * @param description  фильтр - вид дефекта
     * @param isEliminated фильтр - устранен ли дефект
     * @param isDeveloper  данные запрашивает застройщик или генподрядчик
     * @param sort         строка сортировки
     * @return данные о дефектах
     */
    @GetMapping(value = "/apartment-inspection/defects")
    public Page<RestDefectDto> fetchDefects(
        @RequestParam("unom") final String unom,
        @RequestParam(value = "pageNum", required = false, defaultValue = "0") final int pageNum,
        @RequestParam(value = "pageSize", required = false, defaultValue = "50") final int pageSize,
        @RequestParam(value = "flat", required = false) final String flat,
        @RequestParam(value = "flatElement", required = false) final String flatElement,
        @RequestParam(value = "description", required = false) final String description,
        @RequestParam(value = "isEliminated", required = false) final Boolean isEliminated,
        @RequestParam(value = "isDeveloper", required = false, defaultValue = "false") final boolean isDeveloper,
        @RequestParam(value = "sort", required = false) final String sort
    ) {
        return apartmentInspectionService.fetchDefects(
            pageNum, pageSize, unom, flat, flatElement, description, isEliminated, isDeveloper, sort
        );
    }

    /**
     * Получить акт по дефектам.
     *
     * @param apartmentInspectionId ИД документа акта осмотра квартиры
     * @return aкт по дефектам
     */
    @GetMapping(value = "/apartment-inspection/{id}")
    public ApartmentInspectionDto fetchApartmentInspectionById(@PathVariable("id") final String apartmentInspectionId) {
        return apartmentInspectionService.fetchApartmentInspectionById(apartmentInspectionId);
    }

    /**
     * Получить список актов по дефектам по unom и flatNumber.
     *
     * @param unom       уном
     * @param flatNumber номер квартиры
     * @return список RestApartmentInspectionDto
     */
    @GetMapping(value = "/apartment-inspections")
    public List<RestApartmentInspectionDto> fetchAllByUnomAndFlatNumber(
        @RequestParam("unom") final String unom, @RequestParam("flatNumber") final String flatNumber
    ) {
        return apartmentInspectionService.fetchAllByUnomAndFlatNumber(unom, flatNumber);
    }

    /**
     * Скачать все акты в zip.
     *
     * @param unom       уном
     * @param flat       номер квартиры
     * @param actNum     номер акта
     * @param filingDate дата регистрации акта
     * @return файл zip
     */
    @GetMapping(value = "/apartment-inspections/zip-report")
    public ResponseEntity<byte[]> getZipReport(
        @RequestParam(value = "unom") final String unom,
        @RequestParam(value = "flat", required = false) final String flat,
        @RequestParam(value = "actNum", required = false) final String actNum,
        @RequestParam(value = "filingDate", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate filingDate
    ) {
        final RestFileDto fileDto = apartmentInspectionService.getZipReport(unom, flat, actNum, filingDate);

        final HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/zip");
        headers.setContentDisposition(
            ContentDisposition.builder("attachment")
                .filename(fileDto.getFileName(), StandardCharsets.UTF_8)
                .build()
        );
        return new ResponseEntity<>(fileDto.getContent(), headers, HttpStatus.OK);
    }

    /**
     * Выгрузить список дефектов по дому в xlsx.
     *
     * @param unom                      уном
     * @param flat                      номер квартиры
     * @param actNum                    номер акта
     * @param filingDate                дата регистрации акта
     * @param flatElement               помещение
     * @param description               описание дефекта
     * @param isEliminated              отметка об устранении
     * @param skipNotPlannedElimination возвращать только дефекты, по которым запланировано устранение
     * @return файл zip
     */
    @GetMapping(value = "/apartment-inspections/excel-report")
    public ResponseEntity<byte[]> getExcelReport(
        @RequestParam(value = "unom") final String unom,
        @RequestParam(value = "flat", required = false) final String flat,
        @RequestParam(value = "actNum", required = false) final String actNum,
        @RequestParam(value = "filingDate", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate filingDate,
        @RequestParam(value = "flatElement", required = false) final String flatElement,
        @RequestParam(value = "description", required = false) final String description,
        @RequestParam(value = "isEliminated", required = false) final Boolean isEliminated,
        @RequestParam(value = "skipNotPlannedElimination", required = false, defaultValue = "false")
        final boolean skipNotPlannedElimination
    ) {
        final RestFileDto fileDto = apartmentInspectionService.getExcelReport(
            unom, flat, actNum, filingDate, flatElement, description, isEliminated, skipNotPlannedElimination
        );

        final HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/vnd.ms-excel");
        headers.setContentDisposition(
            ContentDisposition.builder("attachment")
                .filename(fileDto.getFileName(), StandardCharsets.UTF_8)
                .build()
        );
        return new ResponseEntity<>(fileDto.getContent(), headers, HttpStatus.OK);
    }

    /**
     * Скачать pdf акта по дефектам.
     *
     * @param id ИД акта по дефектам
     * @return pdf-файл акта по дефектам
     */
    @GetMapping(value = "/apartment-inspections/{id}/report")
    public ResponseEntity<byte[]> getReport(@PathVariable final String id) {
        final RestFileDto fileDto = apartmentInspectionService.getReport(id);
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(
            ContentDisposition.builder("attachment")
                .filename(fileDto.getFileName(), StandardCharsets.UTF_8)
                .build()
        );
        return new ResponseEntity<>(fileDto.getContent(), headers, HttpStatus.OK);
    }

    /**
     * Создать pdf акта по дефектам.
     *
     * @param restApartmentInspectionReportDto данные для формирования pdf
     * @return pdf-файл акта по дефектам
     */
    @PostMapping(value = "/apartment-inspections/report")
    public ResponseEntity<byte[]> createReport(
        @RequestBody final RestApartmentInspectionReportDto restApartmentInspectionReportDto
    ) {
        final RestFileDto fileDto = apartmentInspectionService.createReport(restApartmentInspectionReportDto);
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(
            ContentDisposition.builder("attachment")
                .filename(fileDto.getFileName(), StandardCharsets.UTF_8)
                .build()
        );
        return new ResponseEntity<>(fileDto.getContent(), headers, HttpStatus.OK);
    }
}
