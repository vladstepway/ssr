package ru.croc.ugd.ssr.controller.notarycompensation;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.generated.api.NotaryCompensationApi;
import ru.croc.ugd.ssr.generated.dto.notarycompensation.ExternalRestCheckNotaryCompensationDto;
import ru.croc.ugd.ssr.service.notarycompensation.RestExternalNotaryCompensationService;

@RestController
@AllArgsConstructor
public class ExternalNotaryCompensationController implements NotaryCompensationApi {

    private final RestExternalNotaryCompensationService externalNotaryCompensationService;

    @Override
    public ResponseEntity<ExternalRestCheckNotaryCompensationDto> check(final String snils, final String ssoId) {
        return ResponseEntity.ok(externalNotaryCompensationService.check(snils, ssoId));
    }
}
