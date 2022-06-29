package ru.croc.ugd.ssr.service.realestate;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static ru.croc.ugd.ssr.service.RealEstateDocumentService.PAGE_SIZE;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.DemolitionData;
import ru.croc.ugd.ssr.HousePreservationData;
import ru.croc.ugd.ssr.RealEstateDataType;
import ru.croc.ugd.ssr.dto.realestate.RestDemolitionDto;
import ru.croc.ugd.ssr.dto.realestate.RestHousePreservationDto;
import ru.croc.ugd.ssr.dto.realestate.RestNonResidentialApartmentDto;
import ru.croc.ugd.ssr.dto.realestate.RestResettlementCompletionDto;
import ru.croc.ugd.ssr.dto.realestate.RestUpdateNonResidentialSpaceInfoDto;
import ru.croc.ugd.ssr.egrn.EgrnFlatRequest;
import ru.croc.ugd.ssr.egrn.EgrnFlatRequestData;
import ru.croc.ugd.ssr.egrn.FlatEgrnResponse;
import ru.croc.ugd.ssr.enums.ResettlementStatus;
import ru.croc.ugd.ssr.mapper.EgrnFlatRequestMapper;
import ru.croc.ugd.ssr.mapper.RealEstateMapper;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.RealEstateDocument;
import ru.croc.ugd.ssr.model.egrn.EgrnFlatRequestDocument;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.RealEstateDocumentService;
import ru.croc.ugd.ssr.service.SsrFilestoreService;
import ru.croc.ugd.ssr.service.document.EgrnFlatRequestDocumentService;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class DefaultRestRealEstateService implements RestRealEstateService {

    private final RealEstateDocumentService realEstateDocumentService;
    private final RealEstateMapper realEstateMapper;
    private final EgrnFlatRequestMapper egrnFlatRequestMapper;
    private final SsrFilestoreService ssrFilestoreService;
    private final PersonDocumentService personDocumentService;
    private final EgrnFlatRequestDocumentService egrnFlatRequestDocumentService;

    @Override
    public void completeResettlement(final String id, final RestResettlementCompletionDto body) {
        final RealEstateDocument realEstateDocument = realEstateMapper.toRealEstateDocument(
            realEstateDocumentService.fetchDocument(id),
            body
        );

        realEstateDocumentService.fillResettlementStatus(realEstateDocument);

        realEstateDocumentService.updateDocument(realEstateDocument, "completeResettlement");
    }

    @Override
    public void preserveHouse(final String id, final RestHousePreservationDto body) {
        final RealEstateDocument realEstateDocument = realEstateMapper.toRealEstateDocument(
            realEstateDocumentService.fetchDocument(id),
            body
        );

        realEstateDocumentService.fillResettlementStatus(realEstateDocument);

        realEstateDocumentService.updateDocument(realEstateDocument, "preserveHouse");
    }

    @Override
    public void setNonResidentialSpaceInfo(final String id, final RestUpdateNonResidentialSpaceInfoDto body) {
        final RealEstateDocument realEstateDocument = realEstateDocumentService.fetchDocument(id);
        final RealEstateDataType realEstateData = realEstateDocument.getDocument().getRealEstateData();

        final Boolean hasNonResidentialSpaces = ofNullable(body.getHasNonResidentialSpaces())
            .orElseGet(realEstateData::isHasNonResidentialSpaces);
        final Boolean withdrawnNonResidentialSpaces = ofNullable(body.getWithdrawnNonResidentialSpaces())
            .orElseGet(realEstateData::isWithdrawnNonResidentialSpaces);

        realEstateData.setHasNonResidentialSpaces(hasNonResidentialSpaces);
        realEstateData.setWithdrawnNonResidentialSpaces(withdrawnNonResidentialSpaces);

        realEstateDocumentService.fillResettlementStatus(realEstateDocument);

        realEstateDocumentService.updateDocument(realEstateDocument, "setNonResidentialSpaceInfo");
    }

    @Override
    public void setDemolitionData(final String id, final RestDemolitionDto body) {
        final RealEstateDocument realEstateDocument = realEstateMapper.toRealEstateDocument(
            realEstateDocumentService.fetchDocument(id),
            body
        );

        realEstateDocumentService.fillResettlementStatus(realEstateDocument);

        realEstateDocumentService.updateDocument(realEstateDocument, "setDemolitionData");
    }

    @Async
    @Override
    public void actualizeRealEstates() {
        log.info("Start actualize real estates.");
        final long countDocuments = realEstateDocumentService.countDocuments();
        for (int page = 0; page <= countDocuments / PAGE_SIZE; page++) {
            realEstateDocumentService.fetchDocumentsPage(page, PAGE_SIZE)
                .forEach(realEstateDocument -> {
                    try {
                        actualizeRealEstate(realEstateDocument);
                    } catch (Exception e) {
                        log.error(
                            "Unable to actualize real estate for realEstateDocumentId = {}: {}",
                            realEstateDocument.getId(),
                            e.getMessage(),
                            e
                        );
                    }
                });
        }
        log.info("Finish actualize real estates.");
    }

    @Async
    @Override
    public void actualizeResettlementStatuses() {
        log.info("Start actualize resettlement statuses.");
        final long countDocuments = realEstateDocumentService.countDocuments();
        for (int page = 0; page <= countDocuments / PAGE_SIZE; page++) {
            realEstateDocumentService.fetchDocumentsPage(page, PAGE_SIZE)
                .forEach(realEstateDocument -> {
                    try {
                        actualizeResettlementStatus(realEstateDocument);
                    } catch (Exception e) {
                        log.error(
                            "Unable to actualize resettlement status for realEstateDocumentId = {}: {}",
                            realEstateDocument.getId(),
                            e.getMessage(),
                            e
                        );
                    }
                });
        }
        log.info("Finish actualize resettlement statuses.");
    }

    private void actualizeRealEstate(final RealEstateDocument realEstateDocument) {
        actualizeFolder(realEstateDocument);
        actualizeDemolitionData(realEstateDocument);
        actualizeResettlementStatus(realEstateDocument);
    }

    private void actualizeFolder(final RealEstateDocument realEstateDocument) {
        if (isNull(realEstateDocument.getFolderId())) {
            ssrFilestoreService.createFolderIfNeeded(SsrDocumentTypes.REAL_ESTATE, realEstateDocument);
            realEstateDocumentService.updateDocument(realEstateDocument, "actualizeFolder");
            log.info("Finish actualize folder for realEstateDocumentId = {}", realEstateDocument.getId());
        }
    }

    private void actualizeDemolitionData(final RealEstateDocument realEstateDocument) {
        final RealEstateDataType realEstateDataType = realEstateDocument.getDocument().getRealEstateData();
        final LocalDate demolitionDate = realEstateDataType.getDemolitionDate();
        if (nonNull(demolitionDate) && isNull(realEstateDataType.getDemolitionData())) {
            final DemolitionData demolitionData = new DemolitionData();
            demolitionData.setIsDemolished(true);
            realEstateDataType.setDemolitionData(demolitionData);
            realEstateDocumentService.updateDocument(realEstateDocument, "actualizeDemolitionData");
            log.info("Finish actualize demolition data for realEstateDocumentId = {}", realEstateDocument.getId());
        }
    }

    private void actualizeResettlementStatus(final RealEstateDocument realEstateDocument) {
        final RealEstateDataType realEstateDataType = realEstateDocument.getDocument().getRealEstateData();
        if (isNull(realEstateDataType.getResettlementStatus())) {
            final List<PersonDocument> personDocuments =
                personDocumentService.fetchByUnom(realEstateDataType.getUNOM());

            final boolean hasNonResidentialSpaces = of(realEstateDataType)
                .map(RealEstateDataType::isHasNonResidentialSpaces)
                .orElse(false);
            final boolean withdrawnNonResidentialSpaces = of(realEstateDataType)
                .map(RealEstateDataType::isWithdrawnNonResidentialSpaces)
                .orElse(false);

            if (personDocuments.isEmpty() || isNull(realEstateDataType.getResettlementBy())) {
                realEstateDataType.setResettlementStatus(ResettlementStatus.NOT_STARTED.getCode());
            } else if (personDocumentService.hasEmptyReleaseFlats(personDocuments)) {
                realEstateDataType.setResettlementStatus(ResettlementStatus.IN_PROGRESS.getCode());
            } else if (hasNonResidentialSpaces && !withdrawnNonResidentialSpaces) {
                realEstateDataType.setResettlementStatus(ResettlementStatus.IN_PROGRESS_WITHDRAW.getCode());
            } else if (isNull(realEstateDataType.getDemolitionDate())) {
                final boolean isPreserved = ofNullable(realEstateDataType.getHousePreservationData())
                    .map(HousePreservationData::isIsPreserved)
                    .orElse(false);
                if (isPreserved) {
                    realEstateDataType.setResettlementStatus(ResettlementStatus.IN_PROGRESS_PRESERVED.getCode());
                } else {
                    realEstateDataType.setResettlementStatus(ResettlementStatus.IN_PROGRESS_DEMOLITION.getCode());
                }
            } else {
                realEstateDataType.setResettlementStatus(ResettlementStatus.FINISHED.getCode());
            }
            realEstateDocumentService.updateDocument(realEstateDocument, "actualizeResettlementStatus");
            log.info("Finish actualize resettlement status for realEstateDocumentId = {}", realEstateDocument.getId());
        }
    }

    @Override
    public List<RestNonResidentialApartmentDto> getNonResidentialApartments(final String id) {
        final RealEstateDocument realEstateDocument = realEstateDocumentService.fetchDocument(id);
        final String unom = realEstateDocument.getDocument().getRealEstateData().getUNOM().toString();

        return egrnFlatRequestDocumentService
            .fetchLastNonResidentialByUnom(unom)
            .stream()
            .map(EgrnFlatRequestDocument::getDocument)
            .map(EgrnFlatRequest::getEgrnFlatRequestData)
            .map(EgrnFlatRequestData::getEgrnResponse)
            .filter(Objects::nonNull)
            .map(FlatEgrnResponse::getExtractAboutPropertyRoom)
            .filter(Objects::nonNull)
            .map(egrnFlatRequestMapper::toRestNonResidentialApartmentDto)
            .collect(Collectors.toList());
    }

}
