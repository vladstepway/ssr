package ru.croc.ugd.ssr.service.notarycompensation;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.generated.dto.notarycompensation.ExternalRestCheckNotaryCompensationDto;

@Service
@AllArgsConstructor
public class DefaultRestExternalNotaryCompensationService implements RestExternalNotaryCompensationService {

    private final NotaryCompensationCheckService notaryCompensationCheckService;

    @Override
    public ExternalRestCheckNotaryCompensationDto check(final String snils, final String ssoId) {
        return notaryCompensationCheckService.getCheckResult(snils, ssoId);
    }
}
