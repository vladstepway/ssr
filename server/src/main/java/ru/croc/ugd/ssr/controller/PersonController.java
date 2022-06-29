package ru.croc.ugd.ssr.controller;

import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import ru.croc.ugd.ssr.dto.RestNotResettledPersonDto;
import ru.croc.ugd.ssr.dto.person.RestOfferLetterDto;
import ru.croc.ugd.ssr.dto.personupload.PersonUploadDto;
import ru.croc.ugd.ssr.integration.service.flows.OfferLettersFlowsService;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.PersonUploadService;
import ru.croc.ugd.ssr.service.UserService;
import ru.croc.ugd.ssr.service.excel.person.PersonLetterAndContractService;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

/**
 * Контроллер по работе с жителми.
 */
@RestController
@RequestMapping("/person")
@RequiredArgsConstructor
public class PersonController {

    private final PersonDocumentService personDocumentService;
    private final PersonUploadService personUploadService;
    private final UserService userService;
    private final OfferLettersFlowsService offerLettersFlowsService;
    private final PersonLetterAndContractService personLetterAndContractService;

    /**
     * Получение всех жителей по UNOM-ам домов.
     *
     * @param unoms UNOM-ы домов
     * @return список жителей
     */
    @ApiOperation(value = "Получение всех жителей по UNOM-ам домов")
    @PostMapping(value = "/fetchByUnoms")
    public List<PersonDocument> fetchByCcoId(
        @ApiParam(value = "UNOM-ы домов") @RequestBody List<String> unoms
    ) {
        return personDocumentService.fetchByUnoms(unoms);
    }

    /**
     * Получить жителей с похожими атрибутами ЕВД.
     * Сравнивает ФИО + ДР, СНИЛС или personID.
     *
     * @param id жителя, с кем сравнивают
     * @return список похожих PersonDocument
     */
    @ApiOperation(value = "Получение жителей с похожими атрибутами ЕВД")
    @GetMapping(value = "/fetchSimilarPersons")
    public List<PersonDocument> fetchSimilarPersons(
        @ApiParam(value = "id жителя") @RequestParam String id
    ) {
        return personDocumentService.fetchSimilarPersons(id);
    }

    /**
     * Загрузка жителей по id файла.
     * @param personUploadDto personUploadDto
     */
    @PostMapping(value = "/upload/file")
    public void savePersonsByFileId(@RequestBody PersonUploadDto personUploadDto) {
        final List<Document> documents = personUploadService.retrieveDocumentsByFileId(
            personUploadDto.getFileId(), personUploadDto.getPassword()
        );
        final String userNameOrLogin = userService.getUserFullNameOrLogin();
        personUploadService.processPersonsFromDocuments(
            personUploadDto.getFileId(),
            personUploadDto.getFileName(),
            userNameOrLogin,
            documents);
    }

    /**
     * Загрузка жителей из архива.
     *
     * @param file zip
     * @param password пароль
     */
    @PostMapping(value = "/upload/zip")
    public void savePersonsFromZip(@RequestParam("file") final MultipartFile file,
                                   @RequestParam("password") final String password) {
        final List<Document> documents = personUploadService.retrieveDocumentsFromZip(file, password);
        final String userNameOrLogin = userService.getUserFullNameOrLogin();
        personUploadService.processPersonsFromDocuments(
            null,
            file.getOriginalFilename(),
            userNameOrLogin,
            documents);
    }

    /**
     * Загрузка жителей из xml.
     *
     * @param file zip
     */
    @PostMapping(value = "/upload/xml")
    public void savePersonsFromXml(@RequestParam("file") final MultipartFile file) {
        final Document document = personUploadService.retrieveDocumentFromXml(file);
        final String userNameOrLogin = userService.getUserFullNameOrLogin();
        personUploadService.processPersonsFromDocuments(
            null,
            file.getOriginalFilename(),
            userNameOrLogin,
            Collections.singletonList(document));
    }

    /**
     * Удаление загруженных жителей.
     */
    @DeleteMapping(value = "/uploaded")
    public void removeUploadedPersons() {
        personUploadService.deleteUploadedPerson();
    }

    /**
     * Проверка существования члена семьи с личным кабинетом на mos.ru
     *
     * @param personDocumentId ИД документа жителя
     * @return результат проверки
     */
    @GetMapping(value = "/{personDocumentId}/affair/with-ssoid")
    public boolean existsFamilyMemberWithSsoId(
        @PathVariable("personDocumentId") final String personDocumentId
    ) {
        return personDocumentService.existsFamilyMemberWithSsoId(personDocumentId);
    }

    /**
     * Запускает процесс извлечения данных по квартирам из писем по всем жителям.
     */
    @PostMapping(value = "/extract-offer-letter-flat-data")
    public void extractFlatDataFromOfferLetterForAll() {
        offerLettersFlowsService.extractFlatDataFromOfferLetterForAll();
    }

    /**
     * Запускает процесс объединения информации по договорам с совпадающими orderId.
     */
    @PostMapping(value = "/merge-contract-data")
    public void mergeContractData() {
        personUploadService.mergeContractData();
    }

    /**
     * Удаление документов жителей посредством их архивирования.
     *
     * @param ids список ИД документов жителей
     * @return список измененных документов жителей
     */
    @DeleteMapping(value = "/delete")
    public List<PersonDocument> deletePersonDocumentsByIds(@RequestBody List<String> ids) {
        return personDocumentService.deletePersonDocumentsByIds(ids);
    }

    /**
     * Получение файла письма с предложением.
     * @param personDocumentId ИД документа жителя
     * @param letterId ИД письма с предложением
     * @return файл письма
     */
    @PreAuthorize("hasAuthority(@authorizationConfig.getPersonGroup())")
    @GetMapping(value = "/{personDocumentId}/offer-letters/{letterId}/pdf-file")
    public ResponseEntity<byte[]> fetchOfferLetterPdfFile(
        @PathVariable final String personDocumentId,
        @PathVariable final String letterId
    ) {
        final Pair<String, byte[]> offerLetterPdfFile = personDocumentService.fetchOfferLetterPdfFile(
            personDocumentId, letterId
        );
        final byte[] offerLetterPdfFileContent = ofNullable(offerLetterPdfFile)
            .map(Pair::getRight)
            .orElse(null);
        final String offerLetterPdfFileName = ofNullable(offerLetterPdfFile)
            .map(Pair::getLeft)
            .orElse(null);

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        if (nonNull(offerLetterPdfFileName)) {
            headers.setContentDisposition(
                ContentDisposition.builder("attachment")
                    .filename(offerLetterPdfFileName, StandardCharsets.UTF_8)
                    .build()
            );
        }

        return new ResponseEntity<>(offerLetterPdfFileContent, headers, HttpStatus.OK);
    }

    /**
     * Загрузка данных о письмах с предложениями.
     */
    @PostMapping(value = "/upload-offer-letter")
    public void uploadOfferLetter(@RequestParam("file") final MultipartFile multipartFile) {
        personLetterAndContractService.uploadOfferLetter(multipartFile);
    }

    /**
     * Получить жителей с незавершенным процессом переселения по УНОМ дома.
     * @param unom УНОМ дома
     * @return данные жителей
     */
    @GetMapping(value = "/not-resettled")
    public List<RestNotResettledPersonDto> fetchNotResettledPersonsByUnom(@RequestParam("unom") final String unom) {
        return personDocumentService.fetchNotResettledPersonsByUnom(unom);
    }

    /**
     * Получить жильцов квартиры по unom and flatNumber.
     *
     * @param unom unom дома
     * @param flatNumber номер квартиры
     * @return список жителей
     */
    @GetMapping(value = "/flat-livers")
    public List<PersonDocument> fetchLiversByUnomAndFlatNumber(
        @RequestParam(value = "unom") final String unom,
        @RequestParam(value = "flatNumber") final String flatNumber
    ) {
        return personDocumentService.fetchLiversByUnomAndFlatNumber(unom, flatNumber);
    }

    /**
     * Изменить доступность подписания договора с помощью УКЭП.
     * @param isEnabled доступно подписание договора с помощью УКЭП
     * @param affairIds список семей
     */
    @PostMapping(value = "/set-electronic-sign-enabled")
    public void setElectronicSignEnabled(
        @RequestParam(value = "isEnabled", required = false, defaultValue = "true") final boolean isEnabled,
        @RequestBody final List<String> affairIds
    ) {
        personDocumentService.setElectronicSignEnabled(isEnabled, affairIds);
    }

    @GetMapping(value = "/offer-letters")
    public List<RestOfferLetterDto> fetchOfferLettersByUnomAndFlatNumber(
        @RequestParam("unom") final String unom,
        @RequestParam("flatNumber") final String flatNumber,
        @RequestParam("cadastralNumber") final String cadastralNumber
    ) {
        return personDocumentService.fetchOfferLettersByUnomAndFlatNumber(unom, flatNumber, cadastralNumber);
    }
}
