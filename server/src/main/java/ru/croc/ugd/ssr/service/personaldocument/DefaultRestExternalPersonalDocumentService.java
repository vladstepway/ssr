package ru.croc.ugd.ssr.service.personaldocument;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.generated.dto.personaldocument.ExternalRestCheckPersonalDocumentDto;

@Service
@AllArgsConstructor
public class DefaultRestExternalPersonalDocumentService implements RestExternalPersonalDocumentService {

    private final PersonalDocumentCheckService personalDocumentCheckService;

    @Override
    public ExternalRestCheckPersonalDocumentDto check(final String snils, final String ssoId) {
        return personalDocumentCheckService.getCheckResult(snils, ssoId);
    }
}
