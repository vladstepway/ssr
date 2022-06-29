package ru.croc.ugd.ssr.service.commissioninspection;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static ru.croc.ugd.ssr.dto.commissioninspection.CommissionInspectionFlowStatus.DEFECTS_CHANGE_ACCEPTED;
import static ru.croc.ugd.ssr.dto.commissioninspection.CommissionInspectionFlowStatus.DEFECTS_CHANGE_REJECTED;
import static ru.croc.ugd.ssr.dto.commissioninspection.CommissionInspectionFlowStatus.DEFECTS_ELIMINATION_DATE_CALCULATED;
import static ru.croc.ugd.ssr.dto.commissioninspection.CommissionInspectionFlowStatus.DEFECTS_FIXED;
import static ru.croc.ugd.ssr.dto.commissioninspection.CommissionInspectionFlowStatus.DEFECTS_FOUND;
import static ru.croc.ugd.ssr.dto.commissioninspection.CommissionInspectionFlowStatus.DEFECTS_NOT_FIXED;
import static ru.croc.ugd.ssr.dto.commissioninspection.CommissionInspectionFlowStatus.FINISHED_DEFECTS_FIXED;
import static ru.croc.ugd.ssr.dto.commissioninspection.CommissionInspectionFlowStatus.FINISHED_NO_CALL;
import static ru.croc.ugd.ssr.dto.commissioninspection.CommissionInspectionFlowStatus.FINISHED_NO_DEFECTS;
import static ru.croc.ugd.ssr.dto.commissioninspection.CommissionInspectionFlowStatus.MOVE_DATE_FIRST_VISIT_ACCEPTED;
import static ru.croc.ugd.ssr.dto.commissioninspection.CommissionInspectionFlowStatus.MOVE_DATE_FIRST_VISIT_REJECTED;
import static ru.croc.ugd.ssr.dto.commissioninspection.CommissionInspectionFlowStatus.MOVE_DATE_FIRST_VISIT_REQUEST;
import static ru.croc.ugd.ssr.dto.commissioninspection.CommissionInspectionFlowStatus.MOVE_DATE_SECOND_VISIT_ACCEPTED;
import static ru.croc.ugd.ssr.dto.commissioninspection.CommissionInspectionFlowStatus.MOVE_DATE_SECOND_VISIT_REJECTED;
import static ru.croc.ugd.ssr.dto.commissioninspection.CommissionInspectionFlowStatus.MOVE_DATE_SECOND_VISIT_REQUEST;
import static ru.croc.ugd.ssr.dto.commissioninspection.CommissionInspectionFlowStatus.PROLONGATION;
import static ru.croc.ugd.ssr.dto.commissioninspection.CommissionInspectionFlowStatus.REFUSE_NO_CALL;
import static ru.croc.ugd.ssr.dto.commissioninspection.CommissionInspectionFlowStatus.REGISTERED;
import static ru.croc.ugd.ssr.dto.commissioninspection.CommissionInspectionFlowStatus.RESUMED_DEFECTS_FIXED;
import static ru.croc.ugd.ssr.dto.commissioninspection.CommissionInspectionFlowStatus.RESUMED_DEFECTS_NOT_FIXED;
import static ru.croc.ugd.ssr.dto.commissioninspection.CommissionInspectionFlowStatus.RESUMED_NO_DEFECTS;
import static ru.croc.ugd.ssr.dto.commissioninspection.CommissionInspectionFlowStatus.TECHNICAL_CRASH_ON_CHANGE;
import static ru.croc.ugd.ssr.dto.commissioninspection.CommissionInspectionFlowStatus.TECHNICAL_CRASH_REGISTRATION;
import static ru.croc.ugd.ssr.dto.commissioninspection.CommissionInspectionFlowStatus.TECHNICAL_CRASH_WITHDRAW;
import static ru.croc.ugd.ssr.dto.commissioninspection.CommissionInspectionFlowStatus.WITHDRAW_ACCEPTED;
import static ru.croc.ugd.ssr.dto.commissioninspection.CommissionInspectionFlowStatus.WITHDRAW_REJECTED;
import static ru.croc.ugd.ssr.enums.CommissionInspectionOrganisationType.HOT_LINE;
import static ru.croc.ugd.ssr.enums.CommissionInspectionOrganisationType.PREFECTURE;
import static ru.croc.ugd.ssr.enums.CommissionInspectionOrganisationType.RENOVATION_FUND;
import static ru.croc.ugd.ssr.service.cip.CipService.ACTIVE_CIP_STATUS;
import static ru.croc.ugd.ssr.service.commissioninspection.CommissionInspectionUtils.addCommissionInspectionHistoryEvent;
import static ru.croc.ugd.ssr.service.commissioninspection.CommissionInspectionUtils.isPrimaryInspection;
import static ru.croc.ugd.ssr.service.commissioninspection.CommissionInspectionUtils.retrieveLastHistoryEventByStatus;
import static ru.croc.ugd.ssr.service.validator.impl.person.WarrantyPeriodExpired.WARRANTY_YEARS;
import static ru.croc.ugd.ssr.types.SsrDocumentTypes.COMMISSION_INSPECTION;
import static ru.croc.ugd.ssr.utils.PersonUtils.getLastAcceptedAgreementOfferLetter;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javers.common.collections.Lists;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.croc.ugd.ssr.ApartmentInspection;
import ru.croc.ugd.ssr.ApartmentInspectionType;
import ru.croc.ugd.ssr.Cip;
import ru.croc.ugd.ssr.CipType;
import ru.croc.ugd.ssr.DelayReasonData;
import ru.croc.ugd.ssr.OrganizationInformation;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.commission.CommissionInspection;
import ru.croc.ugd.ssr.commission.CommissionInspectionData;
import ru.croc.ugd.ssr.commission.CommissionInspectionHistoryEvent;
import ru.croc.ugd.ssr.commission.CommissionInspectionHistoryEvents;
import ru.croc.ugd.ssr.commission.DefectList;
import ru.croc.ugd.ssr.commission.DefectType;
import ru.croc.ugd.ssr.config.ApartmentInspectionProperties;
import ru.croc.ugd.ssr.dto.commissioninspection.CommissionInspectionFlowStatus;
import ru.croc.ugd.ssr.exception.SsrException;
import ru.croc.ugd.ssr.exception.commissioninspection.MoveDateRequestRejected;
import ru.croc.ugd.ssr.integration.service.notification.CommissionInspectionElkNotificationService;
import ru.croc.ugd.ssr.integration.util.MessageUtils;
import ru.croc.ugd.ssr.model.ApartmentInspectionDocument;
import ru.croc.ugd.ssr.model.CipDocument;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.api.chess.CcoSolrResponse;
import ru.croc.ugd.ssr.model.commissioninspection.CommissionInspectionDocument;
import ru.croc.ugd.ssr.model.commissionorganisation.CommissionInspectionOrganisationDocument;
import ru.croc.ugd.ssr.model.integration.etpmv.ArrayOfServiceDocument;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateData;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateMessage;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateStatusData;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateStatusMessage;
import ru.croc.ugd.ssr.model.integration.etpmv.RequestServiceForSign;
import ru.croc.ugd.ssr.model.integration.etpmv.ServiceDocument;
import ru.croc.ugd.ssr.mq.listener.InvalidCoordinateMessage;
import ru.croc.ugd.ssr.mq.listener.commissioninspection.EtpCommissionInspectionMapper;
import ru.croc.ugd.ssr.service.ApartmentInspectionService;
import ru.croc.ugd.ssr.service.CapitalConstructionObjectService;
import ru.croc.ugd.ssr.service.bpm.BpmService;
import ru.croc.ugd.ssr.service.cip.CipService;
import ru.croc.ugd.ssr.service.document.CommissionInspectionDocumentService;
import ru.croc.ugd.ssr.service.document.CommissionInspectionOrganisationDocumentService;
import ru.croc.ugd.ssr.solr.SsrDocumentReindexService;
import ru.mos.gu.service._0834.EtpDefectList;
import ru.mos.gu.service._0834.EtpDefectType;
import ru.mos.gu.service._0834.ServiceProperties;
import ru.mos.gu.service._0834.ServicePropertiesStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * DefaultCommissionInspectionService.
 */
@Slf4j
@Service
@AllArgsConstructor
public class DefaultCommissionInspectionService implements CommissionInspectionService {

    private static final String PROCESS_KEY_CONFIRM_INSPECTION_DATE = "ugdssrCommissionInspection_confirmDate";
    private static final String PROCESS_DOCUMENT_KEY_CONFIRM_DATE_TIME_TASK_NAME = "ConfirmDateTimeTaskName";

    private static final String COMMISSION_INSPECTION_READ_LDAP_GROUP = "UGD_SSR_COMMISSION_INSPECTION_READ";
    private static final String PERSON_RENOVATION_FOND_LDAP_GROUP = "UGD_SSR_PERSON_RENOVATION_FOND";

    private static final String FIRST_VISIT_CONFIRM_DATE_TIME_TASK_NAME = "Назначение даты комиссионного осмотра";
    private static final String SECOND_VISIT_CONFIRM_DATE_TIME_TASK_NAME =
        "Назначение даты повторного комиссионного осмотра";

    private static final String FIXED_BY_PERSON_REASON_CODE = "5";

    private static final int MAX_ALLOWED_MOVE_DATE_NUMBER = 2;

    private final CommissionInspectionDocumentService commissionInspectionDocumentService;
    private final ApartmentInspectionService apartmentInspectionService;
    private final ApartmentInspectionProperties apartmentInspectionProperties;
    private final CommissionInspectionElkNotificationService commissionInspectionElkNotificationService;
    private final EtpCommissionInspectionMapper etpCommissionInspectionMapper;
    private final CapitalConstructionObjectService capitalConstructionObjectService;
    private final BpmService bpmService;
    private final MessageUtils messageUtils;
    private final CommissionInspectionCheckService commissionInspectionCheckService;
    private final CommissionInspectionOrganisationDocumentService organisationDocumentService;
    private final CipService cipService;
    private final SsrDocumentReindexService ssrDocumentReindexService;

    @Override
    public void finishBpmProcess(final CommissionInspectionDocument commissionInspectionDocument) {
        final String processInstanceId = retrieveProcessInstanceId(commissionInspectionDocument);
        if (StringUtils.hasText(processInstanceId)) {
            try {
                bpmService.deleteProcessInstance(processInstanceId);
                ssrDocumentReindexService.reindexDocumentsInSolr(
                    COMMISSION_INSPECTION, Collections.singletonList(commissionInspectionDocument)
                );
            } catch (Exception e) {
                log.warn("Unable to finish bpm process for commission inspection due to: {}", e.getMessage(), e);
            }
        }
    }

    private static String retrieveProcessInstanceId(final CommissionInspectionDocument commissionInspectionDocument) {
        return of(commissionInspectionDocument.getDocument())
            .map(CommissionInspection::getCommissionInspectionData)
            .map(CommissionInspectionData::getProcessInstanceId)
            .orElse(null);
    }

    @Override
    public void processRegistration(final CoordinateMessage coordinateMessage) {
        String commissionInspectionId = null;
        try {
            validateCoordinateMessage(coordinateMessage);
            final PersonDocument person = commissionInspectionCheckService.retrievePerson(coordinateMessage);

            final CommissionInspectionDocument commissionInspectionDocument = etpCommissionInspectionMapper
                .toCommissionInspectionDocument(
                    coordinateMessage,
                    person,
                    capitalConstructionObjectService::getCcoAddressByUnom
                );

            if (isDuplicate(commissionInspectionDocument)) {
                log.warn("Commission inspection has been already registered: {}", coordinateMessage);
                return;
            }

            if (!commissionInspectionCheckService.canCreateApplication(commissionInspectionDocument, person)) {
                log.warn("Failed to pass validation to create commission inspection: {}", coordinateMessage);
                commissionInspectionElkNotificationService.sendStatusAsync(
                    TECHNICAL_CRASH_REGISTRATION,
                    messageUtils.retrieveEno(coordinateMessage).orElse(null)
                );
                return;
            }

            assignOrganisation(commissionInspectionDocument, person);

            commissionInspectionDocumentService.createDocument(commissionInspectionDocument, true, null);
            commissionInspectionId = commissionInspectionDocument.getId();

            createConfirmDateTask(commissionInspectionDocument)
                .ifPresent(processInstanceId ->
                    populateProcessIdAndStatus(processInstanceId, REGISTERED, commissionInspectionDocument)
                );
            addCommissionInspectionHistoryEvent(commissionInspectionDocument, REGISTERED);

            sendFlowStatus(commissionInspectionDocument, REGISTERED);

            commissionInspectionDocumentService.updateDocument(
                commissionInspectionDocument.getId(), commissionInspectionDocument, true, true, null
            );
        } catch (Exception e) {
            log.warn("Unable to save commissionInspectionDocument due to: {}", e.getMessage(), e);
            commissionInspectionElkNotificationService.sendStatusAsync(
                TECHNICAL_CRASH_REGISTRATION,
                messageUtils.retrieveEno(coordinateMessage).orElse(null)
            );

            if (Objects.nonNull(commissionInspectionId)) {
                commissionInspectionDocumentService.deleteDocument(commissionInspectionId, true, null);
            }
        }
    }

    private void assignOrganisation(
        final CommissionInspectionDocument commissionInspectionDocument, final PersonDocument person
    ) {
        final CommissionInspectionData commissionInspection = commissionInspectionDocument
            .getDocument()
            .getCommissionInspectionData();

        final CipDocument cipDocument = getLastAcceptedAgreementOfferLetter(person)
            .map(PersonType.OfferLetters.OfferLetter::getIdCIP)
            .map(cipService::fetchDocument)
            .orElse(null);

        if (commissionInspection.isKpUgs()) {
            assignCipOrPrefecture(commissionInspection, cipDocument);
        } else {
            assignRenovationFundOrPrefecture(commissionInspection, cipDocument);
        }
    }

    private void assignCipOrPrefecture(
        final CommissionInspectionData commissionInspection,
        final CipDocument cipDocument
    ) {
        if (isCipOpen(cipDocument)) {
            ofNullable(cipDocument)
                .map(CipDocument::getId)
                .ifPresent(commissionInspection::setCipId);
        } else {
            retrieveArea(commissionInspection.getCcoUnom())
                .flatMap(this::retrievePrefectureOrHotLine)
                .ifPresent(commissionInspection::setOrganisationId);
        }
    }

    private void assignRenovationFundOrPrefecture(
        final CommissionInspectionData commissionInspection,
        final CipDocument cipDocument
    ) {
        if ("заселение".equalsIgnoreCase(commissionInspection.getFlatStatus())) {
            retrieveArea(commissionInspection.getCcoUnom())
                .flatMap(this::retrieveRenovationFund)
                .ifPresent(commissionInspection::setOrganisationId);
        } else {
            ofNullable(commissionInspection.getCcoUnom())
                .map(capitalConstructionObjectService::getCcoSolrResponseByUnom)
                .ifPresent(ccoSolrResponse ->
                    assignWarrantyPeriodOrganisation(commissionInspection, cipDocument, ccoSolrResponse)
                );
        }
    }

    private void assignWarrantyPeriodOrganisation(
        final CommissionInspectionData commissionInspection,
        final CipDocument cipDocument,
        final CcoSolrResponse ccoSolrResponse
    ) {
        final LocalDate completionDate = ccoSolrResponse.getCompletionDate();
        final String ccoArea = ccoSolrResponse.getArea();
        if (isNull(completionDate) || completionDate.plusYears(WARRANTY_YEARS).isAfter(LocalDate.now())) {
            ofNullable(retrieveRenovationFund(ccoArea))
                .orElseGet(() -> retrievePrefectureOrHotLine(ccoArea))
                .ifPresent(commissionInspection::setOrganisationId);
        } else {
            assignCipOrPrefecture(commissionInspection, cipDocument);
        }
    }

    private Optional<String> retrieveRenovationFund(final String ccoArea) {
        return ofNullable(ccoArea)
            .flatMap(area -> organisationDocumentService
                .findByAreaAndType(null, RENOVATION_FUND.getTypeValue())
            )
            .map(CommissionInspectionOrganisationDocument::getId);
    }

    private Optional<String> retrievePrefectureOrHotLine(final String ccoArea) {
        return ofNullable(ccoArea)
            .map(area -> organisationDocumentService
                .findByAreaAndType(area, PREFECTURE.getTypeValue())
            )
            .orElseGet(() -> organisationDocumentService
                .findByAreaAndType(null, HOT_LINE.getTypeValue())
            )
            .map(CommissionInspectionOrganisationDocument::getId);
    }

    private Optional<String> retrieveArea(final String ccoUnom) {
        return ofNullable(ccoUnom)
            .map(capitalConstructionObjectService::getCcoSolrResponseByUnom)
            .map(CcoSolrResponse::getArea);
    }

    private static boolean isCipOpen(final CipDocument cipDocument) {
        return ofNullable(cipDocument)
            .map(CipDocument::getDocument)
            .map(Cip::getCipData)
            .filter(DefaultCommissionInspectionService::isCipOpen)
            .filter(DefaultCommissionInspectionService::isCipActive)
            .isPresent();
    }

    private static boolean isCipOpen(final CipType cip) {
        return isNull(cip.getCipDateEnd()) || cip.getCipDateEnd().isAfter(LocalDate.now());
    }

    private static boolean isCipActive(final CipType cip) {
        return Objects.equals(ACTIVE_CIP_STATUS, cip.getCipStatus());
    }

    private boolean isDuplicate(final CommissionInspectionDocument commissionInspectionDocument) {
        return of(commissionInspectionDocument.getDocument())
            .map(CommissionInspection::getCommissionInspectionData)
            .map(CommissionInspectionData::getEno)
            .flatMap(commissionInspectionDocumentService::findByEno)
            .isPresent();
    }

    private static void validateCoordinateMessage(final CoordinateMessage coordinateMessage) {
        final ServiceProperties serviceProperties = ofNullable(coordinateMessage)
            .map(CoordinateMessage::getCoordinateDataMessage)
            .map(CoordinateData::getSignService)
            .map(RequestServiceForSign::getCustomAttributes)
            .map(RequestServiceForSign.CustomAttributes::getAny)
            .filter(customAttributes -> customAttributes instanceof ServiceProperties)
            .map(ServiceProperties.class::cast)
            .orElseThrow(InvalidCoordinateMessage::new);

        if (isNull(serviceProperties.getLetterId())
            && (isNull(serviceProperties.getCcoUnom()) || isNull(serviceProperties.getFlatNum()))
        ) {
            throw new SsrException("Either letter or address details should be in coordinateMessage");
        }
    }

    @Override
    public void withdraw(final String commissionInspectionId, final String reason) {
        final CommissionInspectionDocument commissionInspectionDocument =
            commissionInspectionDocumentService.fetchDocument(commissionInspectionId);

        withdraw(commissionInspectionDocument, reason);
    }

    @Override
    public void withdraw(final CoordinateStatusMessage coordinateStatus) {
        final String eno = messageUtils.retrieveEno(coordinateStatus).orElse(null);
        final Optional<CommissionInspectionDocument> optionalCommissionInspectionDocument =
            commissionInspectionDocumentService.findByEno(eno);

        if (optionalCommissionInspectionDocument.isPresent()) {
            final CommissionInspectionDocument document = optionalCommissionInspectionDocument.get();
            try {
                withdraw(document, null);
            } catch (SsrException exception) {
                sendFlowStatus(document, WITHDRAW_REJECTED);
            }
        } else {
            log.debug("Unable to find CommissionInspectionDocument by eno {}", eno);
            commissionInspectionElkNotificationService.sendStatusAsync(TECHNICAL_CRASH_WITHDRAW, eno);
        }
    }

    private void withdraw(final CommissionInspectionDocument commissionInspectionDocument, final String reason) {
        final CommissionInspectionData commissionInspection = commissionInspectionDocument
            .getDocument()
            .getCommissionInspectionData();

        validateRequestIsBeforeInspection(commissionInspection);

        commissionInspection.setApplicationStatusId(WITHDRAW_ACCEPTED.getId());
        commissionInspection.setCompletionDateTime(LocalDateTime.now());
        commissionInspection.setCompletionReason(reason);
        commissionInspection.setConfirmedInspectionDateTime(null);

        addCommissionInspectionHistoryEvent(commissionInspectionDocument, WITHDRAW_ACCEPTED);

        sendFlowStatus(commissionInspectionDocument, WITHDRAW_ACCEPTED);

        commissionInspectionDocumentService.updateDocument(
            commissionInspectionDocument.getId(), commissionInspectionDocument, true, true, null
        );

        finishBpmProcess(commissionInspectionDocument);
    }

    @Override
    public void processChangeDefectsRequest(final CoordinateStatusMessage coordinateStatus) {
        final CommissionInspectionDocument commissionInspectionDocument =
            fetchCommissionInspectionForChange(coordinateStatus);

        final List<DefectType> newDefects = etpCommissionInspectionMapper
            .toDefectTypeList(retrieveEtpDefects(coordinateStatus));
        if (newDefects.isEmpty()) {
            log.debug(
                "Empty defect list received for commission inspection application {}",
                commissionInspectionDocument.getId()
            );
            return;
        }

        try {
            processChangeDefectsRequest(newDefects, commissionInspectionDocument);
        } catch (SsrException exception) {
            sendFlowStatus(commissionInspectionDocument, DEFECTS_CHANGE_REJECTED);
        }
    }

    private void processChangeDefectsRequest(
        final List<DefectType> newDefects, final CommissionInspectionDocument commissionInspectionDocument
    ) {
        final CommissionInspectionData commissionInspection = commissionInspectionDocument
            .getDocument()
            .getCommissionInspectionData();

        validateRequestIsBeforeInspection(commissionInspection);

        final DefectList defectList = ofNullable(commissionInspection.getDefects())
            .orElseGet(DefectList::new);

        final List<DefectType> defectsToAdd = retrieveDefectsToAdd(newDefects, defectList.getDefect());
        defectList.getDefect().addAll(defectsToAdd);
        commissionInspection.setDefects(defectList);

        addCommissionInspectionHistoryEvent(commissionInspectionDocument, DEFECTS_CHANGE_ACCEPTED);

        sendFlowStatus(commissionInspectionDocument, DEFECTS_CHANGE_ACCEPTED);

        commissionInspectionDocumentService
            .updateDocument(commissionInspectionDocument.getId(), commissionInspectionDocument, true, false, null);
    }

    private List<DefectType> retrieveDefectsToAdd(
        final List<DefectType> newDefects, List<DefectType> existingDefects
    ) {
        final List<String> concatenatedDefects = existingDefects.stream()
            .map(defect -> defect.getDescription() + defect.getFlatElement())
            .collect(Collectors.toList());

        return newDefects.stream()
            .filter(defect -> {
                final String concatenatedDefect = defect.getDescription() + defect.getFlatElement();
                return !concatenatedDefects.contains(concatenatedDefect);
            })
            .collect(Collectors.toList());
    }

    private List<EtpDefectType> retrieveEtpDefects(final CoordinateStatusMessage coordinateStatus) {
        return ofNullable(coordinateStatus.getCoordinateStatusDataMessage())
            .map(CoordinateStatusData::getDocuments)
            .map(ArrayOfServiceDocument::getServiceDocument)
            .map(List::stream)
            .orElse(Stream.empty())
            .findFirst()
            .map(ServiceDocument::getCustomAttributes)
            .map(ServiceDocument.CustomAttributes::getAny)
            .filter(any -> any instanceof ServicePropertiesStatus)
            .map(ServicePropertiesStatus.class::cast)
            .map(ServicePropertiesStatus::getDefects)
            .map(EtpDefectList::getDefect)
            .orElse(Collections.emptyList());
    }

    @Override
    @Transactional
    public void processMoveDateRequest(final CoordinateStatusMessage coordinateStatus) {
        final CommissionInspectionDocument commissionInspectionDocument =
            fetchCommissionInspectionForChange(coordinateStatus);
        final CommissionInspectionData commissionInspection = commissionInspectionDocument.getDocument()
            .getCommissionInspectionData();

        final boolean isPrimary = isPrimaryInspection(commissionInspection);
        final CommissionInspectionFlowStatus moveDateFlowStatus = isPrimary
            ? MOVE_DATE_FIRST_VISIT_REQUEST
            : MOVE_DATE_SECOND_VISIT_REQUEST;

        validateOnConfirmationPeriodExpiration(commissionInspectionDocument, isPrimary);
        validateOnMoveDateRequestNumber(
            commissionInspectionDocument,
            commissionInspection.getCurrentApartmentInspectionId(),
            isPrimary,
            moveDateFlowStatus
        );

        createConfirmDateTask(commissionInspectionDocument)
            .ifPresent(processInstanceId ->
                populateProcessIdAndStatus(processInstanceId, moveDateFlowStatus, commissionInspectionDocument)
            );
        addCommissionInspectionHistoryEvent(commissionInspectionDocument, moveDateFlowStatus);

        commissionInspection.setConfirmedInspectionDateTime(null);
        sendMoveDateRequestAcceptedStatus(commissionInspectionDocument, isPrimary);

        commissionInspectionDocumentService.updateDocument(
            commissionInspectionDocument.getId(), commissionInspectionDocument, true, true, null
        );
    }

    public void sendMoveDateRequestAcceptedStatus(
        final CommissionInspectionDocument commissionInspectionDocument, final boolean isPrimaryInspection
    ) {
        final CommissionInspectionFlowStatus flowStatus = isPrimaryInspection
            ? MOVE_DATE_FIRST_VISIT_ACCEPTED
            : MOVE_DATE_SECOND_VISIT_ACCEPTED;

        addCommissionInspectionHistoryEvent(commissionInspectionDocument, flowStatus);
        sendFlowStatus(commissionInspectionDocument, flowStatus);
    }

    @Override
    public void sendFlowStatus(
        final CommissionInspectionDocument commissionInspectionDocument,
        final CommissionInspectionFlowStatus flowStatus
    ) {
        commissionInspectionElkNotificationService.sendStatusAsync(flowStatus, commissionInspectionDocument);
    }

    @Override
    public void sendFlowStatusWithHistoryUpdate(
        final CommissionInspectionDocument commissionInspectionDocument,
        final CommissionInspectionFlowStatus flowStatus
    ) {
        commissionInspectionElkNotificationService.sendStatusAsync(flowStatus, commissionInspectionDocument);
        addCommissionInspectionHistoryEvent(commissionInspectionDocument, flowStatus);

        commissionInspectionDocumentService.updateDocument(
            commissionInspectionDocument.getId(), commissionInspectionDocument, true, true, null
        );
    }

    @Override
    @Transactional
    public void processDefectsFixed(final String commissionInspectionId) {
        final CommissionInspectionDocument commissionInspectionDocument = commissionInspectionDocumentService
            .fetchDocument(commissionInspectionId);

        addCommissionInspectionHistoryEvent(commissionInspectionDocument, DEFECTS_FIXED);
        createConfirmDateTask(commissionInspectionDocument)
            .ifPresent(processInstanceId ->
                populateProcessIdAndStatus(processInstanceId, DEFECTS_FIXED, commissionInspectionDocument)
            );

        sendFlowStatus(commissionInspectionDocument, DEFECTS_FIXED);

        commissionInspectionDocumentService.updateDocument(
            commissionInspectionDocument.getId(), commissionInspectionDocument, true, true, null
        );
    }

    @Override
    public void processDefectsFound(final String commissionInspectionId) {
        final CommissionInspectionDocument commissionInspectionDocument = commissionInspectionDocumentService
            .fetchDocument(commissionInspectionId);
        final CommissionInspectionData commissionInspection = commissionInspectionDocument
            .getDocument()
            .getCommissionInspectionData();

        commissionInspection.setApplicationStatusId(DEFECTS_FOUND.getId());
        addCommissionInspectionHistoryEvent(commissionInspectionDocument, DEFECTS_FOUND);

        sendFlowStatus(commissionInspectionDocument, DEFECTS_FOUND);

        commissionInspectionDocumentService
            .updateDocument(commissionInspectionDocument.getId(), commissionInspectionDocument, true, true, null);
    }

    @Override
    public void processDefectsNotFound(final String commissionInspectionId) {
        final CommissionInspectionDocument commissionInspectionDocument = commissionInspectionDocumentService
            .fetchDocument(commissionInspectionId);
        final CommissionInspectionData commissionInspection = commissionInspectionDocument
            .getDocument()
            .getCommissionInspectionData();

        addCommissionInspectionHistoryEvent(commissionInspectionDocument, RESUMED_NO_DEFECTS);

        commissionInspection.setApplicationStatusId(FINISHED_NO_DEFECTS.getId());
        commissionInspection.setCompletionDateTime(LocalDateTime.now());
        addCommissionInspectionHistoryEvent(commissionInspectionDocument, FINISHED_NO_DEFECTS);

        commissionInspectionElkNotificationService
            .sendStatusAsync(Arrays.asList(RESUMED_NO_DEFECTS, FINISHED_NO_DEFECTS), commissionInspectionDocument);

        commissionInspectionDocumentService
            .updateDocument(commissionInspectionDocument.getId(), commissionInspectionDocument, true, true, null);
    }

    @Override
    public void processActClosure(
        final String commissionInspectionId,
        final ApartmentInspectionType apartmentInspection
    ) {
        final CommissionInspectionDocument commissionInspectionDocument = commissionInspectionDocumentService
            .fetchDocument(commissionInspectionId);
        final CommissionInspectionData commissionInspection = commissionInspectionDocument
            .getDocument()
            .getCommissionInspectionData();

        final boolean hasConsent = apartmentInspection.isHasConsent();
        if (hasConsent) {
            addCommissionInspectionHistoryEvent(commissionInspectionDocument, RESUMED_DEFECTS_FIXED);

            commissionInspection.setCompletionDateTime(LocalDateTime.now());
            commissionInspection.setApplicationStatusId(FINISHED_DEFECTS_FIXED.getId());
            addCommissionInspectionHistoryEvent(commissionInspectionDocument, FINISHED_DEFECTS_FIXED);

            commissionInspectionElkNotificationService.sendStatusAsync(
                Arrays.asList(RESUMED_DEFECTS_FIXED, FINISHED_DEFECTS_FIXED),
                commissionInspectionDocument
            );
        } else {
            addCommissionInspectionHistoryEvent(commissionInspectionDocument, RESUMED_DEFECTS_NOT_FIXED);

            commissionInspection.setApplicationStatusId(DEFECTS_NOT_FIXED.getId());
            addCommissionInspectionHistoryEvent(commissionInspectionDocument, DEFECTS_NOT_FIXED);

            try {
                final CompletableFuture<Void> completableFuture = commissionInspectionElkNotificationService
                    .sendStatusAsync(
                        Arrays.asList(RESUMED_DEFECTS_NOT_FIXED, DEFECTS_NOT_FIXED),
                        commissionInspectionDocument
                    );

                completableFuture.get();
            } catch (Exception e) {
                throw new SsrException(e.getMessage());
            }

            final ApartmentInspectionDocument newApartmentInspection = apartmentInspectionService
                .createDocumentFromPrevious(apartmentInspection);
            commissionInspection.setCurrentApartmentInspectionId(newApartmentInspection.getId());
        }

        commissionInspectionDocumentService
            .updateDocument(commissionInspectionDocument.getId(), commissionInspectionDocument, true, true, null);
    }

    @Override
    public void processActClosureWithoutConsent(
        final String commissionInspectionId, final String completionReasonCode, final String completionReason
    ) {
        final CommissionInspectionDocument commissionInspectionDocument = commissionInspectionDocumentService
            .fetchDocument(commissionInspectionId);
        final CommissionInspectionData commissionInspection = commissionInspectionDocument
            .getDocument()
            .getCommissionInspectionData();

        commissionInspection.setCompletionDateTime(LocalDateTime.now());
        commissionInspection.setCompletionReasonCode(completionReasonCode);
        commissionInspection.setCompletionReason(completionReason);

        final CommissionInspectionFlowStatus flowStatus = completionReasonCode.equals(FIXED_BY_PERSON_REASON_CODE)
            ? FINISHED_DEFECTS_FIXED
            : FINISHED_NO_CALL;

        commissionInspection.setApplicationStatusId(flowStatus.getId());
        sendFlowStatus(commissionInspectionDocument, flowStatus);
        addCommissionInspectionHistoryEvent(commissionInspectionDocument, flowStatus);

        commissionInspectionDocumentService
            .updateDocument(commissionInspectionDocument.getId(), commissionInspectionDocument, true, true, null);
    }

    @Override
    public void processNoCall(
        final CommissionInspectionDocument commissionInspectionDocument,
        final CommissionInspectionFlowStatus noCallStatus
    ) {
        if (noCallStatus != REFUSE_NO_CALL && noCallStatus != FINISHED_NO_CALL) {
            return;
        }

        final CommissionInspectionData commissionInspection = commissionInspectionDocument
            .getDocument()
            .getCommissionInspectionData();

        commissionInspection.setCompletionDateTime(LocalDateTime.now());
        commissionInspection.setApplicationStatusId(noCallStatus.getId());
        sendFlowStatus(commissionInspectionDocument, noCallStatus);
        addCommissionInspectionHistoryEvent(commissionInspectionDocument, noCallStatus);

        commissionInspectionDocumentService.updateDocument(
            commissionInspectionDocument.getId(), commissionInspectionDocument, true, true, null
        );

        finishBpmProcess(commissionInspectionDocument);
    }

    @Override
    public void actualizeTaskCandidates(final CommissionInspectionDocument commissionInspectionDocument) {
        final CommissionInspectionData commissionInspection = commissionInspectionDocument
            .getDocument()
            .getCommissionInspectionData();

        final boolean isRenovationFundTask = isRenovationFundTask(commissionInspection);

        ofNullable(commissionInspection.getProcessInstanceId())
            .ifPresent(processInstanceId -> bpmService
                .actualizeTaskCandidates(processInstanceId,
                    isRenovationFundTask ? PERSON_RENOVATION_FOND_LDAP_GROUP : COMMISSION_INSPECTION_READ_LDAP_GROUP)
            );
    }

    @Override
    public void defectsProlongation(final String commissionInspectionId) {
        final CommissionInspectionDocument commissionInspectionDocument = commissionInspectionDocumentService
            .fetchDocument(commissionInspectionId);

        final long delayReasonCount = ofNullable(commissionInspectionDocument.getDocument()
            .getCommissionInspectionData().getCurrentApartmentInspectionId())
            .map(apartmentInspectionService::fetchDocument)
            .map(ApartmentInspectionDocument::getDocument)
            .map(ApartmentInspection::getApartmentInspectionData)
            .map(ApartmentInspectionType::getDelayReason)
            .map(Collection::stream)
            .orElse(Stream.empty())
            .map(DelayReasonData::getDelayDate)
            .filter(Objects::nonNull)
            .count();

        if (delayReasonCount == 1) {
            sendFlowStatusWithHistoryUpdate(commissionInspectionDocument, DEFECTS_ELIMINATION_DATE_CALCULATED);
        } else if (delayReasonCount > 1) {
            sendFlowStatusWithHistoryUpdate(commissionInspectionDocument, PROLONGATION);
        }
    }

    private CommissionInspectionDocument fetchCommissionInspectionForChange(
        final CoordinateStatusMessage coordinateStatus
    ) {
        final String eno = messageUtils.retrieveEno(coordinateStatus).orElse(null);
        final Optional<CommissionInspectionDocument> optionalCommissionInspectionDocument =
            commissionInspectionDocumentService.findByEno(eno);
        if (!optionalCommissionInspectionDocument.isPresent()) {
            log.debug("Unable to find CommissionInspectionDocument by eno {}", eno);
            commissionInspectionElkNotificationService.sendStatusAsync(TECHNICAL_CRASH_ON_CHANGE, eno);
            throw new SsrException("Невозможно найти заявление c ено " + eno);
        }

        return optionalCommissionInspectionDocument.get();
    }

    private void populateProcessIdAndStatus(
        final String processInstanceId,
        final CommissionInspectionFlowStatus status,
        final CommissionInspectionDocument commissionInspectionDocument
    ) {
        final CommissionInspectionData commissionInspection = commissionInspectionDocument
            .getDocument()
            .getCommissionInspectionData();

        commissionInspection.setProcessInstanceId(processInstanceId);
        commissionInspection.setApplicationStatusId(status.getId());
    }

    private void validateOnConfirmationPeriodExpiration(
        final CommissionInspectionDocument commissionInspectionDocument, final boolean isPrimaryInspection
    ) {
        if (isPrimaryInspection) {
            validatePrimaryOnConfirmationPeriodExpiration(commissionInspectionDocument);
        } else {
            validateSecondaryOnConfirmationPeriodExpiration(commissionInspectionDocument);
        }
    }

    private void validateOnMoveDateRequestNumber(
        final CommissionInspectionDocument commissionInspectionDocument,
        final String apartmentInspectionId,
        final boolean isPrimaryInspection,
        final CommissionInspectionFlowStatus moveDateFlowStatus
    ) {
        final long moveDateRequestNumber = of(commissionInspectionDocument.getDocument())
            .map(CommissionInspection::getCommissionInspectionData)
            .map(CommissionInspectionData::getHistory)
            .map(CommissionInspectionHistoryEvents::getEvents)
            .map(List::stream)
            .orElse(Stream.empty())
            .filter(event -> Objects.equals(event.getInspectionId(), apartmentInspectionId))
            .filter(event -> moveDateFlowStatus.getId().equals(event.getEventId()))
            .count();

        if (moveDateRequestNumber >= MAX_ALLOWED_MOVE_DATE_NUMBER) {
            final CommissionInspectionFlowStatus moveDateRejectedStatus = isPrimaryInspection
                ? MOVE_DATE_FIRST_VISIT_REJECTED
                : MOVE_DATE_SECOND_VISIT_REJECTED;

            sendFlowStatus(commissionInspectionDocument, moveDateRejectedStatus);
            throw new MoveDateRequestRejected("заявитель запросил перенос времени более 2 раз.");
        }
    }

    private void validatePrimaryOnConfirmationPeriodExpiration(
        final CommissionInspectionDocument commissionInspectionDocument
    ) {
        final LocalDateTime applicationDateTime = commissionInspectionDocument
            .getDocument()
            .getCommissionInspectionData()
            .getApplicationDateTime();

        if (isNull(applicationDateTime) || applicationDateTime.plusWeeks(2).isBefore(LocalDateTime.now())) {
            sendFlowStatus(commissionInspectionDocument, MOVE_DATE_FIRST_VISIT_REJECTED);
            throw new MoveDateRequestRejected(
                "срок согласования даты осмотра превысил 2 недели с даты регистрации заявки."
            );
        }
    }

    private void validateSecondaryOnConfirmationPeriodExpiration(
        final CommissionInspectionDocument commissionInspectionDocument
    ) {
        final CommissionInspectionData commissionInspection = commissionInspectionDocument
            .getDocument()
            .getCommissionInspectionData();

        final LocalDateTime defectsFixedDateTime =
            retrieveLastHistoryEventByStatus(commissionInspection, DEFECTS_FIXED)
                .map(CommissionInspectionHistoryEvent::getCreatedAt)
                .orElseThrow(() -> new SsrException("Отсутствует осмотр в статусе " + DEFECTS_FIXED.name()));

        if (defectsFixedDateTime.plusWeeks(2).isBefore(LocalDateTime.now())) {
            sendFlowStatus(commissionInspectionDocument, MOVE_DATE_SECOND_VISIT_REJECTED);

            throw new MoveDateRequestRejected("срок согласования даты повторного осмотра превысил 2 недели.");
        }
    }

    private static void validateRequestIsBeforeInspection(final CommissionInspectionData commissionInspection) {
        final LocalDateTime confirmedInspectionDateTime = commissionInspection.getConfirmedInspectionDateTime();
        final LocalDateTime now = LocalDateTime.now();

        if (nonNull(confirmedInspectionDateTime) && confirmedInspectionDateTime.isBefore(now)) {
            throw new SsrException("Невозможно отозвать заявление");
        }
    }

    private Optional<String> createConfirmDateTask(final CommissionInspectionDocument commissionInspectionDocument) {
        final CommissionInspectionData commissionInspection = commissionInspectionDocument
            .getDocument()
            .getCommissionInspectionData();
        final boolean isPrimary = isPrimaryInspection(commissionInspection);
        final boolean isRenovationFundTask = isRenovationFundTask(commissionInspection);

        return createConfirmDateTask(commissionInspectionDocument.getId(), isPrimary, isRenovationFundTask);
    }

    private Optional<String> createConfirmDateTask(
        final String commissionInspectionId, final boolean isPrimary, final boolean isRenovationFundTask
    ) {
        final Map<String, String> variablesMap = new HashMap<>();

        variablesMap.put(BpmService.PROCESS_DOCUMENT_KEY_ENTITY_ID_VAR, commissionInspectionId);
        variablesMap.put(BpmService.PROCESS_DOCUMENT_KEY_CANDIDATE_GROUPS,
            isRenovationFundTask ? PERSON_RENOVATION_FOND_LDAP_GROUP : COMMISSION_INSPECTION_READ_LDAP_GROUP);
        variablesMap.put(
            PROCESS_DOCUMENT_KEY_CONFIRM_DATE_TIME_TASK_NAME,
            isPrimary ? FIRST_VISIT_CONFIRM_DATE_TIME_TASK_NAME : SECOND_VISIT_CONFIRM_DATE_TIME_TASK_NAME
        );

        return ofNullable(bpmService.startNewProcess(PROCESS_KEY_CONFIRM_INSPECTION_DATE, variablesMap));
    }

    private boolean isRenovationFundTask(final CommissionInspectionData commissionInspection) {
        if (isNull(commissionInspection)
            || isNull(commissionInspection.getCcoUnom())
            || "заселение".equalsIgnoreCase(commissionInspection.getFlatStatus())) {
            return false;
        }

        final List<OrganizationInformation> developers =
            capitalConstructionObjectService.getDeveloperOrganisationsByUnom(commissionInspection.getCcoUnom());

        final String kpUgsInn = getKpUgsInn();

        return developers
            .stream()
            .map(OrganizationInformation::getExternalId)
            .noneMatch(kpUgsInn::equals);
    }

    private String getKpUgsInn() {
        return apartmentInspectionProperties.getKpUgsInn();
    }
}
