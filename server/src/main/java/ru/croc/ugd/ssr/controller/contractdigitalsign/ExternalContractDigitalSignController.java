package ru.croc.ugd.ssr.controller.contractdigitalsign;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.generated.api.ContractDigitalSignApi;
import ru.croc.ugd.ssr.generated.dto.contractdigitalsign.ExternalRestCheckContractDigitalSignDto;
import ru.croc.ugd.ssr.service.contractdigitalsign.RestExternalContractDigitalSignService;

@RestController
@AllArgsConstructor
public class ExternalContractDigitalSignController implements ContractDigitalSignApi {

    private final RestExternalContractDigitalSignService externalContractDigitalSignService;

    @Override
    public ResponseEntity<ExternalRestCheckContractDigitalSignDto> checkPerson(final String eno) {
        return ResponseEntity.ok(externalContractDigitalSignService.check(eno));
    }
}
