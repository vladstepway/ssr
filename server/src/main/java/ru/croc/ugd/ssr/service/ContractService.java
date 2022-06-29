package ru.croc.ugd.ssr.service;

import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.PersonType.Contracts.Contract;
import ru.croc.ugd.ssr.PersonType.Contracts.Contract.Files.File;
import ru.croc.ugd.ssr.dto.SignContractDto;
import ru.croc.ugd.ssr.enums.ContractFileType;
import ru.croc.ugd.ssr.exception.PersonNotFoundException;
import ru.croc.ugd.ssr.exception.SsrException;
import ru.croc.ugd.ssr.integration.service.flows.ContractSignedFlowsService;
import ru.croc.ugd.ssr.integration.service.flows.SignActStatusFlowService;
import ru.croc.ugd.ssr.mapper.ContractMapper;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPSignActStatusType;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPSignAgreementStatusType;
import ru.croc.ugd.ssr.service.contractappointment.ContractAppointmentService;
import ru.croc.ugd.ssr.utils.PersonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContractService {

    @Value("${integration.ched.agreementEquivalentApartmentCode:12027}")
    private String agreementEquivalentApartmentCode;
    @Value("${integration.ched.agreementEquivalentApartmentDocType:AgreementEquivalentApartment}")
    private String agreementEquivalentApartmentDocType;
    @Value("${integration.ched.acceptCertOfResidentialPremisesCode:12900}")
    private String acceptCertOfResidentialPremisesCode;
    @Value("${integration.ched.acceptCertOfResidentialPremisesDocType:AcceptanceCertificateOfResidentialPremises}")
    private String acceptCertOfResidentialPremisesDocType;

    private final PersonDocumentService personDocumentService;
    private final ChedFileService chedFileService;
    private final UserService userService;
    private final ContractAppointmentService contractAppointmentService;
    private final ContractMapper contractMapper;
    private final ContractSignedFlowsService contractSignedFlowsService;
    private final SignActStatusFlowService signActStatusFlowService;

    @Transactional
    public void signContract(final String contractOrderId, final SignContractDto signContractDto) {
        if (signContractDto.isContractEntered()) {
            processSignedContract(contractOrderId, signContractDto);
        } else {
            ofNullable(signContractDto.getContractAppointmentId())
                .ifPresent(contractAppointmentId ->
                    contractAppointmentService.processUnsignedContract(
                        contractAppointmentId, signContractDto.getRefuseReason()
                    ));
        }
    }

    private void processSignedContract(final String contractOrderId, final SignContractDto signContractDto) {
        final String personDocumentId = signContractDto.getPersonDocumentId();
        final PersonDocument personDocument = personDocumentService.fetchById(personDocumentId)
            .orElseThrow(() ->
                new PersonNotFoundException(String.format("Person not found by id %s", personDocumentId))
            );

        final List<PersonDocument> owners = personDocumentService.getOtherFlatOwners(personDocument, true);
        final Contract signedContract = PersonUtils.getContractByOrderId(personDocument, contractOrderId)
            .map(contract -> updateSignContractDetails(contract, signContractDto))
            .orElseThrow(() -> new SsrException("No contract found with orderId " + contractOrderId));

        owners.forEach(ownerDocument -> {
            final PersonType owner = ownerDocument
                .getDocument()
                .getPersonData();
            owner.setRelocationStatus("9");

            final boolean hasContract = PersonUtils.getContractByOrderId(ownerDocument, contractOrderId).isPresent();

            if (hasContract) {
                final PersonType.Contracts contracts = updateContracts(owner, contractOrderId, signedContract);
                owner.setContracts(contracts);

                final String userLogin = userService.getCurrentUserLogin();
                final String userFullName = userService.getCurrentUserName();

                if (nonNull(signContractDto.getContractFileId()) && nonNull(signContractDto.getContractSignDate())) {
                    sendSignContractStatus(signedContract, ownerDocument, owner.getAffairId(), userLogin, userFullName);
                }

                if (nonNull(signContractDto.getActFileId()) && nonNull(signContractDto.getActSignDate())) {
                    sendSignActStatus(signedContract, ownerDocument, owner.getAffairId(), userLogin, userFullName);
                }
            }

            personDocumentService.updateDocument(ownerDocument.getId(), ownerDocument, true, false, null);

        });

        ofNullable(signContractDto.getContractAppointmentId())
            .ifPresent(contractAppointmentId -> contractAppointmentService
                .processSignedContract(contractAppointmentId, signedContract)
            );
    }

    private Contract updateSignContractDetails(final Contract contract, final SignContractDto signContractDto) {
        ofNullable(signContractDto.getContractSignDate())
            .ifPresent(contract::setContractSignDate);
        ofNullable(signContractDto.getActSignDate())
            .ifPresent(contract::setActSignDate);

        final Contract.TechInfo techInfo = contractMapper
            .toContractTechInfo(userService.getCurrentUserLogin(), userService.getCurrentUserName());
        contract.setTechInfo(techInfo);

        final List<File> signedDocumentsFiles = createSignedDocumentsFiles(signContractDto);
        final Contract.Files files = ofNullable(contract.getFiles())
            .orElseGet(Contract.Files::new);
        files.getFile().addAll(signedDocumentsFiles);
        contract.setFiles(files);

        return contract;
    }

    private List<File> createSignedDocumentsFiles(final SignContractDto signContractDto) {
        final List<File> files = new ArrayList<>();

        final String contractFileId = signContractDto.getContractFileId();
        if (nonNull(contractFileId)) {
            final File contractFile = contractMapper.toContractFile(
                ContractFileType.SIGNED_CONTRACT.getStringValue(),
                contractFileId,
                signContractDto.getContractFileName(),
                uploadContractFileToChed(contractFileId)
            );
            files.add(contractFile);
        }

        final String actFileId = signContractDto.getActFileId();
        if (nonNull(actFileId)) {
            final File actFile = contractMapper.toContractFile(
                ContractFileType.SIGNED_ACT.getStringValue(),
                actFileId,
                signContractDto.getActFileName(),
                uploadActFileToChed(actFileId)
            );
            files.add(actFile);
        }

        return files;
    }

    private String uploadContractFileToChed(final String fileId) {
        return chedFileService.uploadFileToChed(
            fileId, agreementEquivalentApartmentCode, agreementEquivalentApartmentDocType
        );
    }

    private String uploadActFileToChed(final String fileId) {
        return chedFileService.uploadFileToChed(
            fileId, acceptCertOfResidentialPremisesCode, acceptCertOfResidentialPremisesDocType
        );
    }

    private PersonType.Contracts updateContracts(
        final PersonType owner, final String contractOrderId, final Contract signedContract
    ) {
        final PersonType.Contracts contracts = ofNullable(owner.getContracts())
            .orElseGet(PersonType.Contracts::new);

        final List<Contract> ownerContractList = contracts.getContract()
            .stream()
            .map(contract -> Objects.equals(contract.getOrderId(), contractOrderId) ? signedContract : contract)
            .collect(Collectors.toList());

        contracts.getContract().clear();
        contracts.getContract().addAll(ownerContractList);

        return contracts;
    }

    private void sendSignActStatus(
        final Contract signedContract,
        final PersonDocument ownerDocument,
        final String affairId,
        final String userLogin,
        final String userFullName
    ) {
        final String actFileChedId = getFileChedIdByFileType(
            signedContract, ContractFileType.SIGNED_ACT.getStringValue()
        );

        final SuperServiceDGPSignActStatusType.TechInfo techInfo = contractMapper
            .toSuperServiceDgpSignActStatusTypeTechInfo(userLogin, userFullName);

        final SuperServiceDGPSignActStatusType signedActStatus = contractMapper
            .toSuperServiceDgpSignActStatus(
                affairId,
                signedContract.getOrderId(),
                actFileChedId,
                signedContract.getActSignDate(),
                techInfo
            );

        signActStatusFlowService.sendSignedActStatus(ownerDocument, signedActStatus);
    }

    private void sendSignContractStatus(
        final Contract signedContract,
        final PersonDocument ownerDocument,
        final String affairId,
        final String userLogin,
        final String userFullName
    ) {
        final String contractFileChedId = getFileChedIdByFileType(
            signedContract, ContractFileType.SIGNED_CONTRACT.getStringValue()
        );

        final SuperServiceDGPSignAgreementStatusType.TechInfo techInfo = contractMapper
            .toSuperServiceDgpSignAgreementStatusTypeTechInfo(userLogin, userFullName);

        final SuperServiceDGPSignAgreementStatusType signContractStatus = contractMapper
            .toSuperServiceDgpSignAgreementStatusType(
                affairId,
                signedContract.getOrderId(),
                contractFileChedId,
                signedContract.getContractSignDate(),
                techInfo
            );

        contractSignedFlowsService.sendSignedContract(ownerDocument, signContractStatus);
    }

    private String getFileChedIdByFileType(final Contract signedContract, final String fileType) {
        return getFileByFileType(signedContract, fileType)
            .map(File::getChedFileId)
            .orElse(null);
    }

    private Optional<File> getFileByFileType(final Contract signedContract, final String fileType) {
        return signedContract.getFiles().getFile()
            .stream()
            .filter(file -> Objects.equals(fileType, file.getFileType()))
            .findFirst();
    }
}
