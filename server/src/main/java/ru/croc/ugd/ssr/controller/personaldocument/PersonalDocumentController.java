package ru.croc.ugd.ssr.controller.personaldocument;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.dto.personaldocument.RestAddApplicationDocumentDto;
import ru.croc.ugd.ssr.dto.personaldocument.RestCreatePersonalDocumentDto;
import ru.croc.ugd.ssr.dto.personaldocument.RestMergeFilesDto;
import ru.croc.ugd.ssr.dto.personaldocument.RestParsedDocumentDto;
import ru.croc.ugd.ssr.dto.personaldocument.RestPersonDataDocumentsDto;
import ru.croc.ugd.ssr.dto.personaldocument.RestPersonalDocumentDto;
import ru.croc.ugd.ssr.service.personaldocument.RestPersonalDocumentService;

import java.util.List;

/**
 * Контроллер для работы со сведениями о документах.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/personal-documents")
public class PersonalDocumentController {

    private final RestPersonalDocumentService restPersonalDocumentService;

    /**
     * Получить сведения о документах.
     *
     * @param id ИД документа сведения о документах
     * @return сведения о документах
     */
    @GetMapping(value = "/{id}")
    public RestPersonalDocumentDto fetchById(@PathVariable("id") final String id) {
        return restPersonalDocumentService.fetchById(id);
    }

    /**
     * Получить документы по жителю.
     *
     * @param personDocumentId ИД документа жителя
     * @return документы
     */
    @GetMapping
    public RestPersonDataDocumentsDto fetchAll(@RequestParam("personDocumentId") final String personDocumentId) {
        return restPersonalDocumentService.fetchAll(personDocumentId);
    }

    /**
     * Создать сведения о документах.
     *
     * @param body тело запроса
     * @return сведения о документах
     */
    @PostMapping
    public RestPersonalDocumentDto create(@RequestBody final RestCreatePersonalDocumentDto body) {
        return restPersonalDocumentService.create(body);
    }

    /**
     * Разобрать документы по типам.
     *
     * @param id ИД документа сведений о документах
     * @param restParsedDocumentDtos документы, выделенные из единого файла документов.
     */
    @PostMapping(value = "/{id}/parse")
    public void parse(
        @PathVariable("id") final String id, @RequestBody final List<RestParsedDocumentDto> restParsedDocumentDtos
    ) {
        restPersonalDocumentService.parse(id, restParsedDocumentDtos);
    }

    /**
     * Объединить файлы документов в единый файл.
     *
     * @param body тело запроса
     * @return единый файл
     */
    @PostMapping("/files/merge")
    public ResponseEntity<byte[]> merge(@RequestBody final RestMergeFilesDto body) {
        final byte[] unionFileContent = restPersonalDocumentService.merge(body);
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        return new ResponseEntity<>(unionFileContent, headers, HttpStatus.OK);
    }

    /**
     * Добавить в сведения о документах документ заявления.
     *
     * @param body тело запроса
     * @return сведения о документах
     */
    @PostMapping("/add-application-document")
    public RestPersonalDocumentDto addApplicationDocument(@RequestBody final RestAddApplicationDocumentDto body) {
        return restPersonalDocumentService.addApplicationDocument(body);
    }
}