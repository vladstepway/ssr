package ru.croc.ugd.ssr.service;

import static ru.croc.ugd.ssr.utils.PersonUtils.retrieveAgreementList;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.db.projection.TenantProjection;
import ru.croc.ugd.ssr.dto.agreement.RestAgreementDto;
import ru.croc.ugd.ssr.mapper.AgreementMapper;
import ru.croc.ugd.ssr.model.PersonDocument;

import java.util.List;

@Service
@AllArgsConstructor
public class AgreementService {

    private final PersonDocumentService personDocumentService;
    private final AgreementMapper agreementMapper;

    public List<RestAgreementDto> findAll(final String personDocumentId) {
        final PersonDocument personDocument = personDocumentService.fetchDocument(personDocumentId);
        final List<PersonType.Agreements.Agreement> agreements = retrieveAgreementList(personDocument);
        return agreementMapper.toRestAgreementDtos(agreements, this::retrieveTenant);
    }

    private TenantProjection retrieveTenant(final String personDocumentId) {
        return personDocumentService.fetchTenantById(personDocumentId)
            .orElse(null);
    }
}
