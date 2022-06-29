package ru.croc.ugd.ssr.service.contractdigitalsign;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.config.contractappointment.ContractAppointmentProperties;
import ru.croc.ugd.ssr.contractappointment.Applicant;
import ru.croc.ugd.ssr.contractappointment.ContractAppointment;
import ru.croc.ugd.ssr.contractappointment.ContractAppointmentData;
import ru.croc.ugd.ssr.contractdigitalsign.ContractDigitalSign;
import ru.croc.ugd.ssr.contractdigitalsign.ContractDigitalSignData;
import ru.croc.ugd.ssr.contractdigitalsign.ContractDigitalSignFile;
import ru.croc.ugd.ssr.contractdigitalsign.ElkUserNotification;
import ru.croc.ugd.ssr.contractdigitalsign.Employee;
import ru.croc.ugd.ssr.contractdigitalsign.Owner;
import ru.croc.ugd.ssr.contractdigitalsign.Owner.ElkUserNotifications;
import ru.croc.ugd.ssr.contractdigitalsign.SignData;
import ru.croc.ugd.ssr.dto.ElkUserNotificationDto;
import ru.croc.ugd.ssr.dto.contractappointment.ContractAppointmentFlowStatus;
import ru.croc.ugd.ssr.dto.contractdigitalsign.ContractDigitalSignFlowStatus;
import ru.croc.ugd.ssr.dto.contractdigitalsign.ContractDigitalSignNotificationStatus;
import ru.croc.ugd.ssr.dto.contractdigitalsign.WordFileLinkDto;
import ru.croc.ugd.ssr.exception.SsrException;
import ru.croc.ugd.ssr.integration.service.notification.ContractDigitalSignElkNotificationService;
import ru.croc.ugd.ssr.mapper.ContractDigitalSignMapper;
import ru.croc.ugd.ssr.model.contractDigitalSign.ContractDigitalSignDocument;
import ru.croc.ugd.ssr.model.contractappointment.ContractAppointmentDocument;
import ru.croc.ugd.ssr.service.ChedFileService;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.SsrFilestoreService;
import ru.croc.ugd.ssr.service.SsrFilestoreSignService;
import ru.croc.ugd.ssr.service.document.ContractAppointmentDocumentService;
import ru.croc.ugd.ssr.service.document.ContractDigitalSignDocumentService;
import ru.croc.ugd.ssr.service.reporter.SsrReporterService;
import ru.croc.ugd.ssr.utils.DateTimeUtils;
import ru.reinform.cdp.filestore.model.signature.SignatureInfo;
import ru.reinform.cdp.filestore.model.signature.SignatureVerifyInfo;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
@AllArgsConstructor
public class DefaultContractDigitalSignService implements ContractDigitalSignService {

    private static final String CONTRACT_FILE_ASGUF_CODE = "12027";
    private static final String CONTRACT_FILE_CHED_DOCUMENT_CLASS = "AgreementEquivalentApartment";

    private static final String ACT_FILE_ASGUF_CODE = "12900";
    private static final String ACT_FILE_CHED_DOCUMENT_CLASS = "AcceptanceCertificateOfResidentialPremises";

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private final ContractDigitalSignDocumentService contractDigitalSignDocumentService;
    private final ContractAppointmentDocumentService contractAppointmentDocumentService;
    private final ContractDigitalSignElkNotificationService contractDigitalSignElkNotificationService;
    private final ContractDigitalSignMapper contractDigitalSignMapper;
    private final ChedFileService chedFileService;
    private final SsrFilestoreService ssrFilestoreService;
    private final SsrReporterService ssrReporterService;
    private final PersonDocumentService personDocumentService;
    private final ContractDigitalSignNotificationDtoFactory contractDigitalSignNotificationDtoFactory;
    private final SsrFilestoreSignService ssrFilestoreSignService;
    private final ContractAppointmentProperties contractAppointmentProperties;

    @Override
    public void sendContractDigitalSignNotificationsForToday() {
        contractDigitalSignDocumentService.fetchAllForToday()
            .stream()
            .map(contractDigitalSignNotificationDtoFactory::createDto)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .forEach(this::processNotificationDto);
    }

    @Override
    public void processElkUserNotificationStatus(
        final Owner owner,
        final ElkUserNotificationDto elkUserNotificationDto,
        final ContractDigitalSignDocument contractDigitalSignDocument
    ) {
        final boolean isSentStatus = addNotificationStatusToContractDigitalSign(
            owner, elkUserNotificationDto, contractDigitalSignDocument
        );

        if (isSentStatus) {
            final Boolean isVerified = saveOwnerSignData(owner, contractDigitalSignDocument);
            final boolean verificationDisabled = ofNullable(contractAppointmentProperties.getElectronicSign())
                .map(ContractAppointmentProperties.ElectronicSign::getVerificationDisabled)
                .orElse(false);
            if (nonNull(isVerified) || verificationDisabled) {
                log.info(
                    "Подпись жителя проверена. Подпись корректна. "
                        + "personDocumentId = {}, contractDigitalSignDocumentId = {}",
                    owner.getPersonDocumentId(),
                    contractDigitalSignDocument.getId()
                );
            }
            if (isNull(isVerified)) {
                if (!verificationDisabled) {
                    log.warn(
                        "Failed to verify signature data: contractDigitalSignDocumentId = {}, personDocumentId = {}",
                        contractDigitalSignDocument.getId(),
                        owner.getPersonDocumentId()
                    );
                }
            } else {
                final ContractAppointmentDocument contractAppointmentDocument =
                    contractAppointmentDocumentService.fetchByContractDigitalSign(contractDigitalSignDocument);
                if (isVerified) {
                    contractDigitalSignElkNotificationService.sendNotificationStatus(
                        ContractDigitalSignNotificationStatus.ACCEPTED,
                        ContractDigitalSignNotificationStatus.SIGNED_BY_OWNER,
                        contractDigitalSignDocument,
                        contractAppointmentDocument,
                        owner.getPersonDocumentId()
                    );
                    sendContractSignedStatusIfNeeded(contractDigitalSignDocument, contractAppointmentDocument);
                } else {
                    contractDigitalSignElkNotificationService.sendNotificationStatus(
                        ContractDigitalSignNotificationStatus.SIGNED_INCORRECTLY,
                        ContractDigitalSignNotificationStatus.SIGNED_INCORRECTLY_BY_OWNER,
                        contractDigitalSignDocument,
                        contractAppointmentDocument,
                        owner.getPersonDocumentId()
                    );
                    sendRefuseToProvideServiceStatus(owner, contractAppointmentDocument);
                }
            }
        }
    }

    @Override
    public void addNotificationStatusToContractDigitalSign(final ElkUserNotificationDto elkUserNotificationDto) {
        final ContractDigitalSignDocument contractDigitalSignDocument = contractDigitalSignDocumentService
            .findByNotificationEno(elkUserNotificationDto.getEno())
            .orElse(null);

        if (nonNull(contractDigitalSignDocument)) {
            final List<Owner> owners = of(contractDigitalSignDocument)
                .map(ContractDigitalSignDocument::getDocument)
                .map(ContractDigitalSign::getContractDigitalSignData)
                .map(ContractDigitalSignData::getOwners)
                .map(ContractDigitalSignData.Owners::getOwner)
                .map(Collection::stream)
                .orElse(Stream.empty())
                .filter(owner -> existsElkUserNotification(owner, elkUserNotificationDto))
                .collect(Collectors.toList());

            if (owners.isEmpty()) {
                log.warn(
                    "Unable to find owner with elkUserNotification in contractDigitalSign: "
                        + "personDocumentId = {}, eno = {}, contractDigitalSignDocumentId = {}",
                    elkUserNotificationDto.getPersonDocumentId(),
                    elkUserNotificationDto.getEno(),
                    contractDigitalSignDocument.getId()
                );
            } else {
                owners.forEach(owner -> processElkUserNotificationStatus(
                    owner, elkUserNotificationDto, contractDigitalSignDocument
                ));
            }

        } else {
            log.warn(
                "Unable to find contractDigitalSign with elkUserNotification: eno = {}",
                elkUserNotificationDto.getEno()
            );
        }
    }

    private boolean addNotificationStatusToContractDigitalSign(
        final Owner owner,
        final ElkUserNotificationDto elkUserNotificationDto,
        final ContractDigitalSignDocument contractDigitalSignDocument
    ) {
        final boolean isSentStatus = contractDigitalSignElkNotificationService.changeElkUserNotificationStatus(
            owner, elkUserNotificationDto
        );
        contractDigitalSignDocumentService.updateDocument(
            contractDigitalSignDocument, "addNotificationStatusToContractDigitalSign"
        );
        return isSentStatus;
    }

    private boolean existsElkUserNotification(final Owner owner, final ElkUserNotificationDto elkUserNotificationDto) {
        return Objects.equals(elkUserNotificationDto.getPersonDocumentId(), owner.getPersonDocumentId())
            && ofNullable(owner.getElkUserNotifications())
            .map(Owner.ElkUserNotifications::getElkUserNotification)
            .map(Collection::stream)
            .orElse(Stream.empty())
            .anyMatch(elkUserNotification -> Objects.equals(
                elkUserNotificationDto.getEno(), elkUserNotification.getEno()
            ));
    }

    public void sendContractSignedStatusIfNeeded(final ContractDigitalSignDocument contractDigitalSignDocument) {
        final ContractAppointmentDocument contractAppointmentDocument =
            contractAppointmentDocumentService.fetchByContractDigitalSign(contractDigitalSignDocument);
        sendContractSignedStatusIfNeeded(contractDigitalSignDocument, contractAppointmentDocument);
    }

    private void sendContractSignedStatusIfNeeded(
        final ContractDigitalSignDocument contractDigitalSignDocument,
        final ContractAppointmentDocument contractAppointmentDocument
    ) {
        final ContractDigitalSignData contractDigitalSignData = contractDigitalSignDocument.getDocument()
            .getContractDigitalSignData();
        if (isVerifiedAllSignatures(contractDigitalSignData)) {
            addAllSignaturesToOwnerFiles(contractDigitalSignData);
            contractDigitalSignDocumentService.updateDocument(
                contractDigitalSignDocument, "addAllSignaturesToOwnerFiles"
            );
            contractDigitalSignElkNotificationService.sendStatus(
                ContractDigitalSignFlowStatus.CONTRACT_SIGNED,
                contractAppointmentDocument
            );
            contractAppointmentDocument.getDocument()
                .getContractAppointmentData().setStatusId(ContractDigitalSignFlowStatus.CONTRACT_SIGNED.getId());
            contractAppointmentDocumentService.updateDocument(
                contractAppointmentDocument, "addAllSignaturesToOwnerFiles"
            );
        }
    }

    private void addAllSignaturesToOwnerFiles(final ContractDigitalSignData contractDigitalSignData) {
        of(contractDigitalSignData.getEmployee().getActFile())
            .map(ContractDigitalSignFile::getFileStoreId)
            .map(fileStoreId -> addAllSignaturesToActFile(fileStoreId, contractDigitalSignData))
            .ifPresent(chedFileId -> contractDigitalSignData.getEmployee().getActFile().setChedFileId(chedFileId));
        of(contractDigitalSignData.getEmployee().getContractFile())
            .map(ContractDigitalSignFile::getFileStoreId)
            .map(fileStoreId -> addAllSignaturesToContractFile(fileStoreId, contractDigitalSignData))
            .ifPresent(chedFileId -> contractDigitalSignData.getEmployee().getContractFile().setChedFileId(chedFileId));
    }

    private String addAllSignaturesToContractFile(
        final String fileStoreId, final ContractDigitalSignData contractDigitalSignData
    ) {
        ofNullable(contractDigitalSignData.getOwners())
            .map(ContractDigitalSignData.Owners::getOwner)
            .map(Collection::stream)
            .orElse(Stream.empty())
            .map(Owner::getContractFile)
            .forEach(ownerFileStoreId -> addSignatureToFile(ownerFileStoreId, fileStoreId));
        final String chedFileId = chedFileService.uploadFileToChed(
            fileStoreId, CONTRACT_FILE_ASGUF_CODE, CONTRACT_FILE_CHED_DOCUMENT_CLASS
        );
        log.info("Договор c подписями сохранен в ЦХЭД: fileStoreId = {}, chedFileId = {}", fileStoreId, chedFileId);
        return chedFileId;
    }

    private String addAllSignaturesToActFile(
        final String fileStoreId, final ContractDigitalSignData contractDigitalSignData
    ) {
        ofNullable(contractDigitalSignData.getOwners())
            .map(ContractDigitalSignData.Owners::getOwner)
            .map(Collection::stream)
            .orElse(Stream.empty())
            .map(Owner::getActFile)
            .forEach(ownerFileStoreId -> addSignatureToFile(ownerFileStoreId, fileStoreId));
        final String chedFileId = chedFileService.uploadFileToChed(
            fileStoreId, ACT_FILE_ASGUF_CODE, ACT_FILE_CHED_DOCUMENT_CLASS
        );
        log.info("Акт c подписями сохранен в ЦХЭД: fileStoreId = {}, chedFileId = {}", fileStoreId, chedFileId);
        return chedFileId;
    }

    private void addSignatureToFile(final ContractDigitalSignFile contractDigitalSignFile, final String fileStoreId) {
        final String ownerFileId = contractDigitalSignFile.getFileStoreId();
        final String ownerSignId = contractDigitalSignFile.getSignData().getSignId();
        final byte[] signContent = ssrFilestoreSignService.getSign(ownerFileId, ownerSignId);
        ssrFilestoreSignService.addSign(fileStoreId, signContent);
    }

    private boolean isVerifiedAllSignatures(final ContractDigitalSignData contractDigitalSignData) {
        final boolean isVerifiedEmployeeSignatures = ofNullable(contractDigitalSignData)
            .map(ContractDigitalSignData::getEmployee)
            .filter(employee -> isVerifiedSignature(employee.getContractFile())
                && isVerifiedSignature(employee.getActFile()))
            .isPresent();
        final boolean isVerifiedOwnerSignatures = ofNullable(contractDigitalSignData)
            .map(ContractDigitalSignData::getOwners)
            .map(ContractDigitalSignData.Owners::getOwner)
            .map(Collection::stream)
            .orElse(Stream.empty())
            .allMatch(owner -> isVerifiedSignature(owner.getContractFile())
                && isVerifiedSignature(owner.getActFile()));
        return isVerifiedEmployeeSignatures && isVerifiedOwnerSignatures;
    }

    private boolean isVerifiedSignature(final ContractDigitalSignFile contractDigitalSignFile) {
        return ofNullable(contractDigitalSignFile)
            .map(ContractDigitalSignFile::getSignData)
            .map(SignData::isIsVerified)
            .orElse(false);
    }

    private void sendRefuseToProvideServiceStatus(
        final Owner owner, final ContractAppointmentDocument contractAppointmentDocument
    ) {
        final String applicantDocumentId = ofNullable(contractAppointmentDocument)
            .map(ContractAppointmentDocument::getDocument)
            .map(ContractAppointment::getContractAppointmentData)
            .map(ContractAppointmentData::getApplicant)
            .map(Applicant::getPersonDocumentId)
            .orElse(null);
        if (Objects.equals(owner.getPersonDocumentId(), applicantDocumentId)) {
            contractDigitalSignElkNotificationService.sendStatus(
                ContractDigitalSignFlowStatus.REFUSE_TO_PROVIDE_SERVICE_BY_APPLICANT,
                contractAppointmentDocument
            );
        } else {
            contractDigitalSignElkNotificationService.sendStatus(
                ContractDigitalSignFlowStatus.REFUSE_TO_PROVIDE_SERVICE_BY_OWNER,
                contractAppointmentDocument
            );
        }
    }

    private Boolean saveOwnerSignData(
        final Owner owner, final ContractDigitalSignDocument contractDigitalSignDocument
    ) {
        final Boolean isVerified = copySignDataFromChed(owner);
        contractDigitalSignDocumentService.updateDocument(contractDigitalSignDocument, "saveOwnerSignData");
        return isVerified;
    }

    public Boolean copySignDataFromChed(final Owner owner) {
        return personDocumentService
            .fetchFolderIdByPersonDocumentId(owner.getPersonDocumentId())
            .map(folderId -> {
                copySignDataFromChed(folderId, owner.getActFile());
                return copySignDataFromChed(folderId, owner.getContractFile());
            })
            .orElse(false);
    }

    private Boolean copySignDataFromChed(final String folderId, final ContractDigitalSignFile contractDigitalSignFile) {
        copyFileWithSignFromChed(folderId, contractDigitalSignFile);
        return setSignatureData(contractDigitalSignFile);
    }

    private void processNotificationDto(final ContractDigitalSignNotificationDto contractDigitalSignNotificationDto) {
        try {
            prepareContractAndAct(contractDigitalSignNotificationDto);
            changeContractAppointmentStatus(contractDigitalSignNotificationDto.getContractAppointmentDocument());
            sendContractDigitalSignNotifications(
                contractDigitalSignNotificationDto.getContractDigitalSignDocument(),
                contractDigitalSignNotificationDto.getContractAppointmentDocument()
            );
        } catch (Exception e) {
            log.error(
                "Unable to prepare or send contract digital sign notifications "
                    + "for contractDigitalSignDocumentId = {}: {}",
                contractDigitalSignNotificationDto.getContractDigitalSignDocument().getId(),
                e.getMessage(),
                e
            );
        }
    }

    private void prepareContractAndAct(final ContractDigitalSignNotificationDto contractDigitalSignNotificationDto) {
        prepareContractAndActForOwners(contractDigitalSignNotificationDto);

        prepareContractAndActForEmployee(contractDigitalSignNotificationDto);

        contractDigitalSignDocumentService.updateDocument(
            contractDigitalSignNotificationDto.getContractDigitalSignDocument(),
            "prepareContractAndAct"
        );
    }

    private void prepareContractAndActForOwners(
        final ContractDigitalSignNotificationDto contractDigitalSignNotificationDto
    ) {
        contractDigitalSignNotificationDto.getOwners().forEach(owner -> {
            final Optional<String> optionalOwnerFolderId = personDocumentService
                .fetchFolderIdByPersonDocumentId(owner.getPersonDocumentId());
            if (optionalOwnerFolderId.isPresent()) {
                final String ownerFolderId = optionalOwnerFolderId.get();
                owner.setContractFile(createContractFile(contractDigitalSignNotificationDto, ownerFolderId));
                owner.setActFile(createActFile(contractDigitalSignNotificationDto, ownerFolderId));
            } else {
                log.warn(
                    "Failed to fetch folderId for personDocumentId: {}",
                    owner.getPersonDocumentId()
                );
            }
        });
    }

    private void prepareContractAndActForEmployee(
        final ContractDigitalSignNotificationDto contractDigitalSignNotificationDto
    ) {
        final String folderId = contractDigitalSignNotificationDto
            .getContractAppointmentDocument()
            .getFolderId();
        if (nonNull(folderId)) {
            final ContractDigitalSignData contractDigitalSign = contractDigitalSignNotificationDto
                .getContractDigitalSignDocument()
                .getDocument()
                .getContractDigitalSignData();
            if (isNull(contractDigitalSign.getEmployee())) {
                contractDigitalSign.setEmployee(new Employee());
            }
            contractDigitalSign.getEmployee()
                .setContractFile(createContractFile(contractDigitalSignNotificationDto, folderId));
            contractDigitalSign.getEmployee()
                .setActFile(createActFile(contractDigitalSignNotificationDto, folderId));
        } else {
            log.warn(
                "Failed to fetch folderId for ContractAppointmentDocument: {}",
                contractDigitalSignNotificationDto
                    .getContractAppointmentDocument()
                    .getId()
            );
        }
    }

    private ContractDigitalSignFile createContractFile(
        final ContractDigitalSignNotificationDto contractDigitalSignNotificationDto, final String folderId
    ) {
        return createContractDigitalSignFile(
            contractDigitalSignNotificationDto,
            folderId,
            "2",
            "Договор_",
            CONTRACT_FILE_ASGUF_CODE,
            CONTRACT_FILE_CHED_DOCUMENT_CLASS,
            this::retrieveWordFileLinkForContract,
            "договора"
        );
    }

    private String retrieveWordFileLinkForContract(final WordFileLinkDto wordFileLinkDto) {
        final String dateByWords = DateTimeUtils.getDateByWords(wordFileLinkDto.getAppointmentDate());

        return ssrReporterService.wordInsertHtml(
            wordFileLinkDto.getRtfFile().getFileLink(),
            "DOCUMENT_DATE_TEXT",
            "<strong style=\"font-size: 11px; font-family: 'Times New Roman', Times, serif;\">"
                + dateByWords
                + "</strong>",
            wordFileLinkDto.getFolderId(),
            "contractToSign__service.rtf"
        );
    }

    private ContractDigitalSignFile createActFile(
        final ContractDigitalSignNotificationDto contractDigitalSignNotificationDto, final String folderId
    ) {
        return createContractDigitalSignFile(
            contractDigitalSignNotificationDto,
            folderId,
            "5",
            "Акт_",
            ACT_FILE_ASGUF_CODE,
            ACT_FILE_CHED_DOCUMENT_CLASS,
            this::retrieveWordFileLinkForAct,
            "акта"
        );
    }

    private String retrieveWordFileLinkForAct(final WordFileLinkDto wordFileLinkDto) {
        final String actDate = wordFileLinkDto.getAppointmentDate().format(DATE_FORMATTER);

        final String wordFileLink = ssrReporterService.wordInsertHtml(
            wordFileLinkDto.getRtfFile().getFileLink(),
            "DocDate1",
            "<span style=\"font-size: 14px; font-family: 'Times New Roman', Times, serif;\">"
                + actDate
                + "</span>",
            wordFileLinkDto.getFolderId(),
            "actToSign__service.rtf"
        );

        return ssrReporterService.wordInsertHtml(
            wordFileLink,
            " от DocDate2 года ",
            "<span style=\"font-size: 16px; font-family: 'Times New Roman', Times, serif;\"> от "
                + actDate
                + " года</span>",
            wordFileLinkDto.getFolderId(),
            "actToSign__service.rtf"
        );
    }

    private ContractDigitalSignFile createContractDigitalSignFile(
        final ContractDigitalSignNotificationDto contractDigitalSignNotificationDto,
        final String folderId,
        final String fileType,
        final String fileNamePrefix,
        final String asgufCode,
        final String chedDocumentClass,
        final Function<WordFileLinkDto, String> retrieveWordFileLink,
        final String documentTypeName
    ) {
        final PersonType.Contracts.Contract contract = contractDigitalSignNotificationDto.getContract();
        final String addressTo = contractDigitalSignNotificationDto.getAddressTo();
        final LocalDate appointmentDate = contractDigitalSignNotificationDto
            .getContractDigitalSignData()
            .getAppointmentDate();
        final PersonType.Contracts.Contract.Files.File rtfFile = ofNullable(contract.getFiles())
            .map(PersonType.Contracts.Contract.Files::getFile)
            .map(Collection::stream)
            .orElse(Stream.empty())
            .filter(file -> fileType.equals(file.getFileType()))
            .reduce(((first, second) -> second))
            .orElseThrow(() -> new SsrException(
                String.format("Rtf file not found: orderId %s, fileType %s", contract.getOrderId(), fileType)
            ));

        final String fileName = fileNamePrefix + addressTo.replaceAll(" ", "_") + ".pdf";

        final WordFileLinkDto wordFileLinkDto = WordFileLinkDto.builder()
            .appointmentDate(appointmentDate)
            .folderId(folderId)
            .rtfFile(rtfFile)
            .build();

        final String pdfFileLink = ssrReporterService.convertWordToPdf(
            retrieveWordFileLink.apply(wordFileLinkDto), folderId, fileName
        );

        final String chedFileId = chedFileService
            .uploadFileToChed(pdfFileLink, asgufCode, chedDocumentClass);

        final ContractDigitalSignFile contractDigitalSignFile = new ContractDigitalSignFile();
        contractDigitalSignFile.setFileStoreId(pdfFileLink);
        contractDigitalSignFile.setChedFileId(chedFileId);
        log.info("Файл {} сформирован и помещен в ЦХЭД: chedFileId = {}", documentTypeName, chedFileId);

        return contractDigitalSignFile;
    }

    private void sendContractDigitalSignNotifications(
        final ContractDigitalSignDocument contractDigitalSignDocument,
        final ContractAppointmentDocument contractAppointmentDocument
    ) {
        final List<ElkUserNotificationDto> elkApplicantNotificationDtos =
            contractDigitalSignElkNotificationService.sendNotification(
                ContractDigitalSignFlowStatus.COLLECT_SIGNATURES_APPLICANT,
                contractAppointmentDocument,
                false
            );

        final List<ElkUserNotificationDto> elkOtherOwnerNotificationDtos =
            contractDigitalSignElkNotificationService.sendNotification(
                ContractDigitalSignFlowStatus.COLLECT_SIGNATURES_OWNER,
                contractAppointmentDocument,
                true
            );

        if (!elkApplicantNotificationDtos.isEmpty() || !elkOtherOwnerNotificationDtos.isEmpty()) {
            elkApplicantNotificationDtos.forEach(elkUserNotificationDto -> addElkUserNotification(
                contractDigitalSignDocument,
                elkUserNotificationDto,
                ContractDigitalSignFlowStatus.COLLECT_SIGNATURES_APPLICANT
            ));
            elkOtherOwnerNotificationDtos.forEach(elkUserNotificationDto -> addElkUserNotification(
                contractDigitalSignDocument,
                elkUserNotificationDto,
                ContractDigitalSignFlowStatus.COLLECT_SIGNATURES_OWNER
            ));
            contractDigitalSignDocumentService.updateDocument(
                contractDigitalSignDocument, "sendContractDigitalSignNotifications"
            );
        }
    }

    private void changeContractAppointmentStatus(
        final ContractAppointmentDocument contractAppointmentDocument
    ) {
        ofNullable(contractAppointmentDocument)
            .map(ContractAppointmentDocument::getDocument)
            .map(ContractAppointment::getContractAppointmentData)
            .ifPresent(contractAppointmentData -> {
                contractAppointmentData.setStatusId(ContractAppointmentFlowStatus.COLLECT_SIGNATURES.getId());
                contractAppointmentDocumentService.updateDocument(
                    contractAppointmentDocument, "changeContractAppointmentStatus"
                );
            });
    }

    private void addElkUserNotification(
        final ContractDigitalSignDocument contractDigitalSignDocument,
        final ElkUserNotificationDto elkUserNotificationDto,
        final ContractDigitalSignFlowStatus contractDigitalSignFlowStatus
    ) {
        if (nonNull(contractDigitalSignDocument)) {
            final List<Owner> owners = of(contractDigitalSignDocument)
                .map(ContractDigitalSignDocument::getDocument)
                .map(ContractDigitalSign::getContractDigitalSignData)
                .map(ContractDigitalSignData::getOwners)
                .map(ContractDigitalSignData.Owners::getOwner)
                .map(Collection::stream)
                .orElse(Stream.empty())
                .filter(owner -> Objects.equals(
                    elkUserNotificationDto.getPersonDocumentId(), owner.getPersonDocumentId()
                ))
                .collect(Collectors.toList());

            if (owners.isEmpty()) {
                log.warn(
                    "Unable to add elkUserNotification in contractDigitalSign: "
                        + "personDocumentId = {}, eno = {}, contractDigitalSignDocumentId = {}",
                    elkUserNotificationDto.getPersonDocumentId(),
                    elkUserNotificationDto.getEno(),
                    contractDigitalSignDocument.getId()
                );
            } else {
                owners.forEach(owner -> addElkUserNotification(
                    owner, elkUserNotificationDto, contractDigitalSignFlowStatus
                ));
            }
        }
    }

    private void addElkUserNotification(
        final Owner owner,
        final ElkUserNotificationDto elkUserNotificationDto,
        final ContractDigitalSignFlowStatus contractDigitalSignFlowStatus
    ) {
        final ElkUserNotification elkUserNotification = contractDigitalSignMapper.toElkUserNotification(
            elkUserNotificationDto, contractDigitalSignFlowStatus
        );

        if (isNull(owner.getElkUserNotifications())) {
            owner.setElkUserNotifications(new ElkUserNotifications());
        }
        owner.getElkUserNotifications().getElkUserNotification().add(elkUserNotification);
    }

    private void copyFileWithSignFromChed(
        final String folderId, final ContractDigitalSignFile contractDigitalSignFile
    ) {
        ofNullable(contractDigitalSignFile)
            .map(ContractDigitalSignFile::getChedFileId)
            .ifPresent(chedFileId -> copyFileWithSignFromChed(folderId, chedFileId, contractDigitalSignFile));
    }

    private void copyFileWithSignFromChed(
        final String folderId, final String chedFileId, final ContractDigitalSignFile contractDigitalSignFile
    ) {
        if (nonNull(contractDigitalSignFile)) {
            ofNullable(contractDigitalSignFile.getFileStoreId())
                .ifPresent(ssrFilestoreService::deleteFile);

            contractDigitalSignFile.setFileStoreId(
                chedFileService.extractFileFromChedAndGetFileLink(chedFileId, folderId)
            );
        }
    }

    private Boolean setSignatureData(final ContractDigitalSignFile contractDigitalSignFile) {
        return ofNullable(contractDigitalSignFile)
            .map(ContractDigitalSignFile::getFileStoreId)
            .map(fileStoreId -> {
                final SignatureVerifyInfo signatureVerifyInfo = ssrFilestoreSignService.verifySign(fileStoreId);
                return ofNullable(signatureVerifyInfo)
                    .map(SignatureVerifyInfo::getSignInfos)
                    .map(Collection::stream)
                    .orElse(Stream.empty())
                    .max(Comparator.comparing(SignatureInfo::getSignTime))
                    .map(signatureInfo -> setSignatureData(
                        contractDigitalSignFile, signatureInfo, signatureVerifyInfo
                    ))
                    .orElse(null);
            })
            .orElse(null);
    }

    private Boolean setSignatureData(
        final ContractDigitalSignFile contractDigitalSignFile,
        final SignatureInfo signatureInfo,
        final SignatureVerifyInfo signatureVerifyInfo
    ) {
        final String signId = signatureInfo.getSignId();
        return ofNullable(signatureVerifyInfo)
            .map(SignatureVerifyInfo::getFileVerifyInfos)
            .map(Collection::stream)
            .orElse(Stream.empty())
            .filter(signatureFileVerifyInfo -> Objects.equals(signatureFileVerifyInfo.getSignId(), signId))
            .findFirst()
            .map(signatureFileVerifyInfo -> {
                final boolean verificationDisabled = ofNullable(contractAppointmentProperties.getElectronicSign())
                    .map(ContractAppointmentProperties.ElectronicSign::getVerificationDisabled)
                    .orElse(false);
                return setSignatureData(
                    contractDigitalSignFile, signatureInfo, signatureFileVerifyInfo.isSuccess() || verificationDisabled
                );
            })
            .orElse(null);
    }

    private Boolean setSignatureData(
        final ContractDigitalSignFile contractDigitalSignFile,
        final SignatureInfo signatureInfo,
        final boolean isSuccess
    ) {
        final SignData signData = contractDigitalSignMapper.toSignData(signatureInfo, isSuccess);
        contractDigitalSignFile.setSignData(signData);
        return signData.isIsVerified();
    }
}
