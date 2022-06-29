package ru.croc.ugd.ssr.service.guardianship;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.Person;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.config.GuardianshipProperties;
import ru.croc.ugd.ssr.dto.guardianship.CreateGuardianshipRequestDto;
import ru.croc.ugd.ssr.dto.guardianship.GuardianshipHandleRequestDto;
import ru.croc.ugd.ssr.dto.guardianship.GuardianshipRequestDto;
import ru.croc.ugd.ssr.exception.guardianship.GuardianshipRequestExists;
import ru.croc.ugd.ssr.guardianship.GuardianshipRequestData;
import ru.croc.ugd.ssr.integration.service.flows.AdministrativeDocumentFlowService;
import ru.croc.ugd.ssr.integration.service.notification.ElkUserNotificationService;
import ru.croc.ugd.ssr.mapper.GuardianshipMapper;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.guardianship.GuardianshipRequestDocument;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.document.GuardianshipRequestDocumentService;
import ru.croc.ugd.ssr.service.document.TradeAdditionDocumentService;
import ru.croc.ugd.ssr.utils.PersonUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@AllArgsConstructor
public class DefaultRestGuardianshipService implements RestGuardianshipService {

    public static final int POSITIVE_GUARDIANSHIP_DECISION_TYPE = 1;

    private final GuardianshipRequestDocumentService guardianshipRequestDocumentService;
    private final GuardianshipService guardianshipService;
    private final ElkUserNotificationService notificationService;
    private final PersonDocumentService personDocumentService;
    private final GuardianshipMapper guardianshipMapper;
    private final GuardianshipProperties guardianshipProperties;
    private final TradeAdditionDocumentService tradeAdditionDocumentService;
    private final GuardianshipEmailNotificationService guardianshipEmailNotificationService;

    @Override
    public void create(final CreateGuardianshipRequestDto createGuardianshipRequestDto) {
        if (guardianshipRequestDocumentService.existsNonCompleteByAffairId(createGuardianshipRequestDto.getAffairId())
        ) {
            throw new GuardianshipRequestExists();
        }

        final GuardianshipRequestDocument guardianshipRequestDocument = guardianshipMapper
            .toGuardianshipRequestDocument(createGuardianshipRequestDto, LocalDateTime.now());

        final GuardianshipRequestDocument createdDocument = guardianshipRequestDocumentService.createDocument(
            guardianshipRequestDocument, true, null
        );

        final GuardianshipRequestData guardianshipRequestData = guardianshipRequestDocument
            .getDocument()
            .getGuardianshipRequestData();

        final PersonDocument personDocument = personDocumentService
            .fetchDocument(createGuardianshipRequestDto.getRequesterPersonId());
        final PersonType personType = personDocument
            .getDocument()
            .getPersonData();
        guardianshipRequestData.setFio(personType.getFIO());
        final String newFlatAddress = personDocumentService.getPersonNewFlatAddress(personType);
        guardianshipRequestData.setAddressTo(newFlatAddress);
        guardianshipRequestDocumentService.updateDocument(
            guardianshipRequestDocument.getId(),
            guardianshipRequestDocument,
            true,
            true,
            null
        );
        if (hasAdministrativeDocument(personDocument) || hasCompensationsOrOutOfDistrict(personType)) {
            guardianshipService.startHandleGuardianshipRequestTask(createdDocument);
        }

        guardianshipEmailNotificationService.sendNotificationEmail(guardianshipRequestDocument);
    }

    private static boolean hasAdministrativeDocument(final PersonDocument personDocument) {
        return Optional.of(personDocument.getDocument())
            .map(Person::getPersonData)
            .map(PersonUtils::getOfferLetterStream)
            .orElse(Stream.empty())
            .map(offerLetter -> PersonUtils.extractOfferLetterFileByType(
                offerLetter, AdministrativeDocumentFlowService.ADMINISTRATIVE_DOC_FILE_TYPE)
            )
            .anyMatch(Optional::isPresent);
    }

    private boolean hasCompensationsOrOutOfDistrict(final PersonType personType) {
        if (personType.getPersonID() == null || personType.getAffairId() == null) {
            return false;
        }
        return tradeAdditionDocumentService
            .hasCompensationsOrOutOfDistrict(personType.getPersonID(), personType.getAffairId());
    }

    @Override
    public void handleRequest(final String id, final GuardianshipHandleRequestDto guardianshipHandleRequestDto) {
        final GuardianshipRequestDocument guardianshipRequestDocument = guardianshipRequestDocumentService
            .fetchDocument(id);

        final GuardianshipRequestData guardianshipRequestData = guardianshipRequestDocument
            .getDocument()
            .getGuardianshipRequestData();

        guardianshipRequestData.setDecisionDate(guardianshipHandleRequestDto.getDecisionDate());
        guardianshipRequestData.setDecisionFileId(guardianshipHandleRequestDto.getDecisionFileId());
        guardianshipRequestData.setDecisionType(guardianshipHandleRequestDto.getDecisionType());
        guardianshipRequestData.setDeclineReasonType(guardianshipHandleRequestDto.getDeclineReasonType());
        guardianshipRequestData.setDeclineReason(guardianshipHandleRequestDto.getDeclineReason());
        guardianshipRequestData.setCompletionDateTime(LocalDateTime.now());

        guardianshipRequestDocumentService.updateDocument(
            id, guardianshipRequestDocument, true, true, null
        );

        guardianshipService.forceFinishProcess(guardianshipRequestDocument);

        final PersonDocument personDocument = personDocumentService
            .fetchDocument(guardianshipRequestData.getRequesterPersonId());

        final Map<String, String> notificationParams = getNotificationTemplateParams(guardianshipRequestData);
        if (guardianshipHandleRequestDto.getDecisionType() == POSITIVE_GUARDIANSHIP_DECISION_TYPE) {
            notificationService.sendGuardianshipAgreementNotification(personDocument, notificationParams);
        } else {
            notificationService.sendGuardianshipDeclineNotification(personDocument, notificationParams);
        }
    }

    @Override
    public Map<String, String> getNotificationTemplateParams(final GuardianshipRequestData guardianshipRequestData) {
        final Map<String, String> notificationParams = new HashMap<>();
        notificationParams.put("$appointmentLink", guardianshipProperties.getPortalUrl());
        if (!StringUtils.isEmpty(guardianshipRequestData.getDeclineReason())) {
            notificationParams.put("$declineReason", guardianshipRequestData.getDeclineReason());
        } else if (guardianshipRequestData.getDecisionType() != null) {
            notificationParams.put("$declineReason", GuardianshipServiceHelper
                .mapDeclineReasonTypeToString(guardianshipRequestData.getDecisionType()));
        }
        return notificationParams;
    }

    @Override
    public List<GuardianshipRequestDto> find(final String affairId, final boolean skipInactive) {
        return guardianshipRequestDocumentService.fetchByAffairIdAndSkipInactive(affairId, skipInactive)
            .stream()
            .map(guardianshipMapper::toGuardianshipRequestDto)
            .collect(Collectors.toList());
    }

    @Override
    public GuardianshipRequestDto findById(String id) {
        return guardianshipMapper
            .toGuardianshipRequestDto(guardianshipRequestDocumentService
                .fetchDocument(id));
    }
}
