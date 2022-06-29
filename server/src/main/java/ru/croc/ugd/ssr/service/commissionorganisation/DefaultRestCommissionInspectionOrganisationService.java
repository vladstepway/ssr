package ru.croc.ugd.ssr.service.commissionorganisation;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.commissionorganisation.CommissionInspectionOrganisationData;
import ru.croc.ugd.ssr.dto.commissionorganisation.RestCommissionInspectionOrganisationDto;
import ru.croc.ugd.ssr.mapper.CommissionInspectionOrganisationMapper;
import ru.croc.ugd.ssr.model.commissionorganisation.CommissionInspectionOrganisationDocument;
import ru.croc.ugd.ssr.service.document.CommissionInspectionOrganisationDocumentService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * DefaultCommissionInspectionOrganisationService.
 */
@Service
@AllArgsConstructor
public class DefaultRestCommissionInspectionOrganisationService implements RestCommissionInspectionOrganisationService {

    private final CommissionInspectionOrganisationMapper commissionInspectionOrganisationMapper;
    private final CommissionInspectionOrganisationDocumentService organisationDocumentService;

    @Override
    public List<RestCommissionInspectionOrganisationDto> getAll() {
        final List<CommissionInspectionOrganisationDocument> documents =
            organisationDocumentService.findAll();

        return commissionInspectionOrganisationMapper.toRestCommissionInspectionOrganisationDtoList(documents);
    }

    @Override
    public List<RestCommissionInspectionOrganisationDto> updateAll(
        final List<RestCommissionInspectionOrganisationDto> organisationDtoList
    ) {
        final List<CommissionInspectionOrganisationDocument> organisationDocuments =
            organisationDocumentService.fetchDocumentsPage(0, 100);

        organisationDocuments.forEach(
            organisationDocument -> {
                final RestCommissionInspectionOrganisationDto organisationDto = organisationDtoList.stream()
                    .filter(dto -> dto.getId().equals(organisationDocument.getId()))
                    .findFirst()
                    .orElse(null);

                if (organisationDto != null) {
                    final CommissionInspectionOrganisationData organisation = organisationDocument
                        .getDocument()
                        .getCommissionInspectionOrganisationData();

                    organisation.setAddress(organisationDto.getAddress());
                    organisation.setPhone(organisationDto.getPhone());

                    organisationDocumentService
                        .updateDocument(organisationDocument.getId(), organisationDocument, true, true, null);
                }
            }
        );

        return organisationDocuments
            .stream()
            .map(commissionInspectionOrganisationMapper::toRestCommissionInspectionOrganisationDto)
            .collect(Collectors.toList());
    }

}
