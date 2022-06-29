package ru.croc.ugd.ssr.service.contractdigitalsign;

import ru.croc.ugd.ssr.generated.dto.contractdigitalsign.ExternalRestCheckContractDigitalSignDto;

public interface RestExternalContractDigitalSignService {

    ExternalRestCheckContractDigitalSignDto check(final String eno);
}
