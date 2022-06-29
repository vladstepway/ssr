package ru.croc.ugd.ssr.integration.service.notification;

import static java.util.Objects.nonNull;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.contractappointment.Applicant;
import ru.croc.ugd.ssr.contractappointment.ContractAppointmentData;
import ru.croc.ugd.ssr.contractappointment.ContractAppointmentFile;
import ru.croc.ugd.ssr.contractdigitalsign.ContractDigitalSignData;
import ru.croc.ugd.ssr.contractdigitalsign.ContractDigitalSignFile;
import ru.croc.ugd.ssr.contractdigitalsign.ElkUserNotification;
import ru.croc.ugd.ssr.contractdigitalsign.Owner;
import ru.croc.ugd.ssr.contractdigitalsign.SignData;
import ru.croc.ugd.ssr.dto.ElkUserNotificationDto;
import ru.croc.ugd.ssr.dto.contractdigitalsign.ContractDigitalSignFlowStatus;
import ru.croc.ugd.ssr.dto.contractdigitalsign.ContractDigitalSignNotificationStatus;
import ru.croc.ugd.ssr.integration.command.ContractDigitalSignPublishNotificationReasonCommand;
import ru.croc.ugd.ssr.integration.command.ContractDigitalSignPublishReasonCommand;
import ru.croc.ugd.ssr.integration.service.ContractDigitalSignEventPublisher;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.contractDigitalSign.ContractDigitalSignDocument;
import ru.croc.ugd.ssr.model.contractappointment.ContractAppointmentDocument;
import ru.croc.ugd.ssr.service.ChedFileService;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.document.ContractDigitalSignDocumentService;
import ru.croc.ugd.ssr.utils.PersonUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Сервис отправки сообщений в ЭЛК для записи на подписание договора с помощью УКЭП.
 */
@Service
@Slf4j
@AllArgsConstructor
public class DefaultContractDigitalSignElkNotificationService implements ContractDigitalSignElkNotificationService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private final ContractDigitalSignEventPublisher contractDigitalSignEventPublisher;
    private final PersonDocumentService personDocumentService;
    private final ChedFileService chedFileService;
    private final ElkUserNotificationService elkUserNotificationService;
    private final ContractDigitalSignDocumentService contractDigitalSignDocumentService;

    @Override
    public void sendStatus(
        final ContractDigitalSignFlowStatus status, final ContractAppointmentDocument contractAppointmentDocument
    ) {
        sendStatus(status, contractAppointmentDocument, null);
    }

    @Override
    public void sendStatus(
        final ContractDigitalSignFlowStatus status,
        final ContractAppointmentDocument contractAppointmentDocument,
        final String ownerFio
    ) {
        final ContractDigitalSignPublishReasonCommand publishReasonCommand = createPublishReasonCommand(
            status, contractAppointmentDocument.getDocument().getContractAppointmentData(), ownerFio
        );
        contractDigitalSignEventPublisher.publishStatus(publishReasonCommand);
        if (status.getEventCode() != null) {
            sendNotification(status, contractAppointmentDocument, true);
        }
    }

    private ContractDigitalSignPublishReasonCommand createPublishReasonCommand(
        final ContractDigitalSignFlowStatus status,
        final ContractAppointmentData contractAppointmentData,
        final String ownerFio
    ) {
        return ContractDigitalSignPublishReasonCommand.builder()
            .contractAppointment(contractAppointmentData)
            .responseReasonDate(LocalDateTime.now())
            .elkStatusText(createStatusText(status, contractAppointmentData, ownerFio))
            .status(status)
            .build();
    }

    private String createStatusText(
        final ContractDigitalSignFlowStatus status,
        final ContractAppointmentData contractAppointment,
        final String ownerFio
    ) {
        switch (status) {
            case REGISTERED:
            case CONTRACT_SIGNED:
            case CANCELED_BY_APPLICANT:
            case CANCELED_BY_OPERATOR:
            case MOVED_BY_OPERATOR:
            case REFUSE_TO_PROVIDE_SERVICE_BY_OWNER:
            case REFUSE_TO_PROVIDE_SERVICE_DUE_TO_OWNER:
                return createComplexStatusText(status, contractAppointment, ownerFio);
            default:
                return status.getStatusText();
        }
    }

    private String createStatusText(
        final ContractDigitalSignNotificationStatus status,
        final ContractAppointmentData contractAppointment,
        final PersonDocument signerPersonDocument
    ) {
        switch (status) {
            case SIGNED_INCORRECTLY_BY_OWNER:
            case SIGNED_BY_OWNER:
            case ACCEPTED:
                return createComplexStatusText(status, contractAppointment, signerPersonDocument);
            default:
                return status.getStatusText();
        }
    }

    private String createComplexStatusText(
        final ContractDigitalSignFlowStatus status,
        final ContractAppointmentData contractAppointment,
        final String ownerFio
    ) {
        final String statusTemplate = status.getStatusText();

        final String addressTo = ofNullable(contractAppointment.getAddressTo()).orElse("-");
        final String eno = ofNullable(contractAppointment.getEno()).orElse("-");

        final String appointmentDate = getFormattedAppointmentDate(contractAppointment);

        final String icsChedFileLink = ofNullable(contractAppointment.getIcsChedFileId())
            .flatMap(chedFileService::getChedFileLink)
            .orElse(null);

        final String cancellationReason = ofNullable(contractAppointment.getCancelReason()).orElse("-");

        final String contractLink = ofNullable(contractAppointment.getContractFile())
            .map(ContractAppointmentFile::getChedFileId)
            .flatMap(chedFileService::getChedFileLink)
            .orElse(null);

        switch (status) {
            case REGISTERED:
            case MOVED_BY_OPERATOR:
                return String.format(
                    statusTemplate,
                    addressTo,
                    appointmentDate,
                    icsChedFileLink
                );
            case CONTRACT_SIGNED:
                return String.format(statusTemplate, appointmentDate, contractLink);
            case CANCELED_BY_APPLICANT:
                return String.format(statusTemplate, eno, addressTo);
            case CANCELED_BY_OPERATOR:
                return String.format(
                    statusTemplate,
                    eno,
                    appointmentDate,
                    cancellationReason
                );
            case REFUSE_TO_PROVIDE_SERVICE_BY_OWNER:
                return String.format(
                    statusTemplate,
                    ownerFio
                );
            case REFUSE_TO_PROVIDE_SERVICE_DUE_TO_OWNER:
                return String.format(
                    statusTemplate,
                    "правообладатель" + ownerFio + "не подписал"
                );
            default:
                return statusTemplate;
        }
    }

    private String createComplexStatusText(
        final ContractDigitalSignNotificationStatus status,
        final ContractAppointmentData contractAppointment,
        final PersonDocument signerPersonDocument
    ) {
        final String statusTemplate = status.getStatusText();

        final String addressTo = ofNullable(contractAppointment.getAddressTo()).orElse("-");
        final String signerFullName = PersonUtils.getFullName(signerPersonDocument);

        switch (status) {
            case SIGNED_INCORRECTLY_BY_OWNER:
                return String.format(statusTemplate, signerFullName);
            case SIGNED_BY_OWNER:
                return String.format(statusTemplate, signerFullName, addressTo);
            case ACCEPTED:
                return String.format(statusTemplate, addressTo);
            default:
                return statusTemplate;
        }
    }

    @Override
    public List<ElkUserNotificationDto> sendNotification(
        final ContractDigitalSignFlowStatus status,
        final ContractAppointmentDocument contractAppointmentDocument,
        final boolean isOtherOwnerNotification
    ) {
        final ContractAppointmentData contractAppointment = contractAppointmentDocument
            .getDocument()
            .getContractAppointmentData();
        if (contractAppointment == null || contractAppointment.getApplicant() == null) {
            log.warn("Skip sending contract digital sign notification as contract appointment or applicant is empty: "
                    + "contractAppointmentDocumentId = {}, statusId = {}",
                contractAppointmentDocument.getId(),
                status.getId()
            );
            return Collections.emptyList();
        }

        final Optional<PersonDocument> optionalRequesterPersonDocument = of(contractAppointment.getApplicant())
            .map(Applicant::getPersonDocumentId)
            .flatMap(personDocumentService::fetchById);

        if (!optionalRequesterPersonDocument.isPresent()) {
            log.warn("Skip sending contract digital sign notification as applicant doesn't exist: "
                    + "contractAppointmentDocumentId = {}, statusId = {}",
                contractAppointmentDocument.getId(),
                status.getId()
            );
            return Collections.emptyList();
        }

        final PersonDocument requesterPersonDocument = optionalRequesterPersonDocument.get();

        final List<PersonDocument> notifiablePersonDocuments = isOtherOwnerNotification
            ? personDocumentService.getOtherFlatOwners(requesterPersonDocument)
            : Collections.singletonList(requesterPersonDocument);

        return sendNotification(
            status, contractAppointment, requesterPersonDocument, notifiablePersonDocuments
        );
    }

    private List<ElkUserNotificationDto> sendNotification(
        final ContractDigitalSignFlowStatus status,
        final ContractAppointmentData contractAppointment,
        final PersonDocument requesterPersonDocument,
        final List<PersonDocument> notifiablePersonDocuments
    ) {
        final Map<String, String> extraTemplateParams = getExtraTemplateParams(
            contractAppointment, requesterPersonDocument.getDocument().getPersonData()
        );

        return notifiablePersonDocuments.stream()
            .map(notifiablePersonDocument -> elkUserNotificationService.sendContractDigitalSignNotification(
                notifiablePersonDocument, status, extraTemplateParams
            ))
            .collect(Collectors.toList());
    }

    @Override
    public Map<String, String> getExtraTemplateParams(
        final ContractAppointmentData contractAppointment, final PersonType requesterPerson
    ) {
        final Map<String, String> extraParams = new HashMap<>();

        if (requesterPerson != null) {
            extraParams.put("$requesterFio", PersonUtils.getFullName(requesterPerson));
        }

        extraParams.put("$appointmentDate", getFormattedAppointmentDate(contractAppointment));

        final String addressTo = ofNullable(contractAppointment.getAddressTo()).orElse("-");
        extraParams.put("$addressTo", addressTo);

        final String eno = ofNullable(contractAppointment.getEno()).orElse("-");
        extraParams.put("$eno", eno);

        final String cancelReason = ofNullable(contractAppointment.getCancelReason()).orElse("-");
        extraParams.put("$cancelReason", cancelReason);

        final String contractLink = ofNullable(contractAppointment.getContractFile())
            .map(ContractAppointmentFile::getChedFileId)
            .flatMap(chedFileService::getChedFileLink)
            .orElse(null);
        extraParams.put("$contractLink", contractLink);

        return extraParams;
    }

    private String getFormattedAppointmentDate(final ContractAppointmentData contractAppointment) {
        return ofNullable(contractAppointment.getAppointmentDateTime())
            .map(e -> e.format(DATE_FORMATTER))
            .orElse("-");
    }

    @Override
    public void sendNotificationStatus(
        final ContractDigitalSignNotificationStatus signerStatus,
        final ContractDigitalSignNotificationStatus otherOwnersStatus,
        final ContractDigitalSignDocument contractDigitalSignDocument,
        final ContractAppointmentDocument contractAppointmentDocument,
        final String signerPersonDocumentId
    ) {
        final List<Owner> owners = of(contractDigitalSignDocument.getDocument().getContractDigitalSignData())
            .map(ContractDigitalSignData::getOwners)
            .map(ContractDigitalSignData.Owners::getOwner)
            .orElse(Collections.emptyList());

        owners.stream()
            .filter(owner -> Objects.equals(owner.getPersonDocumentId(), signerPersonDocumentId))
            .findFirst()
            .ifPresent(owner -> sendNotificationStatus(signerStatus, contractAppointmentDocument, owner));

        final PersonDocument signerPersonDocument = personDocumentService.fetchById(signerPersonDocumentId)
            .orElse(null);
        final Integer reasonCode = retrieveReasonCode(otherOwnersStatus, owners);
        owners.stream()
            .filter(owner -> !Objects.equals(owner.getPersonDocumentId(), signerPersonDocumentId))
            .forEach(owner -> sendNotificationStatus(
                otherOwnersStatus, contractAppointmentDocument, owner, signerPersonDocument, reasonCode
            ));

        contractDigitalSignDocumentService.updateDocument(contractDigitalSignDocument, "sendNotificationStatus");
    }

    @Override
    public void sendNotificationStatus(
        final ContractDigitalSignNotificationStatus status,
        final ContractDigitalSignDocument contractDigitalSignDocument,
        final ContractAppointmentDocument contractAppointmentDocument
    ) {
        of(contractDigitalSignDocument.getDocument().getContractDigitalSignData())
            .map(ContractDigitalSignData::getOwners)
            .map(ContractDigitalSignData.Owners::getOwner)
            .map(Collection::stream)
            .orElse(Stream.empty())
            .forEach(owner -> sendNotificationStatus(status, contractAppointmentDocument, owner));
    }

    private void sendNotificationStatus(
        final ContractDigitalSignNotificationStatus status,
        final ContractAppointmentDocument contractAppointmentDocument,
        final Owner owner
    ) {
        sendNotificationStatus(status, contractAppointmentDocument, owner, null, status.getSubCode());
    }

    public void sendNotificationStatus(
        final ContractDigitalSignNotificationStatus status,
        final ContractAppointmentDocument contractAppointmentDocument,
        final Owner owner,
        final PersonDocument signerPersonDocument,
        final Integer reasonCode
    ) {
        final String elkUserNotificationEno = retrieveElkUserNotificationEno(owner, status.getEventCode());
        if (nonNull(elkUserNotificationEno)) {
            final ContractDigitalSignPublishNotificationReasonCommand publishNotificationReasonCommand =
                createPublishNotificationReasonCommand(
                    status,
                    contractAppointmentDocument.getDocument().getContractAppointmentData(),
                    signerPersonDocument,
                    elkUserNotificationEno,
                    reasonCode
                );
            contractDigitalSignEventPublisher.publishNotificationStatus(publishNotificationReasonCommand);

            final ElkUserNotificationDto elkUserNotificationDto = ElkUserNotificationDto.builder()
                .eno(elkUserNotificationEno)
                .statusId(retrieveStatusId(status, reasonCode))
                .build();
            changeElkUserNotificationStatus(owner, elkUserNotificationDto);
        }
    }

    public String retrieveStatusId(final ContractDigitalSignNotificationStatus status, final Integer reasonCode) {
        return Stream.of(status.getCode(), reasonCode)
            .map(s -> ofNullable(s)
                .map(String::valueOf)
                .orElse(null)
            )
            .filter(Objects::nonNull)
            .collect(Collectors.joining("."));
    }

    @Override
    public boolean changeElkUserNotificationStatus(
        final Owner owner, final ElkUserNotificationDto elkUserNotificationDto
    ) {
        return ofNullable(owner.getElkUserNotifications())
            .map(Owner.ElkUserNotifications::getElkUserNotification)
            .map(Collection::stream)
            .orElse(Stream.empty())
            .filter(elkUserNotification -> Objects.equals(
                elkUserNotificationDto.getEno(), elkUserNotification.getEno()
            ))
            .findFirst()
            .map(elkUserNotification -> {
                final String statusId = elkUserNotificationDto.getStatusId();
                elkUserNotification.setStatusId(statusId);
                return ContractDigitalSignNotificationStatus.SENT.getId().equals(statusId);
            })
            .orElse(false);
    }

    @Override
    public Integer retrieveReasonCode(final ContractDigitalSignNotificationStatus status, final List<Owner> owners) {
        if (status == ContractDigitalSignNotificationStatus.SIGNED_BY_OWNER) {
            return owners.stream()
                .filter(owner -> existsVerifiedContractSign(owner) && existsVerifiedActSign(owner))
                .map(owner -> 1)
                .reduce(0, Integer::sum);
        } else {
            return status.getSubCode();
        }
    }

    private boolean existsVerifiedContractSign(final Owner owner) {
        return ofNullable(owner)
            .map(Owner::getContractFile)
            .map(ContractDigitalSignFile::getSignData)
            .map(SignData::isIsVerified)
            .orElse(false);
    }

    private boolean existsVerifiedActSign(final Owner owner) {
        return ofNullable(owner)
            .map(Owner::getActFile)
            .map(ContractDigitalSignFile::getSignData)
            .map(SignData::isIsVerified)
            .orElse(false);
    }

    private String retrieveElkUserNotificationEno(final Owner owner, final String eventCode) {
        return ofNullable(owner)
            .map(Owner::getElkUserNotifications)
            .map(Owner.ElkUserNotifications::getElkUserNotification)
            .map(Collection::stream)
            .orElse(Stream.empty())
            .filter(elkUserNotification -> Objects.equals(elkUserNotification.getEventCode(), eventCode))
            .reduce((first, second) -> second)
            .map(ElkUserNotification::getEno)
            .orElse(null);
    }

    private ContractDigitalSignPublishNotificationReasonCommand createPublishNotificationReasonCommand(
        final ContractDigitalSignNotificationStatus status,
        final ContractAppointmentData contractAppointmentData,
        final PersonDocument signerPersonDocument,
        final String eno,
        final Integer reasonCode
    ) {
        return ContractDigitalSignPublishNotificationReasonCommand.builder()
            .eno(eno)
            .responseReasonDate(LocalDateTime.now())
            .elkStatusText(createStatusText(status, contractAppointmentData, signerPersonDocument))
            .status(status)
            .reasonCode(reasonCode)
            .build();
    }
}
