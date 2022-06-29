package ru.croc.ugd.ssr.service.notarycompensation;

import ru.croc.ugd.ssr.generated.dto.notarycompensation.ExternalRestCheckNotaryCompensationDto;

public interface RestExternalNotaryCompensationService {

    ExternalRestCheckNotaryCompensationDto check(final String snils, final String ssoId);
}
