package ru.croc.ugd.ssr.service.contractdigitalsign;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.generated.dto.contractdigitalsign.ExternalRestCheckContractDigitalSignDto;

@Service
@AllArgsConstructor
public class DefaultRestExternalContractDigitalSignService implements RestExternalContractDigitalSignService {

    private final ContractDigitalSignCheckService contractDigitalSignCheckService;

    @Override
    public ExternalRestCheckContractDigitalSignDto check(final String eno) {
        return contractDigitalSignCheckService.getCheckResult(eno);
    }
}
