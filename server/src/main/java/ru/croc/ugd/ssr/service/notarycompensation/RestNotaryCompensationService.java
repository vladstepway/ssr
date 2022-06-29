package ru.croc.ugd.ssr.service.notarycompensation;

import ru.croc.ugd.ssr.dto.notarycompensation.RestNotaryCompensationDto;
import ru.croc.ugd.ssr.dto.notarycompensation.RestNotaryCompensationMoneyNotPayedDto;
import ru.croc.ugd.ssr.dto.notarycompensation.RestNotaryCompensationRejectDto;

public interface RestNotaryCompensationService {

    RestNotaryCompensationDto fetchById(final String notaryCompensationDocumentId);

    void confirm(final String notaryCompensationDocumentId);

    void reject(
        final String notaryCompensationDocumentId,
        final RestNotaryCompensationRejectDto rejectDto
    );

    void moneyNotPayed(
        final String notaryCompensationDocumentId,
        final RestNotaryCompensationMoneyNotPayedDto moneyNotPayedDto
    );
}