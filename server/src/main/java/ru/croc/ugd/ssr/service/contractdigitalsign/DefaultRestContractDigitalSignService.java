package ru.croc.ugd.ssr.service.contractdigitalsign;

import static java.util.Objects.nonNull;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static ru.croc.ugd.ssr.service.utils.IcsUtils.generateIcsFileName;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.FlatType;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.RealEstate;
import ru.croc.ugd.ssr.RealEstateDataType;
import ru.croc.ugd.ssr.config.contractappointment.ContractAppointmentProperties;
import ru.croc.ugd.ssr.contractappointment.Applicant;
import ru.croc.ugd.ssr.contractappointment.ContractAppointment;
import ru.croc.ugd.ssr.contractappointment.ContractAppointmentData;
import ru.croc.ugd.ssr.contractdigitalsign.ContractDigitalSign;
import ru.croc.ugd.ssr.contractdigitalsign.ContractDigitalSignData;
import ru.croc.ugd.ssr.contractdigitalsign.ContractDigitalSignFile;
import ru.croc.ugd.ssr.contractdigitalsign.SignData;
import ru.croc.ugd.ssr.dto.contractdigitalsign.ContractDigitalSignFlowStatus;
import ru.croc.ugd.ssr.dto.contractdigitalsign.ContractDigitalSignMoveDateDto;
import ru.croc.ugd.ssr.dto.contractdigitalsign.RestEmployeeContractDigitalSignDto;
import ru.croc.ugd.ssr.dto.contractdigitalsign.RestSaveSignatureDto;
import ru.croc.ugd.ssr.exception.SsrException;
import ru.croc.ugd.ssr.integration.service.notification.ContractDigitalSignElkNotificationService;
import ru.croc.ugd.ssr.mapper.ContractDigitalSignMapper;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.RealEstateDocument;
import ru.croc.ugd.ssr.model.api.chess.CcoSolrResponse;
import ru.croc.ugd.ssr.model.contractDigitalSign.ContractDigitalSignDocument;
import ru.croc.ugd.ssr.model.contractappointment.ContractAppointmentDocument;
import ru.croc.ugd.ssr.service.CapitalConstructionObjectService;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.RealEstateDocumentService;
import ru.croc.ugd.ssr.service.SsrBusinessCalendarService;
import ru.croc.ugd.ssr.service.SsrFilestoreService;
import ru.croc.ugd.ssr.service.SsrFilestoreSignService;
import ru.croc.ugd.ssr.service.UserService;
import ru.croc.ugd.ssr.service.document.ContractAppointmentDocumentService;
import ru.croc.ugd.ssr.service.document.ContractDigitalSignDocumentService;
import ru.croc.ugd.ssr.service.ics.IcsFileService;
import ru.croc.ugd.ssr.utils.DateTimeUtils;
import ru.croc.ugd.ssr.utils.PersonUtils;
import ru.croc.ugd.ssr.utils.RealEstateUtils;
import ru.reinform.cdp.filestore.model.signature.SignatureInfo;
import ru.reinform.cdp.filestore.model.signature.SignatureVerifyInfo;

import java.math.BigInteger;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Service
@AllArgsConstructor
public class DefaultRestContractDigitalSignService implements RestContractDigitalSignService {

    public static final Duration CONTRACT_DIGITAL_SIGN_DURATION = Duration.ofDays(1);

    private final ContractDigitalSignDocumentService contractDigitalSignDocumentService;
    private final ContractAppointmentDocumentService contractAppointmentDocumentService;
    private final RealEstateDocumentService realEstateDocumentService;
    private final PersonDocumentService personDocumentService;
    private final ContractDigitalSignMapper contractDigitalSignMapper;
    private final SsrFilestoreService ssrFilestoreService;
    private final SsrFilestoreSignService ssrFilestoreSignService;
    private final CapitalConstructionObjectService capitalConstructionObjectService;
    private final UserService userService;
    private final IcsFileService icsFileService;
    private final ContractDigitalSignElkNotificationService contractDigitalSignElkNotificationService;
    private final ContractDigitalSignService contractDigitalSignService;
    private final SsrBusinessCalendarService ssrBusinessCalendarService;
    private final ContractAppointmentProperties contractAppointmentProperties;

    @Override
    public List<RestEmployeeContractDigitalSignDto> fetchAll() {
        final String signingContractRole = ofNullable(contractAppointmentProperties.getElectronicSign())
            .map(ContractAppointmentProperties.ElectronicSign::getSigningContractRole)
            .orElse(null);

        final String employeeLogin = userService.getCurrentUserLogin();
        final boolean isAllowedToSingUnassignedDocuments = userService.checkUserGroup(signingContractRole);

        final List<ContractDigitalSignDocument> contractDigitalSignDocuments = isAllowedToSingUnassignedDocuments
            ? contractDigitalSignDocumentService.findUnassignedOrActualByEmployeeLogin(employeeLogin)
            : contractDigitalSignDocumentService.findActualByEmployeeLogin(employeeLogin);

        return contractDigitalSignMapper.toRestEmployeeContractDigitalSignDtos(
            contractDigitalSignDocuments,
            this::retrieveContractAppointment,
            this::retrieveCapitalConstructionObjectData,
            this::retrieveAddressFrom,
            ssrFilestoreService::getFileInfo
        );
    }

    private String retrieveAddressFrom(final String personDocumentId) {
        final PersonDocument personDocument = personDocumentService.fetchDocument(personDocumentId);
        final PersonType personData = personDocument.getDocument().getPersonData();
        final RealEstateDataType realEstateData = retrieveRealEstate(personData);

        final FlatType flat = ofNullable(personData.getFlatID())
            .map(flatId -> RealEstateUtils.getFlatByFlatId(realEstateData, flatId))
            .orElse(null);

        return RealEstateUtils.getFlatAddress(realEstateData, flat);
    }

    private RealEstateDataType retrieveRealEstate(final PersonType personData) {
        return ofNullable(personData.getUNOM())
            .map(BigInteger::toString)
            .map(realEstateDocumentService::fetchDocumentByUnom)
            .map(RealEstateDocument::getDocument)
            .map(RealEstate::getRealEstateData)
            .orElse(null);
    }

    private ContractAppointment retrieveContractAppointment(
        final ContractDigitalSignDocument contractDigitalSignDocument
    ) {
        return ofNullable(contractDigitalSignDocument)
            .map(ContractDigitalSignDocument::getDocument)
            .map(ContractDigitalSign::getContractDigitalSignData)
            .map(ContractDigitalSignData::getContractAppointmentId)
            .flatMap(contractAppointmentDocumentService::fetchById)
            .map(ContractAppointmentDocument::getDocument)
            .orElse(null);
    }

    private CcoSolrResponse retrieveCapitalConstructionObjectData(final ContractAppointment contractAppointment) {
        return ofNullable(contractAppointment)
            .map(ContractAppointment::getContractAppointmentData)
            .map(this::retrieveCcoUnom)
            .map(capitalConstructionObjectService::getCcoSolrResponseByUnom)
            .orElse(null);
    }

    private String retrieveCcoUnom(final ContractAppointmentData contractAppointmentData) {
        final String contractOrderId = ofNullable(contractAppointmentData)
            .map(ContractAppointmentData::getContractOrderId)
            .orElse(null);
        return ofNullable(contractAppointmentData)
            .map(ContractAppointmentData::getApplicant)
            .map(Applicant::getPersonDocumentId)
            .flatMap(personDocumentService::fetchById)
            .map(personDocument -> PersonUtils.getNewFlatByOrderId(personDocument, contractOrderId))
            .map(PersonType.NewFlatInfo.NewFlat::getCcoUnom)
            .map(BigInteger::toString)
            .orElse(null);
    }

    @Override
    public void moveAppointmentDate(ContractDigitalSignMoveDateDto dto) {
        final ContractDigitalSignDocument contractDigitalSignDocument =
            contractDigitalSignDocumentService.findByContractAppointmentId(dto.getContractAppointmentDocumentId())
                .orElseThrow(() -> new SsrException(
                    "Не найдено многостороннее подписание договора с использованием УКЭП для заявления "
                        + dto.getContractAppointmentDocumentId()
                ));

        if (nonNull(dto.getAppointmentDate()) && isWorkDay(dto.getAppointmentDate())) {
            updateAppointmentDate(dto, contractDigitalSignDocument);
        } else {
            throw new SsrException(
                nonNull(dto.getAppointmentDate())
                    ? "Дата " + DateTimeUtils.getFormattedDate(dto.getAppointmentDate()) + " является выходным днем"
                    : "Пустая дата подписания"
            );
        }
    }

    private boolean isWorkDay(final LocalDate date) {
        return ssrBusinessCalendarService.isWorkDay(date);
    }

    private void updateAppointmentDate(
        final ContractDigitalSignMoveDateDto dto,
        final ContractDigitalSignDocument contractDigitalSignDocument
    ) {
        final ContractAppointmentDocument contractAppointmentDocument =
            contractAppointmentDocumentService.fetchDocument(dto.getContractAppointmentDocumentId());
        final ContractAppointmentData contractAppointmentData = contractAppointmentDocument
            .getDocument()
            .getContractAppointmentData();

        final ContractDigitalSignData contractDigitalSignData = contractDigitalSignDocument
            .getDocument()
            .getContractDigitalSignData();

        contractDigitalSignData.setAppointmentDate(dto.getAppointmentDate());

        contractDigitalSignDocumentService.updateDocument(contractDigitalSignDocument, "updateAppointmentDate");

        contractAppointmentData.setAppointmentDateTime(dto.getAppointmentDate().atStartOfDay());

        createIcsFileInFileStore(contractAppointmentDocument)
            .ifPresent(contractAppointmentData::setIcsFileStoreId);

        ofNullable(contractAppointmentData.getIcsFileStoreId())
            .flatMap(icsFileService::uploadIcsFileInChed)
            .ifPresent(contractAppointmentData::setIcsChedFileId);

        contractAppointmentDocumentService.updateDocument(contractAppointmentDocument, "updateAppointmentDate");

        contractDigitalSignElkNotificationService.sendStatus(
            ContractDigitalSignFlowStatus.MOVED_BY_OPERATOR,
            contractAppointmentDocument
        );
    }

    private Optional<String> createIcsFileInFileStore(
        final ContractAppointmentDocument contractAppointmentDocument
    ) {
        final ContractAppointmentData contractAppointment = contractAppointmentDocument
            .getDocument()
            .getContractAppointmentData();

        return icsFileService.createIcsFileInFileStore(
            "Заключение договора с УКЭП",
            "Заключение договора с УКЭП",
            null,
            contractAppointment.getAppointmentDateTime(),
            CONTRACT_DIGITAL_SIGN_DURATION,
            generateIcsFileName(contractAppointmentDocument.getId()),
            contractAppointmentDocument.getFolderId()
        );
    }

    @Override
    public void saveSignatureData(final String id, final RestSaveSignatureDto restSaveSignatureDto) {
        final String fileStoreId = restSaveSignatureDto.getFileStoreId();
        final ContractDigitalSignDocument contractDigitalSignDocument =
            contractDigitalSignDocumentService.fetchDocument(id);
        final SignatureVerifyInfo signatureVerifyInfo = ssrFilestoreSignService.verifySign(fileStoreId);

        ofNullable(signatureVerifyInfo)
            .map(SignatureVerifyInfo::getSignInfos)
            .map(Collection::stream)
            .orElse(Stream.empty())
            .max(Comparator.comparing(SignatureInfo::getSignTime))
            .ifPresent(signatureInfo -> saveSignatureData(
                contractDigitalSignDocument, fileStoreId, signatureInfo, signatureVerifyInfo
            ));
    }

    private void saveSignatureData(
        final ContractDigitalSignDocument contractDigitalSignDocument,
        final String fileStoreId,
        final SignatureInfo signatureInfo,
        final SignatureVerifyInfo signatureVerifyInfo
    ) {
        final String signId = signatureInfo.getSignId();
        ofNullable(signatureVerifyInfo)
            .map(SignatureVerifyInfo::getFileVerifyInfos)
            .map(Collection::stream)
            .orElse(Stream.empty())
            .filter(signatureFileVerifyInfo -> Objects.equals(signatureFileVerifyInfo.getSignId(), signId))
            .findFirst()
            .ifPresent(signatureFileVerifyInfo -> {
                final boolean verificationDisabled = ofNullable(contractAppointmentProperties.getElectronicSign())
                    .map(ContractAppointmentProperties.ElectronicSign::getVerificationDisabled)
                    .orElse(false);
                saveSignatureData(
                    contractDigitalSignDocument,
                    fileStoreId,
                    signatureInfo,
                    signatureFileVerifyInfo.isSuccess() || verificationDisabled
                );
            });
    }

    private void saveSignatureData(
        final ContractDigitalSignDocument contractDigitalSignDocument,
        final String fileStoreId,
        final SignatureInfo signatureInfo,
        final boolean isSuccess
    ) {
        final SignData signData = contractDigitalSignMapper.toSignData(signatureInfo, isSuccess);

        final ContractDigitalSignFile contractDigitalSignFile = of(contractDigitalSignDocument.getDocument())
            .map(ContractDigitalSign::getContractDigitalSignData)
            .map(ContractDigitalSignData::getEmployee)
            .map(employee -> Stream.of(employee.getActFile(), employee.getContractFile()))
            .orElse(Stream.empty())
            .filter(file -> Objects.equals(file.getFileStoreId(), fileStoreId))
            .findFirst()
            .orElseThrow(() -> new SsrException(
                "В данных для многостороннего подписания не найден файл " + fileStoreId
            ));

        contractDigitalSignFile.setSignData(signData);

        try {
            contractDigitalSignDocument.getDocument().getContractDigitalSignData().getEmployee().setLogin(
                userService.getCurrentUserLogin()
            );
        } catch (Exception e) {
            log.error("Current user not found");
        }
        contractDigitalSignDocumentService.updateDocument(contractDigitalSignDocument, "saveSignatureData");

        log.info(
            "Сохранены сведения о подписании сотрудником: contractDigitalSignDocumentId = {}",
            contractDigitalSignDocument.getId()
        );
        if (isSuccess) {
            log.info(
                "Подпись сотрудника прошла верификацию: contractDigitalSignDocumentId = {}",
                contractDigitalSignDocument.getId()
            );
        }

        if (isSuccess) {
            contractDigitalSignService.sendContractSignedStatusIfNeeded(contractDigitalSignDocument);
        }
    }
}
