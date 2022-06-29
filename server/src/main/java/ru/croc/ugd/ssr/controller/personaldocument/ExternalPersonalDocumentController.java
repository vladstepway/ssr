package ru.croc.ugd.ssr.controller.personaldocument;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.generated.api.PersonalDocumentApi;
import ru.croc.ugd.ssr.generated.dto.personaldocument.ExternalRestCheckPersonalDocumentDto;
import ru.croc.ugd.ssr.service.personaldocument.RestExternalPersonalDocumentService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@AllArgsConstructor
public class ExternalPersonalDocumentController implements PersonalDocumentApi {

    private final RestExternalPersonalDocumentService externalPersonalDocumentService;

    @Override
    public ResponseEntity<ExternalRestCheckPersonalDocumentDto> checkDocuments(
        @NotNull @Valid final String snils,
        @NotNull @Valid final String ssoId
    ) {
        return ResponseEntity.ok(externalPersonalDocumentService.check(snils, ssoId));
    }
}
