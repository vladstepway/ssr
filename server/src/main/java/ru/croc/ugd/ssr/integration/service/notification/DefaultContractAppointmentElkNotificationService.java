package ru.croc.ugd.ssr.integration.service.notification;

import static java.util.Optional.ofNullable;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.Cip;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.config.contractappointment.ContractAppointmentProperties;
import ru.croc.ugd.ssr.contractappointment.ContractAppointmentData;
import ru.croc.ugd.ssr.contractappointment.ContractAppointmentFile;
import ru.croc.ugd.ssr.dto.contractappointment.ContractAppointmentFlowStatus;
import ru.croc.ugd.ssr.integration.command.ContractAppointmentPublishReasonCommand;
import ru.croc.ugd.ssr.integration.command.ContractDigitalSignPublishReasonCommand;
import ru.croc.ugd.ssr.integration.service.ContractAppointmentEventPublisher;
import ru.croc.ugd.ssr.integration.service.ContractDigitalSignEventPublisher;
import ru.croc.ugd.ssr.model.CipDocument;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.contractappointment.ContractAppointmentDocument;
import ru.croc.ugd.ssr.service.ChedFileService;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.cip.CipService;
import ru.croc.ugd.ssr.service.contractappointment.unsinged.ContractDigitalSignCancellationReason;
import ru.croc.ugd.ssr.utils.CipUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Сервис отправки сообщений в ЭЛК для записи на подписание договора.
 */
@Service
@Slf4j
@AllArgsConstructor
public class DefaultContractAppointmentElkNotificationService implements ContractAppointmentElkNotificationService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final ContractAppointmentEventPublisher contractAppointmentEventPublisher;
    private final CipService cipService;
    private final PersonDocumentService personDocumentService;
    private final ChedFileService chedFileService;
    private final ElkUserNotificationService elkUserNotificationService;
    private final ContractAppointmentProperties contractAppointmentProperties;
    private final ContractDigitalSignEventPublisher contractDigitalSignEventPublisher;

    @Override
    public void sendStatus(final ContractAppointmentFlowStatus status, final ContractAppointmentDocument document) {
        final ContractAppointmentPublishReasonCommand publishReasonCommand
            = createPublishReasonCommand(status, document.getDocument().getContractAppointmentData());
        contractAppointmentEventPublisher.publishStatus(publishReasonCommand);
        if (status.getEventCode() != null
            && (!ContractAppointmentFlowStatus.CONTRACT_SIGNED.equals(status)
            || contractAppointmentProperties.isModernizationEnabled())) {
            sendNotification(status, document);
        }
    }

    @Override
    public void sendStatus(final ContractAppointmentFlowStatus status, final String eno) {
        final ContractAppointmentPublishReasonCommand publishReasonCommand =
            createPublishReasonCommand(status, eno);
        contractAppointmentEventPublisher.publishStatus(publishReasonCommand);
    }

    @Override
    public void sendToBk(final String message) {
        contractAppointmentEventPublisher.publishToBk(message);
    }

    @Override
    public void sendStatusToBk(final String message) {
        contractAppointmentEventPublisher.publishStatusToBk(message);
    }

    @Override
    public void sendUnsignedContractStatus(final ContractDigitalSignCancellationReason reason) {
        final ContractDigitalSignPublishReasonCommand publishReasonCommand =
            ContractDigitalSignPublishReasonCommand.builder()
            .contractAppointment(reason.getContractAppointmentDocument().getDocument().getContractAppointmentData())
            .responseReasonDate(LocalDateTime.now())
            .elkStatusText(reason.getStatusText())
            .status(reason.getFlowStatus())
            .build();
        contractDigitalSignEventPublisher.publishStatus(publishReasonCommand);
    }

    @Override
    public Map<String, String> getExtraTemplateParams(
        final ContractAppointmentData contractAppointment,
        final PersonType requesterPersonType,
        final PersonType ownerPersonType
    ) {
        final Map<String, String> extraParams = new HashMap<>();

        if (ownerPersonType != null) {
            extraParams.put("$ownerFio", ownerPersonType.getFIO());
        }
        if (requesterPersonType != null) {
            extraParams.put("$requesterFio", requesterPersonType.getFIO());
        }
        extraParams.put("$contractSignDate", getFormattedContractSignDate(contractAppointment));
        extraParams.put("$appointmentDate", getFormattedAppointmentDate(contractAppointment));
        extraParams.put("$appointmentTime", getFormattedAppointmentTime(contractAppointment));

        final String cipAddress = ofNullable(contractAppointment.getCipId())
            .flatMap(cipService::fetchById)
            .map(CipDocument::getDocument)
            .map(Cip::getCipData)
            .map(CipUtils::getCipAddress)
            .orElse(null);
        extraParams.put("$cipAddress", cipAddress);

        final String addressTo = ofNullable(contractAppointment.getAddressTo()).orElse("");
        extraParams.put("$addressTo", addressTo);

        final String cancelReason = ofNullable(contractAppointment.getCancelReason()).orElse("");
        extraParams.put("$cancelReason", cancelReason);

        final String contractLink = ofNullable(contractAppointment.getContractFile())
            .map(ContractAppointmentFile::getChedFileId)
            .flatMap(chedFileService::getChedFileLink)
            .orElse(null);
        extraParams.put("$contractLink", contractLink);

        return extraParams;
    }

    public String retrieveContractSignedStatusText(
        final String statusTemplate, final ContractAppointmentData contractAppointment
    ) {
        final String contractSignDate = getFormattedContractSignDate(contractAppointment);
        if (contractAppointmentProperties.isModernizationEnabled()) {
            final String contractLink = ofNullable(contractAppointment.getContractFile())
                .map(ContractAppointmentFile::getChedFileId)
                .flatMap(chedFileService::getChedFileLink)
                .orElse(null);
            return String.format(statusTemplate, contractSignDate, contractLink);
        } else {
            return String.format("Договор на равнозначное жилое помещение заключен %s.", contractSignDate);
        }
    }

    private ContractAppointmentPublishReasonCommand createPublishReasonCommand(
        final ContractAppointmentFlowStatus status, final String eno
    ) {
        final ContractAppointmentData contractAppointment = new ContractAppointmentData();
        contractAppointment.setEno(eno);
        return createPublishReasonCommand(status, contractAppointment);
    }

    private ContractAppointmentPublishReasonCommand createPublishReasonCommand(
        final ContractAppointmentFlowStatus status, final ContractAppointmentData contractAppointment
    ) {
        return ContractAppointmentPublishReasonCommand.builder()
            .contractAppointment(contractAppointment)
            .responseReasonDate(LocalDateTime.now())
            .elkStatusText(createStatusText(status, contractAppointment))
            .status(status)
            .build();
    }

    private String createStatusText(
        final ContractAppointmentFlowStatus status, final ContractAppointmentData contractAppointment
    ) {
        switch (status) {
            case REGISTERED:
            case MOVED_BY_OPERATOR:
            case CANCELED_BY_APPLICANT:
            case CANCELED_BY_OPERATOR:
            case CONTRACT_SIGNED:
                return createComplexStatusText(status, contractAppointment);
            default:
                return status.getStatusText();
        }
    }

    private String createComplexStatusText(
        final ContractAppointmentFlowStatus status, final ContractAppointmentData contractAppointment
    ) {
        final String statusTemplate = status.getStatusText();

        final String addressTo = ofNullable(contractAppointment.getAddressTo()).orElse("-");
        final String eno = ofNullable(contractAppointment.getEno()).orElse("-");

        final String appointmentDate = getFormattedAppointmentDate(contractAppointment);
        final String appointmentTime = getFormattedAppointmentTime(contractAppointment);

        final String cancellationReason = ofNullable(contractAppointment.getCancelReason()).orElse("-");

        final String icsChedFileLink = ofNullable(contractAppointment.getIcsChedFileId())
            .flatMap(chedFileService::getChedFileLink)
            .orElse(null);

        final String cipAddress = ofNullable(contractAppointment.getCipId())
            .flatMap(cipService::fetchById)
            .map(CipDocument::getDocument)
            .map(Cip::getCipData)
            .map(CipUtils::getCipAddress)
            .orElse(null);

        switch (status) {
            case REGISTERED:
                return String.format(
                    statusTemplate,
                    addressTo,
                    appointmentDate,
                    appointmentTime,
                    cipAddress,
                    icsChedFileLink
                );
            case MOVED_BY_OPERATOR:
                return String.format(
                    statusTemplate,
                    addressTo,
                    appointmentDate,
                    appointmentTime,
                    cipAddress,
                    eno,
                    icsChedFileLink
                );
            case CANCELED_BY_APPLICANT:
                return String.format(statusTemplate, eno, addressTo);
            case CANCELED_BY_OPERATOR:
                return String.format(
                    statusTemplate,
                    eno,
                    appointmentDate,
                    appointmentTime,
                    cancellationReason
                );
            case CONTRACT_SIGNED:
                return retrieveContractSignedStatusText(statusTemplate, contractAppointment);
            default:
                return statusTemplate;
        }
    }

    private String getFormattedAppointmentDate(final ContractAppointmentData contractAppointment) {
        return ofNullable(contractAppointment.getAppointmentDateTime())
            .map(e -> e.format(DATE_FORMATTER))
            .orElse("-");
    }

    private String getFormattedContractSignDate(final ContractAppointmentData contractAppointment) {
        return ofNullable(contractAppointment.getContractSignDate())
            .map(e -> e.format(DATE_FORMATTER))
            .orElse("-");
    }

    private String getFormattedAppointmentTime(final ContractAppointmentData contractAppointment) {
        return ofNullable(contractAppointment.getAppointmentDateTime())
            .map(e -> e.format(TIME_FORMATTER))
            .orElse("-");
    }

    private void sendNotification(
        final ContractAppointmentFlowStatus status, final ContractAppointmentDocument document
    ) {
        final ContractAppointmentData contractAppointment = document.getDocument().getContractAppointmentData();
        if (contractAppointment == null || contractAppointment.getApplicant() == null) {
            log.info("Skipping DefaultContractAppointmentElkNotificationService.sendNotification"
                + " as contract appointment or applicant is empty");
            return;
        }
        final String applicantId = contractAppointment.getApplicant().getPersonDocumentId();
        final PersonDocument requesterPersonDocument = personDocumentService.fetchDocument(applicantId);
        sendNotificationForAllOwners(status, contractAppointment, requesterPersonDocument);
    }

    private void sendNotificationForAllOwners(
        final ContractAppointmentFlowStatus status,
        final ContractAppointmentData contractAppointment,
        final PersonDocument requesterPersonDocument
    ) {
        final PersonType requesterPersonType = requesterPersonDocument.getDocument().getPersonData();
        final List<PersonDocument> ownerPersonDocuments = personDocumentService
            .getOtherFlatOwners(requesterPersonDocument);

        ownerPersonDocuments.forEach(ownerPersonDocument ->
            elkUserNotificationService.sendContractAppointmentNotification(
                ownerPersonDocument,
                status,
                getExtraTemplateParams(contractAppointment,
                    requesterPersonType,
                    ownerPersonDocument
                        .getDocument()
                        .getPersonData()))
        );
    }
}
