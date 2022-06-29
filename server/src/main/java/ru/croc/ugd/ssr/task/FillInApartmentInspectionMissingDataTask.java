package ru.croc.ugd.ssr.task;

import static java.util.Optional.ofNullable;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.ApartmentInspection;
import ru.croc.ugd.ssr.ApartmentInspectionType;
import ru.croc.ugd.ssr.OrganizationInformation;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.model.ApartmentInspectionDocument;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.api.oks.Organization;
import ru.croc.ugd.ssr.service.ApartmentInspectionService;
import ru.croc.ugd.ssr.service.CapitalConstructionObjectService;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.utils.ApartmentInspectionUtils;
import ru.reinform.cdp.search.model.search_output.SearchResponse;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//TODO remove according to the task #
// source task #119131, #119272

/**
 * Задача для заполнения поля general contractors, developers, unom в Apartment Inspection.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FillInApartmentInspectionMissingDataTask {

    private static final String GENERAL_CONTRACTORS_ROLE_NAME = "Генеральный подрядчик";
    private static final String DEVELOPER_ROLE_NAME = "Застройщик";

    private static final String UPDATE_DOCUMENT_LOG_MESSAGE =
        "В документ apartment inspection c id = %s добавлены значения general contractors, developers, unom";
    private static final String FINISH_TASK_LOG_MESSAGE =
        "Выполнение FillInApartmentInspectionMissingDataTask завершено";
    private static final String UPDATE_DOCUMENT_ERROR_LOG_MESSAGE =
        "Возникла ошибка при обновлении apartment inspection c id = %s: %s";

    private final Map<String, Map<String, Object>> oksStore = new HashMap<>();

    private final ApartmentInspectionService apartmentInspectionService;
    private final CapitalConstructionObjectService capitalConstructionObjectService;
    private final PersonDocumentService personDocumentService;

    /**
     * Заполнить акт недостающими данными.
     */
    @Async
    public void runTask() {

        final List<ApartmentInspectionDocument> apartmentInspectionDocuments =
            apartmentInspectionService.getApartmentInspections();

        final Map<String, List<ApartmentInspectionDocument>> apartmentInspectionMap =
            apartmentInspectionDocuments
                .stream()
                .filter(apartmentInspection -> ofNullable(apartmentInspection)
                    .map(ApartmentInspectionDocument::getDocument)
                    .map(ApartmentInspection::getApartmentInspectionData)
                    .map(ApartmentInspectionType::getPersonID)
                    .isPresent()
                )
                .collect(Collectors
                    .groupingBy(apartmentInspection ->
                        apartmentInspection.getDocument().getApartmentInspectionData().getPersonID()
                    )
                );

        apartmentInspectionMap.forEach(this::fillDataForApartmentInspections);

        log.info(FINISH_TASK_LOG_MESSAGE);
    }

    private void fillDataForApartmentInspections(
        final String personId, final List<ApartmentInspectionDocument> apartmentInspectionDocumentsByPersonId
    ) {
        if (personId == null) {
            return;
        }

        final PersonDocument personDocument = personDocumentService.fetchById(personId).orElse(null);

        if (personDocument == null) {
            return;
        }

        apartmentInspectionDocumentsByPersonId.forEach(apartmentInspectionDocument ->
            fillDataForApartmentInspection(personDocument, apartmentInspectionDocument)
        );
    }

    private void fillDataForApartmentInspection(
        final PersonDocument personDocument, final ApartmentInspectionDocument apartmentInspectionDocument
    ) {
        try {
            final String generalContractors =
                ApartmentInspectionUtils.joinOrgInfoFullName(apartmentInspectionDocument
                    .getDocument().getApartmentInspectionData().getGeneralContractors());
            final String developers =
                ApartmentInspectionUtils.joinOrgInfoFullName(apartmentInspectionDocument
                    .getDocument().getApartmentInspectionData().getDevelopers());
            String unom = apartmentInspectionDocument.getDocument().getApartmentInspectionData().getUnom();

            if (generalContractors != null && developers != null && unom != null) {
                return;
            }
            if (unom == null) {

                if (personDocument.getDocument().getPersonData().getNewFlatInfo() == null
                    || personDocument.getDocument().getPersonData().getNewFlatInfo().getNewFlat().size() == 0) {
                    return;
                }

                final List<PersonType.NewFlatInfo.NewFlat> newFlats =
                    personDocument.getDocument().getPersonData().getNewFlatInfo().getNewFlat();

                final String apartmentInspectionAddress = apartmentInspectionDocument
                    .getDocument()
                    .getApartmentInspectionData()
                    .getAddress();
                unom = getUnomByAddress(newFlats, apartmentInspectionAddress);
            }

            if (unom == null) {
                return;
            }

            final List<OrganizationInformation> generalContractorsNames =
                getOrgNamesByRole(unom, GENERAL_CONTRACTORS_ROLE_NAME);
            final List<OrganizationInformation> developersNames =
                getOrgNamesByRole(unom, DEVELOPER_ROLE_NAME);

            if (generalContractorsNames.isEmpty() && developersNames.isEmpty()) {
                return;
            }

            final List<String> existedGeneralContractorsExternalIds =
                apartmentInspectionDocument
                    .getDocument()
                    .getApartmentInspectionData()
                    .getGeneralContractors()
                    .stream()
                    .map(OrganizationInformation::getExternalId)
                    .collect(Collectors.toList());

            final List<OrganizationInformation> generalContractorsToAdd =
                generalContractorsNames
                    .stream()
                    .filter(generalContractor ->
                        !existedGeneralContractorsExternalIds.contains(generalContractor.getExternalId())
                    )
                    .collect(Collectors.toList());

            apartmentInspectionDocument
                .getDocument()
                .getApartmentInspectionData()
                .getGeneralContractors()
                .addAll(generalContractorsToAdd);

            final List<String> existedDevelopersExternalIds =
                apartmentInspectionDocument
                    .getDocument()
                    .getApartmentInspectionData()
                    .getDevelopers()
                    .stream()
                    .map(OrganizationInformation::getExternalId)
                    .collect(Collectors.toList());

            final List<OrganizationInformation> developersToAdd =
                developersNames
                    .stream()
                    .filter(developer ->
                        !existedDevelopersExternalIds.contains(developer.getExternalId())
                    )
                    .collect(Collectors.toList());

            apartmentInspectionDocument
                .getDocument()
                .getApartmentInspectionData()
                .getDevelopers()
                .addAll(developersToAdd);

            apartmentInspectionDocument
                .getDocument()
                .getApartmentInspectionData()
                .setUnom(unom);

            final String id = apartmentInspectionService.updateDocument(apartmentInspectionDocument)
                .getId();

            log.info(String.format(UPDATE_DOCUMENT_LOG_MESSAGE, id));
        } catch (Exception e) {
            log.error(
                String.format(UPDATE_DOCUMENT_ERROR_LOG_MESSAGE, apartmentInspectionDocument.getId(), e.getMessage()), e
            );
        }
    }

    private String getUnomByAddress(final List<PersonType.NewFlatInfo.NewFlat> newFlats, final String address) {
        String unom;
        if (newFlats.size() == 1) {
            unom = newFlats.get(0).getCcoUnom().toString();
        } else {
            final PersonType.NewFlatInfo.NewFlat newFlat = newFlats.stream()
                .filter(flat -> flat.getCcoUnom() != null)
                .filter(flat -> {
                    final Map<String, Object> doc = getCcoByUnom(flat.getCcoUnom().toString());
                    if (doc == null) {
                        return false;
                    }
                    final String oksAddress = (String) doc.get("ugd_ps_oks_assignedAddress");
                    if (oksAddress == null) {
                        return false;
                    }
                    return oksAddress.equals(address);
                })
                .findFirst()
                .orElse(null);
            unom = newFlat != null ? newFlat.getCcoUnom().toString() : null;
        }

        return unom;
    }

    private List<OrganizationInformation> getOrgNamesByRole(final String unom, final String role) {
        return ofNullable(getCcoIdUnom(unom))
            .map(Collections::singletonList)
            .map(capitalConstructionObjectService::psGetOrganizationsFromCco)
            .orElse(Collections.emptyList())
            .stream()
            .filter(organization -> StringUtils.equalsIgnoreCase(organization.getRoleName(), role))
            .map(this::mapToOrganizationInformation)
            .collect(Collectors.toList());
    }

    private OrganizationInformation mapToOrganizationInformation(final Organization organization) {
        final OrganizationInformation organizationInformation = new OrganizationInformation();
        organizationInformation.setExternalId(organization.getInn());
        organizationInformation.setOrgFullName(organization.getFullName());
        return organizationInformation;
    }

    private Map<String, Object> getCcoByUnom(final String unom) {
        if (oksStore.containsKey(unom)) {
            return oksStore.get(unom);
        }
        final SearchResponse searchResponse = capitalConstructionObjectService.getCcoFromSolrByUnom(unom);
        if (searchResponse.getDocs() == null) {
            return null;
        }
        final Map<String, Object> doc = searchResponse.getDocs().get(0);
        final String oksUnom = (String) doc.get("ugd_ps_oks_unom");
        oksStore.put(oksUnom, doc);
        return doc;
    }

    private String getCcoIdUnom(final String unom) {
        return ofNullable(getCcoByUnom(unom))
            .map(cco -> (String) cco.get("sys_documentId"))
            .orElse(null);
    }

}
