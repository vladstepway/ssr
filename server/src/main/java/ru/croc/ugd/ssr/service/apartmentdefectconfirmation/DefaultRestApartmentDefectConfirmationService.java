package ru.croc.ugd.ssr.service.apartmentdefectconfirmation;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.ApartmentDefectConfirmationData;
import ru.croc.ugd.ssr.ApartmentDefectElimination;
import ru.croc.ugd.ssr.ApartmentEliminationDefectType;
import ru.croc.ugd.ssr.ApartmentInspection;
import ru.croc.ugd.ssr.ApartmentInspectionType;
import ru.croc.ugd.ssr.DefectData;
import ru.croc.ugd.ssr.DelayReasonData;
import ru.croc.ugd.ssr.Person;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.WorkConfirmationFile;
import ru.croc.ugd.ssr.db.dao.ApartmentDefectConfirmationDao;
import ru.croc.ugd.ssr.db.projection.ApartmentDefectProjection;
import ru.croc.ugd.ssr.dto.apartmentdefectconfirmation.RestCorrectDefectDto;
import ru.croc.ugd.ssr.dto.apartmentdefectconfirmation.RestDefectConfirmationDto;
import ru.croc.ugd.ssr.dto.apartmentdefectconfirmation.RestDefectDto;
import ru.croc.ugd.ssr.dto.apartmentdefectconfirmation.RestProcessReviewDefectDto;
import ru.croc.ugd.ssr.dto.apartmentinspection.DelayReasonDto;
import ru.croc.ugd.ssr.mapper.ApartmentDefectConfirmationMapper;
import ru.croc.ugd.ssr.model.ApartmentDefectConfirmationDocument;
import ru.croc.ugd.ssr.model.ApartmentInspectionDocument;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.service.ApartmentInspectionService;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.document.ApartmentDefectConfirmationDocumentService;
import ru.croc.ugd.ssr.utils.ApartmentInspectionUtils;
import ru.croc.ugd.ssr.utils.PageUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@AllArgsConstructor
public class DefaultRestApartmentDefectConfirmationService implements RestApartmentDefectConfirmationService {

    private final ApartmentDefectConfirmationDocumentService apartmentDefectConfirmationDocumentService;
    private final ApartmentDefectConfirmationMapper apartmentDefectConfirmationMapper;
    private final ApartmentInspectionService apartmentInspectionService;
    private final ApartmentDefectConfirmationDao apartmentDefectConfirmationDao;
    private final PersonDocumentService personDocumentService;
    private final ApartmentDefectConfirmationService apartmentDefectConfirmationService;

    @Override
    public void requestConfirmation(final String id, final RestDefectConfirmationDto restDefectConfirmationDto) {
        log.info("Start request confirmation: apartmentDefectConfirmationId = {}", id);
        final ApartmentDefectConfirmationDocument apartmentDefectConfirmationDocument =
            apartmentDefectConfirmationDocumentService.fetchDocument(id);
        final ApartmentDefectConfirmationDocument updatedApartmentDefectConfirmationDocument =
            apartmentDefectConfirmationMapper.toApartmentDefectConfirmationDocument(
                apartmentDefectConfirmationDocument,
                restDefectConfirmationDto,
                LocalDateTime.now(),
                false,
                apartmentInspectionService::retrieveAffairId
            );
        apartmentDefectConfirmationDocumentService.updateDocument(
            updatedApartmentDefectConfirmationDocument, "requestConfirmation"
        );
        if (nonNull(restDefectConfirmationDto.getDefects())) {
            setIsBlockedForAllDefects(restDefectConfirmationDto.getDefects());
        }

        apartmentDefectConfirmationService.startConfirmationProcess(apartmentDefectConfirmationDocument);
        log.info("Finish request confirmation: apartmentDefectConfirmationId = {}", id);
    }

    @Override
    public Page<RestDefectDto> fetchDefects(
        final String id,
        final int pageNum,
        final int pageSize,
        final String flat,
        final String flatElement,
        final String description,
        final Boolean isEliminated,
        final boolean skipApproved,
        final boolean skipExcluded,
        final String sort
    ) {
        final Page<ApartmentDefectProjection> apartmentDefectProjections = apartmentDefectConfirmationDao.fetchDefects(
            id, flat, flatElement, description, isEliminated, skipApproved, skipExcluded,
            PageRequest.of(pageNum, pageSize, PageUtils.retrieveSort(sort))
        );
        return apartmentDefectProjections.map(apartmentDefectProjection -> {
            final List<PersonDocument> personDocuments = ofNullable(apartmentDefectProjection)
                .map(ApartmentDefectProjection::getApartmentInspectionId)
                .map(apartmentInspectionService::fetchDocument)
                .map(ApartmentInspectionDocument::getDocument)
                .map(ApartmentInspection::getApartmentInspectionData)
                .map(ApartmentInspectionType::getPersonID)
                .map(personDocumentService::fetchDocument)
                .map(Collections::singletonList)
                .orElse(Collections.emptyList());
            return apartmentDefectConfirmationMapper.toRestDefectDto(apartmentDefectProjection, personDocuments);
        });
    }

    private void setIsBlockedForAllDefects(final List<RestDefectDto> defects) {
        defects.stream()
            .collect(Collectors.groupingBy(
                RestDefectDto::getApartmentInspectionId,
                Collectors.mapping(RestDefectDto::getId, Collectors.toList())
            ))
            .forEach((key, value) -> apartmentInspectionService.addBlockingInformation(key, value, true));
    }

    @Override
    public void processReview(final String id, final RestProcessReviewDefectDto restProcessReviewDefectDto) {
        log.info("Start review process: apartmentDefectConfirmationId = {}", id);
        final ApartmentDefectConfirmationDocument apartmentDefectConfirmationDocument =
            apartmentDefectConfirmationDocumentService.fetchDocument(id);
        final ApartmentDefectConfirmationData apartmentDefectConfirmationData =
            apartmentDefectConfirmationDocument.getDocument().getApartmentDefectConfirmationData();

        if (nonNull(restProcessReviewDefectDto.getRejectionReason())) {
            log.info(
                "Developer rejects apartment defect change: apartmentDefectConfirmationId = {}, reason = {}",
                id,
                restProcessReviewDefectDto.getRejectionReason()
            );
            apartmentDefectConfirmationData.setRejectionReason(restProcessReviewDefectDto.getRejectionReason());
        } else {
            log.info("Developer approves apartment defect change: apartmentDefectConfirmationId = {}", id);
        }
        apartmentDefectConfirmationData.setReviewDateTime(LocalDateTime.now());

        final List<DefectData> approvedDefects = new ArrayList<>();
        ofNullable(apartmentDefectConfirmationData.getDefects())
            .map(ApartmentDefectConfirmationData.Defects::getDefect)
            .orElse(Collections.emptyList())
            .forEach(defectData -> {
                final String defectId = defectData.getId();
                if (existsDefectId(restProcessReviewDefectDto.getApprovedDefectIds(), defectId)) {
                    defectData.setIsApproved(true);
                    approvedDefects.add(defectData);
                } else if (existsDefectId(restProcessReviewDefectDto.getRejectedDefectIds(), defectId)) {
                    defectData.setIsApproved(false);
                }
            });

        apartmentDefectConfirmationDocumentService.updateDocument(
            apartmentDefectConfirmationDocument, "processReview"
        );

        final boolean areConfirmed = ofNullable(apartmentDefectConfirmationData.getDefects())
            .map(ApartmentDefectConfirmationData.Defects::getDefect)
            .map(Collection::stream)
            .orElse(Stream.empty())
            .allMatch(defectData -> Objects.equals(defectData.isIsApproved(), true) || defectData.isIsExcluded());

        ofNullable(apartmentDefectConfirmationData.getProcessInstanceId())
            .ifPresent(processInstanceId -> apartmentDefectConfirmationService.closeConfirmationTask(
                processInstanceId, areConfirmed
            ));

        approvedDefects.stream()
            .collect(Collectors.groupingBy(DefectData::getApartmentInspectionId))
            .forEach((key, value) -> saveApprovedData(
                key, value, apartmentDefectConfirmationData.getWorkConfirmationFiles()
            ));
        log.info("Finish review process: apartmentDefectConfirmationId = {}", id);
    }

    private boolean existsDefectId(final List<String> defectIds, final String defectId) {
        return nonNull(defectIds) && defectIds.contains(defectId);
    }

    private void saveApprovedData(
        final String apartmentInspectionId,
        final List<DefectData> defects,
        final List<WorkConfirmationFile> workConfirmationFiles
    ) {
        log.info("Start approved data saving: apartmentInspectionId = {}", apartmentInspectionId);
        final ApartmentInspectionDocument apartmentInspectionDocument = apartmentInspectionService.fetchDocument(
            apartmentInspectionId
        );
        final ApartmentInspectionType apartmentInspection = apartmentInspectionDocument.getDocument()
            .getApartmentInspectionData();
        apartmentInspection.getWorkConfirmationFiles().addAll(workConfirmationFiles);
        defects.forEach(defect -> addDefectData(defect, apartmentInspection.getApartmentDefects()));
        apartmentInspectionService.updateDocument(apartmentInspectionDocument, "updateDefectsInApartmentInspection");

        defectsEliminationIfNeeded(defects, apartmentInspectionDocument);
        log.info("Finish approved data saving: apartmentInspectionId = {}", apartmentInspectionId);
    }

    private void defectsEliminationIfNeeded(
        final List<DefectData> defects, final ApartmentInspectionDocument apartmentInspectionDocument
    ) {
        final ApartmentInspectionType apartmentInspection = apartmentInspectionDocument.getDocument()
            .getApartmentInspectionData();

        final LocalDate maxEliminationDate = defects.stream()
            .map(DefectData::getEliminationData)
            .map(ApartmentDefectElimination::getEliminationDate)
            .filter(Objects::nonNull)
            .max(LocalDate::compareTo)
            .orElse(null);

        final LocalDate lastDelayDate = ApartmentInspectionUtils.getLastDelayDate(apartmentInspection)
            .orElse(null);

        LocalDate delayDate = lastDelayDate;
        boolean areDefectsEliminated = false;
        boolean shouldRunDefectsElimination = false;

        if (nonNull(maxEliminationDate)
            && (isNull(lastDelayDate) || lastDelayDate.isBefore(maxEliminationDate))) {
            delayDate = maxEliminationDate;
            shouldRunDefectsElimination = true;
        }

        if (checkAllDefectsEliminated(apartmentInspection.getApartmentDefects())) {
            areDefectsEliminated = true;
            shouldRunDefectsElimination = true;
            apartmentInspectionService.forceFinishProcess(apartmentInspectionDocument);
        }

        if (shouldRunDefectsElimination) {
            final DelayReasonDto delayReasonDto = DelayReasonDto.builder()
                .delayDate(delayDate)
                .areDefectsEliminated(areDefectsEliminated)
                .build();
            apartmentInspectionService.defectsElimination(apartmentInspectionDocument.getId(), delayReasonDto);
        }
    }

    private boolean checkAllDefectsEliminated(final List<ApartmentInspectionType.ApartmentDefects> apartmentDefects) {
        return apartmentDefects.stream()
            .map(ApartmentInspectionType.ApartmentDefects::getApartmentDefectData)
            .allMatch(ApartmentEliminationDefectType::isIsEliminated);
    }

    private void addDefectData(
        final DefectData defectData, final List<ApartmentInspectionType.ApartmentDefects> apartmentDefects
    ) {
        apartmentDefects.stream()
            .filter(apartmentDefect -> nonNull(apartmentDefect)
                && nonNull(apartmentDefect.getApartmentDefectData())
                && Objects.equals(apartmentDefect.getApartmentDefectData().getId(), defectData.getId()))
            .findFirst()
            .ifPresent(apartmentDefect -> {
                final ApartmentEliminationDefectType apartmentDefectData = apartmentDefectConfirmationMapper
                    .toApartmentDefectData(apartmentDefect.getApartmentDefectData(), defectData, false);
                apartmentDefect.setApartmentDefectData(apartmentDefectData);
            });
    }

    @Override
    public void saveConfirmation(final String id, final RestDefectConfirmationDto restDefectConfirmationDto) {
        log.info("Start confirmation saving: apartmentDefectConfirmationId = {}", id);
        final ApartmentDefectConfirmationDocument apartmentDefectConfirmationDocument =
            apartmentDefectConfirmationDocumentService.fetchDocument(id);
        final ApartmentDefectConfirmationDocument updatedApartmentDefectConfirmationDocument =
            apartmentDefectConfirmationMapper.toApprovedApartmentDefectConfirmationDocument(
                apartmentDefectConfirmationDocument,
                restDefectConfirmationDto,
                LocalDateTime.now(),
                false,
                apartmentInspectionService::retrieveAffairId
            );
        apartmentDefectConfirmationDocumentService.updateDocument(
            updatedApartmentDefectConfirmationDocument, "saveConfirmation"
        );

        final ApartmentDefectConfirmationData apartmentDefectConfirmationData =
            apartmentDefectConfirmationDocument.getDocument().getApartmentDefectConfirmationData();
        ofNullable(apartmentDefectConfirmationData.getDefects())
            .map(ApartmentDefectConfirmationData.Defects::getDefect)
            .map(List::stream)
            .orElse(Stream.empty())
            .collect(Collectors.groupingBy(DefectData::getApartmentInspectionId))
            .forEach((key, value) -> saveApprovedData(
                key, value, apartmentDefectConfirmationData.getWorkConfirmationFiles()
            ));
        log.info("Finish confirmation saving: apartmentDefectConfirmationId = {}", id);
    }

    @Override
    public void correct(final String id, final RestCorrectDefectDto restCorrectDefectDto) {
        log.info("Start correction: apartmentDefectConfirmationId = {}", id);
        final ApartmentDefectConfirmationDocument apartmentDefectConfirmationDocument =
            apartmentDefectConfirmationDocumentService.fetchDocument(id);

        final ApartmentDefectConfirmationData apartmentDefectConfirmationData =
            apartmentDefectConfirmationDocument.getDocument().getApartmentDefectConfirmationData();
        apartmentDefectConfirmationData.setRequestDateTime(LocalDateTime.now());

        final List<WorkConfirmationFile> workConfirmationFiles =
            apartmentDefectConfirmationMapper.toWorkConfirmationFiles(restCorrectDefectDto.getWorkConfirmationFiles());
        apartmentDefectConfirmationData.getWorkConfirmationFiles().addAll(workConfirmationFiles);

        final Map<String, RestDefectDto> correctedDefects = ofNullable(restCorrectDefectDto.getCorrectedDefects())
            .map(List::stream)
            .orElse(Stream.empty())
            .collect(Collectors.toMap(
                RestDefectDto::getId,
                Function.identity(),
                (d1, d2) -> d1)
            );

        if (nonNull(apartmentDefectConfirmationData.getDefects())) {
            final List<DefectData> excludedDefects = new ArrayList<>();
            final List<DefectData> actualizedDefects = apartmentDefectConfirmationData.getDefects()
                .getDefect()
                .stream()
                .map(defectData -> {
                    final String defectId = defectData.getId();
                    if (correctedDefects.containsKey(defectId)) {
                        final RestDefectDto restDefectDto = correctedDefects.get(defectId);
                        return apartmentDefectConfirmationMapper.toDefectData(
                            restDefectDto, apartmentInspectionService::retrieveAffairId
                        );
                    } else if (existsDefectId(restCorrectDefectDto.getExcludedDefectIds(), defectId)) {
                        defectData.setIsExcluded(true);
                        excludedDefects.add(defectData);
                    }
                    return defectData;
                })
                .collect(Collectors.toList());
            apartmentDefectConfirmationData.getDefects().getDefect().clear();
            apartmentDefectConfirmationData.getDefects().getDefect().addAll(actualizedDefects);

            excludedDefects.stream()
                .collect(Collectors.groupingBy(
                    DefectData::getApartmentInspectionId,
                    Collectors.mapping(DefectData::getId, Collectors.toList())
                ))
                .forEach((key, value) -> apartmentInspectionService.addBlockingInformation(key, value, false));
        }

        apartmentDefectConfirmationDocumentService.updateDocument(
            apartmentDefectConfirmationDocument, "correct"
        );

        ofNullable(apartmentDefectConfirmationData.getProcessInstanceId())
            .ifPresent(apartmentDefectConfirmationService::closeCorrectionTask);
        log.info("Finish correction: apartmentDefectConfirmationId = {}", id);
    }

    @Override
    public RestDefectConfirmationDto create() {
        final ApartmentDefectConfirmationDocument apartmentDefectConfirmationDocument =
            apartmentDefectConfirmationMapper.toEmptyApartmentDefectConfirmationDocument(true);
        final ApartmentDefectConfirmationDocument createdApartmentDefectConfirmationDocument =
            apartmentDefectConfirmationDocumentService.createDocument(
                apartmentDefectConfirmationDocument, false, "create"
            );
        return apartmentDefectConfirmationMapper.toApartmentDefectConfirmationDto(
            createdApartmentDefectConfirmationDocument
        );
    }

    @Override
    public RestDefectConfirmationDto fetchById(final String id) {
        final ApartmentDefectConfirmationDocument apartmentDefectConfirmationDocument =
            apartmentDefectConfirmationDocumentService.fetchDocument(id);
        return apartmentDefectConfirmationMapper.toApartmentDefectConfirmationDto(apartmentDefectConfirmationDocument);
    }

}
