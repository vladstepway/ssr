package ru.croc.ugd.ssr.service.commissioninspection;

import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static ru.croc.ugd.ssr.dto.commissioninspection.CommissionInspectionFlowStatus.MOVE_DATE_FIRST_VISIT_ACCEPTED;
import static ru.croc.ugd.ssr.dto.commissioninspection.CommissionInspectionFlowStatus.MOVE_DATE_FIRST_VISIT_REQUEST;
import static ru.croc.ugd.ssr.dto.commissioninspection.CommissionInspectionFlowStatus.MOVE_DATE_SECOND_VISIT_ACCEPTED;
import static ru.croc.ugd.ssr.dto.commissioninspection.CommissionInspectionFlowStatus.MOVE_DATE_SECOND_VISIT_REQUEST;
import static ru.croc.ugd.ssr.dto.commissioninspection.CommissionInspectionFlowStatus.REFUSE_FLAT;
import static ru.croc.ugd.ssr.dto.commissioninspection.CommissionInspectionFlowStatus.TIME_CONFIRMED_FIRST_VISIT;
import static ru.croc.ugd.ssr.dto.commissioninspection.CommissionInspectionFlowStatus.TIME_CONFIRMED_SECOND_VISIT;
import static ru.croc.ugd.ssr.service.commissioninspection.CommissionInspectionUtils.addCommissionInspectionHistoryEvent;
import static ru.croc.ugd.ssr.service.commissioninspection.CommissionInspectionUtils.isPrimaryInspection;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.croc.ugd.ssr.ApartmentInspectionType;
import ru.croc.ugd.ssr.application.Applicant;
import ru.croc.ugd.ssr.commission.CommissionInspection;
import ru.croc.ugd.ssr.commission.CommissionInspectionData;
import ru.croc.ugd.ssr.db.dao.DefectDao;
import ru.croc.ugd.ssr.db.projection.CommissionInspectionProjection;
import ru.croc.ugd.ssr.dto.commissioninspection.CommissionInspectionCheckResponse;
import ru.croc.ugd.ssr.dto.commissioninspection.CommissionInspectionConfig;
import ru.croc.ugd.ssr.dto.commissioninspection.CommissionInspectionFlowStatus;
import ru.croc.ugd.ssr.dto.commissioninspection.DefectDto;
import ru.croc.ugd.ssr.dto.commissioninspection.RestCommissionInspectionConfirmDateDto;
import ru.croc.ugd.ssr.dto.commissioninspection.RestCommissionInspectionDto;
import ru.croc.ugd.ssr.dto.commissioninspection.RestCommissionInspectionMoveDateDto;
import ru.croc.ugd.ssr.dto.commissioninspection.RestCommissionInspectionVisitsDto;
import ru.croc.ugd.ssr.exception.SsrException;
import ru.croc.ugd.ssr.exception.commissioninspection.MoveDateMissed;
import ru.croc.ugd.ssr.generated.dto.RestCommissionInspectionCheckResponseDto;
import ru.croc.ugd.ssr.generated.dto.RestDefectDto;
import ru.croc.ugd.ssr.integration.service.notification.CommissionInspectionElkNotificationService;
import ru.croc.ugd.ssr.mapper.RestCommissionInspectionMapper;
import ru.croc.ugd.ssr.model.ApartmentInspectionDocument;
import ru.croc.ugd.ssr.model.commissioninspection.CommissionInspectionDocument;
import ru.croc.ugd.ssr.service.ApartmentInspectionService;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.document.CommissionInspectionDocumentService;
import ru.croc.ugd.ssr.utils.StreamUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * DefaultRestCommissionInspectionService.
 */
@Service
@AllArgsConstructor
@Slf4j
public class DefaultRestCommissionInspectionService implements RestCommissionInspectionService {

    private final ApartmentInspectionService apartmentInspectionService;
    private final DefectDao defectDao;
    private final PersonDocumentService personDocumentService;
    private final CommissionInspectionDocumentService commissionInspectionDocumentService;
    private final CommissionInspectionElkNotificationService commissionInspectionElkNotificationService;
    private final RestCommissionInspectionMapper restCommissionInspectionMapper;
    private final CommissionInspectionService commissionInspectionService;
    private final CommissionInspectionCheckService commissionInspectionCheckService;
    private final CommissionInspectionConfig commissionInspectionConfig;

    @Override
    public List<RestCommissionInspectionDto> findAll(final String personId, final String statuses) {
        final List<CommissionInspectionDocument> commissionInspectionDocuments = commissionInspectionDocumentService
            .findAll(personId, statuses);

        final Map<String, List<CommissionInspectionDocument>> groupByPersonId = commissionInspectionDocuments.stream()
            .collect(Collectors.groupingBy(commissionInspectionDocument ->
                of(commissionInspectionDocument.getDocument())
                    .map(CommissionInspection::getCommissionInspectionData)
                    .map(CommissionInspectionData::getApplicant)
                    .map(Applicant::getPersonId)
                    .orElse(null)
            ));

        return groupByPersonId.entrySet()
            .stream()
            .map(entry -> {
                final String personFullName = personDocumentService.getPersonFullNameById(entry.getKey()).orElse(null);
                return restCommissionInspectionMapper
                    .toRestCommissionInspectionDtoList(entry.getValue(), personFullName);
            })
            .flatMap(List::stream)
            .collect(Collectors.toList());
    }

    @Override
    public RestCommissionInspectionDto fetchById(final String commissionInspectionId) {
        final CommissionInspectionDocument commissionInspectionDocument = commissionInspectionDocumentService
            .fetchDocument(commissionInspectionId);
        final CommissionInspectionData commissionInspection = commissionInspectionDocument
            .getDocument()
            .getCommissionInspectionData();

        final String personFullName = ofNullable(commissionInspection)
            .map(CommissionInspectionData::getApplicant)
            .map(Applicant::getPersonId)
            .flatMap(personDocumentService::getPersonFullNameById)
            .orElse(null);

        return restCommissionInspectionMapper.toRestCommissionInspectionDto(
            commissionInspectionId,
            commissionInspection,
            personFullName
        );
    }

    @Override
    public ApartmentInspectionDocument fetchCurrentApartmentInspection(String commissionInspectionId) {
        final CommissionInspectionDocument commissionInspectionDocument = commissionInspectionDocumentService
            .fetchDocument(commissionInspectionId);
        final CommissionInspectionData commissionInspection = commissionInspectionDocument
            .getDocument()
            .getCommissionInspectionData();

        return apartmentInspectionService.fetchDocument(commissionInspection.getCurrentApartmentInspectionId());
    }

    @Override
    public void delete(final String commissionInspectionId) {
        commissionInspectionDocumentService.deleteDocument(commissionInspectionId, true, null);
    }

    @Override
    public RestCommissionInspectionCheckResponseDto check(final String snils, final String ssoId) {
        final CommissionInspectionCheckResponse checkResponse = commissionInspectionCheckService
            .checkPerson(snils, ssoId);
        return restCommissionInspectionMapper.toRestCommissionInspectionCheckResponseDto(checkResponse);
    }

    @Override
    public void moveDate(
        final String commissionInspectionId,
        final RestCommissionInspectionMoveDateDto restCommissionInspectionMoveDateDto
    ) {
        final LocalDateTime moveDateTime = ofNullable(restCommissionInspectionMoveDateDto.getMoveDateTime())
            .orElseThrow(MoveDateMissed::new);

        final CommissionInspectionDocument commissionInspectionDocument = commissionInspectionDocumentService
            .fetchDocument(commissionInspectionId);

        moveDate(commissionInspectionDocument, moveDateTime);
    }

    private void moveDate(
        final CommissionInspectionDocument commissionInspectionDocument, final LocalDateTime moveDateTime
    ) {
        final CommissionInspectionData commissionInspection = commissionInspectionDocument
            .getDocument()
            .getCommissionInspectionData();
        final LocalDateTime confirmedDateTime = removeSeconds(moveDateTime);
        final boolean isPrimary = isPrimaryInspection(commissionInspection);

        final ApartmentInspectionDocument apartmentInspectionDocument = apartmentInspectionService
            .fetchDocument(commissionInspection.getCurrentApartmentInspectionId());
        final ApartmentInspectionType apartmentInspection = apartmentInspectionDocument
            .getDocument()
            .getApartmentInspectionData();

        populateVisitDateTime(apartmentInspection, confirmedDateTime, isPrimary);
        commissionInspection.setConfirmedInspectionDateTime(confirmedDateTime);
        apartmentInspectionService.updateDocument(apartmentInspectionDocument);

        final CommissionInspectionFlowStatus moveDateRequestStatus = isPrimary
            ? MOVE_DATE_FIRST_VISIT_REQUEST
            : MOVE_DATE_SECOND_VISIT_REQUEST;

        final CommissionInspectionFlowStatus timeConfirmedStatus = isPrimary
            ? TIME_CONFIRMED_FIRST_VISIT
            : TIME_CONFIRMED_SECOND_VISIT;

        if (isOperatorMoveDateRequest(commissionInspection, moveDateRequestStatus)) {
            addCommissionInspectionHistoryEvent(commissionInspectionDocument, moveDateRequestStatus);

            final CommissionInspectionFlowStatus moveDateAcceptedStatus = isPrimary
                ? MOVE_DATE_FIRST_VISIT_ACCEPTED
                : MOVE_DATE_SECOND_VISIT_ACCEPTED;

            addCommissionInspectionHistoryEvent(commissionInspectionDocument, moveDateAcceptedStatus);

            addCommissionInspectionHistoryEvent(commissionInspectionDocument, timeConfirmedStatus);

            commissionInspectionElkNotificationService.sendStatusAsync(
                Arrays.asList(moveDateAcceptedStatus, timeConfirmedStatus),
                commissionInspectionDocument
            );
        } else {
            addCommissionInspectionHistoryEvent(commissionInspectionDocument, timeConfirmedStatus);

            commissionInspectionService.sendFlowStatus(commissionInspectionDocument, timeConfirmedStatus);
        }

        commissionInspection.setApplicationStatusId(timeConfirmedStatus.getId());

        commissionInspectionDocumentService.updateDocument(
            commissionInspectionDocument.getId(),
            commissionInspectionDocument,
            true,
            true,
            null
        );

        commissionInspectionService.finishBpmProcess(commissionInspectionDocument);
    }

    private static void populateVisitDateTime(
        final ApartmentInspectionType apartmentInspection,
        final LocalDateTime visitDateTime,
        final boolean isPrimary
    ) {
        if (isPrimary) {
            apartmentInspection.setFirstVisitDateTime(visitDateTime);
        } else {
            apartmentInspection.setSecondVisitDateTime(visitDateTime);
        }
    }

    private boolean isOperatorMoveDateRequest(
        final CommissionInspectionData commissionInspection,
        final CommissionInspectionFlowStatus moveDateFlowStatus
    ) {
        return ofNullable(commissionInspection.getApplicationStatusId())
            .map(CommissionInspectionFlowStatus::of)
            .filter(StreamUtils.not(moveDateFlowStatus::equals))
            .isPresent();
    }

    @Override
    public List<RestDefectDto> getAllDefects() {
        final List<DefectDto> defects = defectDao.fetchAll()
            .stream()
            .map(defect -> DefectDto.builder()
                .id(defect.getId())
                .description(defect.getDescription())
                .flatElement(defect.getFlatElement())
                .build()
            )
            .collect(Collectors.toList());

        return restCommissionInspectionMapper.toRestDefectDtoList(defects);
    }

    @Override
    public void confirmDate(
        final String commissionInspectionId,
        final RestCommissionInspectionConfirmDateDto commissionInspectionConfirmDateDto
    ) {
        final CommissionInspectionDocument commissionInspectionDocument
            = commissionInspectionDocumentService.fetchDocument(commissionInspectionId);
        final CommissionInspectionData commissionInspection = commissionInspectionDocument
            .getDocument()
            .getCommissionInspectionData();

        populateAddressDetails(commissionInspection, commissionInspectionConfirmDateDto);

        final LocalDateTime confirmedDateTime = removeSeconds(
            commissionInspectionConfirmDateDto.getConfirmedDateTime()
        );
        final boolean isPrimary = isPrimaryInspection(commissionInspection);

        final ApartmentInspectionDocument apartmentInspectionDocument =
            ofNullable(commissionInspection.getCurrentApartmentInspectionId())
                .map(apartmentInspectionService::fetchDocument)
                .map(document -> {
                    final ApartmentInspectionType apartmentInspection = document
                        .getDocument()
                        .getApartmentInspectionData();
                    populateVisitDateTime(apartmentInspection, confirmedDateTime, isPrimary);
                    apartmentInspectionService.updateDocument(document);

                    return document;
                })
                .orElseGet(() -> apartmentInspectionService.createPendingApartmentInspectionDocument(
                    commissionInspectionDocument.getId(), commissionInspection, confirmedDateTime
                ));
        commissionInspection.setCurrentApartmentInspectionId(apartmentInspectionDocument.getId());
        commissionInspection.setConfirmedInspectionDateTime(confirmedDateTime);

        final CommissionInspectionFlowStatus timeConfirmedFlowStatus = isPrimary
            ? TIME_CONFIRMED_FIRST_VISIT
            : TIME_CONFIRMED_SECOND_VISIT;

        commissionInspection.setApplicationStatusId(timeConfirmedFlowStatus.getId());
        addCommissionInspectionHistoryEvent(commissionInspectionDocument, timeConfirmedFlowStatus);

        commissionInspectionService.sendFlowStatus(commissionInspectionDocument, timeConfirmedFlowStatus);

        commissionInspectionDocumentService
            .updateDocument(commissionInspectionId, commissionInspectionDocument, true, true, null);

        commissionInspectionService.finishBpmProcess(commissionInspectionDocument);
    }

    private void populateAddressDetails(
        final CommissionInspectionData commissionInspectionData,
        final RestCommissionInspectionConfirmDateDto commissionInspectionConfirmDateDto
    ) {
        ofNullable(commissionInspectionConfirmDateDto.getAddress())
            .filter(StringUtils::hasText)
            .ifPresent(commissionInspectionData::setAddress);

        ofNullable(commissionInspectionConfirmDateDto.getUnom())
            .filter(StringUtils::hasText)
            .ifPresent(commissionInspectionData::setCcoUnom);

        ofNullable(commissionInspectionConfirmDateDto.getFlatNumber())
            .filter(StringUtils::hasText)
            .ifPresent(commissionInspectionData::setFlatNum);

        if (!StringUtils.hasText(commissionInspectionData.getAddress())
            || !StringUtils.hasText(commissionInspectionData.getFlatNum())) {
            throw new SsrException("Отсутствует адрес или номер квартиры");
        }
    }

    @Override
    public void refuseByLetterId(final String letterId, final String reason) {
        commissionInspectionDocumentService
            .findRefuseableByLetterId(letterId)
            .forEach(commissionInspectionDocument -> {
                refuse(
                    commissionInspectionDocument,
                    reason,
                    REFUSE_FLAT
                );
            });
    }

    @Override
    public void refuse(final String commissionInspectionId, final String reason, final String refusalStatusId) {
        final CommissionInspectionFlowStatus refusalStatus = CommissionInspectionFlowStatus.of(refusalStatusId);

        final CommissionInspectionDocument commissionInspectionDocument = commissionInspectionDocumentService
            .fetchDocument(commissionInspectionId);

        refuse(commissionInspectionDocument, reason, refusalStatus);
    }

    private void refuse(
        final CommissionInspectionDocument commissionInspectionDocument,
        final String reason,
        final CommissionInspectionFlowStatus refusalStatus
    ) {
        final CommissionInspectionData commissionInspection = commissionInspectionDocument
            .getDocument()
            .getCommissionInspectionData();

        ofNullable(commissionInspection.getCurrentApartmentInspectionId())
            .ifPresent(apartmentInspectionId ->
                apartmentInspectionService
                    .processCommissionInspectionRefusal(apartmentInspectionId, refusalStatus == REFUSE_FLAT)
            );

        commissionInspection.setConfirmedInspectionDateTime(null);
        commissionInspection.setApplicationStatusId(refusalStatus.getId());
        commissionInspection.setCompletionReason(reason);
        commissionInspection.setCompletionDateTime(LocalDateTime.now());

        addCommissionInspectionHistoryEvent(commissionInspectionDocument, refusalStatus);

        commissionInspectionService.sendFlowStatus(commissionInspectionDocument, refusalStatus);

        commissionInspectionDocumentService
            .updateDocument(commissionInspectionDocument.getId(), commissionInspectionDocument, true, true, null);

        commissionInspectionService.finishBpmProcess(commissionInspectionDocument);
    }

    @Override
    public void withdraw(final String commissionInspectionId, final String reason) {
        commissionInspectionService.withdraw(commissionInspectionId, reason);
    }

    @Override
    public void deleteAll() {
        commissionInspectionDocumentService.fetchDocumentsPage(0, 1000)
            .forEach(document ->
                commissionInspectionDocumentService.deleteDocument(document.getId(), true, null)
            );
    }

    @Override
    public List<RestCommissionInspectionVisitsDto> getVisits(final String unom, final LocalDate date) {
        final LocalDateTime startDateTime = date.atStartOfDay();
        final LocalDateTime endDateTime = date.atStartOfDay().plusDays(1);

        final List<CommissionInspectionProjection> commissionInspectionProjections =
            commissionInspectionDocumentService.findAllByCcoUnomAndDateBetweenAndTimeConfirmedStatus(
                unom, startDateTime, endDateTime
            );

        return commissionInspectionProjections.stream()
            .map(RestCommissionInspectionVisitsDto::of)
            .collect(Collectors.toList());
    }

    @Override
    public void actualizeTaskCandidates() {
        log.info("Start actualizeTaskCandidates for all commission inspections");

        final long documentsCount = commissionInspectionDocumentService.countDocuments();

        commissionInspectionDocumentService
            .fetchDocumentsPage(0, ((int) documentsCount))
            .forEach(commissionInspectionDocument -> {
                try {
                    log.info(
                        "Start actualizeTaskCandidates for commission inspection {}",
                        commissionInspectionDocument.getId()
                    );

                    commissionInspectionService.actualizeTaskCandidates(commissionInspectionDocument);

                    log.info(
                        "Finish actualizeTaskCandidates for commission inspection {}",
                        commissionInspectionDocument.getId()
                    );
                } catch (Exception e) {
                    log.warn(
                        "Unable to actualizeTaskCandidates for commission inspection {}",
                        commissionInspectionDocument.getId()
                    );
                }
            });

        log.info("Finish actualizeTaskCandidates for all commission inspections");
    }

    @Override
    public void actualizeTaskCandidates(final String commissionInspectionId) {
        final CommissionInspectionDocument commissionInspectionDocument = commissionInspectionDocumentService
            .fetchDocument(commissionInspectionId);

        commissionInspectionService.actualizeTaskCandidates(commissionInspectionDocument);
    }

    @Override
    public void defectsProlongation(final String commissionInspectionId) {
        if (commissionInspectionConfig.isCommissionInspectionModernizationEnabled()) {
            commissionInspectionService.defectsProlongation(commissionInspectionId);
        }
    }

    private static LocalDateTime removeSeconds(final LocalDateTime localDateTime) {
        return ofNullable(localDateTime)
            .map(dateTime -> dateTime.withSecond(0))
            .orElse(null);
    }

}
