package ru.croc.ugd.ssr.controller.notary;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.service.notary.RestNotaryApplicationService;

@RestController
@AllArgsConstructor
public class NotaryApplicationIcsController {

    private final RestNotaryApplicationService restNotaryApplicationService;

    /**
     * Получить содержимое ics файла.
     *
     * @param id ИД документа
     * @return содержимое ics файла
     */
    @GetMapping(value = "/notary-application/{id}/ics")
    public ResponseEntity<byte[]> getIcsFileContent(@PathVariable("id") final String id) {
        final byte[] icsFileContent = restNotaryApplicationService.getIcsFileContent(id);
        final HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "text/calendar;charset=UTF-8");
        return new ResponseEntity<>(icsFileContent, headers, HttpStatus.OK);
    }
}
