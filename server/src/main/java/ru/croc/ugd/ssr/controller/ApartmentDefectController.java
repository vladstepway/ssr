package ru.croc.ugd.ssr.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.generated.api.ApartmentDefectApi;
import ru.croc.ugd.ssr.model.ApartmentDefectDocument;
import ru.croc.ugd.ssr.service.ApartmentDefectService;
import ru.reinform.cdp.utils.mapper.JsonMapper;

import java.util.Arrays;
import java.util.List;
import javax.validation.Valid;

/**
 * Контроллер для устранения дефектов в квартире.
 */
@RestController
@AllArgsConstructor
@Slf4j
public class ApartmentDefectController implements ApartmentDefectApi {

    private final ApartmentDefectService apartmentDefectService;

    private final JsonMapper jsonMapper;

    @Override
    public ResponseEntity<String> createListOfApartmentDefectAct(
        @RequestBody @Valid String apartmentDefectDocumentString) {
        final ApartmentDefectDocument[] apartmentDefectDocumentList = jsonMapper.readObject(
            apartmentDefectDocumentString,
            ApartmentDefectDocument[].class);
        List<ApartmentDefectDocument> createdDocuments =
            apartmentDefectService.createDocuments(Arrays.asList(apartmentDefectDocumentList));
        final String stringifiedJson = jsonMapper.writeObject(createdDocuments);
        return new ResponseEntity<>(stringifiedJson, HttpStatus.OK);
    }

    /**
     * Executes defect type initialization.
     */
    @GetMapping("apartmentdefect/initDefectTypes")
    public void initDefectType() {
        apartmentDefectService.initDefectTypes();
    }
}
