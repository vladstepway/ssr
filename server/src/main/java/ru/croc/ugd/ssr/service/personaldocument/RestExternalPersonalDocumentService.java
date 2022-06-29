package ru.croc.ugd.ssr.service.personaldocument;

import ru.croc.ugd.ssr.generated.dto.personaldocument.ExternalRestCheckPersonalDocumentDto;

public interface RestExternalPersonalDocumentService {

    ExternalRestCheckPersonalDocumentDto check(final String snils, final String ssoId);
}
