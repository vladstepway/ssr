package ru.croc.ugd.ssr.controller.egrn;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.service.RealEstateDocumentService;
import ru.croc.ugd.ssr.service.document.EgrnFlatRequestDocumentService;

@RestController
@AllArgsConstructor
@RequestMapping("/test-egrn-flat-requests")
public class TestEgrnFlatRequestController {

    private final EgrnFlatRequestDocumentService egrnFlatRequestDocumentService;
    private final RealEstateDocumentService realEstateDocumentService;

    @PostMapping("/update-by-egrn-response")
    public void updateByEgrnResponse(
        @RequestParam final String id
    ) {
        realEstateDocumentService.updateByEgrnResponse(egrnFlatRequestDocumentService.fetchDocument(id));
    }
}
